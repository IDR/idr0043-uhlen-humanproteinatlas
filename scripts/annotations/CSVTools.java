/*
 *------------------------------------------------------------------------------
 *  Copyright (C) 2018 University of Dundee. All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */

package annotations;

import static annotations.BasicCSVUtils.join;
import static annotations.BasicCSVUtils.split;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Some methods making it easier to handle CSV data.
 * 
 * @author Dominik Lindner &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:d.lindner@dundee.ac.uk">d.lindner@dundee.ac.uk</a>
 */
public class CSVTools {

    /**
     * Reads a text file
     * 
     * @param inFile
     *            The file to read
     * @return The content
     * @throws IOException
     */
    public static String readFile(String inFile) throws IOException {
        BufferedReader r = new BufferedReader(new FileReader(inFile));
        StringBuilder output = new StringBuilder();
        String line = null;
        while ((line = r.readLine()) != null) {
            output.append(line + "\n");
        }
        r.close();
        return output.toString();
    }

    /**
     * Writes a text file
     * 
     * @param outFile
     *            The file to write
     * @param content
     *            The content
     * @throws IOException
     */
    public static void writeFile(String outFile, String content)
            throws IOException {
        BufferedWriter w = new BufferedWriter(new FileWriter(outFile));
        w.write(content);
        w.close();
    }

    /**
     * Change the format, e.g. from tab separated to comma separated. Also makes
     * sure that all rows have the some amount of columns.
     * 
     * @param input
     *            The input text
     * @param fromSep
     *            The current separator
     * @param toSep
     *            The new separator
     * @return The modified CSV string
     */
    public static String format(String input, char fromSep, char toSep) {
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        // make sure every row has exactly the same amount of columns
        // as the header
        int nCols = split(lines[0], fromSep).length;

        for (int i = 0; i < lines.length; i++) {
            String[] parts = split(lines[i], fromSep);
            String[] parts2 = new String[nCols];
            for (int n = 0; n < nCols; n++)
                if (n < parts.length)
                    parts2[n] = parts[n];
            output.append(join(parts2, toSep) + "\n");
        }

        return output.toString();
    }

    /**
     * Extract certain columns.
     * Also makes sure that each row is unique.
     * 
     * @param input
     *            The input text
     * @param columnIndex
     *            The columns to extract
     * @param separator
     *            The separator character
     * @return The separated columns
     */
    public static String extractColumns(String input, int[] columnIndex,
            char separator) {
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        HashSet<String> unique = new HashSet<String>();
        String[] headers = split(lines[0], separator);
        String[] newHeaders = new String[columnIndex.length];
        for (int i = 0; i < columnIndex.length; i++) {
            newHeaders[i] = headers[columnIndex[i]];
        }

        output.append(join(newHeaders, separator) + "\n");

        for (int l = 1; l < lines.length; l++) {
            String parts[] = split(lines[l], separator);
            String[] outline = new String[columnIndex.length];

            String tmp = "";
            for (int j = 0; j < columnIndex.length; j++) {
                outline[j] = parts[columnIndex[j]];
                tmp += parts[columnIndex[j]];
            }

            if (!unique.contains(tmp)) {
                output.append(join(outline, separator) + "\n");
                unique.add(tmp);
            }
        }
        return output.toString();
    }

    /**
     * Add a column.
     * 
     * @param input
     *            The input
     * @param sep
     *            The separator character
     * @param colIndex
     *            The column index
     * @param content
     *            The content of the column
     * @param header
     *            The header of the column
     * @return The modified CSV string
     */
    public static String addColumn(String input, char sep, int colIndex,
            String content, String header) {
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        for (int l = 0; l < lines.length; l++) {
            String parts[] = split(lines[l], sep);
            String parts2[] = new String[parts.length + 1];
            for (int j = 0; j < parts.length; j++) {
                if (j < colIndex)
                    parts2[j] = parts[j];
                else if (j == colIndex) {
                    if (l == 0) {
                        parts2[j] = header;
                    } else {
                        parts2[j] = content;
                    }
                    parts2[j + 1] = parts[j];
                } else
                    parts2[j + 1] = parts[j];
            }
            output.append(join(parts2, sep) + "\n");
        }

        return output.toString();
    }

