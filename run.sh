#!/bin/bash

usage() { echo "Usage: $0 [-m <server|client>] [-c <voltdb_benchmark|voltdb_utilities>] [-a <voltdb_benchmark|voltdb_utilities>]" 1>&2; exit 1; }

# setting default values
m="client";
c="start";
a="voltdb_benchmark";

while getopts ":m:c:" o; do
    case "${o}" in
        m)
            m=${OPTARG}
            ((m == "server" || m == "client" )) || usage
            ;;
        c)
            c=${OPTARG}
            ((c == "start" || c == "stop" )) || usage
            ;;
        a)
            a=${OPTARG}
            ((a == "voltdb_benchmark" || a == "voltdb_utilities" )) || usage
            ;;
        *)
            usage
            ;;
    esac
done

if [ "${m}" == "client" ]; then
    /bin/bash ./shell/data_access.sh ${m} ${c} ${a}
else
    /bin/bash ./shell/data_access.sh ${m} ${c} ${a} >& results-${c}.log
fi

