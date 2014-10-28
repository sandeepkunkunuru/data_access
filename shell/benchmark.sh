#!/usr/bin/env bash

export CLASSES=$PROJECT_HOME/$APPNAME/target/classes

# run the client that drives the example
function client() {
    async-benchmark
}

# Asynchronous benchmark sample
# Use this target for argument help
function async-benchmark-help() {
    srccompile
    java -classpath $CLASSES:$CLIENTCLASSPATH:$CLASSES reviewer.native_api.AsyncBenchmark --help
}

# latencyreport: default is OFF
# ratelimit: must be a reasonable value if lantencyreport is ON
# Disable the comments to get latency report
function async-benchmark() {
    srccompile
    java -classpath $CLASSES:$CLIENTCLASSPATH:$CLASSES -Dlog4j.configuration=file://$LOG4J \
        reviewer.native_api.AsyncBenchmark \
        --displayinterval=5 \
        --warmup=5 \
        --duration=120 \
        --servers=localhost:21212 \
        --books=6 \
        --maxreviews=2 \
        --latencyreport=true \
        --statsfile=Results.csv 
#        --ratelimit=100000
}

function simple-benchmark() {
    srccompile
    java -classpath $CLASSES:$CLIENTCLASSPATH:$CLASSES -Dlog4j.configuration=file://$LOG4J \
        reviewer.SimpleBenchmark localhost
}

# Multi-threaded synchronous benchmark sample
# Use this target for argument help
function sync-benchmark-help() {
    srccompile
    java -classpath $CLASSES:$CLIENTCLASSPATH:$CLASSES reviewer.native_api.SyncBenchmark --help
}

function sync-benchmark() {
    srccompile
    java -classpath $CLASSES:$CLIENTCLASSPATH:$CLASSES -Dlog4j.configuration=file://$LOG4J \
        reviewer.native_api.SyncBenchmark \
        --displayinterval=5 \
        --warmup=5 \
        --duration=120 \
        --servers=localhost:21212 \
        --books=6 \
        --maxreviews=2 \
        --threads=40
}

# JDBC benchmark sample
# Use this target for argument help
function jdbc-benchmark-help() {
    srccompile
    java -classpath $CLASSES:$CLIENTCLASSPATH:$CLASSES reviewer.jdbc.JDBCBenchmark --help
}

function jdbc-benchmark() {
    srccompile
    java -classpath $CLASSES:$CLIENTCLASSPATH:$CLASSES -Dlog4j.configuration=file://$LOG4J \
        reviewer.jdbc.JDBCBenchmark \
        --displayinterval=5 \
        --duration=120 \
        --maxreviews=2 \
        --servers=localhost:21212 \
        --books=6 \
        --threads=40
}

#function help() {
#    echo "Usage: ./run.sh {clean|catalog|server|async-benchmark|aysnc-benchmark-help|...}"
#    echo "       {...|sync-benchmark|sync-benchmark-help|jdbc-benchmark|jdbc-benchmark-help}"
#}

# Run the target passed as the first arg on the command line
# If no first arg, run server
#if [ $# -gt 1 ]; then help; exit; fi
#if [ $# = 1 ]; then $1; else server; fi
