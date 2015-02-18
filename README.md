# ⚡  SR Grid - Power Grabber

 > ℹ Tool which was used in research in 2015. Now in 2020 it's ready for the GitHub archive.

A simple command line website grabber that reads a single value from a website and stores it in a CSV file.  
It's used to periodically monitor the _[SR Grid Frequency](https://en.wikipedia.org/wiki/Utility_frequency)_ from the 
**Power System Operation Corporation Ltd.** - A Government of India Enterprise.

The parsed websites are:
 * http://www.srldc.org
 * http://www.nrldc.org

I also deployed it to google app engine, but at the end it was enough to run it on a local desktop PC.

### 🛠 Options

The options will be provided during start as console input - The Grabber will aks for them.

 * `updateIntervall` Update interval in seconds 
 * `readingTimeout` Timeout for loading the site in seconds 
 
### 🚀 Run
 
``` java
java -jar website-grabber.jar 
```

### ⌨ Commands
 * start
 * stop
 * exit