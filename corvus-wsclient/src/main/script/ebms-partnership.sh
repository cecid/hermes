#!/bin/sh

LIB_PATH=./lib
ARGS=$*

for i in `ls -1 $LIB_PATH`
do
       WSC_CLASSPATH=$WSC_CLASSPATH$LIB_PATH"/"$i":"
done

if [ $# -lt 3 ]; then
	if [ "$1" = "" ]; then
		ARGS="./config/ebms-partnership.xml"
	fi
	if [ "$2" = "" ]; then
		ARGS="$ARGS ./config/ebms-partnership/ebms-request.xml"	
	fi
	if [ "$3" = "" ]; then
		ARGS="$ARGS ./logs/ebms-partnership.log"
	fi	
fi

echo $ARGS

EXEC="$JAVA_HOME/bin/java -cp $WSC_CLASSPATH hk.hku.cecid.corvus.http.EBMSPartnershipSender $ARGS"
echo $EXEC
exec $EXEC
