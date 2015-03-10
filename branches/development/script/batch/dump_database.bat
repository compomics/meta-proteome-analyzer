:: Run MYSQLDUMP to create a complete backup of MPA relevant database files
echo off
set SAVESTAMP=%DATE:-=_%
set SAVESTAMP=metaprot_dump_%SAVESTAMP: =%.sql
echo dumping metaprot database to file: %SAVESTAMP% ...
\xampp\mysql\bin\mysqldump.exe -u root -p --add-drop-database metaprot > \metaprot\dump\%SAVESTAMP%
echo ... finished database dump.