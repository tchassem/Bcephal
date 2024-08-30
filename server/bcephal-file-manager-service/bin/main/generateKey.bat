@echo off
:: https://www.ssl.com/fr/how-to/export-certificates-private-key-from-pkcs12-file-with-openssl/
set JAVA_HOME=D:\PROJECTS\BCEPHAL\cots\jdk1.8.0_77 - Copie\jre
set alias=bcephal-alias
set pwd=_bcphlD_.541
set jks-keyfile=bcephal-keystore.jks
set pkcs-keyfile=bcephal-keystore.p12

:: generate jks key
echo generate jks key
"%JAVA_HOME%\bin\keytool" -genkeypair -alias %alias% -keyalg RSA -keysize 2048 -keystore %jks-keyfile% -validity 3650 -storepass %pwd%

:: generate pkcs key
echo generate pkcs key
"%JAVA_HOME%\bin\keytool" -genkeypair -alias %alias% -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore %pkcs-keyfile% -validity 3650 -storepass %pwd%

::Convert a JKS keystore into PKCS12
echo Convert a JKS keystore into PKCS12
"%JAVA_HOME%\bin\keytool" -importkeystore -srckeystore %jks-keyfile% -destkeystore %pkcs-keyfile% -deststoretype pkcs12

:: exporter certificat
::  openssl pkcs12 -in bcephal-keystore.p12 -out bcephal-keystore.crt -nodes