package kz.tamur.comps;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class CommonUtil {
    private static Map<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
    
    private static Font defaultFont = new Font("Dialog", Font.PLAIN, 11);
    private static Font defaultComponentFont = new Font("Dialog", Font.PLAIN, 12);
   
    private static Color lightSysColor = new Color(216, 221, 231);
    private static Color darkShadowSysColor = new Color(70, 81, 106);

    public static Font getDefaultComponentFont() {
        return defaultComponentFont;
    }
    
    public static Font getDefaultFont() {
        return defaultFont;
    }

    public static Color getLightSysColor() {
        return lightSysColor;
    }
    
    public static Color getDarkShadowSysColor() {
        return darkShadowSysColor;
    }

    public static ImageIcon getImageIcon(String name) {
        ImageIcon icon = icons.get(name);
        if (icon == null) {
        try {
            icon = new ImageIcon(CommonUtil.class.getResource("images/" + name + ".gif"));
            icons.put(name, icon);
        } catch (Exception e) {
            System.out.println("Изображение не найдено! Имя: (\"" + name + "\")");
            // e.printStackTrace();
            return null;
        }
        }
        return icon;
    }
    
    public static ImageIcon getImageIconFull(String path) {
        ImageIcon icon = icons.get(path);
        try {
            if (icon == null) {
                if (CommonUtil.class.getResource("images/" + path) != null) {
                    icon = new ImageIcon(CommonUtil.class.getResource("images/" + path));
                }
                icons.put(path, icon);
            }
        } catch (NullPointerException e) {
            System.out.println("Изображение не найдено! Имя: (\"" + path + "\")");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icon;
    }
    
    
    public static String getString(KrnObject obj, String attrName, long langId, Kernel krn)
		    throws KrnException {
		String res = null;
		String[] strs = krn.getStrings(obj, attrName, langId, 0);
		if (strs.length > 0)
		    res = strs[strs.length - 1];
		return res;
	}

    public static void copy(KrnAttribute dst, KrnAttribute src) {
        dst.id = src.id;
        dst.classId = src.classId;
        dst.typeClassId = src.typeClassId;
        dst.name = src.name;
        dst.collectionType = src.collectionType;
        dst.isUnique = src.isUnique;
        dst.isIndexed = src.isIndexed;
        dst.isMultilingual = src.isMultilingual;
        dst.size = src.size;
        dst.rAttrId = src.rAttrId;
        dst.sAttrId = src.sAttrId;
        dst.sDesc = src.sDesc;
        dst.isRepl = src.isRepl;
        dst.flags = src.flags;
        dst.tname = src.tname;
    }

}
