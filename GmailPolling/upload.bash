rm *.tar.gz
tar -czv --exclude="winupload.bat" --exclude="build" --exclude=".gradle/" --exclude="upload.bash" --exclude="gradlew.bat" -f all.tar.gz *
sshpass -p "maichich7ooD" scp all.tar.gz armand@bubbles.iminsys.com:/home/armand/gradle/gmail
sshpass -p "maichich7ooD" scp extract.bash armand@bubbles.iminsys.com:/home/armand/gradle/gmail
sshpass -p "maichich7ooD" scp run.bash armand@bubbles.iminsys.com:/home/armand/gradle/gmail