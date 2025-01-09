package kz.tamur.admin;
import java.util.Hashtable;
import javax.swing.Icon;

import com.cifs.or2.kernel.KrnAttribute;

import kz.tamur.util.ImageOverlay;

/**
 * Created by Eclipse
 * User: Naik
 * Date: 01.03.2011
 * Time: 11:04:00
 * To change this template use File | Settings | File Templates.
 */
//Загрузка и хэширование иконок для дерева атрибутов
public class AttributeTreeIconLoader {
	private static final int PROP_SHOW_COUNT = 6;//Кол-во отображаемых свойств атрибута	
	private static final int WITH_REPL 		= 0;//Реплицируемый
	private static final int WITH_MULTI 	= 1;//Мультиязычный
	private static final int WITH_MAND		= 2;//Обязательный
	private static final int WITH_INDEX		= 3;//Индексируемый
	private static final int WITH_REVERSE	= 4;//Обратный
	private static final int WITH_AGGR		= 5;//Аггрегатный
	private static Icon simpleIcon = kz.tamur.rt.Utils.getImageIcon("attr");
	private static Icon replIcon = kz.tamur.rt.Utils.getImageIcon("attr-repl");
	private static Icon multIcon = kz.tamur.rt.Utils.getImageIcon("attr-mult");
	private static Icon mandIcon = kz.tamur.rt.Utils.getImageIcon("attr-mand");
	private static Icon indexIcon = kz.tamur.rt.Utils.getImageIcon("attr-index");
	private static Icon reverseIcon = kz.tamur.rt.Utils.getImageIcon("attr-reverse");
	private static Icon aggrIcon = kz.tamur.rt.Utils.getImageIcon("attr-aggr");
	private static Hashtable<Integer,Icon> hash = new Hashtable<Integer,Icon>();	
	private static int[] bitSum = new int[PROP_SHOW_COUNT];
	private static int[] comb = new int[PROP_SHOW_COUNT];//комбинация параметров
	static{		
		for(int i=0;i<PROP_SHOW_COUNT;i++){			
			bitSum[i] = 1 << i;
		}
		loadImages(0);
		
	}
	
	public static Icon getIcon(KrnAttribute krnAttr){
		return getIcon(krnAttr.isRepl, krnAttr.isMultilingual, krnAttr.isMandatory(), 
        		krnAttr.isIndexed, krnAttr.rAttrId != 0, krnAttr.isAggregate());
	}
	
	public static Icon getIcon(boolean isRepl,boolean isMult,boolean isMand,
			boolean isIndex,boolean isReverse,boolean isAggr){		
		int sum = 0;		
		if(isRepl)sum += bitSum[WITH_REPL];		
		if(isMult)sum += bitSum[WITH_MULTI];
		if(isMand)sum += bitSum[WITH_MAND];
		if(isIndex)sum += bitSum[WITH_INDEX];
		if(isReverse)sum += bitSum[WITH_REVERSE];
		if(isAggr)sum += bitSum[WITH_AGGR];
		return hash.get(sum);
		
	}
	//Генерация иконок
	private static void loadImages(int pos){
		if(pos == PROP_SHOW_COUNT){
			ImageOverlay imgOverlay = new ImageOverlay(simpleIcon);
			if(comb[WITH_REVERSE] == 1)
				imgOverlay.addLayer(reverseIcon);
			if(comb[WITH_REPL] == 1)
				imgOverlay.addLayer(replIcon);			
			if(comb[WITH_MULTI] == 1)
				imgOverlay.addLayer(multIcon);
			if(comb[WITH_MAND] == 1)
				imgOverlay.addLayer(mandIcon);			
			if(comb[WITH_INDEX] == 1)
				imgOverlay.addLayer(indexIcon);
			if(comb[WITH_AGGR] == 1){
				imgOverlay.addLayer(aggrIcon);
			}
			hash.put(calcHash(), imgOverlay.finalImage());			
		}else{
			comb[pos] = 0;			
			loadImages(pos + 1);
			comb[pos] = 1;
			loadImages(pos + 1);
		}
	}
	private static int calcHash(){
		int sum = 0;
		for(int i=0;i<PROP_SHOW_COUNT;i++){
			sum += bitSum[i] * comb[i];
		}
		return sum;
	}
}
