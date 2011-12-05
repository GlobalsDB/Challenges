This is an attempt to fulfil some of the requests laid out in the 2nd challenge.

The plan was to serve or write log entries to \log_src\log.txt using the \log_src\logFeeder class, which would be picked up by the logReader class.

The \log_src\logFeeder reads from log_src.txt. Running \log_src\LogFeeder.bat runs the logFeeder to write the entries. 

The \LogReader.bat in the root folder launches the user program to read in the entries in \log_src\log.txt and store them in a database. 
Launch the LogReader.bat and press 'Read log file'. This can take a few minuets.

You can then use the find fields and button to find entries.
The Next and Prev button are for paging through the database. They do not apply to the search results. Pressing Next and PRev will display 10 entries at a time.

That is as far as I got.
