@echo off

set LIB_PATH=./lib
set ARGS=%*

set WSC_CLASSPATH=.

for %%j in (%LIB_PATH%/*.jar) do (call  set-classpath.bat %LIB_PATH%/%%j )

:checkRequestFile
if not ""%1"" == """" goto checkLogFile
set ARGS=%ARGS% ./config/message-status/ms-request.xml

:checkLogFile
if not ""%2"" == """" goto execCmd
set ARGS=%ARGS% ./logs/message-status.log

:execCmd
@echo on
echo %ARGS%

"%JAVA_HOME%\bin\java" -cp "%WSC_CLASSPATH%" hk.hku.cecid.corvus.ws.MessageStatusSender %ARGS%

PAUSE