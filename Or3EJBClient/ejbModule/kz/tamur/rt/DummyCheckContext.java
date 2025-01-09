package kz.tamur.rt;

import kz.tamur.rt.adapters.OrRef;
import kz.tamur.lang.parser.ASTStart;

/*
 * User: vital
 * Date: 21.10.2005
 * Time: 10:03:23
 */

public class DummyCheckContext implements CheckContext {

    private OrRef ref;

    public DummyCheckContext(OrRef ref) {
        this.ref = ref;
    }

    public long getLangId() {
        return 0;
    }

    public int getReqGroup() {
        return 0;
    }

    public int getEnterDB() {
        return 0;
    }

    public boolean isActive() {
        return true;
    }

    public OrRef getRef() {
        return ref;
    }

    public String getCExpr() {
        return null;
    }

    public String getReqMsg() {
        return null;
    }

    public ASTStart getCTemplate() {
        return null;
    }

    public boolean isCheckConstr() {
        return false;
    }

    public boolean isCheckConstrValue() {
        return false;
    }

    public void setState(Integer index, Integer type) {

    }

    public void removeState(Integer index) {

    }

    public void clearStates() {

    }

    public Integer getState(Integer index) {
        return new Integer(0);
    }

    public String getConstrMsg() {
        return null;
    }

	@Override
	public String getUUID() {
		return null;
	}
}
