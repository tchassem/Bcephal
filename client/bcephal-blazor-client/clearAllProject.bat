@echo off
Echo Try to Clear All Projects
for /f %%f IN ('DIR /b Bcephal.*') do cmd /c Execmd.bat %%f

