package kz.tamur.ods;

import java.util.ArrayList;
import java.util.List;

import com.cifs.or2.kernel.KrnAttribute;

import kz.tamur.util.Pair;

public class AttrRequestCache {
	public String sql;
	public List<Pair<KrnAttribute, Integer>> attrs = new ArrayList<Pair<KrnAttribute, Integer>>();
	public int joinTableCount;
	public int whereTableCount;
	
	public List<AttrRequestCache> parts = new ArrayList<AttrRequestCache>();
	
	public void clear() {
		this.sql = null;
		this.attrs = null;
		this.joinTableCount = 0;
		this.whereTableCount = 0;
		this.parts.clear();
	}
	
	public AttrRequestCache createPart() {
		AttrRequestCache part = new AttrRequestCache();
		parts.add(part);
		return part;
	}
}
