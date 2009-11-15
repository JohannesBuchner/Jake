# Makefile for Jake
#
# 	provides a dependency system to avoid duplicate builds, tests 
# 	and packaging.

VERSION="1.0-SNAPSHOT"
SHELL="bash"
MVN=mvn ${MVNEXTRAARGS}
GUITYPE="swing"
DEBUGGUITYPE="console"
LAUNCH4J=/Applications/launch4j/launch4j

all: install

help:
	@cat Makefile|grep '^# ' |sed 's/^# \([^: \t]* *\): \(.*\)/\t\1\t\2/g'|sed 's/#\(.*\)#/\n[1;34m\1[0m/g'|sed 's/#//g'|sed 's/\t@\([^: \t]*\)/     \*\t[1;32m\1[0m/g'

# starting #


# @start    : start gui with simple install (no dependency system)
start: install
	cd gui; ${MVN} -o exec:java

# multistart: allow jake to run multiple instances (no dependency 
# 			  system)
multistart: install
	cd gui; ${MVN} -o exec:java -Dcom.jakeapp.gui.ignoresingleinstance

# @depstart: start gui with dependency system
depstart: gui 
	cd gui; ${MVN} exec:java

# instantquit: start gui and quit it immediatly (for debugging spring)
instantquit: gui
	cd gui; ${MVN} exec:java -Dcom.jakeapp.gui.test.instantquit

# quickstart: start gui with the projectfolder $PROJECTFOLDER
quickstart: gui
	cd gui; ${MVN} exec:java -Dexec.args=${PROJECTFOLDER}

# console   : start console-gui (commander)
console: commander 
	cd commander; ${MVN} exec:java -Dexec.mainClass=com.jakeapp.gui.console.JakeCommander

xmpp-console: commander
	cd commander; ${MVN} exec:java -Dexec.mainClass=com.jakeapp.gui.console.XmppCommander

# building & installing #

# @install   : simply install everything (no dependency system)
install: 
	mvn -Dmaven.test.skip=true install

# @jar       : deploy to a single jar file
jar:
	#${MVN} clean
	${MVN} install -Dmaven.test.skip=true
	mkdir -p releases
	cd releases && rm -rf temp && mkdir -p temp 
	cd releases/temp && unzip ../../gui/target/gui-swing-${VERSION}.one-jar.jar && cp -v ../../{core,ics,ics-xmpp}/target/*.jar main/ && rm -f ../jake-current.jar && jar cvfm ../jake-current.jar meta-inf/manifest.mf .
	cd releases; rm -rf temp
	@echo release ready under releases/jake-current.jar
	@echo run with java -jar releases/jake-current.jar


# @packages   : create the packages for linux, mac and windows
packages: jar package-win package-mac package-linux

package-mac: jar
	@echo Creating Mac Package...
	# cleaning up
	rm -rf releases/Jake.app releases/Jake.dmg releases/tmp.dmg releases/dmgtmp
	# generate the mac app
	cp -r launcher/Jake.app releases/
	cp releases/jake-current.jar releases/Jake.app/Contents/Resources/Java
	# generate the mac disk image
	rm -rf releases/dmgtmp
	mkdir releases/dmgtmp
	cp -r releases/Jake.app releases/dmgtmp
	cp launcher/resources/dot_background.png releases/dmgtmp/.background.png
	cp launcher/resources/dot_VolumeIcon.icns releases/dmgtmp/.VolumeIcon.icns
	cp launcher/resources/dot_DS_Store releases/dmgtmp/.DS_Store
	ln -s /Applications releases/dmgtmp/Applications
	which hdiutil 2>&1 && hdiutil create -volname "Jake" -srcfolder releases/dmgtmp "releases/tmp.dmg" -noscrub
	which hdiutil 2>&1 && hdiutil convert -format UDBZ "releases/tmp.dmg" -o "releases/Jake.dmg"
	which hdiutil 2>&1 && hdiutil internet-enable -no "releases/Jake.dmg"
	cd releases; rm -rf dmgtmp tmp.dmg Jake.app
	@echo Mac Package: Jake.dmg


package-win: jar
	@echo Creating Windows Package with Launch4j: ${LAUNCH4J}...
	rm -rf releases/Jake.exe releases/jakeapp.ico releases/jake.xml
	cp launcher/resources/jake.xml releases/
	cp launcher/resources/jakeapp.ico releases/
	${LAUNCH4J} $(CURDIR)/releases/jake.xml
	rm -f releases/jake.xml releases/jakeapp.ico
	@echo Winows Package: releases/Jake.exe
	@echo TODO create installer with NULLSOFT or use other db path
	
package-linux: jar
	@echo Creating Linux Package...
	rm -f releases/Jake.bin
	cat launcher/jake.sh releases/jake-current.jar > releases/jake.bin
	chmod +x releases/jake.bin
	#tar cjvf releases/jake.tar.bz2 releases/jake.bin 

	@echo Linux Package: releases/Jake.bin



# gui        : build gui component
gui: core availablelater
	@bash .build.sh ${VERSION} $@ "$^"
	rm -f .rebuild* */.rebuild

