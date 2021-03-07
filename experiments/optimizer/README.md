# Using the Dispatcher Optimizer Tool (DOT)

This experiment demonstrates how to use the Dispatcher Optimizer Tool — its Maven Plugin in particular — to help 
you create better configurations that adhere to best practices and avoid inadvertent configuration errors.

## Compatibility

This experiment is compatible with both AEM 6.5 and AEM as a Cloud Service.

## Problem

The Dispatcher configuration is complicated and is easily left to the end of a project.  At that point it is
tempting to do whatever it takes to get the Dispatcher to work without following steps that will avoid problems in
the future.  The approach of "good enough" is often taken and only later after an outage, Dispatcher "bypass", or DoS 
attack is the configuration given a closer look.

It is also easy to end up with a configuration with syntax errors.  These errors can be hard to spot when
the dispatcher configuration is large.

**Security** - it should be noted that although the Dispatcher can provide an additional layer to mitigate the impact 
of unexpected requests, it should **NOT** be considered a security wall.  The Publish tier should follow all 
documented security best practices to be secure.

The DOT can be used in a few ways:
* A `plugin` entry can be added to the Maven POM of the Dispatcher module.  This will trigger the 
DOT to execute within the 'analyze' livecycle during development, giving the developer immediate feedback on the 
perceived accuracy of the Dispatcher configuration.
* Coming soon, the DOT will be included as a step in the Cloud Manager code quality pipeline, where it will be run 
automatically to analyze the provided configuration.
* A developer can use the open-source DOT Core library to create a new application, perhaps for the command line.

This experiment deals with the DOT Maven Plugin.

## Setup

First, ensure you have followed the "Getting set up" instructions on the main [README](../..).

