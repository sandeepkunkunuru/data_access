#!/bin/bash


# build an application catalog
function catalog() {
    srccompile
    echo "Compiling the reviewer application catalog."
    echo "To perform this action manually, use the command line: "
    echo
    echo "voltdb compile --classpath target/classes -o $APPNAME.jar ddl.sql"
    echo
    $VOLTDB compile --classpath target/classes -o $APPNAME.jar ddl.sql
    # stop if compilation fails
    if [ $? != 0 ]; then exit; fi
}

# run the voltdb server locally
function server() {
    # if a catalog doesn't exist, build one
    if [ ! -f $APPNAME.jar ]; then catalog; fi
    # run the server
    echo "Starting the VoltDB server."
    echo "To perform this action manually, use the command line: "
    echo
    echo "$VOLTDB create -d deployment.xml -l $LICENSE -H $HOST $APPNAME.jar"
    echo
    nohup $VOLTDB create -d deployment.xml -l $LICENSE -H $HOST $APPNAME.jar >& voltdb-server.log &
}

# run the voltdb server locally
#function rejoin() {
#    # if a catalog doesn't exist, build one
#    if [ ! -f $APPNAME.jar ]; then catalog; fi
#    # run the server
#    $VOLTDB rejoin -H $HOST -d deployment.xml -l $LICENSE
#}
