# upload Frontend
cd ../Frontend
gradle build
sshpass -p "maichich7ooD" scp build/libs/*.jar armand@bubbles.iminsys.com:/home/armand/frontend
