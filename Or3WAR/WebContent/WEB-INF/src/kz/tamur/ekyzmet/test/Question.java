package kz.tamur.ekyzmet.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cifs.or2.kernel.KrnObject;

public final class Question implements Serializable {

	private static final long serialVersionUID = -9159117887729485281L;

	public final Block blk;
	public final KrnObject obj;
	public final String text;
	public final List<Answer> answers;
	
	public Question(Block blk, KrnObject obj, String text) {
		super();
		this.obj = obj;
		this.text = text;
		this.answers = new ArrayList<Answer>();
		
		this.blk = blk;
		blk.questions.add(this);
	}
}
