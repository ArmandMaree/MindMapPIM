#!/bin/bash
for file in *; do
    if [ "$file" != "gradle" ] && [ "$file" != "gradlew" ] && [ "$file" != "extract.bash" ] &&[ "$file" != "run.bash" ] && [ "$file" != "all.tar.gz" ] && [ "$file" != "all.zip" ] && [ "$file" != "." ] && [ "$file" != ".." ]; then
    	rm -R "$file";
    fi
done

if [ -f "all.tar.gz" ];
then
   tar -xf all.tar.gz
else
   unzip all.zip
fi

./run.bash
./gradlew build -x test
java -jar "build/libs/frontend-service-0.1.0.java"
