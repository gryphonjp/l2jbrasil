@echo off
title Login Server Console
:start
echo Starting L2J Login Server.
echo.
java -Xmx256m -cp ./lib/* com.l2jbr.loginserver.L2LoginServer
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin Restart ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly
echo.
:end
echo.
echo server terminated
echo.
pause
