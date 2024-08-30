@echo off
:: https://www.ssl.com/fr/how-to/export-certificates-private-key-from-pkcs12-file-with-openssl/
set JAVA_HOME=%JAVA_HOME18%
set alias=bcephal-alias_
set pwd=_bcphlD_.541
set jks-keyfile=bcephalsecurity.jks
set pkcs-keyfile=bcephalsecurity.p12
set bcephal-keyfile=bcephalsecurity.key
set bcephal-crtfile=bcephalsecurity.crt

:: generate jks key
echo generate jks key
"%JAVA_HOME%\bin\keytool" -genkeypair -alias %alias% -keyalg RSA -keysize 2048 -keystore %jks-keyfile% -validity 3650 -storepass %pwd%

:: generate pkcs key
echo generate pkcs key
"%JAVA_HOME%\bin\keytool" -genkeypair -alias %alias% -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore %pkcs-keyfile% -validity 3650 -storepass %pwd%
::"$JAVA_HOME/bin/keytool" -genkeypair -alias bcephal-alias_ -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore bcephalsecurity.p12 -validity 3650 -storepass _bcphlD_.541

::Convert a JKS keystore into PKCS12
echo Convert a JKS keystore into PKCS12
"%JAVA_HOME%\bin\keytool" -importkeystore -srckeystore %jks-keyfile% -destkeystore %pkcs-keyfile% -deststoretype pkcs12




::Convert keystorep.12 file to keystore
::echo Convert keystorep.12 file to keystore
::"%JAVA_HOME%\bin\keytool" -importkeystore -srckeystore %pkcs-keyfile% -srcstoretype pkcs12 -destkeystore %jks-keyfile% -deststoretype JKS
::Create key
::echo Create key
::openssl pkcs12 -in %pkcs-keyfile% -nocerts -nodes -out %bcephal-keyfile%

::openssl pkcs12 -in bcephalsecurity.p12 -nocerts -nodes -out bcephalsecurity.key

::Create certificate
::echo Create certificate
::openssl pkcs12 -in %pkcs-keyfile% -nokeys -out %bcephal-crtfile%

::openssl pkcs12 -in bcephalsecurity.p12 -nokeys -out bcephalsecurity.crt

:: exporter certificat
::  openssl pkcs12 -in bcephal-keystore.p12 -out bcephal-keystore.crt -nodes

::%JAVA_HOME18%/bin/keytool -trustcacerts -keystore "D:/BCEPHAL/V08/tools/sts-4.15.1.RELEASE/plugins/org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_17.0.3.v20220515-1416/jre/lib/security/cacerts" -storepass changeit -importcert -alias bcephal1 -file "D:\BCEPHAL\V08\dev\server\bcephal-gateway-service\src\main\resources\cert-auto-signed\bcephalsecurity.crt"
::%JAVA_HOME18%/bin/keytool -trustcacerts -keystore "D:/BCEPHAL/V08/tools/sts-4.15.1.RELEASE/plugins/org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_17.0.3.v20220515-1416/jre/lib/security/cacerts" -storepass changeit -importcert -alias bcephal2 -file "D:\BCEPHAL\V08\dev\server\bcephal-gateway-service\src\main\resources\cert-auto-signed\bcephalsecurity-ca-cert.crt"

::%JAVA_HOME18%/bin/keytool -trustcacerts -keystore "%JAVA_HOME18%/lib/security/cacerts" -storepass changeit -importcert -alias bcephal -file "D:\BCEPHAL\V08\tools\data-cle\bcephalsecurity.crt"
::%JAVA_HOME21%/bin/keytool -trustcacerts -keystore "%JAVA_HOME21%/lib/security/cacerts" -storepass changeit -importcert -alias bcephal -file "D:\BCEPHAL\V08\tools\data-cle\bcephalsecurity.crt"
::"$JAVA_HOME/bin/keytool" -trustcacerts -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit -importcert -alias bcephal -file star_finflag_cloud.crt
::"$JAVA_HOME/bin/keytool" -delete -alias bcephal -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit



:: bonne configuration pour la creation d'un certificate privé
:: sudo $JAVA_HOME/bin/keytool -genkey -keyalg RSA -keystore bcephal-keystore.jks -validity 3650 -keysize 2048 -alias 173.249.47.89 -ext san=ip:173.249.47.89
:: sudo $JAVA_HOME/bin/keytool -importkeystore -srckeystore bcephal-keystore.jks -destkeystore bcephal-keystore.p12 -deststoretype pkcs12
:: sudo openssl s_client -showcerts -connect 173.249.47.89:8383 < /dev/null | openssl x509 -outform PEM > bcephal-keystore.pem
:: sudo $JAVA_HOME/bin/keytool -import -trustcacerts -alias 173.249.47.89 -keystore $JAVA_HOME/lib/security/cacerts -file bcephal-keystore.pem -storepass changeit -noprompt
:: sudo openssl pkcs12 -in bcephal-keystore.p12 -nocerts -nodes -out bcephal-keystore.key
:: sudo openssl pkcs12 -in bcephal-keystore.p12 -nokeys -out bcephal-keystore.crt
:: windows
:: "%JAVA_HOME%/bin/keytool" -genkey -keyalg RSA -keystore bcephal-keystore.jks -validity 3650 -keysize 2048 -alias 94.176.99.136 -ext san=ip:94.176.99.136
:: "%JAVA_HOME%/bin/keytool" -importkeystore -srckeystore bcephal-keystore.jks -destkeystore bcephal-kc-keystore.p12 -deststoretype pkcs12
:: "C:\Program Files\OpenSSL-Win64\bin\openssl" s_client -showcerts -connect 94.176.99.136:8181 > D:\bcephalv8\temp.txt
:: "C:\Program Files\OpenSSL-Win64\bin\openssl" x509 -outform PEM -in D:\bcephalv8\temp.txt -out D:\bcephalv8\bcephal-keystore.pem
:: "%JAVA_HOME%/bin/keytool" -import -trustcacerts -alias 94.176.99.136 -keystore "%JAVA_HOME%/lib/security/cacerts" -file D:\bcephalv8\bcephal-keystore.pem -storepass changeit -noprompt