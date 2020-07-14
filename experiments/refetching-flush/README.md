# Effect of Re-fetching Dispatcher Flush

This experiment shows how you can reduce load spikes on high-traffic sites that occur after a flush event (such as a page publish/activation).

## Setup

To simulate an page that is expensive to render, we will be using the page located at `/content/dispatchertester/us/en/page-with-bad-component`. This page has a component on it which takes a configurable amount of time to render, simulating a slow query or other synchronous process which must complete in order to render the page.

Try it out on the author instance: http://localhost:4502/editor.html/content/dispatchertester/us/en/page-with-bad-component.html

You can configure how many milliseconds the component will sleep for by opening it's dialog in edit mode (default is 10 seconds).

Once this component is cached by the dispatcher, it will render immediately. 

Try accessing: http://aem-publish.local:8080/content/dispatchertester/us/en/page-with-bad-component.html

With an empty cache, the page will take ~10 seconds to render.

**However!** Once the page has rendered once and is present in the cache, it will be render immediately without requiring any of the (simulated) expensive rendering on the publish instance.

Try accessing the above link again, once the initial render has completed. The dispatcher should return the cached page and it will render immediately.


## Problem

When this page is changed on the author instance and Published/Activated, it will be *flushed* (deleted) from the dispatcher's cache. This means that *every request* that is received for this page while it is missing from the cache will make its way back to the publish instance, causing the expensive query or process to fire each time. With a high load, and depending on the scale of the publish tier, this could potentially cause the publish instance to run out of threads or memory.

Try this yourself:

- Open `page-with-bad-component` in author mode: http://localhost:4502/editor.html/content/dispatchertester/us/en/page-with-bad-component.html
- "Publish" the page

<img src="../img/publish-page.png" width="400">

- Note how `content/dispatchertester/us/en/page-with-bad-component.html` has been flushed/removed from the dispatcher cache

> Did it not get removed? There may be an issue with your publish instance's [Dispatcher Flush agent](../../docs/Flush-agent-setup.md)

Knowing this, how can we support activation of expensive pages in high load scenarios?

## Test #1: standard cache flush behavior

// TODO!

JMeter command (50 users): 

```
jmeter -n -t Page-with-bad-component-test-plan.jmx -Jrampup=2 -Jthreads=50
```

Result:

```
summary +      1 in 00:00:05 =    0.2/s Avg:  3736 Min:  3736 Max:  3736 Err:     0 (0.00%) Active: 50 Started: 50 Finished: 0
summary +     49 in 00:00:00 = 1884.6/s Avg:  4176 Min:  3195 Max:  5081 Err:     0 (0.00%) Active: 0 Started: 50 Finished: 50
summary =     50 in 00:00:05 =    9.6/s Avg:  4167 Min:  3195 Max:  5081 Err:     0 (0.00%)
```

JMeter command (250 users):

```
jmeter -n -t Page-with-bad-component-test-plan.jmx -Jrampup=10 -Jthreads=250
```

Result:

```
summary +      1 in 00:00:05 =    0.2/s Avg:  1084 Min:  1084 Max:  1084 Err:     0 (0.00%) Active: 130 Started: 130 Finished: 0
summary +    249 in 00:00:05 =   51.9/s Avg:  1363 Min:     1 Max:  5135 Err:     0 (0.00%) Active: 0 Started: 250 Finished: 250
summary =    250 in 00:00:10 =   25.0/s Avg:  1362 Min:     1 Max:  5135 Err:     0 (0.00%)
```

## Setting up Re-fetching flush

The steps to enable Re-fetching Dispatcher Flush are detailed in the "Optimizing the Dispatcher cache" HelpX document. Follow the steps in the "Re-fetching Dispatcher Flush" section, making careful note to modify the `Dispatcher Flush (flush)` agent on your **Publish** instance (as opposed to the Agent on author): https://helpx.adobe.com/ca/experience-manager/kb/optimizing-the-dispatcher-cache.html#refetching-flush

A few helpful links:

- Log in to your publish instance: http://localhost:4503/libs/granite/core/content/login.html
- Package manager on publish: http://localhost:4503/crx/packmgr/index.jsp
- The Dispatcher Flush agent on publish: http://localhost:4503/etc/replication/agents.publish/flush.html

## Confirm that Re-fetching flush is working

- Open a page in author mode that is present in the cache, such as [regular-page](http://localhost:4502/editor.html/content/dispatchertester/us/en/regular-page.html) or [page-with-bad-component](http://localhost:4502/editor.html/content/dispatchertester/us/en/page-with-bad-component.html)
- Observe the directory on your filesystem containing the cached page (eg, `ls /Library/WebServer/docroot/publish/content/dispatchertester/us/en`)
- Publish the page

Note how the cached page is not deleted from the cache! In the background, the dispatcher is making a request back to the publish instance to _replace_ the cached file, instead of deleting it. Once the request is complete, the cached page will be replaced.

## Running the JMeter test


