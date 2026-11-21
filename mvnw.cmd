@echo off
setlocal
set "DIR=%~dp0"
if exist "%DIR%..\.tools\apache-maven-3.9.16\bin\mvn.cmd" (
    "%DIR%..\.tools\apache-maven-3.9.16\bin\mvn.cmd" %*
) else if exist "C:\Users\Nimit\.tools\apache-maven-3.9.16\bin\mvn.cmd" (
    "C:\Users\Nimit\.tools\apache-maven-3.9.16\bin\mvn.cmd" %*
) else (
    mvn %*
)

