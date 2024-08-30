#!/bin/sh
BCEPHAL_PATH="/opt/bcephal/v8/integration-service"
SCRIPT_PATH="$BCEPHAL_PATH/installService.sh"
TMP_PATH="/home/bcephal/.Bcp-config/8/integration"
sudo su <<EOFF
if [ $(getent passwd "bcephal") ];then 
 echo "exist bcephal" 
 userdel "bcephal" 
fi 
 echo "Try to created "bcephal" user" 
 useradd -g root -G sudo -rm -s /bin/bash -d /home/"bcephal" -ou 1001 "bcephal" 
 usermod -aG sudo "bcephal" 
 echo "bcephal:bce5412_tFDX-KHV7452144_RRXXSSw#waaWWZZjjggFFCCDD8745Pphal" | chpasswd 
 echo "Sucssefuly to created bcephal user .." 
mkdir -p "/var/run/Bcephal/8" || true
mkdir -p "/var/Bcephal/8" || true
touch "/var/run/Bcephal/8/Bcephal-Integration-Service.pid" || true
touch "/var/Bcephal/8/Bcephal-Integration-Service.log" || true
chown -R bcephal:root "/var/run/Bcephal/8"
chown -R bcephal:root "/var/Bcephal/8"
chmod  ugo+rwx -R  "/tmp/"
chown -R bcephal:root "/home/bcephal" || true
chmod ugo+rwxs -R "$BCEPHAL_PATH"
"$SCRIPT_PATH"
EOFF