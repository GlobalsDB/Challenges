globalsdb/java application

Source code contains Eclipse project. Unfortunatelly i have no time to make release version
My solution works with apache access log file
To Run solution:

1) Install Globals
2) Run Installation.java with 2 parameters: Apache access log file path & date format in Apache access log file
3) Run ApacheGlobalsFrame.java
You can select Apache Log File and then Push Button "Load File"
Than you should refresh data in grid. 
If data Changed and you haven't refresh it yet - You can see that data is not valid
Also you can set Data reload on timer.

Also you can choose Dynamic mode and after loading File my solution will listen to changes of apache access log file.
In last minutes i got a problem with filters in the interface, but you can try them in Utils (Class SearchEngine) - there they works
