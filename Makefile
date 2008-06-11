VERSION="1.0-SNAPSHOT"
SHELL="bash"


all: gui

start: gui
	cd gui; mvn exec:java -Dexec.mainClass=com.doublesignal.sepm.jake.gui.StartJake

gui: core
	[[ -e .rebuild_$@ ]] && { cd $@; mvn package install; } || true
	cd $@; [[ -e target/$@-${VERSION}.jar ]] || mvn package install || true
	cd $@; find src/ -type f -newer target/$@-${VERSION}.jar | grep -v "\.svn" -q && mvn package install || true
	rm -f .rebuild_gui

core: fss ics
	[[ -e .rebuild_$@ ]] && { cd $@; mvn package install; } || true
	cd $@; [[ -e target/$@-${VERSION}.jar ]] || { mvn package install; touch ../.rebuild_gui; } || true
	cd $@; find src/ -type f -newer target/$@-${VERSION}.jar | grep -v "\.svn" -q &&  { mvn package install; touch ../.rebuild_gui; } || true
	rm -f .rebuild_$@

fss:
	cd $@; [[ -e target/$@-${VERSION}.jar ]] || { mvn package install; touch ../.rebuild_core; } || true
	cd $@; find src/ -type f -newer target/$@-${VERSION}.jar | grep -v "\.svn" -q &&  { mvn package install; touch ../.rebuild_core; } || true
ics:
	cd $@; [[ -e target/$@-${VERSION}.jar ]] || { mvn package install; touch ../.rebuild_core; } || true
	cd $@; find src/ -type f -newer target/$@-${VERSION}.jar | grep -v "\.svn" -q &&  { mvn package install; touch ../.rebuild_core; } || true

clean:
	mvn clean

up:
	oldrev=$$(svn info |grep '^Revision: '|sed 's/Revision: //g'); svn up; newrev=$$(svn info |grep '^Revision: '|sed 's/Revision: //g'); [ "$$oldrev" == "$$newrev" ] || svn log -v -r$$oldrev:$$newrev|while read line; do echo "$$line"; sleep 0.3; echo "$$line"|grep -q -- "-----" && sleep 3; done

.PHONY: gui core fss ics
