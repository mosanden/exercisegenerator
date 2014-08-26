import java.io.*;

/**
 * Parser to parse integer labels.
 * @author Thomas Stroeder
 * @version 1.0
 */
public class IntLabelParser implements LabelParser<Integer> {

    /* (non-Javadoc)
     * @see LabelParser#parse(java.lang.String)
     */
    @Override
    public Integer parse(String text) throws IOException {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IOException(e);
        }
    }

}
