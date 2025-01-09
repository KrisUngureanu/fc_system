package kz.tamur.ekyzmet.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cifs.or2.kernel.KrnObject;

public final class Program implements Serializable {

	private static final long serialVersionUID = -536782288585562413L;
	
	public final KrnObject obj;
	public final String nameRu;
	public final List<Section> sections;
	public final Map<Long, SubSection> subSections;
	public final Map<Long, SubSection> subSectionsByKspId;
	public final Map<Long, Answer> answers;
	public final Map<Long, Block> blocks;
	public final Map<Long, Question> questions;
	
	public Program(KrnObject obj, String nameRu) {
		super();
		this.obj = obj;
		this.nameRu = nameRu;
		this.sections = new ArrayList<Section>();
		this.subSections = new HashMap<Long, SubSection>();
		this.subSectionsByKspId = new HashMap<Long, SubSection>();
		this.answers = new HashMap<Long, Answer>();
		this.questions = new HashMap<Long, Question>();
		this.blocks = new HashMap<Long, Block>();
	}
	
	public Section getSection(long secId) {
		Iterator<Section> it = sections.iterator();
		while (it.hasNext()) {
			Section sec = it.next();
			if (sec.obj.id == secId)
				return sec;
		}
		return null;
	}
	
	public Section getFirstSection() {
		return sections.size() > 0 ? sections.get(0) : null;
	}

	public Section getNextSection(Section sec) {
		Iterator<Section> it = sections.iterator();
		while (it.hasNext())
			if (it.next().obj.id == sec.obj.id)
				return it.hasNext() ? it.next() : null;
		return null;
	}
	
	public int getTimeRemaining(Section sec) {
		int time = 0;
		for (Section s : sections)
			if (s.obj.id == sec.obj.id)
				time = s.time;
			else if (time > 0)
				time += s.time;
		return time;
	}
}
