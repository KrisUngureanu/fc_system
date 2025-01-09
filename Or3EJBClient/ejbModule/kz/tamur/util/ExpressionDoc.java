package kz.tamur.util;

import kz.tamur.rt.Utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Utilities;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Author: kazakbala
 * Date: 07.07.2004
 * Time: 10:38:01
 */
public class ExpressionDoc extends DefaultStyledDocument {

	private static final long serialVersionUID = 1L;
	private EditorPane editor;
    private Map vars, funcs;
    private Set<String> left, right;
    private boolean editable = true;
    private int FUNC = 0;
    private int VARS = 1;
    private int UVARS = 2;
    private int TXT = 3;

    public ExpressionDoc(EditorPane editor, Map vars, Map funcs) {
        this.editor = editor;
        this.vars = vars;
        this.funcs = funcs;

        left = new HashSet<String>(2);
        left.add("(");
        left.add("{");

        right = new HashSet<String>(2);
        right.add(")");
        right.add("}");


    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        int word_e, word_s;
        if (editable) {
            if (str.length() > 1) {
                super.insertString(offs, str, a);
                multiInsert(offs, str);
            } else {
                super.insertString(offs, str, Utils.getStyle("none"));
                /*if (!str.equals(" ") && !str.equals(".") && !str.equals("(")) {
                    word_s = Utilities.getPreviousWord(editor, offs);
                    word_e = Utilities.getWordEnd(editor, word_s);
                    String part = getText(word_s, word_e - word_s);
                    Mark(part,word_s);
                } */
                if (str.equals(" ") || str.equals(".") || str.equals("(")) {
                    word_s = Utilities.getPreviousWord(editor, offs);
                    word_e = Utilities.getWordEnd(editor, word_s);
                    String word = getText(word_s, word_e - word_s);
                    if (word.indexOf(".") > 0) {
                        int pos = word.indexOf(".");
                        String words = word.substring(pos + 1);
                        word = words;
                        word_s = word_s + pos + 1;
                    }
                    Mark(word, word_s);
                    /*if (vars.containsKey(word)) {
                        word_s = Utilities.getPreviousWord(editor, word_s);
                        word_e = Utilities.getWordEnd(editor, word_s);
                        String dollar = getText(word_s, word_e - word_s);
                        if (dollar.equals("$"))
                            word = dollar + word;
                        Mark(word, word_s, VARS);
                    } else if (functions.contains(word)) {
                        int sign_s = Utilities.getPreviousWord(editor, word_s);
                        int sign_e = Utilities.getWordEnd(editor, sign_s);
                        String sign = getText(sign_s, sign_e - sign_s);
                        if (sign.equals("#")) {
                            word = "#"+word;
                            word_s--;
                        }
                        Mark(word, word_s, FUNC);
                    } else {
                        word_s = Utilities.getPreviousWord(editor, word_s);
                        word_e = Utilities.getWordEnd(editor, word_s);
                        String dollar = getText(word_s, word_e - word_s);
                        if (dollar.equals("$")) {
                            word = dollar + word;
                            Mark(word, word_s, UVARS);
                        }
                    }*/
                }
            }
        }
    }

    private String filter(String word) {
        if (word.indexOf(".") > 0) {
            int index = word.indexOf(".");

            return word.substring(index + 1);
        } else
            return word;
    }


    public void remove(int offs, int len) throws BadLocationException {
        int word_s, word_e;
        if (editable) {
            if (getLength() > 0 && getText(offs, 1) != "/") {
                super.remove(offs, len);
                word_s = Utilities.getWordStart(editor, offs);
                word_e = Utilities.getWordEnd(editor, offs);
                String word = getText(word_s, word_e - word_s);
                if (word.equals("(")) {
                    word_s = Utilities.getPreviousWord(editor, offs);
                    word_e = Utilities.getWordEnd(editor, word_s);
                    word = getText(word_s, word_e - word_s);
                }
                //CheckandMark(word, word_s);
            }
        }
    }

    public void Mark(String word, int word_s) throws BadLocationException {
        int word_e;
        setCharacterAttributes(word_s, word.length(), Utils.getStyle("none"), false);
        if (vars.containsKey(word)) {
            word_s = Utilities.getPreviousWord(editor, word_s);
            word_e = Utilities.getWordEnd(editor, word_s);
            String dollar = getText(word_s, word_e - word_s);
            if (dollar.equals("$"))
                word = dollar + word;
            //Mark(word, word_s, VARS);
            setCharacterAttributes(word_s, word.length(), Utils.getStyle("vars"), false);
        } else if (funcs.containsKey(word)) {
            int sign_s = Utilities.getPreviousWord(editor, word_s);
            int sign_e = Utilities.getWordEnd(editor, sign_s);
            String sign = getText(sign_s, sign_e - sign_s);
            if (sign.equals("#")) {
                word = "#" + word;
                word_s--;
            }
            setCharacterAttributes(word_s, word.length(), Utils.getStyle("func"), false);
        } else {
            word_s = Utilities.getPreviousWord(editor, word_s);
            word_e = Utilities.getWordEnd(editor, word_s);
            String dollar = getText(word_s, word_e - word_s);
            if (dollar.equals("$")) {
                word = dollar + word;
                setCharacterAttributes(word_s, word.length(), Utils.getStyle("myvars"), false);
            }
        }
        /*if (type == VARS) {
            setCharacterAttributes(start, str.length(), Utils.getStyle("vars"), false);
        } else if (type == TXT){
            setCharacterAttributes(start, str.length(), Utils.getStyle("none"), false);
        } else if (type == FUNC) {
            setCharacterAttributes(start, str.length(), Utils.getStyle("func"), false);
        } else if (type== UVARS) {
            setCharacterAttributes(start, str.length(), Utils.getStyle("myvars"), false);
        }   */

    }

    public void multiInsert(int start, String str) {
        int word_s, word_e = 0;
        /*try {
            if (str.substring(0, 1).equals(" ")) {
                word_s = Utilities.getNextWord(editor, start);
                word_e = Utilities.getWordEnd(editor, word_s);
            } else {
                word_s = start;
                word_e = Utilities.getWordEnd(editor, word_s);
            }
            String txt = getText(word_s, word_e - word_s);
            Mark(txt, word_s);
        } catch (BadLocationException e) {

        } */
        word_s = start;
        boolean end = false;
        while (!end) {
            try {
                int add = 0;
                word_s = Utilities.getNextWord(editor, word_s);
                word_e = Utilities.getWordEnd(editor, word_s);
                String txt = getText(word_s, word_e - word_s);
                if (txt.indexOf(".") > 0) {
                    String parts[] = txt.split("[.]");
                    int s_pos = word_s;
                    for (int i = 0; i < parts.length; i++) {
                        Mark(parts[i], s_pos);
                        s_pos = word_s + parts[i].length() + 1;
                    }

                    //word_e = word_s + pos;

                } else {
                    Mark(txt, word_s);
                }
                add = 0;
                if (word_e == str.length()) end = true;
            } catch (BadLocationException e) {
                end = true;
            }

        }
    }


    public void setEditable(boolean status) {
        editable = status;
    }


}

