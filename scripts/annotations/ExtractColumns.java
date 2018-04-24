package annotations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;

import static annotations.CSVUtils.*;

/**
 * Extract specific columns from a csv file, while also avoiding duplicate 
 * entries (each row will be unique).
 */
public class ExtractColumns {

    public static void main(String[] args) throws Exception {
        
        // BEGIN Parameters
        
        String in = "/Users/dlindner/Repositories/idr0043-uhlen-humanproteinatlas/experimentA/idr0043-experimentA-assays.txt";
        String out = "/Users/dlindner/Repositories/idr0043-uhlen-humanproteinatlas/experimentA/idr0043-experimentA-filePaths.tsv";

        // The columns to extract
        String[] columns = {"Assay Name", "Comment [Image File Path]"};
        
        char splitSeparator = '\t';
        char joinSeparator = '\t';
        // END Parameters
        
        
        BufferedReader r = new BufferedReader(new FileReader(in));
        BufferedWriter w = new BufferedWriter(new FileWriter(out));
        
        HashSet<String> unique = new HashSet<String>();
        
        String[] headers = split(r.readLine(), splitSeparator);
        int[] index = new int[columns.length];
        for(int i=0; i<columns.length; i++) {
            for(int j=0; j<headers.length; j++) {
                if(headers[j].equals(columns[i])) {
                    index[i] = j;
                }
            }
        }
        
        String line = null;
        w.write(join(columns, joinSeparator)+"\n");

        while ((line = r.readLine()) != null) {
            String parts[] = split(line, splitSeparator);
            String[] outline = new String[columns.length];

            String tmp = "";
            for (int i = 0; i < columns.length; i++) {
                outline[i] = parts[index[i]];
                tmp += parts[index[i]];
            }

            if (!unique.contains(tmp)) {
                w.write(join(outline, joinSeparator) + "\n");
                unique.add(tmp);
            }
        }
        
        r.close();
        w.close();
    }
}
