package annotations;

/**
 * Process the content of a CSV cell
 * 
 * @author Dominik Lindner &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:d.lindner@dundee.ac.uk">d.lindner@dundee.ac.uk</a>
 */
public interface Processor {

    /**
     * @param input
     *            The input string
     * @return The modified string
     */
    String process(String input);

}
