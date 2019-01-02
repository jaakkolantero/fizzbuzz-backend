# play-hello-world-docker

Simple helloworld scala app with play framework. Docker files included. 

## Getting Started

From __conf__ folder change __production_example.conf__ to __production.conf__.

Run app

```
sbt run
```
and head to http://localhost:9000/

### Prerequisites

* scala ~2.12.6
* Play framework ~2.6.18
* SBT ~1.2.4


## Deployment

Deploy to now.js
``` 
./stageAndBuild
now
```