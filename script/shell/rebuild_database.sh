#!/bin/sh 
#log into mysql, create metaprot database and use structure scripts
echo off
echo re-creating empty metaprot database ...
mysql -u root -p < /scratch/metaprot/sql/reset.sql
echo ... create metaprot database finished.
