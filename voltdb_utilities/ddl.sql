-- sample books table holds the books ids and names
CREATE TABLE books
(
  book_id integer     NOT NULL
, book_name   varchar(50) NOT NULL
, CONSTRAINT PK_books PRIMARY KEY
  (
    book_id
  )
);
PARTITION TABLE books ON COLUMN book_id;

-- stored procedures
CREATE PROCEDURE FROM CLASS procedures.GroupConcat;

