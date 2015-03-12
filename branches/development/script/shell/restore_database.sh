#!/bin/sh 
#Run MYSQLDUMP to restore MPA database from a dump file
#Script needs the absolute path to the dumped .sql file as an argument
echo off
echo restoring metaprot database from file: $1 ...
mysql -u root -p metaprot < $1
echo ... finished restoring database.
