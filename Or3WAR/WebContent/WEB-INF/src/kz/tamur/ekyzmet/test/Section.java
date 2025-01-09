package kz.tamur.ekyzmet.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cifs.or2.kernel.KrnObject;

public final class Section implements Serializable {

	private static final long serialVersionUID = 8979590715576706049L;
	
	public final KrnObject obj;
	public final String nameRu;
	public final String nameKz;
	public final int time;
	public final int level;
	public final List<SubSection> sections;
	public final boolean joinQuestions;
	public SubSection joinedSubSection;
	public final boolean navNext;
	
	public Section(KrnObject obj, String nameRu, String nameKz, int time, int level, boolean joinQuestions, boolean navNext) {
		super();
		this.obj = obj;
		this.nameRu = nameRu;
		this.nameKz = nameKz;
		this.time = time;
		this.level = level;
		this.sections = new ArrayList<SubSection>();
		this.joinQuestions = joinQuestions;
		this.navNext = navNext;
	}
	
}
