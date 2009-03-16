# Makefile for Jake
#
# 	provides a dependency system to avoid duplicate builds, tests 
# 	and packaging.

VERSION="1.0-SNAPSHOT"
SHELL="bash"
MVN=mvn ${MVNEXTRAARGS}
GUITYPE="swing"
DEBUGGUITYPE="console"

all: install

help:
	@cat Makefile|grep '^# ' |sed 's/^# \([^: \t]* *\): \(.*\)/\t\1\t\2/g'|sed 's/#\(.*\)#/\n[1;34m\1[0m/g'|sed 's/#//g'|sed 's/\t@\([^: \t]*\)/     \*\t[1;32m\1[0m/g'
	@svn info |grep '^Revision: '|sed 's/Revision: / Repository revision: /g'

# starting #

# @start    : start gui with simple install (no dependency system)
start: install
	cd gui; ${MVN} exec:java

# multistart: allow jake to run multiple instances (no dependency 
# 			  system)
multistart: install
	cd gui; ${MVN} exec:java -Dcom.jakeapp.gui.ignoresingleinstance

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
	cd releases && rm -rf temp && mkdir -p temp 
	cd releases/temp && unzip ../../gui/target/gui-swing-${VERSION}.one-jar.jar && cp -v ../../{core,ics,ics-xmpp,fss}/target/*-${VERSION}.jar main/ && rm -f ../jake-current.jar && jar cvfm ../jake-current.jar meta-inf/manifest.mf .
	cd releases; rm -rf temp
	@echo release ready under releases/jake-current.jar
	@echo run with java -jar releases/jake-current.jar



# gui        : build gui component
gui: core
	@bash .build.sh ${VERSION} $@ "$^"
	rm -f .rebuild* */.rebuild

# commander  : build commander component
commander: core
	@bash .build.sh ${VERSION} $@ "$^"
	rm -f .rebuild* */.rebuild

# core       : build core component
core: fss ics ics-xmpp
	@bash .build.sh ${VERSION} $@ "$^"
	rm -f .rebuild_ics_dependent .rebuild_ics-xmpp_dependent

# fss        : build fss component
fss:
	@bash .build.sh ${VERSION} $@ "$^"

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

# up         : update to newest revision and scroll logs
up:
	@oldrev=$$(svn info |grep '^Revision: '|sed 's/Revision: //g'); svn up; newrev=$$(svn info |grep '^Revision: '|sed 's/Revision: //g'); [ "$$oldrev" == "$$newrev" ] || svn log -v -r$$oldrev:$$newrev|while read line; do echo "$$line"; sleep 0.3; echo "$$line"|grep -q -- "-----" && sleep 3; done

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
.PHONY: install jar gui core fss ics ics-xmpp commander start depstart instantquit quickstart console clean mrproper lazyclean up
