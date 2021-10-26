#!/bin/bash
MODULES=$(jdeps  --print-module-deps $1)

$JAVA_HOME/bin/jlink \
         --add-modules jdk.crypto.ec,$MODULES \
         --output /javaruntime