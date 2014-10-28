#!/bin/bash
# steps to install VoltDB 4.x release on single node cluster setup
# Prerequisites:
#       Java 6 installed
#       Download Voltdb community edition tarball - LINUX-voltdb-4.8.tar.gz - http://downloads.voltdb.com/technologies/server/LINUX-voltdb-4.8.tar.gz
#           and extract contents to say /home/sandeep/tools/voltdb
#       Download Maven - apache-maven-3.2.3-bin.tar.gz - ftp://mirror.reverse.net/pub/apache/maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz
#           and extract contents to say /home/sandeep/tools/maven
#       Install VoltDB client jar into local maven repository - http://blog.tingri.me/?p=254
echo "###############"
echo "Setup"
echo "###############"
# Environment variables that can not be pushed to env.sh are here.
export USER=sandeep
export PROJECT_HOME=/home/$USER/projects/data_access
export SHELL_HOME=$PROJECT_HOME/shell
export APPNAME="$3"

# Setup Environment Variables
source $SHELL_HOME/env.sh
# Add common functions
source $SHELL_HOME/common_functions.sh

# Move to Module home
cd $PROJECT_HOME/$APPNAME

if [ "$1" == "server" ]; then
    if [ "$2" == "start" ]; then
        echo "###############"
        echo "# Start VoltDB server"
        echo "###############"
        source $SHELL_HOME/start.sh
        clean;
        server;
        cd $PROJECT_HOME
    else
        echo "########################"
        echo "# Stop the processes"
        echo "########################"
        source $SHELL_HOME/stop.sh
        stop;
        clean;
    fi
else
    if [ "$1" == "client" ]; then
        echo "#######"
        echo "# Client"
        echo "#######"

        if [ "$3" == "voltdb_benchmark" ]; then
            source $SHELL_HOME/benchmark.sh
            # Run benchmark
            client;
        else
            source $SHELL_HOME/utilities.sh
        fi

        $VOLTDB_SQLCMD --debug
    fi
fi

