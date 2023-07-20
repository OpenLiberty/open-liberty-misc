#!/bin/bash

if [ $# -ne 1 ]; then
    echo "Exactly one argument is required"
    exit 0
fi

JAVA_VERSION=$1
SUB_PROJECT=$PWD/io.openliberty.java.internal_fat_$JAVA_VERSION
TEST_CLASS=$SUB_PROJECT/build/classes/java/main/io/openliberty/java/internal/TestApp.class


echo "Checking class"
SCRIPTS=$PWD/.github/workflows/scripts/
pushd $SCRIPTS &> /dev/null
    java ClassVersionChecker.java $TEST_CLASS $JAVA_VERSION
popd &> /dev/null
echo "--------------------"
