package annotations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import static annotations.CSVUtils.*;

/**
 * Simply prefixes (or postfixes) the content of every cell 
 * in specified column (index).
 */
public class PrefixColumns {
    
    public static void main(String[] args) throws Exception {
        
        // BEGIN Parameters
        
        String in = "idr0043-uhlen-humanproteinatlas/experimentA/idr0043-experimentA-filePaths.tsv";
        String out = "idr0043-uhlen-humanproteinatlas/experimentA/idr0043-experimentA-filePaths_2.tsv";
        
        // prefix or postfix
        boolean prefix = true;
        
        // the text to use as prefix/postfix
        String concat = "Dataset:name:";
        
        // the column index
        int index = 0;
        
        char separator = '\t';
        
        // END Parameters
        
        BufferedReader r = new BufferedReader(new FileReader(in));
        BufferedWriter w = new BufferedWriter(new FileWriter(out));

        w.write(r.readLine()+"\n"); // header
        
        String line = null;
        while ((line = r.readLine()) != null) {
            String parts[] = split(line, separator);
            if (prefix)
                parts[index] = concat+parts[index];
            else
                parts[index] = parts[index]+concat;
            w.write(join(parts, separator)+"\n");
        }
        
        r.close();
        w.close();
    }
}
