DEL "*.tar.gz"
DEL "*.zip"
7z a -x!"build/" -x!".gradle/" -x!"gradle/" -x!"gradlew" -x!"upload.bash" -x!"gradlew.bat" -x!"run.bash" -x!"extract.bash" -x!"winupload.bat" -ttar -so all.tar * | 7z a -si all.tar.gz
winscp /command "option batch abort" "option confirm off" "open sftp://armand:maichich7ooD@unclutter.iminsys.com/" "put all.tar.gz /home/armand/gradle/gmail/" "exit"
if "%1"=="doexit" exit
