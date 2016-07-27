# upload Gmail
cd ../GmailPolling
gradle build
scp build/libs/*.jar armand@bubbles.iminsys.com:/home/armand/gmailpolling
