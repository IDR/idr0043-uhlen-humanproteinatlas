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
        
        final String datasetName = "hpa_run_01";
        final String path = "/uod/idr/filesets/idr0043-uhlen-humanproteinatlas/20180624-ftp";
        
        final String assayFile = "/idr0043-uhlen-humanproteinatlas/experimentA/hpa_run_01/idr0043-experimentA-assays.txt";
        final String fileNameColumn = "Image File";
        final String filePathColumn = "Comment [Image File Path]";
        final String datasetNameColumn = "Dataset Name";
        final String geneSymColumn = "Comment [Gene Symbol]";
        final String geneIdColumn = "Comment [Gene Identifier]";
        
        final String filePathsFile = "/idr0043-uhlen-humanproteinatlas/experimentA/hpa_run_01/idr0043-experimentA-filePaths.tsv";
        final String annotationFile = "/idr0043-uhlen-humanproteinatlas/experimentA/hpa_run_01/idr0043-experimentA-annotation.csv";

        // =====================
        
        final char TSV = '\t';
        final char CSV = ',';
        
        String input = readFile(assayFile);
        
        /**
         *  create filePath.tsv
         */
        
        // Extract the column with the image file paths
        int index = getColumnIndex(input, filePathColumn, TSV);
        String filePapthsContent = extractColumns(input, new int[]{index}, TSV);
        
        // Add a column with the dataset name
        filePapthsContent = addColumn(filePapthsContent, TSV, 0, "Dataset:name:"+datasetName, "");
        
        // Prefix the (relative) image file paths with the /uod/idr/filesets/... path to get the absolute path
        filePapthsContent = prefixColumn(filePapthsContent, 1, TSV, path+"/", null);
        
        // remove the header line
        filePapthsContent = removeRow(filePapthsContent, 0);
        
        // save the filePaths.tsv file
        writeFile(filePathsFile, filePapthsContent);
        
        
        /**
         * create annotation.csv
         */
        
        // Convert the tsv assays file to a csv file
        String annotationContent = format(input, TSV, CSV);
        
        // Remove empty columns
        annotationContent = removeEmptyColumns(annotationContent, CSV);
        
        // Move the file name column to the front
        index = getColumnIndex(annotationContent, fileNameColumn, CSV);
        annotationContent = swapColumns(annotationContent, index, 0, CSV);
        
        // Rename that column to "Image Name"
        annotationContent = renameColumn(annotationContent, 0, "Image Name", CSV);
        
        // There is already a "Dataset Name" column, rename it to "Original Dataset Name"
        index = getColumnIndex(annotationContent, datasetNameColumn, CSV);
        annotationContent = renameColumn(annotationContent, index, "Original Dataset Name", CSV);
        
        // Add a "Dataset Name" column 'hpa_run_xx' as first column (first two columns must be "Dataset Name" and "Image Name")
        annotationContent = addColumn(annotationContent, CSV, 0, datasetName, "Dataset Name");
        
        // The gene id column can have multiple entries, split them into separate columns
        index = getColumnIndex(annotationContent, geneIdColumn, CSV);
        annotationContent = splitColumn(annotationContent, index, CSV, ';');
        
        // Same for the gene symbol column
        index = getColumnIndex(annotationContent, geneSymColumn, CSV);
        annotationContent = splitColumn(annotationContent, index, CSV, ';');
        
        // Finally save the annotion.csv file
        writeFile(annotationFile, annotationContent);
    }
}
