#!/bin/bash

MVN=~/apache-maven-2.0.9/bin/mvn
#MVN=mvn
MVNPARAM="-q"
MVNPARAM=""
MVN="$MVN $MVNPARAM"
CDBACK="cd $OLDPWD >/dev/null 2>/dev/null"
CDBACK="cd .." 

function mvnsite(){
	$MVN site | 
		grep -v 'VM_global_library.vm' | 
		grep -v '[INFO]'
}

root=$(dirname $0)/..
cd $root

n=0
for proj in  fss ics sync documentation; do
	echo "=== $proj ==="
	cd $proj && { mvnsite && n=$(($n+1)); cd ..; }
done

echo "$n of 4 sites built successfully"
#{ echo FSService; cd fss; mvnsite && fss=1; $CDBACK; }
#{ echo ICService; cd ics; mvnsite && ics=1; $CDBACK; }
#{ echo SyncService; cd sync; mvnsite && sync=1; $CDBACK; }
#{ echo Documentation; cd documentation; mvnsite && doc=1; $CDBACK; }



