/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao.ds;

import hk.hku.cecid.piazza.commons.util.StringUtilities;


/**
 * SQLBuilder is a tool which can construct prepared SQL statements from the 
 * specified table, keys, and columns.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class SQLBuilder {

    private String table;
    private String[] keys;
    private String[] columns;
    
    /**
     * Creates a new instance of SQLBuilder.
     * 
     * @param table the table name.
     * @param key the comma delimited set of keys of the table.
     * @param columns the columns of the table.
     */
    public SQLBuilder(String table, String key, String[] columns) {
        this(table, StringUtilities.tokenize(key, ", "), columns);
    }
    
    /**
     * Creates a new instance of SQLBuilder.
     * 
     * @param table the table name.
     * @param keys the keys of the table.
     * @param columns the columns of the table.
     */
    public SQLBuilder(String table, String[] keys, String[] columns) {
        this.table = table;
        this.keys = keys;
        this.columns = columns;
    }

    /**
     * Gets the SELECT statement.
     * 
     * @return the SELECT statement.
     */
    public String getSelectStatement() {
        String stmt = "SELECT ";
        stmt += StringUtilities.concat(columns, ", ") + " FROM " + table + " WHERE ";
        stmt += StringUtilities.concat(keys, "", "=?", " AND ");
        return stmt;
    }
    
    /**
     * Gets the INSERT statement.
     * 
     * @return the INSERT statement.
     */
    public String getInsertStatement() {
        return getInsertStatement(columns);
    }
    
    /**
     * Gets the INSERT statement.
     * 
     * @param cols the columns to be updated.
     * @return the INSERT statement.
     */
    public String getInsertStatement(String[] cols) {
        String stmt = "INSERT INTO " + table + " (";
        stmt += StringUtilities.concat(cols, ", ") + ") VALUES (";
        String[] params = StringUtilities.toArray("?", cols==null? 0:cols.length);  
        stmt += StringUtilities.concat(params, ",") + ")";
        return stmt;
    }
    
    /**
     * Gets the UPDATE statement.
     * 
     * @return the UPDATE statement.
     */
    public String getUpdateStatement() {
        return getUpdateStatement(columns);
    }

    /**
     * Gets the UPDATE statement.
     * 
     * @param cols the columns to be updated.
     * @return the UPDATE statement.
     */
    public String getUpdateStatement(String[] cols) {
        String stmt = "UPDATE " + table + " SET ";
        stmt += StringUtilities.concat(cols, "", "=?", ", ") + " WHERE ";
        stmt += StringUtilities.concat(keys, "", "=?", " AND ");
        return stmt;
    }

    /**
     * Gets the DELETE statement.
     * 
     * @return the DELETE statement.
     */
    public String getDeleteStatement() {
        String stmt = "DELETE FROM " + table + " WHERE ";
        stmt += StringUtilities.concat(keys, "", "=?", " AND ");
        return stmt;
    }
    
    /**
     * Gets the columns.
     * 
     * @return the columns.
     */
    public String[] getColumns() {
        return columns;
    }
    
    /**
     * Gets the keys.
     * 
     * @return the keys.
     */
    public String[] getKeys() {
        return keys;
    }
    
    /**
     * Gets the table name.
     * 
     * @return the table name.
     */
    public String getTable() {
        return table;
    }
    
    /**
     * Checks if this SQL builder is valid. It is valid if and only if the table, 
     * columns, and keys have been defined.
     * 
     * @return true if this SQL builder is valid.
     */
    public boolean isValid() {
        if (table == null || table.length()==0) {
            return false;
        }
        else if (columns == null || columns.length == 0) {
            return false;
        }
        else if (keys == null || keys.length == 0) {
            return false;
        }
        else {
            return true;
        }
    }
}
