#!/bin/sh

LIB_PATH=./lib
ARGS=$*

CLASSPATH=./conf:

for i in `ls -1 $LIB_PATH`
do
       CLASSPATH=$CLASSPATH$LIB_PATH"/"$i":"
done

exec java -classpath $CLASSPATH "hk.hku.cecid.corvus.test.EbMSLoopback"
