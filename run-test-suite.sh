#!/bin/bash

set -e

if [ $# -ne 1 ]; then
    echo "Usage: ./run-test-suite.sh [SOURCE DIRECTORY]"
    exit 1
fi

sourceDirectory=$1

function banner() {
    echo "===== $1 ====="
}

function clean() {
    banner "cleaning"
    (set -x ; find "$sourceDirectory" -name '*.class' | xargs rm -f)
}

function compileSource() {
    banner "compiling source files"
    (set -x ; find "$sourceDirectory" -name '*.java' | xargs javac -cp junit.jar)
}

function runTests() {
    banner "running tests"
    (set -x ; java -jar junit.jar --cp "$sourceDirectory" -c DictionaryTreeTests)
}

clean
compileSource
runTests

