@echo off

set LIB_PATH=./lib
set ARGS=%*

set WSC_CLASSPATH=.

for %%j in (%LIB_PATH%/*.jar) do (call  set-classpath.bat %LIB_PATH%/%%j )

:checkConfigFile
if not ""%1"" == """" goto checkLogFile
set ARGS=%ARGS% ./config/ebms-permitdl/ebms-request.xml

:checkLogFile
if not ""%2"" == """" goto checkPayload
set ARGS=%ARGS% ./logs/ebms-permitdl.log

:execCmd
@echo on

"%JAVA_HOME%\bin\java" -cp "%WSC_CLASSPATH%" hk.hku.cecid.corvus.ws.EBMSPermitRedownloadSender %ARGS%

PAUSE
