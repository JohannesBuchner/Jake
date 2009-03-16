#!/bin/sh
##################################################################
#Set application name
appName=jake
# env var $JAVA_OPTS is set with default values if not set
defaultJAVA_OPTS='-Xms32M -Xmx384M -Djava.net.preferIPv4Stack=true'
##################################################################
 
##################################################################
showmessage() {
	echo "$0 - $1"
	#Use KDE dialog
	if [ $DE = "kde" ]; then
		kdialog -title "$0" --error "$1"
	#Use Gnomes dialog
	elif [ $DE -eq "gnome" ]; then
		zenity --title "$0" --error --text="$1"
	#Fallback for other DE
	else
		xmessage -center "$1"
	fi
}
 
###################################################################
#Detect DE for future message showings
if [ "$KDE_FULL_SESSION" = "true" ]; then
	DE=kde;
elif [ "$GNOME_DESKTOP_SESSION_ID" != "" ]; then
	DE=gnome;
else
	DE=x;
fi
###################################################################
#Detect Java and Java Opts
JAVA=java
echo 'Starting Jake...'
which $JAVA 2>&1 || {
	showmessage "Cannot find Java.\nPlease install or correct your JAVA_HOME!"; 
	exit 2
}
 
echo exec -a $appName $JAVA $JAVA_OPTS -jar $0
exec $JAVA $JAVA_OPTS -jar $0
