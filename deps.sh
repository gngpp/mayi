#!/bin/bash
MODULES=$(jdeps  --print-module-deps --ignore-missing-deps $1)

$JAVA_HOME/bin/jlink \
         --add-modules jdk.crypto.ec,java.logging,java.base,java.management,java.sql \
         --output /javaruntime