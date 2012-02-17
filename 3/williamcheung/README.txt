For the third Globals Challenge, we were asked to explore innovative use of data to create a new market. The idea I came up with is "Globals for the Environment", using the world's most flexible database, Globals, to gain valuable insights into environmental issues of concern to the planet. In this project I developed the "Globals Big Geospatial Data MapReducer" (GGSMR) to perform big data analytics on environmental datasets which are valuable for research by governments, academia, and environmental groups.

The use case for GGSMR I chose to address was the more than 5.2 million measurements of surface carbon dioxide obtained over the global oceans from 1957-2011. I used GGSMR to do a MapReduce analysis of this data, exported the results from Globals, and visualized those results in a web app I developed: http://globals-for-the-environment.herokuapp.com. For a deep dive into the process and the opportunity for Globals, please read my paper "Globals for the Environment - Gaining Insights into CO2 Levels on the Global Oceans over the last 50+ years", at http://globals-for-the-environment.herokuapp.com/README.pdf (also included in this project).

The Code
--------

GGSMR - the Globals Big Geospatial Data MapReducer
--------------------------------------------------

The GGSMR project is under the globals-geospatial-mapreducer folder. Because this is a Swing app built using Groovy, to run GGSMR you first need to install Groovy from http://groovy.codehaus.org/Download, then go to the src subfolder and simply run:

groovy Main

An Eclipse .project file is also provided which you can import into Eclipse. Then assuming you have the Groovy Eclipse plugin installed, you can select Main.groovy and do Run As/Groovy Script.

Please read the README.pdf at http://globals-for-the-environment.herokuapp.com/README.pdf for step-by-step instructions on how to use GGSMR to MapReduce the LDEO 5.2 million row dataset in Globals and export the results from Globals. "LDEO" refers to the Lamont-Doherty Earth Observatory whose researchers have been collecting this data since 1957. Because their dataset is large and needs to be downloaded and unzipped, I include a subset of the data in this project which you can alternatively use: LDEO_Database_V2010_subset.txt. This subset consists of the first 25 data rows and can be used for an instant demo of GGSMR and its features. The time for the MapReduce job to complete on the 5.2 million row dataset will vary depending on your hardware. Note that the LDEO dataset is currently updated yearly, and GGSMR was tested with the latest version (which although named as "2010", also includes measurements from 2011).

GGSMR uses Groovy to access the Globals Java API so you need to have your environment set up for the Java API as explained at http://globalsdb.org/getting-started/quickstart.


Globals for the Environment - the visualization of results exported from GGSMR
------------------------------------------------------------------------------

The Globals for the Environment project is under the globals-for-the-environment folder. This is a node.js app which I deployed to Heroku at http://globals-for-the-environment.herokuapp.com. Note that unlike GGSMR, this project does not use Globals directly. It is merely a way to visualize the results of GGSMR. A package.json is provided defining the dependencies of the app (along with a Procfile for deployment to Heroku). To run the app, simply do:

node app.js


Enjoy!

William Cheung
Toronto, February 17, 2012
Globals Community member: wcheung
