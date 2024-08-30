#!/bin/sh
DIR=$(readlink -f "$0")
BCEPHAL_PATH=$(dirname "$DIR")
export serviceName=Bcephal-Integration-Service.service
export service="$BCEPHAL_PATH/$serviceName"
export runService="$BCEPHAL_PATH/runManager.sh"
export start="$runService"
export stop="$runService"
echo '#!/bin/sh'  > "$runService"
echo 'set -e'  >> "$runService"
echo 'DIR=$(readlink -f "$0")' >> "$runService"
echo 'PATHDIR=$(dirname "$DIR")' >> "$runService"
echo 'export DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,address=4011,suspend=n"' >> "$runService"
echo 'export UV="$PATHDIR/jdk17"' >> "$runService"
echo 'export opts="-Xms64m -Xmx512m -Dfile.encoding=UTF-8 -Dserver.port=8008"' >> "$runService"
echo 'export JarPH="*.jar"' >> "$runService"
echo 'export class=' >> "$runService"
echo 'export BCEPHAL_JAVA_JRE="$BCEPHAL_JRE_HOME"' >> "$runService"
echo 'export BCEPHAL_JAVA_OPT=$BCEPHAL_JAVA_OPT' >> "$runService"
echo 'export BCEPHAL_JAVA_OPT_=' >> "$runService"
PIDFILE_="/var/run/Bcephal/8/Bcephal-Integration-Service.pid"
LOGFILE_="/var/Bcephal/8/Bcephal-Integration-Service.log"
echo 'if [ -z "$BCEPHAL_JAVA_JRE" ] && [ -n "$BCEPHAL_JAVA_HOME" ] && [ -d "$BCEPHAL_JAVA_HOME/jre" ];then'   >> "$runService"
echo '	BCEPHAL_JAVA_JRE="$BCEPHAL_JAVA_HOME/jre"'  >> "$runService"
echo ' else' >> "$runService"
echo '	if [ ! -d "$BCEPHAL_JAVA_JRE" ] && [ -n "$JRE_HOME" ] && [ -d "$JRE_HOME" ];then'   >> "$runService"
echo '    	BCEPHAL_JAVA_JRE="$JRE_HOME"'  >> "$runService"
echo '	elif [ ! -d "$BCEPHAL_JAVA_JRE" ] && [ -n "$JAVA_HOME" ];then' >> "$runService"
echo '		if [ -d "$JAVA_HOME/jre" ];then' >> "$runService"
echo '			BCEPHAL_JAVA_JRE="$JAVA_HOME/jre"'  >> "$runService"
echo '		else'  >> "$runService"
echo '			BCEPHAL_JAVA_JRE="$JAVA_HOME"'  >> "$runService"
echo '		fi'  >> "$runService"
echo '	fi' >> "$runService"
echo 'fi' >> "$runService"
echo 'if [ -z "$BCEPHAL_JAVA_JRE" ] || [ -n "$BCEPHAL_JAVA_JRE" ] && [ ! -d "$BCEPHAL_JAVA_JRE" ];then'   >> "$runService"
echo '  	BCEPHAL_JAVA_JRE="$UV"'  >> "$runService"
echo 'fi'  >> "$runService"
echo 'if [ -z "$BCEPHAL_JAVA_OPT" ];then'   >> "$runService"
echo '	BCEPHAL_JAVA_OPT="$JAVA_OPT"'  >> "$runService"
echo 'fi' >> "$runService"
echo 'if [ -z "$BCEPHAL_JAVA_OPT" ];then'   >> "$runService"
echo ' 	BCEPHAL_JAVA_OPT="$opts"'  >> "$runService"
echo 'fi'  >> "$runService"
echo 'if test "$2" = "DEBUG";then' >> "$runService"
echo 'BCEPHAL_JAVA_OPT_="$DEBUG $BCEPHAL_JAVA_OPT  -jar "$JarPH" $class"' >> "$runService"
echo 'else' >> "$runService"
echo 'BCEPHAL_JAVA_OPT_="$BCEPHAL_JAVA_OPT  -jar "$JarPH" $class"' >> "$runService"
echo 'fi' >> "$runService"
echo '' >> "$runService"
DAEMONUSER=bcephal
echo ' DAEMONUSER=bcephal'		>> "$runService"
echo  'JAVA_WORKDIR="$BCEPHAL_JAVA_JRE/bin"'  >> "$runService"
echo  'WORKDIR="$PATHDIR"'  >> "$runService"
echo  'daemon_NAME="java"'  >> "$runService"
echo   "PIDFILE_=\"$PIDFILE_\""  >> "$runService"
echo   "LOGFILE_=\"$LOGFILE_\""  >> "$runService"
echo  'export PATH="/sbin:/bin:/usr/sbin:/usr/bin"'  >> "$runService"
echo  '#test -x "$JAVA_WORKDIR/$daemon_NAME" || exit 0'  >> "$runService"
echo  ". /lib/lsb/init-functions"  >> "$runService"
echo  'do_start_prepare()'  >> "$runService"
echo  '{'  >> "$runService"
echo  'return'  >> "$runService"
echo  '}'  >> "$runService"
echo  "d_start () {"  >> "$runService"
echo  "	do_start_prepare"  >> "$runService"
echo  '	log_daemon_msg "Starting system Bcephal Integration Service Daemon"'  >> "$runService"
echo  '	log_daemon_msg "BCEPHAL_JAVA_JRE: $BCEPHAL_JAVA_JRE"'  >> "$runService"
echo  '	log_daemon_msg "BCEPHAL_JAVA_OPT: $BCEPHAL_JAVA_OPT"'  >> "$runService"
echo  '	start-stop-daemon --start --quiet --chuid "$DAEMONUSER" --name "$daemon_NAME" --make-pidfile --pidfile $PIDFILE_  --no-close --background  --chdir "$WORKDIR" --exec "$JAVA_WORKDIR/$daemon_NAME" -- $BCEPHAL_JAVA_OPT_ '  >> "$runService"
echo  '	RETVAL=$?'  >> "$runService"
echo  '	log_daemon_msg "Started status : $RETVAL"'  >> "$runService"
echo  '	log_end_msg $RETVAL'  >> "$runService"
echo  '	return $RETVAL'  >> "$runService"
echo  "  }"  >> "$runService"
echo  ""  >> "$runService"
echo  "d_stop () {"  >> "$runService"
echo  '	log_daemon_msg "Stopping system Bcephal Integration Service Daemon"'  >> "$runService"
echo  '	start-stop-daemon --stop --quiet --pidfile $PIDFILE_  '  >> "$runService"
echo  '	start-stop-daemon --stop --quiet --oknodo --pidfile $PIDFILE_  --retry=0/30/TERM/5/KILL/5'  >> "$runService"
echo  '	RETVAL=$?'  >> "$runService"
echo  '	[ "$RETVAL" = 2 ] && return 2'  >> "$runService"
echo  '	rm -f $PIDFILE_'  >> "$runService"
echo  '	log_daemon_msg "Daemon Stoped with status : $RETVAL"'  >> "$runService"
echo  '	log_end_msg $RETVAL'  >> "$runService"
echo  '	return $RETVAL'  >> "$runService"
echo  "   }"  >> "$runService"
echo  ""  >> "$runService"
echo  'case "$1" in'  >> "$runService"
echo  ""  >> "$runService"
echo  "start|stop)"  >> "$runService"
echo  '	d_${1}'  >> "$runService"
echo  "	;;"  >> "$runService"
echo  ""  >> "$runService"
echo  "restart|reload|force-reload)"  >> "$runService"
echo  '	d_stop'  >> "$runService"
echo  "	sleep 1"  >> "$runService"
echo  '	d_start'  >> "$runService"
echo  "	;;"  >> "$runService"
echo  ""  >> "$runService"
echo  "force-stop)"  >> "$runService"
echo  '	d_stop'  >> "$runService"
echo  '	killall -q $daemon_NAME || true'  >> "$runService"
echo  "	sleep 1"  >> "$runService"
echo  '	killall -q -9 $daemon_NAME || true'  >> "$runService"
echo  "	;;"  >> "$runService"
echo  ""  >> "$runService"
echo  "status)"  >> "$runService"
echo  '	status_of_proc "$daemon_NAME" "$JAVA_WORKDIR/$daemon_NAME" "system-wide $daemon_NAME" && exit 0 || exit $?'  >> "$runService"
echo  "	;;"  >> "$runService"
echo  "*)"  >> "$runService"
echo  '	echo "Usage:  $0 {start|stop|force-stop|restart|reload|force-reload|status}"'  >> "$runService"
echo  "	exit 1"  >> "$runService"
echo  "	;;"  >> "$runService"
echo  "esac"  >> "$runService"
echo  "exit 0"  >> "$runService"

