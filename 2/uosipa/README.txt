Log viewer - solution for Intersystems GlobaDB Challenge #2.

Supported features:
- viewing static Apache httpd access log files
- viewing static Apache Tomcat log files
- filtering logs by severity
- viewing streaming logs from server side


Installation:
1. Install GlobalsDB.
    - Make sure GLOBALS_HOME is set properly
    - Make sure PATH includes %GLOBALS_HOME%\bin
2. Install globalsdb.jar to local maven repository with following command.
    mvn install:install-file -Dfile=globalsdb.jar -DgroupId=com.intersys.globals -DartifactId=globalsdb -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
3. Build project with following command "mvn clean package".
4. Deploy web/target/globalsdb.war into Tomcat Manager.
5. Enjoy.

You can try it:
http://globals.uosipa.com

Dmitry Levshunov