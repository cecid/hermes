@echo off

set LIB_PATH=./lib
set ARGS=%*

set WSC_CLASSPATH=.

for %%j in (%LIB_PATH%/*.jar) do (call  set-classpath.bat %LIB_PATH%/%%j )
:checkPartnershipFile
if not ""%1"" == """" goto checkConfigFile
set ARGS=
set ARGS=%ARGS% ./config/as2-partnership.xml

:checkConfigFile
if not ""%2"" == """" goto checkLogFile
set ARGS=%ARGS% ./config/as2-send/as2-request.xml

:checkLogFile
if not ""%3"" == """" goto checkPayload
set ARGS=%ARGS% ./logs/as2-send.log

:checkPayload
if not ""%4"" == """" goto execCmd
set ARGS=%ARGS% ./config/as2-send/testpayload

:execCmd
@echo on
 
"%JAVA_HOME%\bin\java" -cp "%WSC_CLASSPATH%" hk.hku.cecid.corvus.ws.AS2MessageSender %ARGS%

PAUSE