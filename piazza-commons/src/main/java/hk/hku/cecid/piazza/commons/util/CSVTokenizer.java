/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import hk.hku.cecid.piazza.commons.io.IOHandler;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CSVTokenizer is a tokenizer which can parses a given CSV and represent its
 * content as multiple rows and columns.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class CSVTokenizer {

    private StringTokenizer rows;

    private String[]        columns;

    private Pattern delimiterPattern = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))");
    
    private String          lineFeed = System.getProperty("line.separator");

    /**
     * Creates a new instance of CSVTokenizer. 
     * 
     * @param csv the CSV content.
     * @throws IOException if unable to read the content from the given reader.
     */
    public CSVTokenizer(Reader csv) throws IOException {
        this(IOHandler.readString(csv));
    }

    /**
     * Creates a new instance of CSVTokenizer. 
     * 
     * @param csv the CSV content.
     */
    public CSVTokenizer(String csv) {
        rows = new StringTokenizer(csv == null ? "" : csv, "\r\n");
    }

    /**
     * Checks if there are any more rows in this tokenizer.
     * 
     * @return true if there are more rows in this tokenizer.
     */
    public boolean hasMoreRows() {
        return rows.hasMoreTokens();
    }

    /**
     * Proceeds to tokenize the next row. 
     */
    public void nextRow() {
        String row = rows.nextToken();
        while (isOpenRow(row)) {
            row += lineFeed + rows.nextToken();
        }
        tokenizeRow(row);
    }

    /**
     * Gets the number of columns in the current row.
     * 
     * @return the number of columns.
     */
    public int getColumnCount() {
        return columns == null ? 0 : columns.length;
    }

    /**
     * Gets a column's value.
     * 
     * @param pos the position of the column.
     * @return the specified column's value.
     */
    public String getColumn(int pos) {
        return columns == null || pos >= columns.length ? null : columns[pos];
    }

    /**
     * Checks if the given row is an open row. 
     * 
     * @param r the row for checking.
     * @return true if the given row is an open row.s
     */
    private boolean isOpenRow(String r) {
        int c = 0;
        for (int i = 0; r != null && i < r.length(); i++) {
            if ('\"' == r.charAt(i)) {
                c++;
            }
        }
        return c % 2 > 0;
    }

    /**
     * Tokenizes the given rows into columns.
     * 
     * @param row the row to be tokenized.
     */
    private void tokenizeRow(String row) {
        ArrayList tokens = new ArrayList();
        Matcher m = delimiterPattern.matcher(row);

        int index = 0;
        while(m.find()) {
            String token = row.subSequence(index, m.start()).toString();
            tokens.add(normalize(token));
            index = m.end();
        }
        tokens.add(normalize(row.substring(index, row.length())));
        
        columns = (String[])tokens.toArray(new String[tokens.size()]);
    }

    /**
     * Normalizes the given string by converting back the double quotes.
     * 
     * @param s the string to be normalized.
     * @return the normalized string.
     */
    private String normalize(String s) {
        return s.replaceAll("^(?s)[\\s]*\"(.*)\"[\\s]*$", "$1").replaceAll("\"\"", "\"");
    }
}