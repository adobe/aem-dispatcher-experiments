# Running a Dispatcher Locally

In a typical AEM implementation, the dispatcher fits in to the AEM publish tier like so (note: no CDN pictured below):

<img src="img/topology.png">

## macOS local Dispatcher installation

### Apache httpd

To [avoid issues](https://helpx.adobe.com/experience-manager/kt/platform-repository/using/dispatcher-macos-technical-video-setup.html) with using the dispatcher module on the Apache version included with macOS, Install Apache 2.4 from Homebrew:

    brew install httpd

Ensure the version you installed is available via `httpd`:

    which httpd
    
    # use the path returned above
    ls -l <path from command above>
    
    # should return "... /usr/local/bin/httpd -> ../Cellar/httpd/2.4.43/bin/httpd"
    # "/Cellar/httpd" indicates the homebrew version is being used.

Do you see `/usr/sbin/httpd` instead? Try including `/usr/local/bin` in your PATH. Place this line in .bashrc or .zshrc, depending on your shell).

    export PATH="/usr/local/bin:$PATH"
    
See where Apache is reading it's config from:

    httpd -V
    # look for httpd.conf path in the last line, `-D SERVER_CONFIG_FILE="/usr/local/etc/httpd/httpd.conf"`

Open this config file - you'll need to customize it below: `code <your SERVER_CONFIG_FILE path>`

Start Apache:

    apachectl start
    # once you've updated configs you'll want to run `apachectl restart` instead 

Verify you can access Apache at the `Listen` address specified in your httpd.conf file (`8080` in my case): http://localhost:8080/

Did you see the following message printed to the console? "AH00558: httpd: Could not reliably determine the server's fully qualified domain name, using YOur-computer-name.local. Set the 'ServerName' directive globally to suppress this message" - if so, it can safely be ignored for a local setup.

### Create the directory structure

The following directories will be used for storing the dispatcher module, it's configuration, and the cached documents.

```
sudo mkdir -p /private/libexec/apache2
sudo mkdir -p /private/etc/apache2/vhosts
sudo mkdir -p /private/etc/apache2/conf
sudo mkdir -p /Library/WebServer/docroot/publish
```

### Download the Dispatcher module

Download the latest release of the Dispatcher module: https://docs.adobe.com/content/help/en/experience-manager-dispatcher/using/getting-started/release-notes.html#apache

You are most likely looking for the Apache 2.4 macOS release, named `dispatcher-apache2.4-darwin-x86_64-4.3.3.tar.gz` - 4.3.3 is the most recent release at the time of writing.

Open the dispatcher-apache2.4-darwin-x86_64-VERSION.tar.gz archive to extract it.

// TODO: continue here!


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