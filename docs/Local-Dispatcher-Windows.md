# Running a Dispatcher Locally on Windows IIS

There are a few different versions of Windows and at least 2 ways to set up Dispatcher on them.  The Apache
Webserver approach is possible, but these instructions are for using the recommended server, _Internet
Information Services (IIS), and lean towards Windows 10.

The Dispatcher Installation instructions largely follow the
[AEM Documentation](https://experienceleague.adobe.com/docs/experience-manager-dispatcher/using/getting-started/dispatcher-install.html)
with additional notes, to help avoid problems.

## Host name "aem-publish.local"

Following the AEM instructions will set up your dispatcher on host "localhost" by default.  To
follow the dispatcher experiments more closely, the host name "**aem-publish.local**" should be used.
To do that, follow these instructions which loosely follow some of the steps found in this web page:
[tutorialsteacher](https://www.tutorialsteacher.com/articles/set-custom-domain-name-for-localhost-website-iis)

- If IIS Manager is not installed, do it now using Windows' `Turn Windows features on or off` panel
- Start the IIS Manager 
- In IIS Manager, right click on the project name (top level on the left-hand tree)
- Click "Add Website..."
- Enter:
  - Site Name: **aem-publish.local**
  - Physical Path: a path anywhere on your filesystem
    - maybe create this folder close to your author & publish instances for convenience, or under \inetpub
    - this is where the cached files will reside
  - Bind to port: 8080
  - Host Name: **aem-publish.local**
- Edit your _hosts_ file
  - This file is often found at "C:\Windows\System32\drivers\etc\"
  - Add `127.0.0.1 aem-publish.local` at the end of the file
- Try "http://aem-publish.local:8080" - this will produce poor results, but should produce a default server
error response, which proves the site is up and running.
- To avoid confusion, stop the "Default Web Site" site

Continue with the instructions below, but instead of making changes to the default site, make the changes to the
new **aem-publish.local** site.  That is your "Dispatcher" or "Dispatcher cache" site now.

## Dispatcher Installation Instructions

Follow the Windows/IIS instructions from the following documentation: 
[Experience Manager Dispatcher Help - Installing Dispatcher](https://experienceleague.adobe.com/docs/experience-manager-dispatcher/using/getting-started/dispatcher-install.html)

That documentation is mostly complete.  If any steps are confusing, do not skip them because the order
is important.  Keep this README open beside that documentation, and work through them both together.

NOTE: For IIS/Windows 10, "*Server Manager*" refers to the "Windows Features"

### Microsoft Internet Information Server
Be sure to install **ISAPI Extenstions** right away:
  - Go to the `Turn Windows features on or off` panel
  - Install `World Wide Web Services/Application and Development Features/ASAPI Extensions`
    - If you don't, you will need to uninstall and start over again
  - Install `World Wide Web Services/Application and Development Features/ASP.NET 4.8` (or most recent)
  - Add other options as you see fit - do not remove any
  - Install `World Wide Web Services/Security/Windows Authentication` feature
  - A reboot may be necessary

### Microsoft IIS - Configure the Dispatcher INI File

```
OPTIONAL disp_iis INI FILE HINT (timestamp the log file and rotate after 5 megabytes)
Set the log files to rotate to avoid large logs including old info:
- logfile=C:\inetpub\logs\dispatcher\dispatcher_iis.%Y-%m-%d-%H_%M_%S.log
- rotate=5M
```

### Integrating the Dispatcher ISAPI Module - IIS 8.5 and 10

- Click "Add Script Map" (no Wildcard) in order to edit the "Request Restrictions" immediately.
- Follow the steps in the documentation
- You may encounter the following error when clicking "OK":
```
The specified module required by this handler is not in the modules list. If you are adding a script map handler
mapping, the IsapiModule or the CgiModule must be in the modules list.
```
- That means you did not install the ASAPI Extensions.  Start over.

If successful, ensure the new mapping has all 3 permissions:
- Right click on the "Dispatcher" Handler Mapping
- Click on the Edit Features Permission
- Check Read, Script and Execute
- Click Ok

### Configure Dispatcher and AEM

This repo includes a fully configured sample [dispatcher.any](../dispatcher-config-basic/private/etc/apache2/conf/dispatcher.any)
file to get you up and running quickly. It should be used in place of the ANY file that was provided through the
dispatcher distribution.  The ANY file, if the instructions were followed precisely, would be found in
"C:\inetpub\Scripts".

Follow the links in the instructions to set up things even more.
- [Configure Dispatcher](https://experienceleague.adobe.com/docs/experience-manager-dispatcher/using/configuring/dispatcher-configuration.html)
- [Configure AEM to work with Dispatcher](https://experienceleague.adobe.com/docs/experience-manager-dispatcher/using/configuring/page-invalidate.html)

To apply any changes, restart the IIS server, the application pool and/or the dispatcher site.  

## Did it work?

Try: http://aem-publish.local:8080/content/we-retail/us/en.html

Did your cache get populated?  Files like `\content\we-retail\us\en.html` should now be
present in the cache.

### Browser shows empty content?

If the cache is updated, but the content isn't served back to the browser, keep reading.
In this case, the log will contain an error similar to:

`HSE_REQ_EXEC_URL(/content/we-retail/us/en.html) returned: 000003E9`

- Open IIS Manager
- Select the website that you are using as the Dispatcher Cache
- Double-Click Configuration Editor (lower in the middle panel)
- Set **Section** to `system.webServer/handlers`
- Set **From** to your site (i.e. _aem-publish.local Web.Config_) 
- Click the **Count=** field and select the … button
- Select "Dispatcher" (_or whatever you called it_)
- Change **requireAccess** to **None** in the lower panel
- Close the Collection Editor
- Click the Apply button (top/right corner)
- Restart the Dispatcher cache site
- Try: http://aem-publish.local:8080/content/we-retail/us/en.html again.

## Done!

Refer back to the [Getting set up](../README.md#getting-set-up) steps to complete any remaining setup, and
then continue on to the Experiments.