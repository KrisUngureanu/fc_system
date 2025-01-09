package kz.tamur.util;

import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 18.01.2005
 * Time: 12:15:43
 * To change this template use File | Settings | File Templates.
 */
public class NullWriter extends Writer {
    public void close() throws IOException {
    }

    public void flush() throws IOException {
    }

    public void write(char cbuf[], int off, int len) throws IOException {
    }
}
