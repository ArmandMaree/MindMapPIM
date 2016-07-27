# upload Frontend
cd ../Frontend
gradle build
scp build/libs/*.jar armand@bubbles.iminsys.com:/home/armand/frontend
