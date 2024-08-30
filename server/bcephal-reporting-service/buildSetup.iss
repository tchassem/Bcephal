; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "B-Cephal-Reporting-Service" 
#define MyAppVersion "8.0.0"
#define MyAppPublisher "Moriset"
#define MyAppURL "http://www.moriset.com/"
#define MyAppExeName "B-Cephal-Reporting-Service.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{8DCD7CB1-9D4E-AC45-36A5-F6BDAE827656}
AppName=B-Cephal Reporting Service
AppVerName=B-Cephal Reporting Service
AppPublisher=Moriset & co
AppPublisherURL=http://www.bcephal.com/
AppSupportURL=http://www.bcephal.com/support
AppUpdatesURL=http://www.bcephal.com/update
DefaultDirName={pf}\Moriset\Bcephal\8\Reporting-Service
DefaultGroupName=Moriset\Bcephal\8\Reporting-Service
OutputDir=.\
OutputBaseFilename=B-Cephal Reporting Service
Compression=lzma
SolidCompression=true
SetupIconFile=bcephal.ico
UsePreviousAppDir=no


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "french"; MessagesFile: "compiler:Languages\French.isl"
Name: "dutch"; MessagesFile: "compiler:Languages\Dutch.isl"
Name: "german"; MessagesFile: "compiler:Languages\German.isl"


[Files]
Source: ".\build\libs\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: ".\resources\*"; Excludes: "*.sh"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: ".\build\resources\main\bootstrap.properties"; DestDir: "{app}"; Flags: onlyifdoesntexist
;Source: ".\build\resources\main\application.properties"; DestDir: "{app}"; Flags: onlyifdoesntexist
;Source: ".\..\..\..\cots\jdk-14.0.2\*"; DestDir: "{app}\jdk14"; Flags: ignoreversion recursesubdirs createallsubdirs


[Icons]
Name: {group}\{cm:UninstallProgram,Bcephal Reporting Service}; Filename: {uninstallexe}

[Run]  
 Filename: {app}\installService.bat; Description: "Run Bcephal Reporting Service like Windows Service"; Flags: runhidden runascurrentuser hidewizard postinstall waituntilidle skipifsilent 
 
[UninstallDelete]
Name: {app}\*; Type: filesandordirs