@ECHO OFF

SETLOCAL EnableDelayedExpansion
SETLOCAL ENABLEEXTENSIONS 

REM Find the directory where the script lives, dependencies are relative to this.
SET SCRIPT_DIR=%~dp0
SET LOCAL_CLASSPATH=%CLASSPATH%;%SCRIPT_DIR%;%SCRIPT_DIR%\hue.jar

java -cp "%LOCAL_CLASSPATH%" -Dutilities.log.file="%SCRIPT_DIR%/../logs/Utilities.log" com.electromagneticsoftware.HueDesktop %*
