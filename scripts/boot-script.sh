#!/bin/bash

#Ensure you're opperating on the right directory
cd /root/proxy/
#Define the new MOTD
TIME=$(date)
MOTD_MESSAGE="motd = \"&aAServer started at $TIME\""
#Echo into the file
echo $MOTD_MESSAGE >> velocity.toml
#Boot up the proxy
screen -dmS "server-proxy" $(curl https://raw.githubusercontent.com/InfinityZ25/uhc-project/script/scripts/velocity-flags)
