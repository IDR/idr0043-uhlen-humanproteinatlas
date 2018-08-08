package annotations;

/**
 * Filter rows of CSV string
 * 
 * @author Dominik Lindner &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:d.lindner@dundee.ac.uk">d.lindner@dundee.ac.uk</a>
 */
public interface Filter {

    /**
     * Return <code>true</code> if the row should be filtered out.
     * 
     * @param input
     *            The input string to check
     * @return See above.
     */
    boolean filter(String input);

}
