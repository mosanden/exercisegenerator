import java.io.*;

/**
 * Parser to parse null labels.
 * @author Thomas Stroeder
 * @version 1.0
 */
public class EmptyLabelParser implements LabelParser<Object> {

    /* (non-Javadoc)
     * @see LabelParser#parse(java.lang.String)
     */
    @Override
    public Object parse(String text) throws IOException {
        return null;
    }

}
