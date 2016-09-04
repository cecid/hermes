@echo off

set LIB_PATH=./lib
set ARGS=%1 %2 %3 %4

set WSC_CLASSPATH=.

for %%j in (%LIB_PATH%/*.jar) do (call  set-classpath.bat %LIB_PATH%/%%j )

:checkPartnershipFile
if not ""%1"" == """" goto checkConfigFile
set ARGS=
set ARGS=%ARGS% ./config/ebms-partnership.xml

:checkConfigFile
if not ""%2"" == """" goto checkLogFile
set ARGS=%ARGS% ./config/ebms-recv/ebms-request.xml

:checkLogFile
if not ""%3"" == """" goto checkOutputDir
set ARGS=%ARGS% ./logs/ebms-recv.log

:checkOutputDir
if not ""%4"" == """" goto checkOutputPattern
set ARGS=%ARGS% ./output/ebms-recv/

:execCmd
@echo on

"%JAVA_HOME%\bin\java" -cp "%WSC_CLASSPATH%" hk.hku.cecid.corvus.ws.EBMSReceiverSender %ARGS%

PAUSE