Intersystems GlobalsDb (http://globalsdb.org) Challenge #2 solution.

IRunning the application:
> java -jar runner.jar

You can generate fake data, to test the application. In order to generate data:
> java -jar runner.jar -generate logfile.log -records 10000

In order to load file, click on File->Open, and enter a full path for the file to load. Please notice that in case the file size is > 10mb, the process of loading might take several mimutes.

I've added generated data as well - 1k and 100k records log files. 

BTW - I've tested it on a log file of 1000k, and it went pretty smoothly.

The keen eyed user might see there are two modules:
1. A WAR (Web application ARchive), which contains the actual solution. Can be deployed seperatly into tomcat-7 container, and used as a web-application.
2. A runner module, which is a java executable. The executable runs an embedded tomcat with the first module deployed in it, and launches an embedded Swing browser as a UI. That way User can judge the solution without needing to establish an external server, as it wraps the web application to become an desktop application.

In the project, I used several open-source projects:
Apache-commons (IO, CLI, Codec, Lang)
Joda-Time 
DJProject (for embeding webbrowser into a desktop application)
primefaces
gson (Goolge json library)
Lombok (makes java easier)
Weld-servelt (JavaEE for servlet containers)
and probably several more.

I've used jrebel-social as well, to save time from redeployments. It is free and wonderful!

Yonatan Graber