# Effect of `enableTTL`

## Setup

For this experiment, you will need to install ACS AEM Commons on your **Publish** instance. We'll be using the "Dispatcher TTL" feature. You can download the latest release here (v4.8.4 at the time of writing): https://adobe-consulting-services.github.io/acs-aem-commons/

And install it on your local publish instance: http://localhost:4503/crx/packmgr/index.jsp

To verify that the package is installed correctly and has interpreted our [DispatcherMaxAgeHeaderFilter OSGi config]() setting the `max.age` to 60 seconds, issue the following request with cURL:

```
curl -H "Server-Agent: Communique-Dispatcher" -D - http://localhost:4503/content/dispatchertester/us/en.html -o /dev/null
```

Ensure the `Cache-Control: max-age=60` header is included in the response before continuing. 

> Is the `Cache-Control` header missing? Ensure you've installed the latest code in `aem-project/` to your publish instance. To do so with a single command, (from the `aem-project/` dir): `mvn -PautoInstallSinglePackagePublish clean install`.

Next, enable `enableTTL` by un-commenting the following line in dispatcher.any (around line 255):

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

## 