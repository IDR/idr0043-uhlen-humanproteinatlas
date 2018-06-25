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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Merges several CSV files into a single CSV file. First line of the CSV files
 * must be a header line! Ignores entries starting with a '#' (also whole
 * columns if their header starts with a '#'!)
 * 
 * Run from command line, e.g. with arguments
 * input1.csv input2.csv input3.csv output.csv
 * 
 * 
 * Can be used to merge the different annotation.csv of the single hpa_runs together
 * into one master annotation.csv which can used to annotate the project.
 * 
 * @author Dominik Lindner &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:d.lindner@dundee.ac.uk">d.lindner@dundee.ac.uk</a>
 */
public class Merge {

    public static void main(String[] args) throws Exception {

        if (args.length < 3) {
            help();
        }

        char sep = ',';
        List<String> files = new ArrayList<String>();
        String outFile = args[args.length - 1];
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-h") || args[i].equals("--help"))
                help();
            else if (args[i].equals("-sep"))
                sep = args[++i].charAt(0);
            else if (i < args.length - 1) {
                File f = new File(args[i]);
                if (f.exists())
                    files.add(args[i]);
                else
                    System.err.println("File " + args[i] + " does not exist.");
            }
        }

        if (files.size() > 1) {
            String[] input = new String[files.size()];
            input = files.toArray(input);
            merge(outFile, input);
        }
    }

    private static void help() {
        System.out
                .println("Usage: java Merge [-sep \",\"] input1.csv input2.csv ... output.csv\n"
                        + "-sep Separator character (optional, default: , )");
        System.exit(1);
    }

    private static void merge(String outFile, String... files) throws Exception {
        BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

        // Parse first line of each file to get the headers
        HashSet<String> tmp = new LinkedHashSet<String>();
        for (String file : files) {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            String[] split = split(line);
            for (String s : split) {
                if (s.length() > 0 && !s.startsWith("#"))
                    tmp.add(s);
            }
            in.close();
        }

        // The headers
        String[] headers = new String[tmp.size()];
        // The headers mapped to their position (column index) in the output
        // file
        HashMap<String, Integer> pos = new HashMap<String, Integer>();
        int i = 0;
        for (String header : tmp) {
            headers[i] = header;
            pos.put(header, new Integer(i));
            i++;
        }

        // write header line
        out.write(join(headers) + "\n");

        // merge the input csv files together
        int c = 0;
        for (String file : files) {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            c++;
            // the headers of this particular input csv
            String[] thisHeaders = split(line);

            while ((line = in.readLine()) != null) {
                c++;
                String[] parts = split(line);

                // the assembled output line
                String[] outline = new String[headers.length];

                // iterate over each column
                for (int col = 0; col < parts.length; col++) {
                    try {
                        if (parts[col].startsWith("#"))
                            continue;

                        if (col >= thisHeaders.length)
                            continue;

                        // get the correct column index for the output
                        Integer outIndex = pos.get(thisHeaders[col]);
                        if (outIndex != null)
                            outline[outIndex.intValue()] = parts[col];
                    } catch (Exception e) {
                        System.err.println("File: "+file+" line: "+c+" column: "+col);
                        e.printStackTrace();
                    }
                }
                out.write(join(outline) + "\n");
            }
            c = 0;
            in.close();
        }

        out.close();
    }

}
