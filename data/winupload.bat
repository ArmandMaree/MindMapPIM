winscp /command "option batch abort" "option confirm off" "open sftp://armand:maichich7ooD@bubbles.iminsys.com/" "put data.jar /home/armand/gradle/data/" "exit"
if "%1"=="doexit" exit