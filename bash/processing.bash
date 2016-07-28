# upload Processing
cd ../Processing
gradle build
sshpass -p "maichich7ooD" scp build/libs/*.jar armand@bubbles.iminsys.com:/home/armand/processing
