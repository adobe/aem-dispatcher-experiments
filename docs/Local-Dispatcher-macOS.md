# Running a Dispatcher Locally on macOS

In a typical AEM implementation, the dispatcher fits in to the AEM publish tier like so (note: no CDN pictured below):

<img src="img/topology.png">

These instructions cover setting up a local dispatcher on Apache 2.4, accessible at `http://aem-publish.local:8080/`.

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

    sudo apachectl start
    # once you've updated configs you'll want to run `sudo apachectl restart` instead 

Verify you can access Apache at the `Listen` address specified in your httpd.conf file (`8080` in my case): http://localhost:8080/. You should see the following:

<img src="img/apache-working.png">

Did you see the following message printed to the console? "AH00558: httpd: Could not reliably determine the server's fully qualified domain name, using Your-computer-name.local. Set the 'ServerName' directive globally to suppress this message" - if so, it can safely be ignored for a local setup.

### Create key directories

The following directories will be used for storing the dispatcher module, it's configuration, and the cached documents:

```
sudo mkdir -p /private/libexec/apache2
sudo mkdir -p /private/etc/apache2/vhosts
sudo mkdir -p /private/etc/apache2/conf
sudo mkdir -p /Library/WebServer/docroot/publish
```

Update the owner of the cache docroot to the `_www` user and `_www` group:

    sudo chown _www:_www /Library/WebServer/docroot/publish

### Download the Dispatcher module

Download the latest release of the Dispatcher module: https://docs.adobe.com/content/help/en/experience-manager-dispatcher/using/getting-started/release-notes.html#apache

You are most likely looking for the Apache 2.4 macOS release, named `dispatcher-apache2.4-darwin-x86_64-4.3.3.tar.gz` - 4.3.3 is the most recent release at the time of writing.

Open the dispatcher-apache2.4-darwin-x86_64-VERSION.tar.gz archive to extract it.

### Install the Dispatcher module

Move the `dispatcher-apache2.4-VERSION.so` module to the `/private/libexec/apache2` directory, and rename it to `mod_dispatcher.so`. Assuming a dispatcher version of 4.3.3, run:

    mv dispatcher-apache2.4-4.3.3.so /private/libexec/apache2/mod_dispatcher.so

You may also use a symlink, if you prefer.

### Load the Dispatcher module

This repo contains a sample [httpd.conf](../dispatcher-config-basic/usr/local/etc/httpd/httpd.conf) which you can use as-is to replace the existing httpd.conf that ships with Apache. From the root of this repo:

    cp dispatcher-config-basic/usr/local/etc/httpd/httpd.conf /usr/local/etc/httpd/httpd.conf

Alternatively, make note of the configuration following the comment `#### DISPATCHER CONFIG ####` (line 184 - 252), and include it in your own httpd.conf.

### Configure the dispatcher

This repo includes a fully configured sample [dispatcher.any](../dispatcher-config-basic/private/etc/apache2/conf/dispatcher.any) file to get you up and running quickly.

The included [httpd.conf](../dispatcher-config-basic/usr/local/etc/httpd/httpd.conf) file instructs the dispatcher to read it's configuration from `/private/etc/apache2/conf/dispatcher.any`. To copy over the included [dispatcher.any](../dispatcher-config-basic/private/etc/apache2/conf/dispatcher.any) file, copy it over to this location (from the root of this repo):

    sudo cp dispatcher-config-basic/private/etc/apache2/conf/dispatcher.any /private/etc/apache2/conf/dispatcher.any

### Configure the publish virtual host

The final step configure our `aem-publish.local` virtual host.

Copy the included [aem-publish.local.conf](../dispatcher-config-basic/private/etc/apache2/vhosts/aem-publish.local.conf) to `/private/etc/apache2/vhosts`:

    sudo cp dispatcher-config-basic/private/etc/apache2/vhosts/aem-publish.local.conf /private/etc/apache2/vhosts/aem-publish.local.conf

Lastly, add a line to `/etc/hosts` to point `aem-publish.local` back at localhost:

    127.0.0.1 aem-publish.local

### Restart Apache

Use the following command to restart the Apache webserver:

    sudo apachectl start

### Did it work?

Try to access the following WeRetail page (assumes sample content is installed): http://aem-publish.local:8080/content/we-retail/us/en.html

### Check the cache

Have a look in the cache document root, and you should see a familiar directory structure:

    ls /Library/WebServer/docroot/publish


| Previous      |         Next |
| :------------ | ------------:|
| [⇦ README](../README.md) | [Flush Agents ⇨](1_FlushAgents.md) |