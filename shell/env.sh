#!/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
export MAVEN_HOME=/home/$USER/tools/maven
export DATA_SETS_FOLDER=$PROJECT_HOME/datasets

export VOLTDB_VER=4.8

export VOLTDB_ROOT=/home/$USER/tools/voltdb
export VOLTDB_HOME=$VOLTDB_ROOT

export PATH=$JAVA_HOME/bin:$PATH

export VOLTDBQL_HOME=$PROJECT_HOME/vql

# find voltdb binaries in either installation or distribution directory.
if [ -n "$(which voltdb 2> /dev/null)" ]; then
    export VOLTDB_BIN=$(dirname "$(which voltdb)")
else
    export VOLTDB_BIN="$(dirname $(dirname $(pwd)))/bin"
    echo "The VoltDB scripts are not in your PATH."
    echo "For ease of use, add the VoltDB bin directory: "
    echo
    echo $VOLTDB_BIN
    echo
    echo "to your PATH."
    echo
fi

# installation layout has all libraries in $VOLTDB_ROOT/lib/voltdb
if [ -d "$VOLTDB_BIN/../lib/voltdb" ]; then
    export VOLTDB_BASE=$(dirname "$VOLTDB_BIN")
    export VOLTDB_LIB="$VOLTDB_BASE/lib/voltdb"
    export VOLTDB_VOLTDB="$VOLTDB_LIB"
# distribution layout has libraries in separate lib and voltdb directories
else
    export VOLTDB_BASE=$(dirname "$VOLTDB_BIN")
    export VOLTDB_LIB="$VOLTDB_BASE/lib"
    export VOLTDB_VOLTDB="$VOLTDB_BASE/voltdb"
fi

export APPCLASSPATH=$CLASSPATH:$({ \
    \ls -1 "$VOLTDB_VOLTDB"/voltdb-*.jar; \
    \ls -1 "$VOLTDB_LIB"/*.jar; \
    \ls -1 "$VOLTDB_LIB"/extension/*.jar; \
} 2> /dev/null | paste -sd ':' - )

export CLIENTCLASSPATH=$CLASSPATH:$({ \
    \ls -1 "$VOLTDB_VOLTDB"/voltdbclient-*.jar; \
    \ls -1 "$VOLTDB_LIB"/commons-cli-1.2.jar; \
} 2> /dev/null | paste -sd ':' - )

export VOLTDB="$VOLTDB_BIN/voltdb"
export VOLTDB_ADMIN="$VOLTDB_BIN/voltadmin"
export VOLTDB_SQLCMD="$VOLTDB_BIN/sqlcmd"
export LOG4J="$VOLTDB_VOLTDB/log4j.xml"
export LICENSE="$VOLTDB_VOLTDB/license.xml"
export HOST="localhost"