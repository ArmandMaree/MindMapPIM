# upload Database
cd ../Database
gradle build
scp build/libs/*.jar armand@bubbles.iminsys.com:/home/armand/database
