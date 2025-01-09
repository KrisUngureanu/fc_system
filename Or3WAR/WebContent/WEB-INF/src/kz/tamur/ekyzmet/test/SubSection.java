package kz.tamur.ekyzmet.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.cifs.or2.kernel.KrnObject;

public final class SubSection implements Serializable {

	private static final long serialVersionUID = 8312927820730021729L;

	public final Section sec;
	public final KrnObject obj;
	public final String nameRu;
	public final String nameKz;
	public final KrnObject kspObj;
	public final int qsnCount;
	public final int level;
	public final List<Block> blocks;
	public final boolean shuffle;
	
	public SubSection(Section sec, KrnObject obj, String nameRu, String nameKz, KrnObject kspObj, int qsnCount, int level, boolean shuffle) {
		super();
		this.sec = sec;
		this.obj = obj;
		this.nameRu = nameRu;
		this.nameKz = nameKz;
		this.kspObj = kspObj;
		this.qsnCount = qsnCount;
		this.level = level;
		this.blocks = new ArrayList<Block>();
		this.shuffle = shuffle;
	}
}
