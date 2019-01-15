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

import static annotations.CSVTools.*;

import java.util.HashMap;

/**
 * Generates the filePaths.tsv and annotation.csv from the provided assays.txt file.
 * 
 * @author Dominik Lindner &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:d.lindner@dundee.ac.uk">d.lindner@dundee.ac.uk</a>
 */
public class IDR0043Workflow {

    public static void main(String[] args) throws Exception {
        
        // ====================
        // Parameters
        final String basedir = "/Users/dlindner/Repositories";
        final String run = "hpa_run_04";
        final String ftpDir = "20190109-ftp";
        
        // Shouldn't have to change anything below this line (if the assays.txt format hasn't changed)
        
        final String assayFile = basedir+"/idr0043-uhlen-humanproteinatlas/experimentA/"+run+"/assays.tsv";
        final String fileNameColumn = "Image File";
        final String filePathColumn = "Comment [Image File Path]";
        final String datasetNameColumn = "Dataset Name";
        
        final String organismColumn = "Characteristics [Organism]";
        final String ensemblColumn = "Analysis Gene Annotation Build";
        
        final String[] removeColumns = {"Term Source 1 REF", "Comment [Image File Type]", "Characteristics [Organism Part]"};
        
        final String[] splitColumns = {"Comment [Gene Identifier]", "Comment [Gene Symbol]"};
        
        final String path = "/uod/idr/filesets/idr0043-uhlen-humanproteinatlas/"+ftpDir;
        
        final String filePathsFile = basedir+"/idr0043-uhlen-humanproteinatlas/experimentA/"+run+"/idr0043-experimentA-filePaths.tsv";
        final String annotationFile = basedir+"/idr0043-uhlen-humanproteinatlas/experimentA/"+run+"/idr0043-experimentA-annotation.csv";

        final boolean genFilepaths = true;
        
        final boolean genAnnotations = true;
        
        // =====================
        
        final char TSV = '\t';
        final char CSV = ',';
        
        String input = readFile(assayFile);
        
        input = removeEmptyColumns(input, TSV);
        
        /**
         *  create filePath.tsv
         */
        if (genFilepaths) {
            // Extract the column with the image file paths
            int fi = getColumnIndex(input, filePathColumn, TSV);
            int di = getColumnIndex(input, datasetNameColumn, TSV);
            String filePathsContent = extractColumns(input, new int[]{di, fi}, TSV);
            
            di = 0;
            fi = 1;
            // This is just a sanity check to make sure each image directory == exactly one dataset
            HashMap<String, String> datasetDirectoryMap = new HashMap<String, String>();
            String[] parts = filePathsContent.split("\n");
            for (int i=1; i<parts.length; i++) {
                String[] tmp = BasicCSVUtils.split(parts[i], TSV);
                String dir = tmp[fi].substring(0, tmp[fi].indexOf('/'));
                if (datasetDirectoryMap.containsKey(tmp[0]) &&
                        !datasetDirectoryMap.get(tmp[di]).equals(dir)) {
                    System.err.println("Line "+i+" Unexpected image directory name. Is: "+
                        dir+", Expected: "+datasetDirectoryMap.get(tmp[di]));
                    System.exit(1);
                } else {
                    datasetDirectoryMap.put(tmp[di], dir);
                }
                i++;
            }
            
            // If above sanity check passes, remove the image file part of the 
            // path so there only one line per dataset/directory
            filePathsContent = process(filePathsContent, fi, TSV, content -> {
                return content.substring(0, content.indexOf('/'));
            });
            
            // prefix Dataset:name: for the dataset name column
            filePathsContent = process(filePathsContent, di, TSV, content -> {
                return "Dataset:name:"+content;
            });
            
            // Prefix the (relative) image file paths with the /uod/idr/filesets/... path to get the absolute path
            filePathsContent = prefixColumn(filePathsContent, fi, TSV, path+"/", null);
            
            // remove the header line
            filePathsContent = removeRow(filePathsContent, 0);
            
            // save the filePaths.tsv file
            writeFile(filePathsFile, filePathsContent);
        }
        
        /**
         * create annotation.csv
         */
        if (genAnnotations) {
            // Convert the tsv assays file to a csv file
            String annotationContent = format(input, TSV, CSV);
            
            // Remove empty columns
            annotationContent = removeEmptyColumns(annotationContent, CSV);
            
            // Move the dataset name column to the front
            int index = getColumnIndex(annotationContent, datasetNameColumn, CSV);
            annotationContent = swapColumns(annotationContent, index, 0, CSV);
            
            // Move the file name column to the front
            index = getColumnIndex(annotationContent, fileNameColumn, CSV);
            annotationContent = swapColumns(annotationContent, index, 1, CSV);
            
            // Rename that column to "Image Name"
            annotationContent = renameColumn(annotationContent, 1, "Image Name", CSV);
            
            // Fix issue with organism name
            index = getColumnIndex(annotationContent, organismColumn, CSV);
            annotationContent = process(annotationContent, index, CSV, content -> {
                return "Homo sapiens";
            });
            
            // Fix the ensembl version column
            index = getColumnIndex(annotationContent, ensemblColumn, CSV);
            annotationContent = renameColumn(annotationContent, index, "Ensembl version", CSV);
            annotationContent = process(annotationContent, index, CSV, content -> {
                return content.replace("Ensembl version ", "");
            });
            
            // Delete columns with unnecessary information
            for (String rem : removeColumns) {
                index = getColumnIndex(annotationContent, rem, CSV);
                annotationContent = removeColumn(annotationContent, index, CSV);
            }
            
            String cName = "Term Source 2 REF";
            index = getColumnIndex(annotationContent, cName, CSV);
            annotationContent = renameColumn(annotationContent, index, "Term Source REF", CSV);
            
            cName = "Term Source 2 Description";
            index = getColumnIndex(annotationContent, cName, CSV);
            annotationContent = renameColumn(annotationContent, index, "Characteristics [Organism Part]", CSV);

            cName = "Term Source 2 Accession";
            index = getColumnIndex(annotationContent, cName, CSV);
            annotationContent = renameColumn(annotationContent, index, "Characteristics [Organism Part] Accession", CSV);
            
            // swap accNo and desc columns
            annotationContent = swapColumns(annotationContent, index, index+1, CSV);
            
            cName = "Term Source 3 Description";
            index = getColumnIndex(annotationContent, cName, CSV);
            annotationContent = renameColumn(annotationContent, index, "Characteristics [Pathology]", CSV);
            
            cName = "Term Source 3 Accession";
            index = getColumnIndex(annotationContent, cName, CSV);
            annotationContent = renameColumn(annotationContent, index, "Characteristics [Pathology] Accession", CSV);
            
            // swap accNo and desc columns
            annotationContent = swapColumns(annotationContent, index, index+1, CSV);
            
            // Split columns which have multiple entries
            for (String split : splitColumns) {
                index = getColumnIndex(annotationContent, split, CSV);
                annotationContent = splitColumn(annotationContent, index, CSV, ';');
            }
            
            // Finally save the annotion.csv file
            writeFile(annotationFile, annotationContent);
        }
    }
}
