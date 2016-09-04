#!/bin/sh

LIB_PATH=./lib
ARGS=$*

for i in `ls -1 $LIB_PATH`
do
       WSC_CLASSPATH=$WSC_CLASSPATH$LIB_PATH"/"$i":"
done

if [ $# -lt 4 ]; then
	if [ "$1" = "" ]; then
		ARGS="$ARGS ./config/ebms-permitdl/ebms-request.xml"
	fi
	if [ "$2" = "" ]; then
		ARGS="$ARGS ./logs/ebms-permitdl.log"
	fi
fi

echo $ARGS

EXEC="$JAVA_HOME/bin/java -cp $WSC_CLASSPATH hk.hku.cecid.corvus.ws.EBMSPermitRedownloadSender $ARGS"
echo $EXEC
exec $EXEC
