# Effect of an `ignoreUrlParams` allow list

This experiment demonstrates the importance of an `ignoreUrlParams` allow list where you set which URL params your app is expecting. This is the preferred alternative to _ignoring_ a fixed list of known/expected params.

## Setup

Open dispatcher.any, and locate the `/ignoreUrlParams` configuration. If you are using the [dispatcher.any from this repository](../../dispatcher-config-basic/private/etc/apache2/conf/dispatcher.any), it will look like so:

```
  /ignoreUrlParams
    {
    /0001 { /glob "*" /type "deny" }
    /0002 { /glob "utm_campaign" /type "allow" }
    }
```

This theoretical site has set up their `/ignoreUrlParams` configuration like this because they are running a marketing campaign, and want the `utm_campaign` URL param to be ignored so that the cache can consider it a `hit` when this URL param is present - no matter what it's value is set to.

> Does it seem backward to have the "utm_campaign" URL param set to "allow"? Think of it as ALLOW'ing the dispatcher to ignore it.

Confirm that this is working as expected:

1. Navigate to `regular-page` on the dispatcher to warm up the cache: http://aem-publish.local:8080/content/dispatchertester/us/en/regular-page.html
1. Follow dispatcher.log: `tail -f /private/var/log/apache2/dispatcher.log`
1. Now, navigate to `regular-page` again, this time with a `utm_campaign` query param: http://aem-publish.local:8080/content/dispatchertester/us/en/regular-page.html?utm_campaign=summer2020

Note the log output in dispatcher.log:

```
[Wed Jul 15 22:01:47 2020] [D] [pid 86982] checking [/content/dispatchertester/us/en/regular-page.html]
[Wed Jul 15 22:01:47 2020] [D] [pid 86982] Query string ignored: utm_campaign=summer2020
[Wed Jul 15 22:01:47 2020] [D] [pid 86982] cache file is newer than lastflush -> use cache [/Library/WebServer/docroot/publish/content/dispatchertester/us/en/regular-page.html]
[Wed Jul 15 22:01:47 2020] [D] [pid 86982] cache-action for [/content/dispatchertester/us/en/regular-page.html]: DELIVER
```

`Query string ignored: utm_campaign=summer2020` - The summer 2020 campaign can continue, without affecting our cache hit ratio (and the performance of our site).

## Problem