echo "[Unit]" > "$service"
echo "Description=B-cephal Integration Service" >> "$service"
echo "Requires=network.target Bcephal-Configuration-Service.service" >> "$service"
echo "After=network.target nss-lookup.target nss-user-lookup.target Bcephal-Configuration-Service.service" >> "$service"
echo "" >> "$service"
echo "[Service]" >> "$service"
echo "User=$DAEMONUSER" >> "$service"
echo "Type=forking" >> "$service"
echo "EnvironmentFile=/etc/environment" >> "$service"
echo "EnvironmentFile=/etc/profile" >> "$service"
echo "WorkingDirectory=$BCEPHAL_PATH" >> "$service"
echo "RuntimeDirectory=Bcephal/8" >> "$service"
echo "ExecStart=/bin/bash "$start"  start" >> "$service"
echo "ExecStop=/bin/bash "$stop" stop" >> "$service"
echo "ExecReload=/bin/bash "$start" reload" >> "$service"
echo "KillMode=mixed" >> "$service"
echo "KillSignal=SIGINT" >> "$service"
echo "Restart=on-failure" >> "$service"
echo "" >> "$service"
echo "[Install]" >> "$service"
echo "WantedBy=multi-user.target" >> "$service"


if [ -n "$start" ] && [ -f "$start" ];then
 chown $DAEMONUSER "$start"
fi
if [ -n "$stop" ] && [ -f "$stop" ];then
 chown $DAEMONUSER "$stop"
fi
if [ -n "$runService" ] && [ -f "$runService" ];then
 chown $DAEMONUSER "$runService"
fi
if [ -n "$service" ] && [ -f "$service" ];then
 chown $DAEMONUSER "$service"
fi
if [ -n "$start" ] && [ -f "$start" ];then
 chmod ugo+rwxs "$start"
fi
if [ -n "$stop" ] && [ -f "$stop" ];then
 chmod ugo+rwxs "$stop"
fi
if [ -n "$runService" ] && [ -f "$runService" ];then
 chmod ugo+rwxs "$runService"
fi
if [ -n "$service" ] && [ -f "$service" ];then
 chmod ugo+rwxs "$service"
 systemctl stop "$serviceName"
 systemctl disable "$serviceName"
 rm -f /etc/systemd/system/$serviceName
 cp "$service" /etc/systemd/system/
# systemctl daemon-reload
 systemctl enable $serviceName
# systemctl start $serviceName
# systemctl status $serviceName
fi
chmod ugo+rwxs -R "$BCEPHAL_PATH"
rm -f $service
rm -f $0