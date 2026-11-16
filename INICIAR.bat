@echo off
chcp 65001 >nul
title LotaMenos API
cd /d "%~dp0"

rem Usa o JDK 21 instalado em .jdks (se existir); senão usa o Java do sistema
if exist "%USERPROFILE%\.jdks\temurin-21.0.13\bin\java.exe" set "JAVA_HOME=%USERPROFILE%\.jdks\temurin-21.0.13"

echo ============================================================
echo   LotaMenos API - iniciando (banco em memoria, perfil dev)
echo   Na primeira vez pode levar alguns minutos.
echo   O navegador abre sozinho quando a API estiver pronta.
echo   Para PARAR a API, feche esta janela.
echo ============================================================

rem Abre o navegador automaticamente quando a API responder
start "" /min powershell -NoProfile -WindowStyle Hidden -Command "for($i=0;$i -lt 300;$i++){try{Invoke-WebRequest -UseBasicParsing http://localhost:8080/actuator/health -TimeoutSec 2 | Out-Null; Start-Process 'http://localhost:8080'; break}catch{Start-Sleep 2}}"

call mvnw.cmd -q spring-boot:run -Dspring-boot.run.profiles=dev
pause
