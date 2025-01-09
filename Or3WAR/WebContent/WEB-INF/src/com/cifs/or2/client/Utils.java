package com.cifs.or2.client;

import java.awt.*;
import java.awt.Cursor;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.io.*;

import com.cifs.or2.kernel.*;

import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.web.controller.WebController;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.gui.*;

import javax.swing.*;
import javax.swing.text.StyleContext;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import kz.tamur.common.ErrorCodes;
import kz.tamur.comps.Constants;
import kz.tamur.comps.PropertyValue;
import kz.tamur.or3.util.PathElement;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.rt.InterfaceManagerFactory;

public class Utils {
    public static String getTitle(KrnObject obj, KrnAttribute attr)
            throws KrnException {
        String title = "" + obj.id;
        if (attr != null) {
            if (attr.typeClassId == Kernel.IC_STRING) {
                String[] values = Kernel.instance().getStrings(obj, attr, 0, 0);
                if (values.length > 0)
                    title = obj.id + " " + values[0];
            } else if (attr.typeClassId == Kernel.IC_INTEGER) {
                long[] values = Kernel.instance().getLongs(obj, attr, 0);
                if (values.length > 0)
                    title = obj.id + " " + values[0];
            }
        }
        return title;
    }

    public static String getTitle(KrnObject obj) {
        String title = "Значение не присвоено";
        if (obj != null && obj.classId != 0) {
            try {
                KrnClass cls = Kernel.instance().getClass(obj.classId);
                KrnAttribute uattr = Kernel.instance().findUniqueAttribute(cls);
                title = getTitle(obj, uattr);
            } catch (KrnException e) {
                e.printStackTrace();
            }
        }
        return title;
    }

    public static void copy(KrnObject dst, KrnObject src) {
        dst.id = src.id;
        dst.uid = src.uid;
        dst.classId = src.classId;
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
    }

    public static KrnObject[] makeObjectArray(Collection objs) {
        KrnObject[] res = new KrnObject[objs.size()];
        int i = 0;
        for (Iterator it = objs.iterator(); it.hasNext(); ++i)
            res[i] = (KrnObject) it.next();
        return res;
    }

    public static long[] makeObjectIdArray(Collection objs) {
        long[] res = new long[objs.size()];
        int i = 0;
        for (Iterator it = objs.iterator(); it.hasNext(); ++i)
            res[i] = ((KrnObject) it.next()).id;
        return res;
    }

    public static LongChange[] makeLChangeArray(Collection lcs) {
        LongChange[] res = new LongChange[lcs.size()];
        int i = 0;
        for (Iterator it = lcs.iterator(); it.hasNext(); ++i)
            res[i] = (LongChange) it.next();
        return res;
    }

    public static StringChange[] makeSChangeArray(Collection scs) {
        StringChange[] res = new StringChange[scs.size()];
        int i = 0;
        for (Iterator it = scs.iterator(); it.hasNext(); ++i)
            res[i] = (StringChange) it.next();
        return res;
    }

    public static FloatChange[] makeFChangeArray(Collection fcs) {
        FloatChange[] res = new FloatChange[fcs.size()];
        int i = 0;
        for (Iterator it = fcs.iterator(); it.hasNext(); ++i)
            res[i] = (FloatChange) it.next();
        return res;
    }

    public static DateChange[] makeDChangeArray(Collection dcs) {
        DateChange[] res = new DateChange[dcs.size()];
        int i = 0;
        for (Iterator it = dcs.iterator(); it.hasNext(); ++i)
            res[i] = (DateChange)it.next();
        return res;
    }

    public static TimeChange[] makeTChangeArray(Collection dcs) {
        TimeChange[] res = new TimeChange[dcs.size()];
        int i = 0;
        for (Iterator it = dcs.iterator(); it.hasNext(); ++i)
            res[i] = (TimeChange)it.next();
        return res;
    }

    public static BlobValue[] makeBValueArray(Collection bvs) {
        BlobValue[] res = new BlobValue[bvs.size()];
        int i = 0;
        for (Iterator it = bvs.iterator(); it.hasNext(); ++i)
            res[i] = (BlobValue) it.next();
        return res;
    }

