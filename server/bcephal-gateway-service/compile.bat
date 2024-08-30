@echo off
set JAVA_HOME0="%~dp0..\..\..\tools\jdk-21.0.1"
set JAVA_HOME=
pushd %JAVA_HOME0%
set JAVA_HOME=%CD%
popd
echo JAVA_HOME=%JAVA_HOME%

::gradlew clean
rd /s /q build
gradlew buildZip