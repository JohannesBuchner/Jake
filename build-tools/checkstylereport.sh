#!/bin/bash
file=""

cat $1 |
while read line; do 
	echo "$line"| grep '<file name="' -q && {
		file=$(echo $line|grep '<file name="[^"]*"'|sed 's/^[^"]*"//g' |sed 's/".*$//g'|sed 's/^.*com\/doublesignal\/sepm\/jake\///g'); 
	}
	echo "$line"|grep '<error' -q && {
		noline=$(echo "$line"|grep -Eo '[^ ]*="[^"]*"'|grep line=|sed 's/^[^=]*=//g'|sed 's/"//g')
		severity=$(echo "$line"|grep -Eo '[^ ]*="[^"]*"'|grep severity=|sed 's/^[^=]*=//g'|sed 's/"//g')
		message=$(echo "$line"|grep -Eo '[^ ]*="[^"]*"'|grep message=|sed 's/^[^=]*=//g'|sed 's/"//g')
		column=$(echo "$line"|grep -Eo '[^ ]*="[^"]*"'|grep column=|sed 's/^[^=]*=//g'|sed 's/"//g')
		echo "$file:$noline:$column $message ($severity)"
	}
done
