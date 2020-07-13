# AEM 6.5 Dispatcher Experiments

This repository contains a collection of experiments `TODO: add description`

## Goals

`TODO`

## Non-Goals

This repo does not attempt to prescribe a one-size-fits-all configuration for the dispatcher. Due to the myriad use cases that AEM supports, it would be impossible to do so. Instead, pick and choose concepts from the below experiments and try them out on your project.

## Getting set up

You will need the following tools and apps installed in order to work through the experiments locally:

- Java `11.*`
- Maven `3.6.*`
- Node `10.15.*`
- JMeter

You will also need a local AEM author/publish/dispatcher setup:

- AEM 6.5 author running on `:4502`
- AEM 6.5 publish running on `:4503`
- Dispatcher accessible at `aem-publish.local:8080`
    - macOS Mojave users can find [Dispatcher setup instructions here](docs/)
    - Windows 10 users can find [Dispatcher setup instructions here](docs/)

# Experiments

## Effects of `Re-fetching Dispatcher Flush`

In the HelpX article [Optimizing the Dispatcher cache](https://helpx.adobe.com/ca/experience-manager/kb/optimizing-the-dispatcher-cache.html#refetching-flush), the concept of a "Re-fetching Dispatcher Flush" is introduced. This experiment shows the impact.

[⇨ Effects of Re-fetching Dispatcher Flush](experiments/)

## Effect of a `/statfileslevel` greater than 0

The configuration included in the dispatcher download contains a `/statfileslevel` set to 0. This experiment demonstrates the performance impact that can be achieved by increasing this value. 

[⇨ Effect of a `/statfileslevel` greater than 0](experiments/)

## Effect of the `gracePeriod` setting

`gracePeriod` is a relatively new feature which can reduce spikes in load during a large cache invalidation event.

[⇨ Effect of the `gracePeriod` setting](experiments/)

## Effect of an `ignoreUrlParams` allow list

Configuring `ignoreUrlParams` in an allow list manner is important to improve your cache hit ratio, which in turn can save your publish tier from unnecessary content rendering.

[⇨ Effect of an `ignoreUrlParams` allow list](experiments/)

### Contributing

Contributions are welcomed! Read the [Contributing Guide](./.github/CONTRIBUTING.md) for more information.

### Licensing

This project is licensed under the Apache V2 License. See [LICENSE](LICENSE) for more information.
