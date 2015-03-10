-- MYSQL command lines that reset the MPA server database
-- Run from command line using mysql.exe i. e. "shell> mysql -u root -p < \metaprot\reset.sql"
DROP DATABASE IF EXISTS metaprot;
CREATE DATABASE metaprot;
USE metaprot
source \metaprot\metaprot.sql
source \metaprot\taxonomy.sql
