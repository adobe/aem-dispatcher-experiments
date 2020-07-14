# Flush Agents

## Configure cache flushing from the Publish instance

In the [CQ Dispatcher Webinar](http://my.adobeconnect.com/p7th2gf8k43/), it was recommended to configure cache flushing from the `publish` instance (instead of the `author`) to avoid a race condition.

To do so:

- Disable the author's [dispatcher flush agent](http://localhost:4502/etc/replication/agents.author.html)
    - It will be named `Dispatcher Flush (flush)`, and may already be `disabled`
- Enable the publish instance's dispatcher flush agent instead
    - Log in first: http://localhost:4503/libs/granite/core/content/login.html
    - Then navigate here: http://localhost:4503/etc/replication/agents.publish.html
    - Open `Dispatcher Flush (flush)`
    - Next to `Settings`, click `Edit`
    - Check the box next to `Enabled`
    - Switch to the Transport tab, set URI to `http://localhost:8080/dispatcher/invalidate.cache`
        - NOTE THE PORT! The above URI assumes your Apache httpd is running on `8080`
    - Switch to the Triggers tab, and enable:
        - `Ignore default` ✅
        - `On Modification` ✅
        - `On Receive` ✅
        - `No Status Update` ✅
        - `No Versioning` ✅
    - Click "OK"
    - With the dialog closed, click the "Test Connection" link
        - Should see a `HTTP/1.1 200 OK` response, and `Replication (TEST) of /content successful.`
        
With the above steps complete, your publish instance will now be responsible for triggering flush requests to the dispatcher.
