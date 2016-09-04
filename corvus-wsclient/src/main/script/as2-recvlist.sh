#!/bin/sh

LIB_PATH=./lib
ARGS=$*

for i in `ls -1 $LIB_PATH`
do
       WSC_CLASSPATH=$WSC_CLASSPATH$LIB_PATH"/"$i":"
done

if [ $# -lt 3 ]; then
	if [ "$1" = "" ]; then
		ARGS="./config/as2-partnership.xml"
	fi
	if [ "$2" = "" ]; then
		ARGS="$ARGS ./config/as2-recvlist/as2-request.xml"	
	fi
	if [ "$3" = "" ]; then
		ARGS="$ARGS ./logs/as2-recvlist.log"
	fi
fi

EXEC="$JAVA_HOME/bin/java -cp $WSC_CLASSPATH hk.hku.cecid.corvus.ws.AS2ReceiverListSender $ARGS"
echo $EXEC
exec $EXEC
