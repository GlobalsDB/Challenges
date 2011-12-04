Introduction
Сlickstream Monitor is an application that allows to track user clickstream activity. When user clicks on DOM-element with Id, application sends saving request with event object. 

How it’s works
A some kind of javascript library (using JQuery) was developed, it’s can be included at any web page. Web page ‘Click Me Application’ was constructed for manual testing user’s clickstream.
Also, was prepared auto mode, (by clicking “Start Highload") JS function will emulate user’s  clicks (every 300ms).
At this page you can click on any elements, and see the results on Monitor page.
Monitor page works in two modes:
historical. Shows all history of  clickstream (limited by last 1000 records), and allows to filter it by(element-type, element id, ip address).
real-time. Continious refreshing list of last added items on page.

Also there is most clickable items chart (Statistics page).

Key features:
An object-globals mapping framework was developed for saving and loading data. Objects should inherit from Persistent class to use it. All data is stored in global ObjectClassName+”D”, indices ObjectClassName+ “I”
Framework maintain indices for better search mechanism (using intersection of indices and searching by range)
(And was made workaround for long text subscript (> 512 symbols)
Graphic data presentation - a statisitics page (using index for better performance).

Futher improvement
Application may be extended for more deeper analysis of user activity for egronomics improvement. Save combinations of clicked elements for UI usability improvement.(Group most often clicked elements together).
Also, it would be perfect to allow to replay recorded clickstream sessions, on client, for testing purposes for example. Like selenium.

Installation:
1. Install GlobalsDB
Set the following environment variables:
GLOBALS_HOME - Pathname of the root of the Globals installation (e.g. C:\Globals\)
PATH - Must include %GLOBALS_HOME%\bin
CLASSPATH - Must include %GLOBALS_HOME%\dev\java\lib\JDK16\globalsdb.jar
2. Install playframework
Download  play framework (http://download.playframework.org/releases/play-1.2.3.zip), extract to folder %PlayFolderRoot%

3. Copy application into folder %AppFolder%
4. Change current directory to %AppFolder%