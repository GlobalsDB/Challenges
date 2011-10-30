This is a very quick attempt at an entry to this challenge.  Unfortunately limited time has meant that this is just uses a simple and basic command line UI, and searching is currently limited to author name.


The Flickr records are stored in a global named ^flickr

To clear down the ^flickr global:

node flickrClear


To update the stored Flickr data using the RSS feed:

node updateFlickr


To query the stored Flickr data by author name:

node flickrQuery <name or prefix>

eg

node flickrQuery a

This will retrieve and list all the records for author names starting with "a"

or 

node flickrQuery smith

This will retrieve and list all the records for author names starting with "Smith"



Note: updateFlickr uses the ewdDOM module: installation instructions at:

https://github.com/robtweed/ewdDOM



Rob Tweed
