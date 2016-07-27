# upload BusinessLogic
cd ../BusinessLogic
gradle build
scp build/libs/*.jar armand@bubbles.iminsys.com:/home/armand/business
