@echo off
setlocal
set PATH=%JAVA_HOME%\bin
set classpath=utilities-3.0.18.jar;mysql-connector-java-3.1.12-bin.jar;log4j-1.2.12.jar;.

@ECHO ON
java com.compomics.util.db.DBAccessorGenerator %*
endlocal
