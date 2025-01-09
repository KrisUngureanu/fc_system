package kz.tamur.ekyzmet.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cifs.or2.kernel.KrnObject;

public final class Block implements Serializable {

	private static final long serialVersionUID = -1960946269062088644L;
	
	public static final int MT_NONE = 0;
	public static final int MT_AUDIO = 1;
	public static final int MT_VIDEO = 2;
	public static final int MT_IMAGE = 3;

	public final KrnObject obj;
	public final String name;
	public final String text;
	public final KrnObject mediaObj;
	public final int mediaType;
	public final SubSection subSec;
	public final List<Question> questions;
	
	public Block(SubSection subSec, KrnObject obj, String name, String text, KrnObject mediaObj, int mediaType) {
		super();
		this.subSec = subSec;
		this.obj = obj;
		this.name = name;
		this.text = text;
		this.mediaObj = mediaObj;
		this.mediaType = mediaType;
		this.questions = new ArrayList<Question>();
		subSec.blocks.add(this);
		if (subSec.sec.joinQuestions) {
			subSec.sec.joinedSubSection.blocks.add(this);
		}
	}
	
}