    public static KrnAttribute[] getAttributesForPath(String path)
            throws KrnException {
        return (InterfaceManagerFactory.instance().getManager() != null)
            ? getAttributesForPath(path, InterfaceManagerFactory.instance().getManager().getKernel())
            : getAttributesForPath(path, Kernel.instance());
    }

    public static KrnAttribute[] getAttributesForPath(String path, Kernel krn)
            throws KrnException {
        if (path == null)
            return null;
        StringTokenizer st = new StringTokenizer(path, ".");
        int count = st.countTokens();
        KrnAttribute[] res = new KrnAttribute[(count == 0) ? 0 : count - 1];
        if (count > 0) {
        	String str = st.nextToken();
        	int p = str.indexOf('(');
        	if (p != -1)
        		str = str.substring(0, p);
            ClassNode cnode = krn.getClassNodeByName(str);
            for (int i = 0; i < count - 1; ++i) {
                KrnAttribute attr = cnode.getAttribute(st.nextToken());
                res[i] = attr;
                if (attr == null) return null;
                cnode = krn.getClassNode(attr.typeClassId);
            }
        }
        return res;
    }

    public static Pair[] parsePath(String path, Kernel krn) throws KrnException {
        if (path == null)
            return null;
        StringTokenizer st = new StringTokenizer(path, ".");
        int count = st.countTokens();
        Pair[] res = new Pair[(count == 0) ? 0 : count - 1];
        if (count > 0) {
            ClassNode cnode = krn.getClassNodeByName(st.nextToken());
            for (int i = 0; i < count - 1; ++i) {
                PathElement pe = kz.tamur.util.Funcs.parseAttrName(st.nextToken());
                KrnAttribute attr = cnode.getAttribute(pe.name);
                if (attr == null)
                    return null;
                if (pe.index instanceof String && attr.collectionType == 0)
                    pe.index = new Integer(-1);
                res[i] = new Pair(attr, pe.index);
                if (pe.castClassName != null) {
                    cnode = krn.getClassNodeByName(pe.castClassName);
                } else {
                	cnode = krn.getClassNode(attr.typeClassId);
                }
            }
        }
        return res;
    }
    
    public static PathElement2[] parsePath2(String path) throws KrnException {
        return (InterfaceManagerFactory.instance().getManager() != null)
            ? parsePath2(path, InterfaceManagerFactory.instance().getManager().getKernel())
            : parsePath2(path, Kernel.instance());
    }

	public static PathElement2[] parsePath2(String path, Kernel krn)
	throws KrnException {
		String[] strs = path.split("\\.");
		if (strs.length > 0) {
			List<PathElement2> res = new ArrayList<PathElement2>(strs.length);
			PathElement pe = kz.tamur.util.Funcs.parseAttrName(strs[0]);
			ClassNode type = (pe.castClassName == null) ?
					krn.getClassNodeByName(pe.name)
					: krn.getClassNodeByName(pe.castClassName);
			res.add(new PathElement2(type.getKrnClass(), null, null, pe.filterUid));
			for (int i = 1; i < strs.length; i++) {
				pe = kz.tamur.util.Funcs.parseAttrName(strs[i]);
				KrnAttribute attr = type.getAttribute(pe.name);
				if (attr == null) {
					throw new KrnException(ErrorCodes.ATTRIBUTE_NOT_FOUND, "Атрибут '" + pe.name + "' не найден в пути '" + path);
				}
				if (attr.collectionType == 0 && !"UNKNOWN".equals(pe.index))
					throw new KrnException(ErrorCodes.ATTRIBUTE_NOT_FOUND, "Неверное использование немножественного атрибута '" + pe.name + "' в пути '" + path);
				type = (pe.castClassName == null) ?
						krn.getClassNode(attr.typeClassId)
						: krn.getClassNodeByName(pe.castClassName);
				res.add(new PathElement2(type.getKrnClass(), attr, pe.index, pe.filterUid));
			}
			return res.toArray(new PathElement2[res.size()]);
		}
		return new PathElement2[0];
	}