    /**
     * Prefix/Postfix the content of each cell of a specific column
     * 
     * @param input
     *            The input
     * @param columnIndex
     *            The column index
     * @param separator
     *            The separator character
     * @param prefix
     *            The prefix text
     * @param postfix
     *            The postfix text
     * @return The modified CSV string
     */
    public static String prefixColumn(String input, int columnIndex,
            char separator, String prefix, String postfix) {
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        output.append(lines[0] + "\n"); // header

        for (int l = 1; l < lines.length; l++) {
            String parts[] = split(lines[l], separator);
            if (prefix != null && prefix.length() > 0)
                parts[columnIndex] = prefix + parts[columnIndex];
            if (postfix != null && postfix.length() > 0)
                parts[columnIndex] = parts[columnIndex] + postfix;
            output.append(join(parts, separator) + "\n");
        }

        return output.toString();
    }

    /**
     * Remove a row
     * 
     * @param input
     *            The input
     * @param rowIndex
     *            The index of the row to remove
     * @return The modified CSV string
     */
    public static String removeRow(String input, int rowIndex) {
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            if (i != rowIndex)
                output.append(lines[i] + "\n");
        }

        return output.toString();
    }

    /**
     * Remove a column
     * 
     * @param input
     *            The input
     * @param colIndex
     *            The index of the column to remove
     * @param sep
     *            The separator character
     * @return The modified CSV string
     */
    public static String removeColumn(String input, int colIndex, char sep) {
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        for (int l = 0; l < lines.length; l++) {
            String parts[] = split(lines[l], sep);
            String parts2[] = new String[parts.length - 1];
            for (int j = 0; j < parts.length; j++) {
                if (j < colIndex)
                    parts2[j] = parts[j];
                if (j > colIndex)
                    parts2[j - 1] = parts[j];
            }
            output.append(join(parts2, sep) + "\n");
        }

        return output.toString();
    }

    /**
     * Remove empty column
     * 
     * @param input
     *            The input
     * @param sep
     *            The separator character
     * @return The modified CSV string
     */
    public static String removeEmptyColumns(String input, char sep) {
        String[] lines = input.split("\n");

        HashSet<Integer> emptyColumns = new HashSet<Integer>();

        String[] parts = split(lines[0], sep);
        for (int i = 0; i < parts.length; i++)
            emptyColumns.add(i);

        for (int l = 1; l < lines.length; l++) {
            parts = split(lines[l], sep);
            for (int i = 0; i < parts.length; i++) {
                if (parts[i] != null && parts[i].trim().length() > 0)
                    emptyColumns.remove(i);
            }
        }

        List<Integer> cols = new ArrayList<Integer>(emptyColumns);
        Collections.sort(cols);
        String result = input;
        for (int i = cols.size() - 1; i >= 0; i--) {
            result = removeColumn(result, cols.get(i), sep);
        }
        return result;
    }

    /**
     * Swap two columns
     * 
     * @param input
     *            The input
     * @param index1
     *            The column to swap
     * @param index2
     *            The column to swap with
     * @param separator
     *            The separator character
     * @return The modified CSV string
     */
    public static String swapColumns(String input, int index1, int index2,
            char separator) {
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        for (int l = 0; l < lines.length; l++) {
            String[] parts = split(lines[l], separator);
            String[] outline = new String[parts.length];
            for (int j = 0; j < parts.length; j++) {
                if (j == index1)
                    outline[j] = parts[index2];
                else if (j == index2)
                    outline[j] = parts[index1];
                else
                    outline[j] = parts[j];
            }
            output.append(join(outline, separator) + "\n");
        }

        return output.toString();
    }

    /**
     * Rename column
     * 
     * @param input
     *            The input
     * @param index
     *            The index of the column to rename
     * @param name
     *            The name
     * @param separator
     *            The separator character
     * @return The modified CSV string
     */
    public static String renameColumn(String input, int index, String name,
            char separator) {
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        String[] parts = split(lines[0], separator);
        String[] outline = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            if (i == index)
                outline[i] = name;
            else
                outline[i] = parts[i];
        }
        output.append(join(outline, separator) + "\n");

        for (int i = 1; i < lines.length; i++) {
            output.append(lines[i] + "\n");
        }

        return output.toString();
    }

    /**
     * Split column. If a column contains multiple values separated by
     * 'separator2', this column will be removed and multiple columns containing
     * the single values attached at the end.
     * 
     * @param input
     *            The input
     * @param columnIndex
     *            The index of the column to split
     * @param separator
     *            The separator character
     * @param separator2
     *            The separator character by which the multiple values are
     *            separated
     * @return The modified CSV string
     */
    public static String splitColumn(String input, int columnIndex,
            char separator, char separator2) {
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        List<String> header = new ArrayList<String>();
        int n = 0;
        String columnHeader = "";
        for (int l = 0; l < lines.length; l++) {
            String[] parts = split(lines[l], separator);
            if (header.isEmpty()) {
                for (int i = 0; i < parts.length; i++) {
                    if (i == columnIndex)
                        columnHeader = parts[i];
                    else
                        header.add(parts[i]);
                }
                continue;
            } else {
                String[] tmp = split(parts[columnIndex], separator2);
                if (tmp.length > n) {
                    n = tmp.length;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            header.add(columnHeader + " " + (i + 1));
        }

        String[] tmp = new String[header.size()];
        tmp = header.toArray(tmp);
        output.append(join(tmp, separator) + "\n");

        for (int l = 1; l < lines.length; l++) {
            String[] outline = new String[header.size()];

            String[] parts = split(lines[l], separator);
            int outIndex = 0;
            for (int i = 0; i < parts.length; i++) {
                if (i != columnIndex) {
                    outline[outIndex] = parts[i];
                    outIndex++;
                }
            }

            String[] parts2 = split(parts[columnIndex], separator2);
            for (int i = 0; i < parts2.length; i++) {
                outline[outIndex + i] = parts2[i];
            }

            output.append(join(outline, separator) + "\n");
        }

        return output.toString();
    }

    /**
     * Process (modify the content of) cells of a specific column. Also makes
     * sure that there are no duplicate rows in the output.
     * 
     * @param input
     *            The input
     * @param colIndex
     *            The column index
     * @param sep
     *            The separator
     * @param processor
     *            The processor
     * @return The modified CSV string
     */
    public static String process(String input, int colIndex, char sep,
            Processor processor) {
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        // copy header
        output.append(lines[0] + "\n");

        HashSet<String> unique = new HashSet<String>();

        for (int i = 1; i < lines.length; i++) {
            String[] parts = split(lines[i], sep);
            parts[colIndex] = processor.process(parts[colIndex]);
            String line = join(parts, sep);
            if (!unique.contains(line)) {
                output.append(line + "\n");
                unique.add(line);
            }
        }

        return output.toString();
    }

    /**
     * Remove rows which cells of a specific column match a specific
     * filter.
     * 
     * @param input
     *            The input
     * @param colIndex
     *            The column index
     * @param sep
     *            The separator
     * @param filter
     *            The filter
     * @return The modified CSV string
     */
    public static String filter(String input, int colIndex, char sep,
            Filter filter) {
        String[] lines = input.split("\n");
        StringBuilder output = new StringBuilder();

        // copy header
        output.append(lines[0] + "\n");

        for (int i = 1; i < lines.length; i++) {
            String[] parts = split(lines[i], sep);
            if (!filter.filter(parts[colIndex])) {
                output.append(lines[i] + "\n");
            }
        }

        return output.toString();
    }
    
    /**
     * Get the index of a column by its header name
     * 
     * @param input
     *            The input
     * @param name
     *            The name of the header
     * @param sep
     *            The separator character
     * @return The index of the column
     */
    public static int getColumnIndex(String input, String name, char sep) {
        String[] lines = input.split("\n");
        String[] parts = split(lines[0], sep);
        for (int i = 0; i < parts.length; i++)
            if (parts[i].equals(name.trim()))
                return i;
        return -1;
    }
}