# commander  : build commander component
commander: core
	@bash .build.sh ${VERSION} $@ "$^"
	rm -f .rebuild* */.rebuild

# core       : build core component
core: availablelater fss ics ics-xmpp core-dao-hibernate-threading 
	@bash .build.sh ${VERSION} $@ "$^"
	rm -f .rebuild_ics_dependent .rebuild_ics-xmpp_dependent

core-domain:
	@bash .build.sh ${VERSION} $@ "$^"

core-dao-interfaces: core-domain
	@bash .build.sh ${VERSION} $@ "$^"

core-dao-tests-base: core-dao-interfaces core-domain
	@bash .build.sh ${VERSION} $@ "$^"

core-dao-hibernate-impl: core-dao-interfaces core-domain
	@bash .build.sh ${VERSION} $@ "$^"

core-dao-hibernate-threading: core-dao-hibernate-impl
	@bash .build.sh ${VERSION} $@ "$^"

availablelater:
	@bash .build.sh ${VERSION} $@ "$^"

# fss        : build fss component
fss: fss-tests-base
	@bash .build.sh ${VERSION} $@ "$^"

fss-tests-base:
	bash .build.sh ${VERSION} $@ "$^"

# ics        : build ics component
ics:
	@bash .build.sh ${VERSION} $@ "$^"

# ics-xmpp   : build ics-xmpp component
ics-xmpp: ics
	bash .build.sh ${VERSION} $@ "$^"

# cleaning #

# clean      : clean build environment
clean:
	${MVN} clean

# mrproper   : clean build environment and uninstall from local 
# 			  repository
mrproper: clean
	rm -rf ~/.m2/repository/com/{jakeapp,doublesignal}/
	rm -f target/*-${VERSION}.jar
	rm -f .rebuild* */.rebuild

# lazyclean  : clean build environment, but keep fss
lazyclean:
	cp fss/target/fss-${VERSION}.jar .backup.fss-${VERSION}.jar
	${MVN} clean
	mkdir fss/target/
	mv .backup.fss-${VERSION}.jar fss/target/fss-${VERSION}.jar

# others # 

generateDaos:
	# use SpringThreadBroker.getInstance() for global dao's
	bash generateDao.sh core/src/main/java/com/jakeapp/core/dao/HibernateConfigurationDao.java      "SpringThreadBroker.getInstance()"
	bash generateDao.sh core/src/main/java/com/jakeapp/core/dao/HibernateProjectDao.java            "SpringThreadBroker.getInstance()"
	bash generateDao.sh core/src/main/java/com/jakeapp/core/dao/HibernateAccountDao.java            "SpringThreadBroker.getInstance()"
	bash generateDao.sh core/src/main/java/com/jakeapp/core/dao/HibernateInvitationDao.java         "SpringThreadBroker.getInstance()"

	# use SpringThreadBroker.getThreadForObject(this) for local (per project) daos
	bash generateDao.sh core/src/main/java/com/jakeapp/core/dao/HibernateFileObjectDao.java         "SpringThreadBroker.getThreadForObject(this)"
	bash generateDao.sh core/src/main/java/com/jakeapp/core/dao/HibernateLogEntryDao.java           "SpringThreadBroker.getThreadForObject(this)"
	bash generateDao.sh core/src/main/java/com/jakeapp/core/dao/HibernateNoteObjectDao.java         "SpringThreadBroker.getThreadForObject(this)"

# 
# You can add arguments to the maven call by setting the MVNEXTRAARGS 
#   environment variable.
# The dependency system does only work with the coreutils package, i.e., only on 
#   Linux. 
# 
.PHONY: install jar packages package-win package-mac package-linux availablelater gui core fss fss-tests-base ics ics-xmpp commander start depstart instantquit quickstart console clean mrproper lazyclean
