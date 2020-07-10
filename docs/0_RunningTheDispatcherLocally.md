# Running a Dispatcher Locally

The goal of this repo is to enable experimentation and theory validation by running a dispatcher instance locally. 

In the most basic case (no CDN), the dispatcher fits in to an AEM publish tier like so:

<img src="img/topology.png">

## AEM 6.5

Run AEM 6.5 _with_ sample content included. We'll be using WeRetail to generate load on the publish instance.

Here's the bash script I use for the author:

```
#!/bin/bash

java -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n -jar aem-author-p4502.jar -nointeractive -nofork
```

And the publish:

```
#!/bin/bash

java -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -jar aem-publish-p4503.jar -nointeractive -nofork
```    

Run the author and publish instances before proceeding (on OS X: `./run.sh`).

## OS X

### Apache httpd

Install Apache 2.4 from Homebrew:

    brew install httpd

Ensure the version you installed is available via `httpd`:

    which httpd
    
    # use the path returned above
    ls -l <path from command above>
    
    # should return "... /usr/local/bin/httpd -> ../Cellar/httpd/2.4.43/bin/httpd"
    # "/Cellar/httpd" indicates the homebrew version is being used
    
See where Apache is reading it's config from:

    httpd -V
    # look for httpd.conf path in the last line, `-D SERVER_CONFIG_FILE="/usr/local/etc/httpd/httpd.conf"`

Open this config file, you'll need to customize it below: `code <your SERVER_CONFIG_FILE path>`

Start Apache:

    sudo apachectl start
    # once you've updated configs you'll want to run `sudo apachectl restart` instead 

Verify you can access Apache at the `Listen` address specified in your httpd.conf `8080` in my case: http://localhost:8080/

### Follow the video guide

Follow this guide: https://helpx.adobe.com/experience-manager/kt/platform-repository/using/dispatcher-macos-technical-video-setup.html

Note this section:

> Attention macOS Mojave users
> ... To work around this issue, a copy of the httpd binary must be copied out, and codesign must be used to remove the signature.

This does not apply to you because you installed httpd via Homebrew (above).

Use the following files as a reference when needed:

- [httpd.conf](/dispatcher-config-basic/usr/local/etc/httpd/httpd.conf)
- [dispatcher.any](/dispatcher-config-basic/private/etc/apache2/conf/dispatcher.any)
- [aem-publish.local.conf](/dispatcher-config-basic/private/etc/apache2/vhosts/aem-publish.local.conf)

### Key directories

```
sudo mkdir -p /private/libexec/apache2
sudo mkdir -p /Library/WebServer/docroot/publish
sudo mkdir -p /private/etc/apache2/vhosts
sudo mkdir -p /private/etc/apache2/conf
```

### /etc/hosts

```
# add the following line

127.0.0.1 aem-publish.local
```

### Did it work?

Try: http://aem-publish.local:8080/content/we-retail/us/en.html

## Windows

> TODO. Sorry Andrew :(
> This link might help: http://www.aemcq5tutorials.com/tutorials/set-up-dispatcher-in-aem/

| Previous      |         Next |
| :------------ | ------------:|
| [⇦ README](../README.md) | [Flush Agents ⇨](1_FlushAgents.md) |