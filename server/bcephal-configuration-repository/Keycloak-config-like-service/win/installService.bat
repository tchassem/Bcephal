@echo off
set serviceName=Bcephal-keycloak-service-8
set serviceNamexml=keycloak-service.xml
REM testing at cmd : sc query "%serviceName%" | findstr RUNNING
REM "%serviceName%" is the name of Service for sample
sc query "%serviceName%" | findstr RUNNING
if %ERRORLEVEL% == 2 goto trouble
if %ERRORLEVEL% == 1 goto install
if %ERRORLEVEL% == 0 goto started
echo unknown status
goto install
:trouble
echo Oh noooo.. trouble mas bro
goto install
:started
echo "Bcephal Keycloak Service (%serviceName%)" is started
net stop "%serviceName%"
echo "Bcephal Keycloak Service (%serviceName%)" is stopped
goto install
:install
sc query | findstr /C:"SERVICE_NAME: %serviceName%"
if %ERRORLEVEL% == 0 do (
	WinSW\WinSW.exe uninstall %serviceNamexml%
)
WinSW\WinSW.exe install %serviceNamexml%
echo Starting Bcephal Keycloak Service
net start "%serviceName%"
goto end
:erro
echo Error please check your command.. mas bro 
goto end

:end
