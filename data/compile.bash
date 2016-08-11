#!/bin/bash
mkdir -p build/data/
mkdir -p build/libs/
cd src/data/
javac -cp "../libs/*:." -d ../../build *.java
cd ../../build/
jar cf "data.jar" */*
mv data.jar ../data.jar
