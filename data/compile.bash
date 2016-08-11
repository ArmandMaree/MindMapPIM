#!/bin/bash
mkdir -p build/data/
cd src/data/
javac -cp ".:../libs/org.springframework.data.core_1.3.1.RELEASE.jar" -d ../../build *.java
cd ../../build/
jar cf "data.jar" */* 
mv data.jar ../data.jar
