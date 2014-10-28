#!/bin/bash

usage() { echo "Usage: $0 [-m <server|client>] [-c <start|stop|async-benchmark|sync-benchmark|jdbc-benchmark>]" 1>&2; exit 1; }

# setting default values
m="client";
c="async-benchmark";

while getopts ":m:c:" o; do
    case "${o}" in
        m)
            m=${OPTARG}
            ((m == "server" || m == "client" )) || usage
            ;;
        c)
            c=${OPTARG}
            ((c == "start" || c == "stop" || c == "async-benchmark" || c == "sync-benchmark" || c == "jdbc-benchmark")) || usage
            ;;
        *)
            usage
            ;;
    esac
done

if [ "${m}" == "client" ]; then
    /bin/bash ./shell/data_access.sh ${m} ${c}
else
    /bin/bash ./shell/data_access.sh ${m} ${c}  >& results-${c}.log
fi

