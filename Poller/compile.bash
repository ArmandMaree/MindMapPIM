#!/usr/bin/env bash

gradle assemble javadoc -x bootRepackage
cp ./build/libs/poller-api-0.1.0.jar poller-api-0.1.0.jar
cp ./build/libs/Poller-javadoc.jar poller-javadoc.jar
