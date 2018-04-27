package annotations;

/**
 * Some CSV utility methods.
 */
public class CSVUtils {

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

        String[] res = input.split("" + sep);
        for (int i = 0; i < res.length; i++)
            res[i] = res[i].trim();

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
            sb.append(input[i] == null ? "" : input[i]);
            if (i < input.length - 1)
                sb.append(sep);
        }
        return sb.toString();
    }
    
}
