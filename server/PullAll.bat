@echo off
Echo Try to Pull All Projects
for /f %%f IN ('DIR /b bcephal-*') do cmd /c Execmd.bat %%f
