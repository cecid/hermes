@echo off

set LIB_PATH=./lib
set ARGS=%*

set WSC_CLASSPATH=.

for %%j in (%LIB_PATH%/*.jar) do (call  set-classpath.bat %LIB_PATH%/%%j )

:checkConfigFile
if not ""%1"" == """" goto checkLogFile
set ARGS=%ARGS% ./config/as2-permitdl/as2-request.xml

:checkLogFile
if not ""%2"" == """" goto checkPayload
set ARGS=%ARGS% ./logs/as2-permitdl.log

:execCmd
@echo on

"%JAVA_HOME%\bin\java" -cp "%WSC_CLASSPATH%" hk.hku.cecid.corvus.ws.AS2PermitRedownloadSender %ARGS%

PAUSE
