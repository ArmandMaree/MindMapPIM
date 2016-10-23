rm *.tar.gz
tar -czv --exclude="winupload.bat" --exclude="node_modules/" --exclude="build" --exclude=".gradle/" --exclude="upload.bash" --exclude="gradlew.bat" -f all.tar.gz *
sshpass -p "maichich7ooD" scp all.tar.gz armand@unclutter.iminsys.com:/home/armand/gradle/twitter
sshpass -p "maichich7ooD" scp extract.bash armand@unclutter.iminsys.com:/home/armand/gradle/twitter
sshpass -p "maichich7ooD" scp run.bash armand@unclutter.iminsys.com:/home/armand/gradle/twitter