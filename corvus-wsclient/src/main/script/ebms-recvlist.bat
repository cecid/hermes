@echo off

set LIB_PATH=./lib
set ARGS=%*

set WSC_CLASSPATH=.

for %%j in (%LIB_PATH%/*.jar) do (call  set-classpath.bat %LIB_PATH%/%%j )

:checkPartnershipFile
if not ""%1"" == """" goto checkConfigFile
set ARGS=%ARGS% ./config/ebms-partnership.xml

:checkConfigFile
if not ""%2"" == """" goto checkLogFile
set ARGS=%ARGS% ./config/ebms-recvlist/ebms-request.xml

:checkLogFile
if not ""%3"" == """" goto execCmd
set ARGS=%ARGS% ./logs/ebms-recvlist.log

:execCmd
@echo on

"%JAVA_HOME%\bin\java" -cp "%WSC_CLASSPATH%" hk.hku.cecid.corvus.ws.EBMSReceiverListSender %ARGS%

PAUSE