//
// Initializes the database, pushing the list of books.
//
package procedures;

import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltTableRow;
import org.voltdb.VoltType;
import util.StringUtils;

public class GroupConcat extends VoltProcedure {
    public VoltTable[] run(String tableName, String column, String filter, String separator) {
        StringBuilder query = new StringBuilder();

        query.append(" select ").append(column);
        query.append(" from ").append(tableName);
        query.append((filter.isEmpty()? "" : (" where " + filter)));

        voltQueueSQLExperimental(query.toString());
        VoltTable rows =  voltExecuteSQL()[0];

        String[] result = new String[rows.getRowCount()];

        for(int i =0; i < rows.getRowCount(); i++){
            VoltTableRow row = rows.fetchRow(i);
            result[i] = row.getString(0);
        }

        VoltTable p = new VoltTable(new VoltTable.ColumnInfo("result", VoltType.STRING));
        p.addRow(new Object[]{StringUtils.join(separator, result)});

        return new VoltTable[]{p};
    }
}
