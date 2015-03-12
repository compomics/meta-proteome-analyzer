#!/bin/sh 
#Run MYSQLDUMP to create a complete backup of MPA relevant database files
echo off
savestamp=$(date "+%Y_%m_%d")
savestamp=metaprot_dump_$savestamp.sql
echo dumping metaprot database to file: $savestamp ...
mkdir /scratch/metaprot/dump/
mysqldump -u root -p --add-drop-database metaprot > /scratch/metaprot/dump/$savestamp
echo ... finished database dump.