    public static String getPathForAttributes(KrnAttribute[] attrs) {
        String res = "";
        if (attrs != null && attrs.length > 0) {
            StringBuffer sb = new StringBuffer(attrs[0].name);
            for (int i = 1; i < attrs.length; ++i) {
            	if (attrs[i].id == 0) {
            		sb.append("<" + attrs[i].name + ">");
            	} else {
            		sb.append("." + attrs[i].name);
            	}
            }
            res = sb.toString();
        }
        return res;
    }

    public static KrnObject findObject(KrnObject[] objs, int id) {
        for (int i = 0; i < objs.length; ++i)
            if (objs[i].id == id)
                return objs[i];
        return null;
    }

    public static long getDataLangId(Kernel krn) {
        KrnObject dataLang = krn.getDataLanguage();
        return (dataLang != null) ? dataLang.id : 0;
    }

    public static long getInterfaceLangId() {
        return (InterfaceManagerFactory.instance().getManager() != null)
            ? getInterfaceLangId(InterfaceManagerFactory.instance().getManager().getKernel())
            : getInterfaceLangId(Kernel.instance());
    }

    public static KrnObject getInterfaceLang() {
        return (InterfaceManagerFactory.instance().getManager() != null)
            ? InterfaceManagerFactory.instance().getManager().getKernel().getInterfaceLanguage()
            : Kernel.instance().getInterfaceLanguage();
    }

    public static KrnObject getInterfaceLang(Kernel krn) {
        return krn.getInterfaceLanguage();
    }
 
    public static long getInterfaceLangId(Kernel krn) {
        KrnObject dataLang = krn.getInterfaceLanguage();
        return (dataLang != null) ? dataLang.id : 0;
    }

    public static String getString(KrnObject obj, String attrName, long langId)
            throws KrnException {
        String res = null;
        String[] strs = Kernel.instance().getStrings(obj, attrName, langId, 0);
        if (strs.length > 0)
            res = strs[strs.length - 1];
        return res;
    }

    public static void copy(File src, File dst)
            throws IOException, FileNotFoundException {
        FileInputStream is = new FileInputStream(src);
        byte[] buf = new byte[(int) src.length()];
        is.read(buf);
        is.close();
        FileOutputStream os = new FileOutputStream(dst);
        os.write(buf);
        os.close();
    }

    public static PropDlg getPropDlg(JComponent comp, boolean hasClearBtn) {
        Container c = comp.getTopLevelAncestor();
        return getPropDlg(c, hasClearBtn);
    }

    public static PropDlg getPropDlg(Container comp, boolean hasClearBtn) {
        PropDlg dlg = null;
        if (comp instanceof Dialog)
            dlg = new PropDlg((JDialog) comp, hasClearBtn);
        else
            dlg = new PropDlg((JFrame) comp, hasClearBtn);

        return dlg;
    }

    public static void drawRects(Graphics g, int compWidth, int compHeight) {
        g.fillRect(0, 0, 4, 4);
        g.fillRect(0, compHeight - 4, 4, 4);
        g.fillRect(compWidth - 4, 0, 4, 4);
        g.fillRect(compWidth - 4, compHeight - 4, 4, 4);
    }

    //Получение текущего объекта OrEnum по его числовому значению
    public static OrEnum getCurrentEnum(OrEnum[] ens, int val) {
        for (int i = 0; i < ens.length; i++)
            if (ens[i].getIntVal() == val)
                return ens[i];
        return ens[0];
    } //

