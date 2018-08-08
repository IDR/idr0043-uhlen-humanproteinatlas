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

import annotations.org.apache.commons.text.StringTokenizer;

/**
 * Some basic split and join methods for handling CSV lines.
 * Needs the https://commons.apache.org/proper/commons-text/ library.
 * 
 * @author Dominik Lindner &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:d.lindner@dundee.ac.uk">d.lindner@dundee.ac.uk</a>
 */
public class BasicCSVUtils {

    static StringTokenizer t = new StringTokenizer();
    static {
        t.setDelimiterChar(',');
        t.setQuoteChar('"');
        t.setIgnoreEmptyTokens(false);
        t.setEmptyTokenAsNull(true);
    }
    
    /**
     * Split and trim a CSV String
     * @param input The input String
     * @return See above
     */
    public static String[] split(String input) {
        return split(input, ',');
    }

    /**
     * Split and trim a String
     * @param input The input String
     * @param sep The separator character
     * @return See above
     */
    public static String[] split(String input, char sep) {
        if (input.charAt(input.length() - 1) == sep) {
            // have to ensure that the line doesn't end
            // with separator char, otherwise the trim()
            // would cut off the last 'cell'
            input += " ";
        }
        t.setDelimiterChar(sep);
        t.reset(input);
        String[] res = t.getTokenArray();
        for (int i = 0; i < res.length; i++) {
            if (res[i] == null)
                res[i] = "";
            else
                res[i] = res[i].trim();
        }

        return res;
    }

    /**
     * Assemble an array of String into an CSV String
     * @param input The String array input
     * @return See above
     */
    public static String join(String[] input) {
        return join(input, ',');
    }

    /**
     * Assemble an array of Strings into a single String
     * @param input The String array input
     * @param sep  The separator character
     * @return See above
     */
    public static String join(String[] input, char sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            if (input[i] == null)
                sb.append("");
            else if(input[i].indexOf(sep)>0)
                sb.append("\""+input[i]+"\"");
            else
                sb.append(input[i]);
            if (i < input.length - 1)
                sb.append(sep);
        }
        return sb.toString();
    }
    
}
