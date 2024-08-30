@echo off 
"%programfiles(x86)%\Google\Chrome\Application\chrome.exe" --remote-debugging-port=9222 --user-data-dir="D:\PROJECTS\BCEPHAL-MICROSERVICE\dev\client\blazor-chrome-debug" http://localhost:5000/bcephal 
