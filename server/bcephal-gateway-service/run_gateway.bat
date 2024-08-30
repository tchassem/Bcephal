@echo off
set JAVA_HOME0="%~dp0..\..\..\tools\jdk-21.0.1"
set JAVA_HOME=
pushd %JAVA_HOME0%
set JAVA_HOME=%CD%
popd
echo JAVA_HOME=%JAVA_HOME%

::gradlew clean
%JAVA_HOME%\bin\java -Dfile.encoding=UTF-8 -Dspring.profiles.active=devj -jar "%~dp0\build\libs\bcephal-gateway-service-0.0.1.jar"