package kz.tamur.admin;
import java.util.Hashtable;

import javax.swing.Icon;

import kz.tamur.util.ImageOverlay;

import com.cifs.or2.kernel.KrnClass;

// Загрузка и хэширование иконок для дерева классов
public class ClassTreeIconLoader {
	private static final int PROP_SHOW_COUNT = 2;
	private static final int WITH_REPL = 0;
	private static final int WITH_VIRTUAL = 1;
	private static Icon simpleIcon = kz.tamur.rt.Utils.getImageIcon("class");
	private static Icon replIcon = kz.tamur.rt.Utils.getImageIcon("class-repl");
	private static Icon virtualIcon = kz.tamur.rt.Utils.getImageIcon("class-virtual");
	private static Hashtable<Integer,Icon> hash = new Hashtable<Integer,Icon>();
	private static int[] bitSum = new int[PROP_SHOW_COUNT];
	private static int[] comb = new int[PROP_SHOW_COUNT];
	static{		
		for(int i=0;i<PROP_SHOW_COUNT;i++){			
			bitSum[i] = 1 << i;
		}
		loadImages(0);
		
	}
	
	public static Icon getIcon(KrnClass cls){
		return getIcon(cls.isRepl, cls.isVirtual());
	}
	
	public static Icon getIcon(boolean isRepl, boolean isVirtual) {
		int sum = 0;		
		if(isRepl)sum += bitSum[WITH_REPL];		
		if(isVirtual)sum += bitSum[WITH_VIRTUAL];
		return hash.get(sum);
	}
	
	private static void loadImages(int pos) {
		if (pos == PROP_SHOW_COUNT) {
			ImageOverlay imgOverlay = new ImageOverlay(simpleIcon);
			if (comb[WITH_VIRTUAL] == 1)
				imgOverlay.addLayer(virtualIcon);
			if (comb[WITH_REPL] == 1)
				imgOverlay.addLayer(replIcon);
			hash.put(calcHash(), imgOverlay.finalImage());
		} else {
			comb[pos] = 0;
			loadImages(pos + 1);
			comb[pos] = 1;
			loadImages(pos + 1);
		}
	}
	
	private static int calcHash() {
		int sum = 0;
		for (int i = 0; i < PROP_SHOW_COUNT; i++) {
			sum += bitSum[i] * comb[i];
		}
		return sum;
	}
}