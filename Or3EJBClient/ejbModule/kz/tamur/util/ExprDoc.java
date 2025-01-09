package kz.tamur.util;

import javax.swing.text.*;
import java.util.*;
import java.awt.*;

import kz.tamur.rt.Utils;
import kz.tamur.util.ExpressionEditor.Undoer;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 07.06.2005
 * Time: 16:05:37
 * To change this template use File | Settings | File Templates.
 */
public class ExprDoc extends DefaultStyledDocument {
    private Element rootElement;

    private boolean multiLineComment;
    private MutableAttributeSet normal;
    private MutableAttributeSet keyword;
    private MutableAttributeSet variable;
    private MutableAttributeSet client_vars;
    private MutableAttributeSet comment;
    private MutableAttributeSet quote;

    private Map keywords = new HashMap();
    private Map Varwords = new HashMap();
    String content;
    private Set<String> delimeter = new HashSet<String>();
    private boolean tabaction = false;
    private boolean replaceAction = false;
    private Undoer undoer = null;

    public ExprDoc(Map vars, Map funcs) {
        rootElement = getDefaultRootElement();
        putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

        normal = new SimpleAttributeSet();
        StyleConstants.setForeground(normal, Color.black);

        keyword = new SimpleAttributeSet();
        StyleConstants.setBold(keyword, true);
        StyleConstants.setForeground(keyword, Utils.getKeywordColor());
        StyleConstants.setItalic(keyword, false);

        variable = new SimpleAttributeSet();
        StyleConstants.setBold(variable, true);
        StyleConstants.setForeground(variable, Utils.getVariableColor());


        client_vars = new SimpleAttributeSet();
        StyleConstants.setBold(client_vars, true);
        StyleConstants.setForeground(client_vars, Utils.getClientVariableColor());

        comment = new SimpleAttributeSet();
        StyleConstants.setForeground(comment, Utils.getCommentColor());
        StyleConstants.setItalic(comment, true);

        quote = new SimpleAttributeSet();
        //StyleConstants.setForeground(quote, Color.red);


        keywords = funcs;
        Varwords = vars;
        delimeter.add("(");
        delimeter.add(")");
        //delimeter.add("[");
        //delimeter.add("]");
        delimeter.add("/");
        delimeter.add("<");
        delimeter.add("=");
        delimeter.add(">");
        delimeter.add(".");
        delimeter.add(",");
        delimeter.add(" ");
        delimeter.add("!");
        delimeter.add("\n");
        delimeter.add("\t");

    }

