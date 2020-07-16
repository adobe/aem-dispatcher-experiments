# AEM Dispatcher Experiments - project code

Sample code for use with the experiments.

## Modules

The main parts of the template are:

* core: Java bundle containing all core functionality like OSGi services, listeners or schedulers, as well as component-related Java code such as servlets or request filters.
* ui.apps: contains the /apps (and /etc) parts of the project, ie JS&CSS clientlibs, components, templates, runmode specific configs as well as Hobbes-tests
* ui.content: contains sample content using the components from the ui.apps
* ui.frontend: an optional dedicated front-end build mechanism (Angular, React or general Webpack project)

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with

    mvn clean install -PautoInstallPackage

Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish

Or alternatively

    mvn clean install -PautoInstallPackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle
    
You can also build and install everything on both author and publish:

    mvn -PautoInstallSinglePackage -PautoInstallSinglePackagePublish clean install

## API dependency

The [page-with-long-request](http://localhost:4502/editor.html/content/dispatchertester/us/en/page-with-long-request.html) contains a component which depends on a webserver running at `http://localhost:3800`. To run this server (from the repo root):

```
cd node-server
npm install
node index.js
```

## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html

## Archetype

This project was bootstrapped with the following command:

```
mvn -B archetype:generate \
 -D archetypeGroupId=com.adobe.granite.archetypes \
 -D archetypeArtifactId=aem-project-archetype \
 -D archetypeVersion=23 \
 -D aemVersion=6.5.0 \
 -D appTitle="Dispatcher Test Harness" \
 -D appId="dispatchertester" \
 -D groupId="com.brucelefebvre" \
 -D frontendModule=general \
 -D includeExamples=y \
 -D singleCountry=n \
 -D includeErrorHandler=y
```