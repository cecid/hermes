@echo off

if "%1" == "ebms" goto doEbms
if "%1" == "as2" goto doAs2

echo Usage:  loopback ( protocols ... )
echo protocols:
echo   ebms              Loop back test for ebMS protocol
echo   as2               Loop back test for AS2 protocol
goto end

:doEbms
call loopback-ebms.bat
goto end

:doAs2
call loopback-as2.bat
goto end

:end