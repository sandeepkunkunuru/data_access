#!/bin/bash

# remove build artifacts
function clean() {
    rm -rf target debugoutput $APPNAME.jar voltdbroot statement-plans log
}


# compile the source code for procedures and the client
function srccompile() {
    mvn -X clean compile
    if [ $? != 0 ]; then exit; fi
}