    /*
     *  Override to apply syntax highlighting after the document has been updated
     */
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
//        if (str.equals("{"))
//            str = addMatchingBrace(offset);
		boolean isWasReplacing = replaceAction;
		if (!isWasReplacing) replaceAction = true;
		try {
	        if (!str.equals("\t")) {
	            int startLine = rootElement.getElementIndex(offset);
	            int startOffset = rootElement.getElement(startLine).getStartOffset();
                int off = offset;
    			
				String[] strs = str.split("\n", -1); 
	        	for (int i=0; i<strs.length; i++) {
	        		String strLine = strs[i];
	        		if (strLine.length() > 0) {
	                    super.insertString(off, strLine, null);
	                    off += strLine.length();
	        		}
	        		if (i < strs.length - 1)
	                    super.insertString(off++, "\n", null);
	
	        	}
	        }
	        if (!tabaction)
	            processChangedLines(offset, str.length());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isWasReplacing) {
				replaceAction = false;
				if (undoer != null)
					undoer.completeComplexUndo();
			}
		}
    }

    public void insertStr(int offset, String str, AttributeSet a) {
        try {
            super.insertString(offset, str, a);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /*
     *  Override to apply syntax highlighting after the document has been updated
     */
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        if (!tabaction)
            processChangedLines(offset, 0);
    }
    
	public void replace(int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		if (length == 0 && (text == null || text.length() == 0)) {
			return;
		}
		boolean isWasReplacing = replaceAction;
		if (!isWasReplacing) replaceAction = true;
		try {
			writeLock();

			if (length > 0) {
				remove(offset, length);
			}
			if (text != null && text.length() > 0) {
				insertString(offset, text, attrs);
			}
	        if (!tabaction)
	            processChangedLines(offset, text.length());
		} finally {
			if (!isWasReplacing) {
				replaceAction = false;
				undoer.completeComplexUndo();
			}
			writeUnlock();
		}
	}

    /*
     *  Determine how many lines have been changed,
     *  then apply highlighting to each line
     */
    public void processChangedLines(int offset, int length)
            throws BadLocationException {
        content = getText(0, getLength());

        //  The lines affected by the latest document update

        int startLine = rootElement.getElementIndex(offset);
        int endLine = rootElement.getElementIndex(offset + length);

        //  Make sure all comment lines prior to the start line are commented
        //  and determine if the start line is still in a multi line comment

        setMultiLineComment(commentLinesBefore(startLine));

        //  Do the actual highlighting

        for (int i = startLine; i <= endLine; i++) {
            applyHighlighting(i);
        }

        //  Resolve highlighting to the next end multi line delimiter

        if (isMultiLineComment())
            commentLinesAfter(endLine);
        else if (startLine != endLine)
            highlightLinesAfter(endLine);

    }

    private void setMultiLineComment(boolean value) {
        multiLineComment = value;
    }

    private boolean isMultiLineComment() {
        return multiLineComment;
    }


    /*
   *  Highlight lines when a multi line comment is still 'open'
   *  (ie. matching end delimiter has not yet been encountered)
   */
    private boolean commentLinesBefore(int line) {
        int offset = rootElement.getElement(line).getStartOffset();

        //  Start of comment not found, nothing to do

        int startDelimiter = lastIndexOf(content, getStartDelimiter(), offset - 2);

        if (startDelimiter < 0)
            return false;

        //  Matching start/end of comment found, nothing to do

        int endDelimiter = indexOf(content, getEndDelimiter(), startDelimiter);

        if (endDelimiter < offset & endDelimiter != -1)
            return false;

        //  End of comment not found, highlight the lines

        setCharacterAttributes(startDelimiter, offset - startDelimiter + 1, comment, false);
        return true;
    }


    private void highlightLinesAfter(int line) {
        int offset = rootElement.getElement(line).getEndOffset();

        //  Start/End delimiter not found, nothing to do

        int startDelimiter = indexOf(content, getStartDelimiter(), offset);
        int endDelimiter = indexOf(content, getEndDelimiter(), offset);

        if (startDelimiter < 0)
            startDelimiter = content.length();

        if (endDelimiter < 0)
            endDelimiter = content.length();

        int delimiter = Math.min(startDelimiter, endDelimiter);

        if (delimiter < offset)
            return;

        //  Start/End delimiter found, reapply highlighting

        int endLine = rootElement.getElementIndex(delimiter);

        for (int i = line + 1; i < endLine; i++) {
            Element branch = rootElement.getElement(i);
            Element leaf = getCharacterElement(branch.getStartOffset());
            AttributeSet as = leaf.getAttributes();

            if (as.isEqual(comment))
                applyHighlighting(i);
        }
    }

    /*
     *  Highlight comment lines to matching end delimiter
    */
    private void commentLinesAfter(int line) {
        int offset = rootElement.getElement(line).getEndOffset();

        //  End of comment not found, nothing to do

        int endDelimiter = indexOf(content, getEndDelimiter(), offset);

        if (endDelimiter < 0)
            return;

        //  Matching start/end of comment found, comment the lines

        int startDelimiter = lastIndexOf(content, getStartDelimiter(), endDelimiter);

        if (startDelimiter < 0 || startDelimiter <= offset) {
            setCharacterAttributes(offset, endDelimiter - offset + 1, comment, false);
        }
    }


    private void applyHighlighting(int line) {
        int startOffset = rootElement.getElement(line).getStartOffset();
        int endOffset = rootElement.getElement(line).getEndOffset();

        int lineLength = endOffset - startOffset;
        if (lineLength > 1) {
	        int contentLength = content.length();
	
	        if (endOffset >= contentLength)
	            endOffset = contentLength - 1;
	
	        //  check for multi line comments
	        //  (always set the comment attribute for the entire line)
	
	        if (endingMultiLineComment(content, startOffset, endOffset)
	                || isMultiLineComment()
	                || startingMultiLineComment(content, startOffset, endOffset)) {
	            setCharacterAttributes(startOffset, endOffset - startOffset + 1, comment, false);
	            return;
	        }
	
	        //  set normal attributes for the line
	        
	        setCharacterAttributes(startOffset, lineLength + 1, normal, false);
	
	        //  check for single line comment
	
	        int index = content.indexOf(getSingleLineDelimiter(), startOffset);
	
	        if ((index > -1) && (index < endOffset)) {
	            setCharacterAttributes(index, endOffset - index - 1, comment, false);
	            endOffset = index;
	        }
	
	        //  check for tokens
	
	        checkForTokens(startOffset, endOffset);
        }
    }


    /*
  *  Does this line contain the start delimiter
  */
    private boolean startingMultiLineComment(String content, int startOffset, int endOffset) {
        int index = indexOf(content, getStartDelimiter(), startOffset);

        if ((index < 0) || (index > endOffset))
            return false;
        else {
            setMultiLineComment(true);
            return true;
        }
    }

    /*
     *  Does this line contain the end delimiter
     */
    private boolean endingMultiLineComment(String content, int startOffset, int endOffset) {
        int index = indexOf(content, getEndDelimiter(), startOffset);

        if ((index < 0) || (index > endOffset))
            return false;
        else {
            setMultiLineComment(false);
            return true;
        }
    }


    /*
     *	Parse the line for tokens to highlight
     */
    private void checkForTokens(int startOffset, int endOffset) {
        while (startOffset < endOffset) {
            while (isDelimiter(content.substring(startOffset, startOffset + 1))) {
                if (startOffset < endOffset - 1)
                    startOffset++;
                else
                    return;
            }

            //if (isQuoteDelimiter(content.substring(startOffset, startOffset + 1))) {
                //startOffset = getQuoteToken(startOffset, endOffset);
            //} else
                startOffset = getOtherToken(startOffset, endOffset);

        }
    }

    private int getQuoteToken(int startOffset, int endOffset) {
        String quoteDelimiter = content.substring(startOffset, startOffset + 1);
        String escapeString = getEscapeString(quoteDelimiter);

        int index;
        int endOfQuote = startOffset;

        //  skip over the escape quotes in this quote

        index = content.indexOf(escapeString, endOfQuote + 1);

        while ((index > -1) && (index < endOffset)) {
            endOfQuote = index + 1;
            index = content.indexOf(escapeString, endOfQuote);
        }

        // now find the matching delimiter

        index = content.indexOf(quoteDelimiter, endOfQuote + 1);

        if ((index < 0) || (index > endOffset))
            endOfQuote = endOffset;
        else
            endOfQuote = index;

        setCharacterAttributes(startOffset, endOfQuote - startOffset + 1, quote, false);

        return endOfQuote + 1;
    }


    /*
     *
     */
    private int getOtherToken(int startOffset, int endOffset) {
        int endOfToken = startOffset + 1;

        while (endOfToken < endOffset) {
            if (isDelimiter(content.substring(endOfToken, endOfToken + 1)))
                break;

            endOfToken++;
        }

        String token = content.substring(startOffset, endOfToken);

        if (isFunc(token)) {
            setCharacterAttributes(startOffset, endOfToken - startOffset, keyword, false);
        } else if (token.startsWith("$") && isVar(token.substring(1))) {
            setCharacterAttributes(startOffset, endOfToken - startOffset, variable, false);
        } else if (token.startsWith("$") && !isVar(token)) {
            setCharacterAttributes(startOffset, endOfToken - startOffset, client_vars, false);
        }

        return endOfToken + 1;
    }

    /*
     *  Assume the needle will the found at the start/end of the line
     */
    private int indexOf(String content, String needle, int offset) {
        int index;

        while ((index = content.indexOf(needle, offset)) != -1) {
            String text = getLine(content, index).trim();

            if (text.startsWith(needle) || text.endsWith(needle))
                break;
            else
                offset = index + 1;
        }

        return index;
    }

    /*
     *  Assume the needle will the found at the start/end of the line
     */
    private int lastIndexOf(String content, String needle, int offset) {
        int index;

        while ((index = content.lastIndexOf(needle, offset)) != -1) {
            String text = getLine(content, index).trim();

            if (text.startsWith(needle) || text.endsWith(needle))
                break;
            else
                offset = index - 1;
        }

        return index;
    }

    private String getLine(String content, int offset) {
        int line = rootElement.getElementIndex(offset);
        Element lineElement = rootElement.getElement(line);
        int start = lineElement.getStartOffset();
        int end = lineElement.getEndOffset();
        return content.substring(start, end - 1);
    }

    /*
     *  Override for other languages
     */
    protected boolean isDelimiter(String character) {
        return delimeter.contains(character);
    }

    /*
     *  Override for other languages
     */
    protected boolean isQuoteDelimiter(String character) {
        String quoteDelimiters = "\"'";
        if (quoteDelimiters.indexOf(character) < 0)
            return false;
        else
            return true;
    }

    /*
     *  Override for other languages
     */
    protected boolean isFunc(String token) {
        return keywords.containsKey(token);
    }

    protected boolean isVar(String token) {
        return Varwords.containsKey(token);
    }

    /*
     *  Override for other languages
     */
    protected String getStartDelimiter() {
        return "/*";
    }

    /*
     *  Override for other languages
     */
    protected String getEndDelimiter() {
        return "*/";
    }

    /*
     *  Override for other languages
     */
    protected String getSingleLineDelimiter() {
        return "//";
    }

    /*
     *  Override for other languages
     */
    protected String getEscapeString(String quoteDelimiter) {
        return "\\" + quoteDelimiter;
    }

    /*
     *
     */
    
//    protected String addMatchingBrace(int offset) throws BadLocationException {
//        StringBuffer whiteSpace = new StringBuffer();
//        int line = rootElement.getElementIndex(offset);
//        int i = rootElement.getElement(line).getStartOffset();
//
//        while (true) {
//            String temp = getText(i, 1);
//
//            if (temp.equals(" ") || temp.equals("\t")) {
//                whiteSpace.append(temp);
//                i++;
//            } else
//                break;
//        }
//
//        return "{\n" + whiteSpace.toString() + "\t\n" + whiteSpace.toString() + "}";
//    }

    public Element getRootElement() {
        return rootElement;
    }
    /*
    public void addTabs(int mark, int dot) {
        try {
            int start = rootElement.getElementIndex(mark);
            int end = rootElement.getElementIndex(dot);
            for (int line = start; line <= end; line++) {
                int startOffset = rootElement.getElement(line).getStartOffset();
                int endOffset = rootElement.getElement(line).getEndOffset() - 1;
                int lineLength = endOffset - startOffset;
                String str = null;
                str = getText(startOffset, lineLength);
                str = "\t" + str;
                tabaction = true;
                replace(startOffset, lineLength, str, null);
                tabaction = false;
            }

        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    */

    public int lastPos(int dot) {
        int pos = rootElement.getElementIndex(dot);
        return rootElement.getElement(pos).getEndOffset() - 1;
    }

	public boolean isReplaceAction() {
		return replaceAction;
	}

	public void setReplaceAction(boolean replaceAction) {
		this.replaceAction = replaceAction;
	}
	
	public void setUndoer(Undoer undoer) {
		this.undoer = undoer;
	}
}
