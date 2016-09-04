@echo off

set LIB_PATH=./lib
set ARGS=%1 %2 %3

set WSC_CLASSPATH=.

for %%j in (%LIB_PATH%/*.jar) do (call  set-classpath.bat %LIB_PATH%/%%j )

:checkPartnershipFile
if not ""%1"" == """" goto checkConfigFile
set ARGS=%ARGS% ./config/as2-partnership.xml

:checkConfigFile
if not ""%2"" == """" goto checkLogFile
set ARGS=%ARGS% ./config/as2-recvlist/as2-request.xml

:checkLogFile
if not ""%3"" == """" goto execCmd
set ARGS=%ARGS% ./logs/as2-recvlist.log

:execCmd
@echo on

"%JAVA_HOME%\bin\java" -cp "%WSC_CLASSPATH%" hk.hku.cecid.corvus.ws.AS2ReceiverListSender %ARGS%

PAUSE