//
// Initializes the database, pushing the list of books.
//
package reviewer.procedures;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;

public class Initialize extends VoltProcedure {
    // Check if the database has already been initialized
    public final SQLStmt checkStmt = new SQLStmt("SELECT COUNT(*) FROM books;");

    // Inserts a book
    public final SQLStmt insertBookStmt = new SQLStmt("INSERT INTO books (book_name, book_id) VALUES (?, ?);");

    public long run(int maxBooks, String books) {

        String[] bookArray = books.split(",");

        voltQueueSQL(checkStmt, EXPECT_SCALAR_LONG);
        long existingBookCount = voltExecuteSQL()[0].asScalarLong();

        // if the data is initialized, return the book count
        if (existingBookCount != 0)
            return existingBookCount;

        // initialize the data
        for (int i = 0; i < maxBooks; i++)
            voltQueueSQL(insertBookStmt, EXPECT_SCALAR_MATCH(1), bookArray[i].trim(), i + 1);
        voltExecuteSQL();

        return maxBooks;
    }
}
