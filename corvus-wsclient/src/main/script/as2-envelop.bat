@echo off

set LIB_PATH=./lib
set ARGS=%*

set WSC_CLASSPATH=.

for %%j in (%LIB_PATH%/*.jar) do (call  set-classpath.bat %LIB_PATH%/%%j )

:checkConfigFile
if not ""%1"" == """" goto checkLogFile
set ARGS=%ARGS% ./config/as2-envelop/as2-request.xml

:checkLogFile
if not ""%2"" == """" goto execCmd
set ARGS=%ARGS% ./logs/as2-envelop.log

:execCmd
@echo on

"%JAVA_HOME%\bin\java"  -cp  "%WSC_CLASSPATH%" hk.hku.cecid.corvus.http.AS2EnvelopQuerySender %ARGS%

PAUSE
