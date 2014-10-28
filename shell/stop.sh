#!/bin/bash
# stop the voltdb server
function stop() {
    $VOLTDB_ADMIN shutdown;
}