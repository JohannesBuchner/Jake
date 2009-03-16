#!/bin/sh
##################################################################
#Set application name
appName=jake
# Define minimum java version
minJavaVersionMajor=1
minJavaVersionMinor=6
minJavaVersionPatch=10
# env var $JAVA_OPTS is set with default values if not set
defaultJAVA_OPTS='-Xms32M -Xmx384M -Djava.net.preferIPv4Stack=true'
##################################################################
 
##################################################################
showmessage() {
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
if [ $KDE_FULL_SESSION = "true" ]; then
DE=kde;
elif [ $GNOME_DESKTOP_SESSION_ID != "" ]; then
DE=gnome;
else
DE=x;
fi
###################################################################
#Detect Java and Java Opts
JAVA=java
if [ -n "$JAVA_HOME" ]; then
JAVA="$JAVA_HOME/bin/java"
fi
# check JAVA_OPTS and set with defaults if not set
if [ -z "$JAVA_OPTS" ]; then
JAVA_OPTS=$defaultJAVA_OPTS
fi
echo 'Starting Jake...'
if ( which $JAVA 2>&1> /dev/null ); then
echo `which $JAVA`
else
echo 'Cannot find Java. Please install or correct your JAVA_HOME'
showmessage "Cannot find Java.\nPlease install or correct your JAVA_HOME!"
exit 2
fi
 
echo ''
#####################################################################
#Detect Java version and check it
#create tmp file name
tmpJavaVersion=mktemp
# query complete version by java
java -version 2>&1 | head -n 1> $tmpJavaVersion
# clean output
javaVersion=`awk 'BEGIN {FS = "\""} {print $2}' $tmpJavaVersion`
# split version parts
javaVersionMajor=`awk 'BEGIN {FS = "."} {print $1}'` <<EOF
$javaVersion
EOF
javaVersionMinor=`awk 'BEGIN {FS = "."} {print $2}'` <<EOF
$javaVersion
EOF
javaVersionPatch=`awk 'BEGIN {FS = "."} {print $3}'` <<EOF
$javaVersion
EOF
javaVersionPatch=`awk 'BEGIN {FS = "_"} {print $1}'` <<EOF
$javaVersionPatch
EOF
######################################################################
# Print found java version
echo "Found Java version:    $javaVersionMajor.$javaVersionMinor.$javaVersionPatch"
echo "Required: $minJavaVersionMajor.$minJavaVersionMinor.$minJavaVersionPatch"
if [ $javaVersionMajor -gt $minJavaVersionMajor ]; then
echo -n ''
else
if [ $javaVersionMajor -eq $minJavaVersionMajor ] &&
[ $javaVersionMinor -gt $minJavaVersionMinor ]; then
echo -n ''
else
if [ $javaVersionMajor -eq $minJavaVersionMajor ] &&
[ $javaVersionMinor -eq $minJavaVersionMinor ] &&
[ $javaVersionPatch -ge $minJavaVersionPatch ]; then
echo -n ''
else
echo "Please update your Java to the required Java version $minJavaVersionMajor.$minJavaVersionMinor.$minJavaVersionPatch"
 
showmessage "Please update your Java to the required Java version $minJavaVersionMajor.$minJavaVersionMinor.$minJavaVersionPatch.\nYour version $javaVersionMajor.$javaVersionMinor.$javaVersionPatch is too old!"
exit 1;
fi
fi
fi
 
echo ''
echo 'Java version is sufficient. Starting right now.'
##########################################################################
exec -a $appName $JAVA $JAVA_OPTS -jar $0
