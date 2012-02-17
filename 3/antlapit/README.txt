Document - oriented database based on GlobalsDB
Current name is GLodocs = GlobalsDB + Documents
GlodocsAPI - source code for API that implements document-oriented paradigm
GlodocsInterface - Swing GUI to work with Glodocs
GlodocsInstallation - Console installation project

To install Glodocs make these steps:
1) Install GlobalsDB
2) StartUP GlobalsDB
3) Go to folder with install.jar
4) execute 
java -Xss1024k -cp install.jar com.xlab.glodocs.install.Installation <username> <password>
where <username> and <password> is optional parameters (login and password of super user)
5) navigate to folder with client.jar
6) make sure that in this folder you have folder with images (if not copy them from GlodocsInterface)
7) execute
java -Xss1024k -cp client.jar com.xlab.glodocs.gui.main.Main
8) enjoy Glodocs

Main Features of Glodocs:
1) Document-oriented paradigm
2) Indexing
3) Search mechanism
4) Swing GUI interface
5) Access managing (only in API)
6) User managing (only in API)


