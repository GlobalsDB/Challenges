Intersystems GlobalsDb (http://globalsdb.org) Challenge #2 solution.

It has two modules:
1. A WAR (Web application ARchive), which contains the actual solution. Can be deployed seperatly into tomcat-7 container, and used as a web-application.
2. A runner module, which is a java executable. The executable runs an embedded tomcat with the first module deployed in it, and launches an embedded Swing browser as a UI. That way User can judge the solution without needing to establish an external server, as it wraps the web application to become an desktop application.

