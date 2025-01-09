package com.cifs.or2.util.expr;

import com.cifs.or2.util.MultiMap;

import java.util.ArrayList;

public class Editor {
    public static final int NOP = 0;
    public static final int LT = 1;
    public static final int GT = 2;
    public static final int EQ = 3;
    public static final int LE = 4;
    public static final int GE = 5;

    private String expr_;
    private ArrayList<String> refPaths_ = new ArrayList<String>();
    private MultiMap filterParams_ = new MultiMap();

    public Editor(String expr) {
    	if (expr != null)
    		expr_ = expr.replaceAll("//.*\\*/.*", "*/").replaceAll("//.*", "").replaceAll("/\\*(.*\\s*)*?.*\\*/", "").trim();
    }

    public static String getExpression(String text) {
        if (text != null && text.length() > 0) {
            int op = getOperation(text);
            if (op == GE || op == LE)
                return text.substring(2);
            else if (op == LT || op == GT || op == EQ)
                return text.substring(1);
            else
                return text;
        }
        return null;
    }

    public static int getOperation(String text) {
        if (text != null && text.length() >= 2) {
            String op = text.substring(0, 2);
            if (op.equals("<="))
                return LE;
            else if (op.equals(">="))
                return GE;
            else {
                char c = op.charAt(0);
                if (c == '<')
                    return LT;
                else if (c == '>')
                    return GT;
                else if (c == '=')
                    return EQ;
            }
        }
        return NOP;
    }

    public ArrayList<String> getRefPaths() {
        updatePaths();
        return refPaths_;
    }

    public MultiMap getFilterParams() {
        updateFilterParams();
        return filterParams_;
    }

    private void updatePaths() {
        String[] formulae_ = {"$Interface.getAttr", "$Interface.setAttr",
                              "$OBJ.getAttr", "$OBJ.setAttr"};
        filterParams_.clear();
        if (expr_ != null && expr_.length() > 0) {
            int index;
            for (int k = 0; k < formulae_.length; k++) {
                index = -1;
                while (true) {
                    index = expr_.indexOf(formulae_[k], index + 1);
                    if (index < 0) break;
                    int beg = expr_.indexOf('\"', index);
                    int end = expr_.indexOf('\"', beg + 1);
                    refPaths_.add(expr_.substring(beg + 1, end));
                }
            }
        }
    }

    private void updateFilterParams() {
        String[] formulae_ = {"$Objects.getFilterParam"};
        refPaths_.clear();
        if (expr_ != null && expr_.length() > 0) {
            int index;
            for (int k = 0; k < formulae_.length; k++) {
                index = -1;
                while (true) {
                    index = expr_.indexOf(formulae_[k], index + 1);
                    if (index < 0) break;
                    int beg = expr_.indexOf('\"', index);
                    int end = expr_.indexOf('\"', beg + 1);
                    String fuid = expr_.substring(beg + 1, end);
                    beg = expr_.indexOf('\"', end + 1);
                    end = expr_.indexOf('\"', beg + 1);
                    String param = expr_.substring(beg + 1, end);
                    filterParams_.put(fuid, param);
                }
            }
        }
    }

    public boolean hasDateOps() {
        String[] formulae_ = {"$Date.readFirstDate", "$Date.readLastDate"};
        if (expr_ != null && expr_.length() > 0) {
            int index = -1;
            for (int k = 0; k < formulae_.length; k++) {
                index = expr_.indexOf(formulae_[k], index + 1);
                if (index > -1) {
                    return true;
                }
            }
        }
        return false;
    }
}
