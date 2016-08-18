#!/usr/bin/expect
set prompt {\$ $};
spawn bash
expect -re $prompt
spawn sudo chmod -R -f 777 gradlew
expect "assword"
send "maichich7ooD\n"