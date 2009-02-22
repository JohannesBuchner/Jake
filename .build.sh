#!/bin/bash

SHELL="bash"
GUITYPE="swing"
DEBUGGUITYPE="console"

VERSION=$1
TARGET=$2
MVN="mvn $MVNEXTRAARGS"
PREREQ=$3

cd $TARGET

JAR=$(ls target/*-$VERSION.jar 2>/dev/null)

info_txt() {
	echo -n "checking if we need to rebuild $TARGET ... "
}

check_prerequisites() {
	for i in $PREREQ; do 
		if [[ -e ../.rebuild_${i}_dependent ]]; then
			if [[ -e ${JAR} && ${JAR} -nt ../.rebuild_${i}_dependent ]]; then
				touch .rebuild
				break
			fi
		fi
	done
	true
}
check_jar() {
	if ! [[ -e ${JAR} ]]; then 
		echo -n "no jar! "; touch .rebuild
	fi
}
check_modified() { 
	find src/ -type f -newer ${JAR} | 
		grep -v '\.svn' && {
			echo -n ${JAR} $PWD "modification found! "
			touch .rebuild
		}
}
check_rebuild() {
	info_txt
	test -e .rebuild || check_jar
	test -e .rebuild || check_modified
}

clearrebuild() {
	rm -f .rebuild
}
do_rebuild() {
	echo pwd: $PWD
	echo ${MVN} install
	${MVN} install && touch ../.rebuild_${TARGET}_dependent && clearrebuild || exit 1
}
rebuild() {
	if test -e .rebuild; then
		echo "yes" 
		do_rebuild
	else
		echo "no"
	fi
}

printtesterrors() {
	[ -e target/surefire-reports/ ] && 
	{ 
		echo "Tests in error in $TARGET:"; 
		cat target/surefire-reports/*.txt|grep '<<<'|grep '^[^()]*[(][^(]*[)]' -Eo || 
			echo "(none)" 
		echo
	}
}


check_prerequisites
check_rebuild
rebuild
printtesterrors

exit 0

