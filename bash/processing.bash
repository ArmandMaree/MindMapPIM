# upload Processing
cd ../Processing
gradle build
scp build/libs/*.jar armand@bubbles.iminsys.com:/home/armand/processing
