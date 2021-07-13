#!/bin/bash
#Ensure you're opperating on the right directory
cd /root/proxy/
#Define the new MOTD
TIME=$(date)
MOTD_MESSAGE="motd = \"&6A Proxy started at &f$TIME\""
#Define new
ENCRYPT_KEY="$(curl https://raw.githubusercontent.com/InfinityZ25/uhc-project/script/scripts/demo-key)"
#Echo into the file
echo "forwarding-secret = \"$ENCRYPT_KEY\"" >> velocity.toml
echo $MOTD_MESSAGE >> velocity.toml
echo "show-max-players = 10" >> velocity.toml
#Boot up the proxy
screen -dmS "server-proxy" $(curl https://raw.githubusercontent.com/InfinityZ25/uhc-project/script/scripts/velocity-flags)
