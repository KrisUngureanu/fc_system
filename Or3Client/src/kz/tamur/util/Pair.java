package kz.tamur.util;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 24.08.2004
 * Time: 16:16:51
 * To change this template use File | Settings | File Templates.
 */
public class Pair<F,S> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final F first;
    public final S second;
    private final int hash;

    public Pair(F o1, S o2) {
        this.first = o1;
        this.second = o2;

        int hash = 7;
		hash = Funcs.add(Funcs.mul(31, hash), (first != null ? first.hashCode() : 0)); 
		hash = Funcs.add(Funcs.mul(31, hash), (second != null ? second.hashCode() : 0)); 
		this.hash = hash;
    }

    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
        if (obj instanceof Pair) {
            return (first.equals(((Pair)obj).first) && second.equals(((Pair)obj).second));   
        } else {
            return false;
        }
    }

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		return new StringBuilder("First: ").append(first).append(", Second: ").append(second).toString();
	}
}
