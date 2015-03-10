:: log into mysql, create metaprot database and use structure scripts
echo off
echo re-creating empty metaprot database ...
\xampp\mysql\bin\mysql.exe -u root -p < \metaprot\reset.sql
echo ... create metaprot database finished.