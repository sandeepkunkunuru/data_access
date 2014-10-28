//
// Initializes the database, pushing the list of books.
//
package procedures;

import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltTableRow;

public class GroupConcat extends VoltProcedure {
    public String run(String tableName, String column, String filter) {
        StringBuilder query = new StringBuilder();

        query.append(" select ").append(column);
        query.append(" from ").append(tableName);
        query.append((filter.isEmpty()? "" : (" where " + filter)));

        voltQueueSQLExperimental(query.toString());
        VoltTable rows =  voltExecuteSQL()[0];

        StringBuilder result = new StringBuilder();

        for(int i =1; i <= rows.getRowCount(); i++){
            VoltTableRow row = rows.fetchRow(i);
            result.append(row.getString(1));
        }

        return result.toString();
    }
}
