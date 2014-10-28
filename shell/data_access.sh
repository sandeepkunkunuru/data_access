#!/bin/bash
# steps to install VoltDB 4.x release on single node cluster setup
# Prerequisites:
#       Java 6 installed
#       Download Voltdb community edition tarball - LINUX-voltdb-4.8.tar.gz - http://downloads.voltdb.com/technologies/server/LINUX-voltdb-4.8.tar.gz
#           and extract contents to say /home/sandeep/tools/voltdb
#       Download Maven - apache-maven-3.2.3-bin.tar.gz - ftp://mirror.reverse.net/pub/apache/maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz
#           and extract contents to say /home/sandeep/tools/maven
#       Install VoltDB client jar into local maven repository - http://dheerajvoltdb.wordpress.com/2013/09/03/installing-voltdb-in-local-maven-repository/
echo "###############"
echo "Setup"
echo "###############"
# Environment variables that can not be pushed to env.sh are here.
export USER=sandeep
export PROJECT_HOME=/home/$USER/projects/data_access
export SHELL_HOME=$PROJECT_HOME/shell
export APPNAME=voltdb_benchmark

# Setup Environment Variables
source $SHELL_HOME/env.sh

# Move to Project home
cd $VOLTDB_HOME

if [ "$1" == "server" ]; then
    if [ "$2" == "start" ]; then
        echo "###############"
        echo "# Start VoltDB server"
        echo "###############"
        cd $PROJECT_HOME/$APPNAME
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
    fi
else
    if [ "$1" == "client" ]; then
        echo "#######"
        echo "# Client"
        echo "#######"
        # Setup Environment Variables
        source $SHELL_HOME/benchmark.sh
        client;

        $VOLTDB_SQLCMD --debug
    fi
fi

