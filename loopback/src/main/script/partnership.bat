@echo off

if "%1" == "ebms" goto doEbms
if "%1" == "as2" goto doAs2
goto showUsage

:doEbms
if "%2" == "-a" goto doRunEbms
if "%2" == "-d" goto doRunEbms
goto showUsage

:doRunEbms
if "%3" == "" goto showUsage
if not exist "%3" goto fileNotExist
call partnership-ebms.bat %2 %3
goto end


:doAs2
if "%2" == "-a" goto doRunAs2
if "%2" == "-d" goto doRunAs2
goto showUsage

:doRunAs2
if "%3" == "" goto showUsage
if not exist "%3" goto fileNotExist
call partnership-as2.bat %2 %3
goto end

:fileNotExist
echo The file '%3' does not exist.
goto end

:showUsage
echo Usage:  partnership ( protocols ... ) ( options... ) "partnership_xml"
echo protocols:
echo   ebms              Partnership maintenance for ebMS protocol
echo   as2               Partnership maintenance for AS2 protocol
echo options:
echo   -a                Add partnership
echo   -d                Remove partnership
goto end

:end