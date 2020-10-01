# Effect of `enableTTL`

Dispatcher versions 4.1.11 and greater can be configured to respect a time-to-live (TTL) based content "timeout". TTL headers (`Cache-Control` or `Expires`) must be included in responses sent from the publish tier in order for the dispatcher to know how long a cached file should "live" for in the cache. When this configuration is enabled (and reasonable TTL values are set), there is no longer a need for a dispatcher flush agent.

## Compatibility

The `enableTTL` configuration can be used with both AEM 6.5 and AEM as a Cloud Service. However, the replication framework from previous versions of AEM is no longer used to publish pages on AEM as a Cloud Service (read the details here: [Content Distribution](https://docs.adobe.com/content/help/en/experience-manager-cloud-service/core-concepts/architecture.html#content-distribution)). You may also find the AEMaaCS [Caching documentation](https://docs.adobe.com/content/help/en/experience-manager-cloud-service/implementing/content-delivery/caching.html) of interest, which goes into more detail about the differences.

## Setup

For this experiment, you will need to install the ACS AEM Commons package on your local **Publish** instance. We'll be using the [Dispatcher TTL](https://adobe-consulting-services.github.io/acs-aem-commons/features/dispatcher-ttl/index.html) feature to set the `max-age` of our content via an OSGi configuration. You can download the latest release here (v4.8.4 at the time of writing): [adobe-consulting-services.github.io/acs-aem-commons/](https://adobe-consulting-services.github.io/acs-aem-commons/)

Next, install it on your local publish instance via Package Manager (you may need to [sign in first](http://localhost:4503/libs/granite/core/content/login.html)): [localhost:4503/crx/packmgr/index.jsp](http://localhost:4503/crx/packmgr/index.jsp)

To verify that the package is installed correctly and has interpreted our [DispatcherMaxAgeHeaderFilter OSGi config](https://github.com/adobe/aem-dispatcher-experiments/blob/enableTTL/aem-project/ui.apps/src/main/content/jcr_root/apps/dispatchertester/config/com.adobe.acs.commons.http.headers.impl.DispatcherMaxAgeHeaderFilter-paths-set-1.xml#L5) configuration included in this repo's `aem-project/`, (which sets the `max.age` of `/content/dispatchertester/*` content to 60 seconds), issue the following request with cURL:

```
curl -H "Server-Agent: Communique-Dispatcher" -D - http://localhost:4503/content/dispatchertester/us/en.html -o /dev/null
```

Ensure the `Cache-Control: max-age=60` header is included in the response before continuing. 

> Is the `Cache-Control` header missing? Ensure you've installed the latest code in `aem-project/` to your publish instance. To do so with a single command, (from the `aem-project/` dir): `mvn -PautoInstallSinglePackagePublish clean install`.

Next, set the `enableTTL` flag to "1" by un-commenting the following line in your dispatcher.any configuration (around line 255):

```
# /enableTTL "1"
```

You will need to restart Apache for this change to take effect:

```
sudo apachectl restart
```

Finally, navigate to regular-page.html and observe the files which are written to the cache:

http://aem-publish.local:8080/content/dispatchertester/us/en/regular-page.html

Result:

<img src="../img/enableTTL-ttl-file.png" width="800">

Note how the last modified timestamp on this file is actually set _in the future_ by a minute - our `max.age`. Once the current time is _after_ the last modified timestamp on it's .ttl file, the file will be considered stale. 

## In the logs

The dispatcher.log output will differ slightly when using `enableTTL` vs. .stat file-based invalidation.

### With `enableTTL`

With `enableTTL` set and a corresponding .ttl file next to the cached resource which is being requested, you will see log entries that match the below format.

When a cached resource's last modified timestamp is BEFORE the last modified timestamp of it's .ttl file, the file is considered VALID:

```
[..] cache file has not expired: /docroot/content/dispatchertester/us/en.html
[..] cache-action for [/content/dispatchertester/us/en.html]: DELIVER
```

When a cached resource's last modified timestamp is AFTER the last modified timestamp of it's .ttl file, the file is considered STALE:

```
[..] cache file has expired: /docroot/content/dispatchertester/us/en.html
[..] cache-action for [/content/dispatchertester/us/en.html]: CREATE
```

### Without `enableTTL`

When `enableTTL` is not set, or a .ttl file is missing for the resource in question, the logs will read differently. When the cached resource is newer than it's nearest .stat file, the cached file is considered VALID:

```
[..] cache file is newer than lastflush -> use cache [/docroot/content/dispatchertester/us/en.html]
[..] cache-action for [/content/dispatchertester/us/en.html]: DELIVER
```

When it's older than it's nearest .stat file, the file is considered STALE:

```
[..] cache file is older than lastflush -> flush [/docroot/content/dispatchertester/us/en.html]
[..] cache-action for [/content/dispatchertester/us/en.html]: CREATE
```

Ensure you are seeing the "cache file has not expired"/"cache file has expired" format in your local dispatcher logs (on macOS: `/private/var/log/apache2/dispatcher.log`) before proceeding.

## Different TTL values

Depending on your use case, you may decide that a TTL of 5 minutes for .html content is reasonable, but cached .js and .css files should live in the cache for 24 hours (perhaps due to your use of [Versioned Clientlibs](https://adobe-consulting-services.github.io/acs-aem-commons/features/versioned-clientlibs/index.html)).