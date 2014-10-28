Data access layer for big data - In memory databases
=====================================================

Scope
------
* Setup VoltDB Community Edition.
* VoltDB features - Tables, stored procedures - inline and user defined, views
* Benchmark VoltDB.
* VoltDB stored procedures to add to the built-in list of functions ex: UDFs like group_concat or listagg and UDTFs like explode

Module - voltdb_benchmark - Description
---------------------------------------
This application has 

- 2 tables : books and reviews  and 
- 4 procedures : Initialize, Review, Results and ReviewsForBook.

### Tables

Books tables holds books and reviews tables hold reviews against those
books given by reviewers identified by unique email ids.

### Procedures

- Initialize - Initializes the books table with 6 books
- Review - Allows you to create a review after doing following validations.
    - If the review is for a valid book.
    - If the reviewer has reviewed more books than permissible.
- Results - Selects the top book based on number of reviews
- ReviewsForBook - Gives the count of reviews for a given book. Implemented using an inline query

References
----------
- Download VoltDB Community Edition  - http://downloads.voltdb.com/technologies/server/LINUX-voltdb-4.8.tar.gz
- Maven Download - ftp://mirror.reverse.net/pub/apache/maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz
- Install VoltDB client jar into local maven repository - http://blog.tingri.me/?p=254
- Voltdb Voter sample application - https://github.com/VoltDB/voltdb/tree/master/examples/voter

