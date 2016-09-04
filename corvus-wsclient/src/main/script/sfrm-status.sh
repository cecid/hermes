#!/bin/sh

LIB_PATH=./lib
ARGS=$*

for i in `ls -1 $LIB_PATH`
do
       WSC_CLASSPATH=$WSC_CLASSPATH$LIB_PATH"/"$i":"
done

if [ $# -lt 2 ]; then
	if [ "$1" = "" ]; then
		ARGS="$ARGS ./config/sfrm-status/sfrm-request.xml"	
	fi
	if [ "$2" = "" ]; then
		ARGS="$ARGS ./logs/sfrm-status.log"
	fi
fi

EXEC="$JAVA_HOME/bin/java -cp $WSC_CLASSPATH hk.hku.cecid.corvus.ws.SFRMStatusQuerySender $ARGS"
echo $EXEC
exec $EXEC
