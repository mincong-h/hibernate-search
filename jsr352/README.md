[![Build Status][travis-img]][travis]

# gsoc-hsearch

This project aims to provide an alternative to the current mass indexer 
implementation, using the Java Batch architecture as defined by JSR 352. This 
standardized tool JSR 352 provides task-and-chunk oriented processing, parallel 
execution and many other optimization features. This batch job should accept 
the entity type(s) to re-index as an input, load the relevant entities from the 
database and rebuild the full-text index from these.


## Run

You can install the project and see test cases using:

    mvn clean install


## Mechanism

This project redesigns the mass index job as a chunk-oriented, non-interactive,
long-running, background execution process. Execution contains operational
control (start/stop/restart), logging, checkpointing and parallelization.

![Workflow of the job "mass-index"][1]

For more information, please check http://mincong-h.github.io/gsoc-hsearch

[1]: https://raw.githubusercontent.com/mincong-h/gsoc-hsearch/master/img/mass-index.png
[travis]: https://travis-ci.org/mincong-h/gsoc-hsearch
[travis-img]: https://travis-ci.org/mincong-h/gsoc-hsearch.svg?branch=master
