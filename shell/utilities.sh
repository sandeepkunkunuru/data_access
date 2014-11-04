#!/bin/bash

export CLIENTCLASSPATH=$PROJECT_HOME/$APPNAME/target/classes:$CLIENTCLASSPATH

# run the client that drives the example
function client() {
    srccompile
    # java -classpath $CLASSES:$CLIENTCLASSPATH reviewer.native_api.AsyncBenchmark --help
}

