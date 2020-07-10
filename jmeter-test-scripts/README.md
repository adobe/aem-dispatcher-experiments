# JMeter test plans

```
brew install jmeter
```

## Notes:

```
Don't use GUI mode for load testing !, only for Test creation and Test debugging.
For load testing, use CLI Mode (was NON GUI):
   jmeter -n -t [jmx file] -l [results file] -e -o [Path to web report folder]
& increase Java Heap to meet your test requirements:
   Modify current env variable HEAP="-Xms1g -Xmx1g -XX:MaxMetaspaceSize=256m" in the jmeter batch file
Check : https://jmeter.apache.org/usermanual/best-practices.html
```

## Before running the tests, observe the publish instance's CPU usage

Find the publish instance's PID:

```
ps -fu $USER | grep publish
```

Use top with the above PID (8171 below) to monitor CPU during the test:

```
top -pid 8171
```

## Running the We.Retail test

```
jmeter -n -t jmeter-test-scripts/WeRetail-test-plan.jmx -l jmeter-test-scripts/log.jtl
```

## Running the expensive component test

Note, a .sleep is not actually expensive, so this is less effective than an expensive render (like We.Retail above):

```
jmeter -n -t jmeter-test-scripts/Expensive-page-test-plan.jmx -l jmeter-test-scripts/log.jtl
```

