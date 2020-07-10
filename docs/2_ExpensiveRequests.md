# Expensive Requests

Let's use JMeter to make some expensive requests and see how the Publish instance handles them.

## Getting set up

### Your Dispatcher's cache folder

It's helpful to have your dispatcher's cache folder open to quickly delete things from the cache. In my configuration, the cache folder can be opened with:

    open /Library/WebServer/docroot/publish
    
You should see a familiar structure here: `content/`, `etc.clientlibs/`, etc.

### Installing the sample code

From the root of this repo, use Maven to install the content to your author and publish instances:

    mvn -PautoInstallSinglePackage -PautoInstallSinglePackagePublish clean install
    
### JMeter

If you don't already have JMeter installed, you can download it with Homebrew:

    brew install jmeter
    
The JMeter test scripts are located in `jmeter-test-scripts/` - you can open them up with JMeter, or run them via the CLI.

## Expensive test #1: Many threads

> TODO: run top first

There is a component in this repo, `/apps/dispatchertester/components/expensivecomponent`, which takes a configurable `x` milliseconds to render.

While it's waiting, it's consuming a thread. Therefore it should be possible to exhaust the publish tier's thread pool if enough requests are made.

Try the page on the publish instance. It should take 10 seconds to render: http://localhost:4503/content/dispatchertester/us/en/page-with-bad-component.html

Try the page on the dispatcher. If _not yet cached_, it should also take 10 seconds to render: http://aem-publish.local:8080/content/dispatchertester/us/en/page-with-bad-component.html

Once cached, the page should render instantly on the dispatcher.

Delete the `/content/dispatchertester` folder from the dispatcher cache before proceeding.

To run this test:

```
jmeter -n -t jmeter-test-scripts/Expensive-page-test-plan.jmx -l jmeter-test-scripts/log.jtl
```


| Previous      |         Next |
| :------------ | ------------:|
| [⇦_Flush Agents](1_FlushAgents.md) | [ ⇨]() |




