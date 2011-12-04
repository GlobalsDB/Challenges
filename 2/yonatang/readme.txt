Intersystems GlobalsDb (http://globalsdb.org) Challenge #2 solution - Log viewer

Main features:
==============
Multi-tab log file viewing
Filter by IP and/or timestamp range
Right-click to follow request from a specific IP
Customable number of records to show per page
Automatic refresh, when you view the tail of the log (the last page)
Can handle very large files (tested it on 1,000,000 records file)
Can be run as a desktop application, or deployed into a Servlet container.

Running the application:
========================
Get the runner.jar file, by one of the following ways:
1. Pull the project, and get the runner.jar file from the root (Challenges/master/2/yonatang/runner.jar), or 
2. Build the application (mvn clean install), and get the jar file from runner/target/runner.jar
Then:
> java -jar runner.jar

You can generate fake data, to test the application. In order to generate data:
> java -jar runner.jar -generate logfile.log -records 10000

Please notice that in case the file size is > 10mb, the process of loading might take several mimutes.
You can find sample data file with 1k and 100k log files at https://github.com/GlobalsDB/Challenges/raw/master/2/yonatang/sample-data.zip

The keen eyed user might see there are two modules:
1. A WAR (Web application ARchive), which contains the actual solution. Can be deployed seperatly into tomcat-7 container, and used as a web-application.
2. A runner module, which is a java executable. The executable runs an embedded tomcat with the first module deployed in it, and launches an embedded Swing browser as a UI. That way User can judge the solution without needing to establish an external server, as it wraps the web application to become an desktop application.

Open source used
================
In the project, I used several open-source projects:
Apache-commons (IO, CLI, Codec, Lang)
Joda-Time (much better than java's calendar)
DJProject (for embeding webbrowser into a desktop application)
primefaces (JSF component library)
gson (Goolge json library)
Lombok (makes java easier)
Weld-servelt (JavaEE for servlet containers)
and probably several more.

I've used jrebel-social as well, to reduce the amount of redeployments required. It is free and wonderful!

License
=======
This software licensed as LGPL 3 (http://www.gnu.org/licenses/lgpl.html) 

Yonatan Graber