***
![#ff0101](https://via.placeholder.com/15/ff0101/000000?text=+)
**NOTE**: The command **mvn dispatcher-optimizer:analyze** uses Maven's 
[Plugin Prefix Resolution](https://maven.apache.org/guides/introduction/introduction-to-plugin-prefix-mapping.html).
If the prefix ("dispatcher-optimizer") cannot be resolved in the local or remote repositories,
which can happen if those repos cannot be reached (ex: require VPN, no internet, etc.), an error
similar to the following will be displayed:
```
"No plugin found for prefix 'dispatcher-optimizer'..."
```
To resolve that error do one of the following
  - ensure the remote repos are reachable (ex: connect through VPN)
  - use the fully-featured form: 
  **mvn com.adobe.aem.dot:dispatcher-optimizer-maven-plugin:analyze** 

This should only happen once, since afterwards it would reside in your local Maven repo.
***

## Test #1: Running the DOT Maven Plugin

If you followed the instructions on the main page, you are almost done this experiment.
- `cd` to `aem-dispatcher-experiments/dispatcher-config-basic`
- Execute: `mvn dispatcher-optimizer:analyze` (_Error?  See ["Setup"](#setup) note above._)
- Watch the console for information about the configuration (in some cases these are directed to the logs).
- Two reports were written to `dispatcher-config-basic/target/dispatcher-optimizer-tool`:
  - results.csv : a file in Comma-Separated Values format
  - results.html : a html file, more friendly to the human eye
- Open `dispatcher-config-basic/target/dispatcher-optimizer-tool/results.html` in a browser
  (_used in subsequent tests_).
- The CSV file contains the same information as the HTML, and is more easily used for scanning by automation.
- Survey the information that the DOT found.

## Test #2: Fix a violation

The HTML from Test #1 should have shown an "Unknown Token" violation around line 89 for the _dispatcher.any_ file 
citing  the token "/0003".  The dispatcher configuration is a structured file, so we know the tokens that we can 
expect to find in each section. "Unknown Token" means there were expected tokens at that location in the configuration,
and the specified token was not one of those.

Let's investigate that violation.

- Edit _dispatcher-config-basic/private/etc/apache2/conf/dispatcher.any_.
- Around line 89, find the "/0003" element.
- This may look fine, or perhaps you may see the problem right away.  We can guess the author was hoping to deny all
  requests that are going to _/top/secret/data_.  So what is the problem?  Notice the closing brace on line 87.  That
  closes the /filter section.  Our "/0003" token was most likely intended to be inside the /filter section.
- Either delete the whole /0003 line or move the line to the bottom of the /filter section and save the file. 
- As was done in Test #1, run the DOT again: `mvn dispatcher-optimizer:analyze`.
- Refresh or Open `dispatcher-config-basic/target/dispatcher-optimizer-tool/results.html`.
- Notice the violation for line 89 (/0003) is longer was reported. (Right?  If it is still there, edit the file
  again.)

Imagine the results of this violation in a public environment - _/top/secret/data_ might have been publicly available
if the Publish tier was not fully secured.

## Test #3: Run the DOT Maven Plugin on your project

- Find a Dispatcher configuration that you have for your own AEM environment
- Setup a POM file:
  - If there is **no** pom.xml file in the `dispatcher` module of your project:
    - Copy `dispatcher-config-basic/pom.xml` to a folder whose children eventually contain both 
      `dispatcher.any` and `httpd.conf` files.
    - Feel free to edit the copied pom.xml's project details (`<groupId>`, `<artifactId>`, etc.) to make it your own.
  - If there **is** an existing pom.xml file in the `dispatcher` module of your project:
    - Add the `dispatcher-optimizer-maven-plugin` plugin details to the _build/plugins_ section of your existing POM.
    - See `dispatcher-config-basic/pom.xml` for an example.
- `cd` to the directory with that POM file.
- Execute `mvn dispatcher-optimizer:analyze`
- Watch the console for information about the configuration (in some cases, additional details about the detected 
violations is printed to the logs).
- Open the generated file: `target/dispatcher-optimizer-tool/results.html`
- Survey the information that the DOT found. Explanations of each of the core rules can be found by following
  the Documentation links in the report, or viewed directly
  [in the DOT GitHub repo](https://github.com/adobe/aem-dispatcher-optimizer-tool/blob/main/docs/Rules.md).
- Update your config to fix the violations that are of concern, and run the DOT again.

It is recommended to use the most recent version of the Dispatcher Optimizer Tool.  See 
[DOT's GitHub Repo](https://github.com/adobe/aem-dispatcher-optimizer-tool)
for more technical information.

## Test #4 - Change report verbosity

Edit the POM file used in any of the previous tests, and change the `<reportVerbosity>` setting
to control the amount of information that is included in the report.  Then run the 'analyze' command
and compare the results.  The options for Verbosity are:
- `FULL`
- `PARTIAL`
- `MINIMIZED`

Example: 
```
<reportVerbosity>MINIMIZED</reportVerbosity>
```

Check
[DOT's GitHub Repo](https://github.com/adobe/aem-dispatcher-optimizer-tool/tree/main/plugin#configuration)
for details on the different options for the Report Verbosity.

For relatively simple Dispatcher configurations there may actually be little difference.  
For real world configurations, it can
reduce the report rows by 100's of (repeated) violations, making the list easier to act upon.

## Test #5 - Overlay some rules

Hopefully the value of the DOT is starting to become clear.  You may start to imagine new rules that you would like
to implement.  Or you may notice a rule and decide, after very careful consideration, that you want to 
change the value being tested, silence it (usually not recommended), or change its severity.  Let's see how to do that.

The DOT allows the user to extend the core rules.  To create a new rule, first review the properties of a rule by
reviewing [the official specifications](https://github.com/adobe/aem-dispatcher-optimizer-tool/tree/main/core#rules)
and 
[instructions on how to extend the rules](https://github.com/adobe/aem-dispatcher-optimizer-tool/tree/main/core#extending-the-core-rules)
on that same page.

Also review the 
[core rules provided with DOT](https://github.com/adobe/aem-dispatcher-optimizer-tool/blob/main/core/src/main/resources/core-rules.json).

### Relax a rule

Let's make the `DOTRules:Disp-7---selector-allow-list` rule less urgent...

In `dispatcher-config-basic/pom.xml`, the `<optimizerRulesPath>` configuration value is already set to the "rules"
directory.  Any rule files in that directory will be read in alphabetical order, and applied to the
currently loaded rule set.  We will edit a file in this directory to extend the core rules.

- Edit `dispatcher-config-basic/rules/experiment_rules.json`
- Replace the contents with the following lines to duplicate the rule, and save the file.  The "EXTEND" mergeMode 
makes an attempt
  to merge the provided rules with the rules already loaded.  For a complete replacement of the ruleset, "REPLACE"
  can be used to clear what is already loaded, and start anew.
```
{
  "mergeMode": "EXTEND",
  "rules": [
    {
      "id": "DOTRules:Disp-7---selector-allow-list",
      "description": "The Dispatcher publish farm filters should specify the allowed Sling selectors in an allow list manner.",
      "severity": "MAJOR",
      "farmTypeList": ["PUBLISH"],
      "element": "farm.filter",
      "type": "Code Smell",
      "tags": ["beta","dispatcher"],
      "enabled": true,
      "checks": [
        {
          "condition": "FILTER_LIST_INCLUDES",
          "filterValue": {
            "type": "DENY",
            "url": "/content*",
            "selectors": "*"
          }
        }
      ]
    }
  ]
}
```
- Lower the rule's severity by changing `severity` to `"INFO"`
- Save the file and run the DOT again.
- Check the "results.html" file and see how the `DOTRules:Disp-7` violation appears, paying attention to its severity.

### Quiet a rule

Let's make the `DOTRules:Disp-7---selector-allow-list` not even appear (again, not recommended)...

- Edit `dispatcher-config-basic/rules/experiment_rules.json`
- Restore the `severity` to `"MAJOR"`
- Disable the rule by changing `enabled` to `false`
- Save the file and run the DOT again.
- Check the "results.html" file and see if the `DOTRules:Disp-7` appears.

### Create a rule

Your organization may come up with its own rules.  Let's say you decide that /filter's with type "ALLOW" should not
be permitted to specify the value using a /glob setting.  Let's implement that.

- Edit `dispatcher-config-basic/rules/experiment_rules.json`
- Delete its contents and replace it with the following:
```
{
  "mergeMode": "EXTEND",
  "rules": [
    {
      "id": "experiment-1---no-globs",
      "description": "Allow filters should not use /glob values",
      "severity": "MAJOR",
      "farmTypeList": ["PUBLISH"],
      "element": "farm.filter",
      "type": "Code Smell",
      "tags": ["experiment"],
      "enabled": true,
      "checks": [
        {
          "failIf": true,
          "condition": "FILTER_LIST_INCLUDES",
          "filterValue": {
            "type": "ALLOW",
            "glob": "regex(.*)"
          }
        }
      ]
    }
  ]
}
```
- NOTE: This rule would pass if the filter contains a glob, but we use the "failIf" setting to reverse the
  condition.
- Save the file and run the DOT again.
- Check the "results.html" file and notice "experiment-1" is not reported.
- Edit the `dispatcher.any` file and add this line to the /filter section (around line 77)
  - ``` /experiment1 { /glob "*" /type "allow" } ```
- Save the file and run the DOT again.
- Check the "results.html" file and notice "experiment-1" is now reported!

To maintain the test state, delete the contents of `dispatcher-config-basic/rules/experiment_rules.json`, revert
the changes to `dispatcher.any` and save the files.

## Reading

Here are some links to help further explain the DOT.

Product Documentation: 
https://experienceleague.adobe.com/docs/experience-manager-dispatcher/using/configuring/dispatcher-configuration.html

The DOT code and its core library are open-source and can be found here:
[DOT's GitHub Repo](https://github.com/adobe/aem-dispatcher-optimizer-tool)

All the documentation for the DOT can be found in its subpages.  Here are some highlights:
- Core Rule Explanation: https://github.com/adobe/aem-dispatcher-optimizer-tool/blob/main/docs/Rules.md
- AnalyzeRule and Check Specification and Rule Extending: 
  https://github.com/adobe/aem-dispatcher-optimizer-tool/tree/main/core#rules
- Plugin details: https://github.com/adobe/aem-dispatcher-optimizer-tool/tree/main/plugin
- Core (default) Rules: 
  https://github.com/adobe/aem-dispatcher-optimizer-tool/blob/main/core/src/main/resources/core-rules.json