    //Пересчёт ширины в зависимости от текста
    public static int recalcWidth(String text, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, g);
        Double w = new Double(rect.getWidth());
        return w.intValue();
    }

    //Пересчёт размеров в зависимости от текста
    public static Dimension recalcSize(String text, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, g);
        Double w = new Double(rect.getWidth());
        Double h = new Double(rect.getHeight());
        return new Dimension(w.intValue(), h.intValue());
    }

    //Получение позиции относительно центра экрана в зависимости от размеров окна
    public static Point centerOnScreen(int width, int height) {
        Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
        int x = sz.width / 2 - width / 2;
        int y = sz.height / 2 - height / 2;
        return new Point(x, y);
    }

    //Перемещение элемента в ArrayList
    public static ArrayList moveElementTo(ArrayList list, int index, int newIndex) {
        Object o = list.remove(index);
        Vector v = new Vector(list);
        v.insertElementAt(o, newIndex);
        ArrayList res = new ArrayList();
        for (int i = 0; i < v.size(); i++) {
            res.add(v.get(i));
        }
        return res;
    }

    public static String convertToTextDate(String d) {
        StringBuffer res = new StringBuffer();
        StringTokenizer st = new StringTokenizer(d, " ");
        int days = Integer.parseInt(st.nextToken());
        int highDay = days/10;
        int lowDay = days - highDay*10;
        if (lowDay > 0 || highDay == 1) {
            switch (highDay) {
                case 0:
                    res.append(dayToText(lowDay));
                    break;
                case 1:
                    res.append(dayToText(lowDay + 10));
                    break;
                case 2:
                    res.append("двадцать ");
                    res.append(dayToText(lowDay).toLowerCase(Constants.OK));
                    break;
                case 3:
                    res.append("тридцать ");
                    res.append(dayToText(lowDay).toLowerCase(Constants.OK));
                    break;
            }
        } else {
            switch (highDay) {
                case 2:
                    res.append("двадцатого");
                    break;
                case 3:
                    res.append("тридцатого");
            }
        }

        String month = st.nextToken().toLowerCase(Constants.OK);
        if (month.endsWith("ь") || month.endsWith("й")) month = month.substring(0, month.length()-1) + "я";
        else month = month + "а";
        res.append(" " + month);
        String year = st.nextToken();
        res.append(" " + yearToString(year) + " года");
        return res.toString();
    }

    private static String dayToText (int day) {
        switch (day) {
            case 1:
                return "первого";
            case 2:
                return "Второго";
            case 3:
                return "третьего";
            case 4:
                return "четвертого";
            case 5:
                return "пятого";
            case 6:
                return "шестого";
            case 7:
                return "седьмого";
            case 8:
                return "восьмого";
            case 9:
                return "девятого";
            case 10:
                return "десятого";
            case 11:
                return "одиннадцатого";
            case 12:
                return "двенадцатого";
            case 13:
                return "тринадцатого";
            case 14:
                return "четырнадцатого";
            case 15:
                return "пятнадцатого";
            case 16:
                return "шестнадцатого";
            case 17:
                return "семнадцатого";
            case 18:
                return "восемнадцатого";
            case 19:
                return "девятнадцатого";
        }
        return "";
    }

    private static String yearToString (String year) {
        StringBuffer res = new StringBuffer();
        int y = Integer.parseInt(year);
        int i3 = y/1000;
        if (i3 == 1) {
            res.append("одна тысяча");
        } else if (y == 2000) {
            res.append("двухтысячного");
        } else if (i3 == 2) {
            res.append("две тысячи");
        }

        int i2 = (y-i3*1000)/100;
        switch (i2) {
            case 1:
                res.append(" сто");
                break;
            case 9:
                res.append(" девятьсот");
                break;
            case 8:
                res.append(" восемьсот");
                break;
        }

        int i1 = (y-i3*1000-i2*100) / 10;
        int i0 = y - i3*1000 - i2*100 - i1*10;

        if (i0 > 0) {
            switch (i1) {
                case 9:
                    res.append(" девяносто");
                    break;
                case 8:
                    res.append(" восемьдесят");
                    break;
                case 7:
                    res.append(" семьдесят");
                    break;
                case 6:
                    res.append(" шестьдесят");
                    break;
                case 5:
                    res.append(" пятьдесят");
                    break;
                case 4:
                    res.append(" сорок");
                    break;
                case 3:
                    res.append(" тридцать");
                    break;
                case 2:
                    res.append(" двадцать");
                    break;
                case 1:
                    i0+=10;
                    break;
            }
        } else {
            switch (i1) {
                case 9:
                    res.append(" девяностого");
                    break;
                case 8:
                    res.append(" восьмидесятого");
                    break;
                case 7:
                    res.append(" семидесятого");
                    break;
                case 6:
                    res.append(" шестидесятого");
                    break;
                case 5:
                    res.append(" пятидесятого");
                    break;
                case 4:
                    res.append(" сорокового");
                    break;
                case 3:
                    res.append(" тридцатого");
                    break;
                case 2:
                    res.append(" двадцатого");
                    break;
                case 1:
                    i0+=10;
                    break;
            }
        }
        switch (i0) {
            case 1:
                res.append(" первого");
                break;
            case 2:
                res.append(" второго");
                break;
            case 3:
                res.append(" третьего");
                break;
            case 4:
                res.append(" четвертого");
                break;
            case 5:
                res.append(" пятого");
                break;
            case 6:
                res.append(" шестого");
                break;
            case 7:
                res.append(" седьмого");
                break;
            case 8:
                res.append(" восьмого");
                break;
            case 9:
                res.append(" девятого");
                break;
            case 10:
                res.append(" десятого");
                break;
            case 11:
                res.append(" одиннадцатого");
                break;
            case 12:
                res.append(" двенадцатого");
                break;
            case 13:
                res.append(" тринадцатого");
                break;
            case 14:
                res.append(" четырнадцатого");
                break;
            case 15:
                res.append(" пятнадцатого");
                break;
            case 16:
                res.append(" шестнадцатого");
                break;
            case 17:
                res.append(" семнадцатого");
                break;
            case 18:
                res.append(" восемнадцатого");
                break;
            case 19:
                res.append(" девятнадцатого");
                break;
        }
        return res.toString();
    }

    public static Font getDefaultFont() {
        return new Font("Tahoma", Font.PLAIN, 12);
    }

    public static String[] wrap(String str, FontMetrics fm, int width) {
        int w = 0;
        StringBuffer sb = new StringBuffer();
        List<String> res = new ArrayList<String>();
        Pattern p = Pattern.compile("\\S+\\s*");
        Matcher m = p.matcher(str);
        for (int i = 0; m.find(i); i = m.end()) {
            String s = str.substring(m.start(), m.end());
            int mw = fm.stringWidth(s);
            w += mw;
            if (w > width && sb.length() > 0) {
                res.add(sb.toString());
                sb = new StringBuffer();
                w = mw;
            }
            sb.append(s);
        }
        res.add(sb.toString());
        return res.toArray(new String[res.size()]);
    }

    public static StyleContext GetStyles() {
        StyleContext styles = new StyleContext();
        Style s = null;

        s = styles.addStyle("cblack", null);
        StyleConstants.setForeground(s, Color.black);

        s = styles.addStyle("cred", null);
        StyleConstants.setForeground(s, Color.red);

        s = styles.addStyle("cblue", null);
        StyleConstants.setForeground(s, Color.blue);

        s = styles.addStyle("cgreen", null);
        StyleConstants.setForeground(s, Color.green);

        s = styles.addStyle("t12", null);
        StyleConstants.setFontSize(s, 12);

        s = styles.addStyle("t14", null);
        StyleConstants.setFontSize(s, 14);

        s = styles.addStyle("t16", null);
        StyleConstants.setFontSize(s, 16);

       return styles;
    }

    public static Cursor getHelpCursor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(Utils.class.getResource("images/HelpCursor.gif"));
        return toolkit.createCustomCursor(image, new Point(0, 0), "helpcursor");
    }

    public static int getFilterFlag(String expr) {
        int res = 0;
        if (expr != null && expr.length() >= 4) {
            res = Character.digit(expr.charAt(3), 10);
        }
        return res;
    }
    
    public static String normalizePath(String path) {
    	return path.replaceAll("<.+?>", "");
    }

    public static String normalizePath2(String path) {
    	return path.replaceAll("<.+?>|\\[.*?\\]", "");
    }

    public static String createFileImg(PropertyValue pv, String prefix) {
        if (pv != null && !pv.isNull()) {
            byte[] b = pv.getImageValue();
            return createFileImg(b, prefix);
        } else {
            return null;
        }
    }

    public static String createFileImg(byte[] b, String prefix) {
        if (b != null && b.length > 0) {
            StringBuilder name = new StringBuilder();
            name.append(prefix);
            kz.tamur.rt.Utils.getHash(b, name);
            name.append(".").append(kz.tamur.rt.Utils.getSignature(b));
            String out = name.toString();
            File dst = Funcs.getCanonicalFile(WebController.IMG_HOME + File.separator + out);
            try {
                if (!dst.exists()) {
                    dst.createNewFile();
                    FileOutputStream os = new FileOutputStream(dst);
                    os.write(b);
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return out;
        } else {
            return null;
        }
    }
}