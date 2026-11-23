@echo off
REM IA Vinto - doble clic para instalar la app en el celular conectado
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0instalar_en_celular.ps1" %*
pause
