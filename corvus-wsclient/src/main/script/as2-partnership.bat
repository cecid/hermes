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
set ARGS=%ARGS% ./config/as2-partnership/as2-request.xml

:checkLogFile
if not ""%3"" == """" goto execCmd
set ARGS=%ARGS% ./logs/as2-partnership.log

:execCmd
@echo on

"%JAVA_HOME%\bin\java" -cp  "%WSC_CLASSPATH%" hk.hku.cecid.corvus.http.AS2PartnershipSender %ARGS%

PAUSE
