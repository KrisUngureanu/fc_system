package kz.tamur.rt;

import kz.tamur.rt.adapters.OrRef;
import kz.tamur.lang.parser.ASTStart;

/*
 * User: vital
 * Date: 20.10.2005
 * Time: 12:23:50
 */

public interface CheckContext {

    public long getLangId();
    public int getReqGroup();
    public int getEnterDB();
    public boolean isActive();
    public OrRef getRef();
    public String getCExpr();
    public String getConstrMsg();
    public String getReqMsg();
    public ASTStart getCTemplate();
    public boolean isCheckConstr();
    public boolean isCheckConstrValue();
    public void setState(Integer index, Integer type);
    public void removeState(Integer index);
    public void clearStates();
    public Integer getState(Integer index);
    public String getUUID();

}
