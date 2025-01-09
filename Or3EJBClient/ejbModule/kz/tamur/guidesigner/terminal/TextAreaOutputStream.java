package kz.tamur.guidesigner.terminal;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

import kz.tamur.util.Funcs;

/**
 * Репликация вывода данных вывода и ошибок в JTextArea target;
 * @author g009c1233
 * @since 2011/06/07
 * @version 0.1
 */
public class TextAreaOutputStream extends BufferedOutputStream {
	
    private JTextArea target;

    public void setTarget(JTextArea target){
    	this.target = target;
    }
    
    public TextAreaOutputStream(OutputStream o)
    {
        super(o);
    }
    
    public TextAreaOutputStream(OutputStream o, int o1){
    	super(o, o1);
    }
    
    public synchronized void write(int b) throws IOException {
    	super.write(b);
        if (this.target == null) {
          throw new NullPointerException();
        }
        else this.target.setText(Funcs.sanitizeMessage(this.target.getText() + String.valueOf(b)));
    }
    
    public synchronized void write(byte b[], int off, int len) throws IOException {
        super.write(b, off, len);
        if (b == null || this.target == null) {
          throw new NullPointerException();
        }
        else if ( (off < 0) || (off > b.length) || (len < 0) ||
                 ( (off + len) > b.length) || ( (off + len) < 0)) {
          throw new IndexOutOfBoundsException();
        }
        else if (len == 0) {
          return;
        }
        String tmp = "";
        for (int i = 0; i < len; i++) {
          tmp = tmp + String.valueOf((char)b[off + i]);
        }
        this.target.setText(Funcs.sanitizeMessage(this.target.getText()+ tmp));
      }
}

/*
public class TextAreaOutputStream extends OutputStream
{
    private static int BUFFER_SIZE = 8192;
    private JTextArea target;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private int pos = 0;

    public TextAreaOutputStream(JTextArea target)
    {
        this.target = target;
    }

    @Override
    public void write(int b) throws IOException
    {
        buffer[pos++] = (byte)b;
        //Append to the TextArea when the buffer is full
        if (pos == BUFFER_SIZE)
            flush();
    }
    
    @Override
    public void write(byte[] arg0) throws IOException{
    	int len = arg0.length;
    	for(int i = 0; i < len; i++) {
    		buffer[pos++] = arg0[i];
    		if(pos == BUFFER_SIZE)
    			flush();
    	}
    }
    
    @Override
    public void write(byte[] arg0, int off, int len) throws IOException{
    	for(int i = 0; i < len; i++){
    		buffer[pos++] = arg0[off + i];
    		if(pos == BUFFER_SIZE)
				flush();
    	}
    }

    //MustToDo!!! Add JTextArea BufferClear on some size!!!
    @Override
    public void flush() throws IOException
    {
        byte[] flush = null;
        if (pos != BUFFER_SIZE) {
            flush = new byte[pos];
            System.arraycopy(buffer, 0, flush, 0, pos);
        }
        else {
            flush = buffer;
        }

        target.append(new String(flush, "UTF-8"));
        
        target.setCaretPosition(target.getDocument().getLength());

        pos = 0;
    }
}
*/

//Oldone
/*
public class TextAreaOutputStream extends OutputStream
{
    private static int BUFFER_SIZE = 8192;
    private JTextArea target;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private int pos = 0;

    public TextAreaOutputStream(JTextArea target)
    {
        this.target = target;
    }

    @Override
    public void write(int b) throws IOException
    {
        buffer[pos++] = (byte)b;
        //Append to the TextArea when the buffer is full
        if (pos == BUFFER_SIZE) {
            flush();
        }
    }

    @Override
    public void flush() throws IOException
    {
        byte[] flush = null;
        if (pos != BUFFER_SIZE) {
            flush = new byte[pos];
            System.arraycopy(buffer, 0, flush, 0, pos);
        }
        else {
            flush = buffer;
        }

        target.append(new String(flush));
        pos = 0;
    }
}
*/