#!/bin/bash
rm data.tar.gz
mkdir -p build/data/
mkdir -p build/doc/
cd src/
javadoc -author -d ../build/doc data
cd data/
javac -cp "../libs/*:." -d ../../build *.java
cd ../../build/
jar cf "data.jar" data/*
cd doc/
jar cf "data-javadoc.jar" *
mv "data-javadoc.jar" "../../data-javadoc.jar"
cd ../
mv "data.jar" "../data.jar"
