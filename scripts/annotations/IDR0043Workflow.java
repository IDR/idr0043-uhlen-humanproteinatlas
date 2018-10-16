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
        final String basedir = "/Users/dlindner/Repositories";
        
        final String assayFile = basedir+"/idr0043-uhlen-humanproteinatlas/experimentA/hpa_run_01/idr0043-experimentA-assays.txt";
        final String fileNameColumn = "Image File";
        final String filePathColumn = "Comment [Image File Path]";
        final String datasetNameColumn = "Dataset Name";
        
        final String organismColumn = "Characteristics [Organism]";
        
        final String[] removeColumns = {"Term Source 1 REF", "Comment [Image File Type]"};
        
        final String[] splitColumns = {"Term Source 2 Accession", "Term Source 2 Description", 
                "Term Source 3 Accession", "Term Source 3 Description", "Comment [Gene Identifier]", 
                "Comment [Gene Symbol]"};
        
        // not relevant for hpa_run_01 as it has two different paths, see getPath() method below
        final String path = "/uod/idr/filesets/idr0043-uhlen-humanproteinatlas/...";
        
        final String filePathsFile = basedir+"/idr0043-uhlen-humanproteinatlas/experimentA/hpa_run_01/idr0043-experimentA-filePaths.tsv";
        final String annotationFile = basedir+"/idr0043-uhlen-humanproteinatlas/experimentA/hpa_run_01/idr0043-experimentA-annotation.csv";

        // =====================
        
        final char TSV = '\t';
        final char CSV = ',';
        
        String input = readFile(assayFile);
        
        /**
         *  create filePath.tsv
         */
        
        // Extract the column with the image file paths
        int fi = getColumnIndex(input, filePathColumn, TSV);
        int di = getColumnIndex(input, datasetNameColumn, TSV);
        String filePathsContent = extractColumns(input, new int[]{di, fi}, TSV);
        
        // prefix Dataset:name: for the dataset name column
        filePathsContent = process(filePathsContent, 0, TSV, content -> {
            return "Dataset:name:"+content;
        });
        
        // Prefix the (relative) image file paths with the /uod/idr/filesets/... path to get the absolute path
        // (for hpa_run_02 it's two different paths, have to look it up in that case)
        // filePathsContent = prefixColumn(filePathsContent, 1, TSV, path+"/", null);
        filePathsContent = process(filePathsContent, 1, TSV, content -> {
           String dir = content.substring(0, content.lastIndexOf('/'));
           String fullPath = getPath(dir)+"/"+content;
           return fullPath;
        });
        
        // remove the header line
        filePathsContent = removeRow(filePathsContent, 0);
        
        // save the filePaths.tsv file
        writeFile(filePathsFile, filePathsContent);
        
        
        /**
         * create annotation.csv
         */
        
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
        
        // Delete columns with unnecessary information
        for (String rem : removeColumns) {
            index = getColumnIndex(annotationContent, rem, CSV);
            annotationContent = removeColumn(annotationContent, index, CSV);
        }
        
        // Split columns which have multiple entries
        for (String split : splitColumns) {
            index = getColumnIndex(annotationContent, split, CSV);
            annotationContent = splitColumn(annotationContent, index, CSV, ';');
        }
        
        // Finally save the annotion.csv file
        writeFile(annotationFile, annotationContent);
    }
    
    final static String path1 = "/uod/idr/filesets/idr0043-uhlen-humanproteinatlas/20180825-ftp";
    final static String path2 = "/uod/idr/filesets/idr0043-uhlen-humanproteinatlas/20180831-ftp";
    
    final static String path1Files = "69401, 69404, 69405, 69406, 69409, 69419, 69423, 69425, 69426, 69427, 69428, 69436, 69439, 69441, 69442, 69443, 69448, 69456, 69473, 69477, 69497, 69503, 69515, 69520, 69524, 69538, 69551, 69552, 69555, 69570, 69585, 69590, 69609, 69613, 69617, 69621, 69688, 69692, 69694, 69701, 69703, 69704, 69706, 69711, 69713, 69716, 69725, 69743, 69751, 69761, 69762, 69769, 69797, 69813, 69817, 69832, 69843, 69849, 69857, 69863, 69879, 69880, 69881, 69888, 69889, 69890, 69896, 69903, 69907, 69914, 69915, 69919, 69921, 69925, 69927, 69930, 69974, 69984, 69985, 69989, 69990, 69993, 70003, 70087, 70124, 70127, 70128, 70132, 70134, 70137, 70142, 70159, 70163, 70164, 70169, 70171, 70190, 70192, 70193, 70231, 70240, 70249, 70252, 70260, 70264, 70267, 70304, 70305, 70314, 70321, 70326, 70338, 70349, 70354, 70355, 70359, 70383, 70395, 70412, 70414, 70417, 70418, 70421, 70422, 70423, 70425, 70429, 70455, 70456, 70467, 70478, 70480, 70495, 70502, 70567, 70592, 70595, 70608, 70614, 70647, 70651, 70657, 70703, 70733, 70749, 70751, 70752, 70759, 70789, 70820, 70831, 70841, 70844, 70846, 70867, 70868, 70870, 70892, 70951, 70954, 71011, 71015, 71086, 71114, 71119, 71133, 71166, 71178, 71213, 71229, 71245, 71246, 71248, 71252, 71267, 71268, 71271, 71285, 71287, 71290, 71309, 71312, 71341, 71347, 71406, 71444, 71452, 71461, 71467, 71469, 71508, 71530, 71539, 71540, 71554, 71562, 71573, 71574, 71582, 71600, 71601, 71684, 71709, 71711, 71726, 71751, 71759, 71767, 71776, 71779, 71786, 71809, 71813, 71815, 71840, 71861, 71866, 71871, 71892, 71906, 71909, 71936, 71956, 71997, 72058, 72105, 72174, 72203, 72204, 72208, 72225, 72231, 72244, 72257, 72263, 72273, 72281, 72282, 72287, 72340, 72341, 72342, 72343, 72344, 72420, 72449, 72450, 72453, 72477, 72496, 72505, 72536, 72554, 72558, 72606, 72613, 72625, 72629, 72634, 72635, 72672, 72674, 72686, 72695, 72705, 72754, 72806, 72810, 72814, 72816, 72820, 72827, 72830, 72833, 72836, 72838, 72845, 72852, 72855, 72856, 72857, 72858, 72859, 72860, 72861, 72862, 72863, 72864, 72865, 72866, 72867, 72868, 72870, 72871, 72872, 72873, 72874, 72875, 72876, 72913, 72934, 72936, 72942, 72947, 72995, 73012, 73031, 73106, 73141, 73155, 73186, 73189, 73195, 73224, 73255, 73303, 73355, 73383, 73396, 73398, 73406, 73407, 73410, 73417, 73419, 73447, 73475, 73478, 73497, 73498, 73519, 73521, 73534, 73535, 73536, 73538, 73539, 73541, 73546, 73560, 73604, 73617, 73619, 73622, 73628, 73644, 73647, 73653, 73656, 73666, 73675, 73693, 73694, 73702, 73705, 73708, 73724, 73739, 73761, 73826, 73848, 73886, 74034, 74239, 74351, 74356, 74391, 74402, 74453, 74530, 74544, 74550, 74559, 74573, 74618, 74653, 74677, 74734, 74749, 74762, 74893, 74974, 75014, 75017, 75062, 75125, 75188, 75286, 75379, 75404, 75423, 75519, 75540, 75570, 75684, 75697, 75699, 75701, 75704, 75722, 75723, 75724, 75725, 75726, 75727, 75728, 75729, 75730, 75731, 75733, 75734, 75735, 75736, 75737, 75739, 75742, 75743, 75744, 75747, 75749, 75750, 75751, 75752, 75753, 75754, 75755, 75756, 75757, 75758, 75997, 76019, 76057, 76104, 76123, 76164, 76165, 76261, 76277, 76311, 76330, 76385, 76386, 76453, 76574, 76587, 76649, 76660, 76730, 76764, 76768, 76960, 77020, 77062, 77070, 77073, 77103, 77159, 77250, 77251, 77266, 77312, 77434, 77492, 77546, 77585, 77597, 77658, 77684, 77685, 77718, 77748, 77757, 77979, 78004, 78021, 78110, 78156, 78157, 78160, 78163, 78165, 78177, 78182, 78183, 78195, 78196, 78198, 78204, 78300, 78302, 78390, 78501, 78571, 78597, 78602, 78644, 78657, 78686, 78687, 79289 ";
    final static String path2Files = "64555, 64556, 64557, 64571, 64576, 64607, 64613, 64621, 64623, 64637, 64670, 64677, 64678, 64686, 64687, 64696, 64702, 64708, 64713, 64734, 64736, 64740, 64755, 64763, 64783, 64784, 64788, 64792, 64821, 64826, 64829, 64835, 64836, 64843, 64845, 64853, 64854, 64856, 64861, 64872, 64874, 64885, 64887, 64892, 64930, 64939, 64946, 64962, 64970, 64978, 64996, 65008, 65016, 65019, 65037, 65044, 65048, 65051, 65052, 65062, 65064, 65092, 65126, 65160, 65166, 65197, 65208, 65214, 65219, 65232, 65235, 65246, 65254, 65257, 65273, 65285, 65287, 65294, 65296, 65302, 65309, 65311, 65317, 65320, 65327, 65331, 65335, 65337, 65343, 65385, 65409, 65419, 65424, 65425, 65436, 65474, 65482, 65484, 65505, 65523, 65526, 65539, 65540, 65576, 65586, 65599, 65600, 65634, 65649, 65661, 65682, 65685, 65686, 65703, 65706, 65711, 65713, 65718, 65720, 65721, 65730, 65734, 65739, 65743, 65753, 65758, 65764, 65766, 65831, 65858, 65890, 65931, 65937, 65946, 65947, 65955, 65958, 65961, 65983, 65996, 66010, 66026, 66029, 66037, 66042, 66046, 66053, 66058, 66060, 66070, 66083, 66098, 66115, 66120, 66142, 66173, 66185, 66197, 66214, 66216, 66229, 66235, 66238, 66240, 66271, 66283, 66290, 66293, 66302, 66315, 66326, 66327, 66349, 66352, 66383, 66463, 66464, 66468, 66478, 66482, 66498, 66509, 66520, 66538, 66548, 66571, 66648, 66695, 66697, 66707, 66710, 66715, 66718, 66721, 66754, 66762, 66771, 66774, 66780, 66782, 66784, 66790, 66832, 66834, 66836, 66838, 66841, 66861, 66872, 66888, 66890, 66900, 66902, 66907, 66920, 66927, 66953, 66957, 66996, 67007, 67015, 67026, 67031, 67045, 67046, 67063, 67097, 67102, 67103, 67114, 67117, 67140, 67151, 67152, 67155, 67160, 67189, 67196, 67203, 67222, 67225, 67230, 67239, 67245, 67249, 67250, 67252, 67258, 67290, 67305, 67322, 67326, 67333, 67336, 67388, 67395, 67407, 67418, 67423, 67433, 67440, 67448, 67455, 67493, 67500, 67503, 67533, 67536, 67538, 67540, 67546, 67561, 67584, 67595, 67601, 67602, 67632, 67639, 67643, 67657, 67671, 67682, 67683, 67685, 67690, 67711, 67734, 67740, 67751, 67758, 67767, 67811, 67812, 67817, 67824, 67827, 67850, 67855, 67875, 67878, 67880, 67881, 67889, 67891, 67895, 67906, 67925, 67946, 67947, 67952, 67966, 67970, 67971, 67972, 67973, 67983, 68009, 68012, 68024, 68049, 68064, 68079, 68082, 68093, 68099, 68106, 68114, 68119, 68122, 68125, 68172, 68175, 68176, 68177, 68178, 68179, 68180, 68181, 68182, 68183, 68184, 68185, 68186, 68187, 68188, 68189, 68190, 68191, 68192, 68193, 68194, 68195, 68196, 68197, 68198, 68199, 68200, 68201, 68203, 68204, 68205, 68206, 68207, 68208, 68209, 68210, 68211, 68212, 68213, 68214, 68215, 68216, 68217, 68218, 68219, 68220, 68221, 68222, 68223, 68224, 68225, 68226, 68227, 68228, 68229, 68230, 68231, 68232, 68233, 68234, 68235, 68236, 68237, 68239, 68240, 68241, 68242, 68243, 68244, 68245, 68246, 68247, 68248, 68249, 68250, 68251, 68252, 68253, 68255, 68266, 68288, 68304, 68322, 68379, 68384, 68399, 68416, 68417, 68418, 68429, 68447, 68461, 68473, 68479, 68501, 68520, 68525, 68560, 68563, 68608, 68609, 68647, 68660, 68664, 68695, 68717, 68727, 68764, 68768, 68771, 68772, 68786, 68787, 68790, 68792, 68795, 68812, 68838, 68843, 68864, 68898, 68925, 68930, 68982, 68992, 69003, 69022, 69037, 69039, 69045, 69056, 69064, 69081, 69088, 69094, 69096, 69097, 69102, 69107, 69116, 69119, 69130, 69136, 69146, 69165, 69176, 69190, 69248, 69278, 69290, 69291, 69297, 69311, 69318, 69319, 69320, 69321, 69328, 69333, 69341, 69344, 69359, 69378, 69386, 69392, 69394, 69395, 69396, 69399, 69400 ";
    
    /**
     * Look up the abs path, only relevant for hpa_run_01
     * @param file
     * @return
     */
    public static String getPath(String file) {
        if (path1Files.contains(file))
            return path1;
        else if (path2Files.contains(file))
            return path2;
        return "???";
    }
}
