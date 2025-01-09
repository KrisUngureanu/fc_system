package kz.tamur.comps;

import static kz.tamur.rt.Utils.getImageIcon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ToolBarUI;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.users.Or3RightsNode;
import kz.tamur.guidesigner.users.Or3RightsTree;
import kz.tamur.guidesigner.users.PolicyNode;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.CheckContext;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.MapMap;
import kz.tamur.util.Pair;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.WebFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.StringValue;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 05.04.2004
 * Time: 17:23:00
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Utils.class.getName());
    private static PolicyNode policyNode;
    private static long ifcLangId = 0;
    private static int width=-1;
    private static int height=-1;

    public static void updateConstraints(OrGuiComponent c) {
        OrGuiContainer cnt = getContainer(c);
        if (cnt != null) {
            cnt.updateConstraints(c);
        }
    }

    public static Map getStrings(int[] objectIds, KrnAttribute[] path, int tr, Kernel krn) throws KrnException {
        Map res = new HashMap();
        for (int i = 0; i < objectIds.length; i++) {
            res.put(new Integer(objectIds[i]), new Integer(objectIds[i]));
        }
        for (int i = 0; i < path.length; i++) {
            KrnAttribute attr = path[i];
            long[] ids = Funcs.makeLongArray(res.values());
            MapMap mmap = null;
            boolean last = (i == path.length - 1);
            if (last) {
                long langId = com.cifs.or2.client.Utils.getDataLangId(krn);
                StringValue[] svs = krn.getStringValues(ids, path[i], langId, false, tr);
                mmap = Funcs.convertStringValues(svs, attr.collectionType == 1);
            } else {
                ObjectValue[] ovs = krn.getObjectValues(ids, path[i], tr);
                mmap = Funcs.convertObjectValues(ovs, attr.collectionType == 1);
            }
            for (Iterator it = res.keySet().iterator(); it.hasNext();) {
                Object key = it.next();
                Integer value = (Integer) res.get(key);
                SortedMap map = (SortedMap) mmap.get(value);
                if (map != null && map.size() > 0) {
                    Pair p = (Pair) map.get(map.lastKey());
                    if (last) {
                        res.put(key, p.first);
                    } else {
                        res.put(key, new Long(((KrnObject) p.first).id));
                    }
                } else {
                    it.remove();
                }
            }
        }
        return res;
    }

    public static OrGuiContainer getContainer(OrGuiComponent c) {
        Container parent = ((Component) c).getParent();
        while (parent != null && !(parent instanceof OrGuiContainer)) {
            parent = parent.getParent();
        }
        return (OrGuiContainer) parent;
    }

    public static void processStdCompProperties(OrGuiComponent c, PropertyValue pv) {
        if (!pv.isNull()) {
            PropertyNode prop = pv.getProperty();
            final String name = getParentName(prop);
            final String fullName = prop.getFullPath();
            if ("pos".equals(name) || "pref".equals(name) || "min".equals(name) || "max".equals(name) || "insets".equals(name)
                    || "view.showDateChooser".equals(fullName) || "pov.copy.copyTitle".equals(fullName)
                    || "pov.copy.copyPath".equals(fullName)) {
                updateConstraints(c);
            }
        }
    }

    private static String getParentName(PropertyNode prop) {
        String[] path = prop.getPath();
        if (path.length > 1) {
            return path[path.length - 2];
        }
        return null;
    }

    public static JToolBar createDesignerToolBar() {
        return new DesignerToolBar();
    }

    public static class DesignerToolBar extends JToolBar {
        private ToolBarUI ui;

        public DesignerToolBar() {
            super();
            ui = getUI();
            setFloatable(false);
            setRollover(true);
        }

        public void addSeparator() {
            ToolSeparator sep = new ToolSeparator();
            this.add(sep);
        }

        protected class ToolSeparator extends JSeparator {

            public ToolSeparator() {
                super();
                setOpaque(true);

                if (DesignerToolBar.this.getOrientation() == JToolBar.HORIZONTAL) {
                    int height = DesignerToolBar.this.getHeight();
                    setPreferredSize(new Dimension(10, height != 0 ? height : 22));
                    // setMaximumSize(new Dimension(10,
                    // DesignerToolBar.this.getHeight()));
                    // setMinimumSize(new Dimension(10,
                    // DesignerToolBar.this.getHeight()));
                } else {
                    setPreferredSize(new Dimension(DesignerToolBar.this.getWidth(), 4));
                    setMaximumSize(new Dimension(DesignerToolBar.this.getWidth(), 4));
                    setMinimumSize(new Dimension(DesignerToolBar.this.getWidth(), 4));
                }
            }

            public void paintComponent(Graphics g) {
                if (DesignerToolBar.this.getOrientation() == JToolBar.HORIZONTAL) {
                    int height = DesignerToolBar.this.getHeight();
                    int s = (height != 0) ? height : 22;
                    Color oldColor = g.getColor();
                    g.setColor(kz.tamur.rt.Utils.getDarkShadowSysColor());
                    g.drawLine(5, 0, 5, s);
                    g.setColor(kz.tamur.rt.Utils.getLightSysColor());
                    g.drawLine(6, 0, 6, s);
                    g.setColor(oldColor);
                } else {
                    int w = DesignerToolBar.this.getWidth();
                    g.setColor(kz.tamur.rt.Utils.getDarkShadowSysColor());
                    g.drawLine(0, 5, w, 5);
                }
            }
        }
    }

    public static void processBorder(OrGuiComponent c, OrFrame frame, Map props) {

        String text = "";
        int pos = TitledBorder.DEFAULT_POSITION;
        int align = TitledBorder.DEFAULT_JUSTIFICATION;
        Font borderFont = kz.tamur.rt.Utils.getDefaultFont();
        Color borderTitleColor = kz.tamur.rt.Utils.getDarkShadowSysColor();
        int thick = 1;
        Color borderColor = kz.tamur.rt.Utils.getDarkShadowSysColor();
        Border b = null;

        PropertyNode borderNode = null;
        borderNode = c.getProperties().getChild("border");
        PropertyValue pv = PropertyHelper.getPropertyValue(borderNode.getChild("borderColor"), c.getXml(), frame, c.getUUID(), c.getClass().getName());
        if (!pv.isNull()) {
            borderColor = pv.colorValue();
        }
        props.put(Constants.BORDER_COLOR, borderColor);

        pv = PropertyHelper.getPropertyValue(borderNode.getChild("borderThick"), c.getXml(), frame, c.getUUID(), c.getClass().getName());
        if (!pv.isNull()) {
            thick = pv.intValue();
        }
        props.put(Constants.BORDER_THICK, new Integer(thick));

        pv = PropertyHelper.getPropertyValue(borderNode.getChild("borderType"), c.getXml(), frame, c.getUUID(), c.getClass().getName());
        if (!pv.isNull()) {
            b = pv.borderValue();
        }
        props.put(Constants.BORDER_STYLE, b);
        PropertyNode titleNode = borderNode.getChild("borderTitle");
        PropertyValue pv1 = PropertyHelper.getPropertyValue(titleNode.getChild("text"), c.getXml(), frame, c.getUUID(), c.getClass().getName());
        if (!pv1.isNull()) {
            Pair p = pv1.resourceStringValue();
            String tUID = (String) p.first;
            props.put(Constants.BORDER_TEXT_UID, tUID);
            pv1 = PropertyHelper.getPropertyValue(titleNode.getChild("borderTitlePos"), c.getXml(), frame, c.getUUID(), c.getClass().getName());
            if (!pv1.isNull()) {
                switch (pv1.intValue()) {
                case Constants.CENTER_POSITION:
                    pos = TitledBorder.CENTER;
                    break;
                case Constants.CENTER1_POSITION:
                    pos = TitledBorder.ABOVE_TOP;
                    break;
                case Constants.CENTER2_POSITION:
                    pos = TitledBorder.BELOW_TOP;
                    break;
                }
                props.put(Constants.BORDER_POS, new Integer(pos));
            }
            pv1 = PropertyHelper.getPropertyValue(titleNode.getChild("borderTitleAlign"), c.getXml(), frame, c.getUUID(), c.getClass().getName());
            if (!pv1.isNull()) {
                switch (pv1.intValue()) {
                case Constants.CENTER_ALIGNMENT:
                    align = TitledBorder.CENTER;
                    break;
                case Constants.LEFT_ALIGNMENT:
                    align = TitledBorder.LEFT;
                    break;
                case Constants.RIGHT_ALIGNMENT:
                    align = TitledBorder.RIGHT;
                    break;
                }
                props.put(Constants.BORDER_JUST, new Integer(align));
            }
            pv1 = PropertyHelper.getPropertyValue(titleNode.getChild("font"), c.getXml(), frame, c.getUUID(), c.getClass().getName());
            if (!pv1.isNull()) {
                borderFont = pv1.fontValue();
                props.put(Constants.BORDER_FONT, borderFont);
            }
            pv1 = PropertyHelper.getPropertyValue(titleNode.getChild("fontColor"), c.getXml(), frame, c.getUUID(), c.getClass().getName());
            if (!pv1.isNull()) {
                borderTitleColor = pv1.colorValue();
                props.put(Constants.BORDER_FONT_COLOR, borderTitleColor);
            }
        }
    }

    public static Or3RightsTree getOr3RightsTree(Element rights) {
        Or3RightsNode root = new Or3RightsNode(rights, "or3rights");
        Or3RightsTree tree = new Or3RightsTree(root);
        return tree;
    }

    public static PolicyNode getPolicyNode(Kernel krn) {
        if (policyNode == null) {
            policyNode = new PolicyNode(krn);
            if (policyNode.getKrnObj() == null)
                policyNode = null;
        } else {
            policyNode.reload(krn);
        }
        return policyNode;
    }

    public static String getFilterNameById(long id) {
        Kernel krn = Kernel.instance();
        String name = "";
        try {
            KrnClass cls = krn.getClassByName("Filter");
            long langId = com.cifs.or2.client.Utils.getInterfaceLangId(krn);
            StringValue[] strs = krn.getStringValues(new long[] { id }, cls.id, "title", langId, false, 0);
            name = (strs.length > 0) ? strs[0].value : "";
        } catch (KrnException e) {
            log.error(e, e);
        }
        return name;
    }

    public static String parseObjectsToTitle(KrnObject[] objs) {
        String title = "";
        if (objs != null && objs.length > 0) {
            Kernel krn = Kernel.instance();
            try {
                KrnClass cls = krn.getClassByName("HiperTree");
                KrnAttribute attr = krn.getAttributeByName(cls, "title");
                /*
                 * java.util.List objIdList = new ArrayList();
                 * for (int i = 0; i < objs.length; i++) {
                 * KrnObject obj = objs[i];
                 * objIdList.add(obj);
                 * }
                 */
                long[] ids = Funcs.makeObjectIdArray(objs);
                StringValue[] strVals = krn.getStringValues(ids, attr, com.cifs.or2.client.Utils.getInterfaceLangId(), false, 0);
                for (int i = 0; i < strVals.length; i++) {
                    StringValue strVal = strVals[i];
                    if (strVal.value.length() > 0) {
                        title = ("".equals(title)) ? strVal.value : title + "," + strVal.value;
                    }
                }
                /*
                 * for (int i = 0; i < objs.length; i++) {
                 * KrnObject obj = objs[i];
                 * String[] strs = krn.getStrings(obj, "title",
                 * com.cifs.or2.client.Utils.getInterfaceLangId(), 0);
                 * if (strs.length > 0) {
                 * title = ("".equals(title)) ? strs[0] : title + "," +strs[0];
                 * }
                 * }
                 */
            } catch (Exception e) {
                log.error(e, e);
            }
        }
        return title;
    }

    /**
     * Получает текстовую строку выполняя переданную формулу
     * 
     * @param expr
     *            Формула
     * @param frame
     *            Фрейм компонента
     * @param ctx
     *            адаптер компонента
     * @return Возвращает объект соответствующего класса
     */
    public static Object evalExp(String expr, OrFrame frame, CheckContext ctx) {
        expr = expr.trim();
        ASTStart dataEvaluate = null;
        if (expr.length() > 0) {
            dataEvaluate = OrLang.createStaticTemplate(expr);
        }
        if (dataEvaluate != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(dataEvaluate, vc, ctx, new Stack<String>());
            } catch (Exception e) {
                ((WebFrame)frame).getLog(Utils.class).error(e, e);
            } finally {
                if (calcOwner) {
                    OrCalcRef.makeCalculations();
                }
            }
            Object rez = vc.get("RETURN");
            return rez;
        }
        return null;
    }
    
    public static Object evalExp(String expr, OrFrame frame, CheckContext ctx, Map<String, Object> vc) {
        expr = expr.trim();
        ASTStart dataEvaluate = null;
        if (expr.length() > 0) {
            dataEvaluate = OrLang.createStaticTemplate(expr);
        }
        if (dataEvaluate != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                orlang.evaluate(dataEvaluate, vc, ctx, new Stack<String>());
            } catch (Exception e) {
                ((WebFrame)frame).getLog(Utils.class).error(e, e);
            } finally {
                if (calcOwner) {
                    OrCalcRef.makeCalculations();
                }
            }
            Object rez = vc.get("RETURN");
            return rez;
        }
        return null;
    }
    
   /**
     * Получает текстовую строку выполняя переданную формулу
     * 
     * @param expr
     *            Формула
     * @param frame
     *            Фрейм компонента
     * @param ctx
     *            адаптер компонента
     * @return Строка которая получается путём выполнения формулы и взятия конечной переменной <code>RETURN</code>>
     */
    public static String getExpReturn(String expr, OrFrame frame, CheckContext ctx) {
        expr = expr.trim();
        ASTStart dataEvaluate = null;
        if (expr.length() > 0) {
            dataEvaluate = OrLang.createStaticTemplate(expr);
        }
        if (dataEvaluate != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            boolean calcOwner = OrCalcRef.setCalculations();
            try {
                Map<String, Object> vc = new HashMap<String, Object>();
                orlang.evaluate(dataEvaluate, vc, ctx, new Stack<String>());
                Object rez = vc.get("RETURN");
                if (rez != null) {
                	return rez.toString();
                } 
            } catch (Exception e) {
            	((WebFrame)frame).getLog(Utils.class).error(e, e);
            } finally {
                if (calcOwner)
                	OrCalcRef.makeCalculations();
            }
        }
        return null;
    }

    public static Cursor getHelpCursor() {
        if (kz.tamur.rt.Utils.helpCursor == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = getImageIcon("HelpCursor").getImage();
            kz.tamur.rt.Utils.helpCursor = toolkit.createCustomCursor(image, new Point(0, 0), "helpcursor");
        }
        return kz.tamur.rt.Utils.helpCursor;
    }

    public static void lookAndFeelMenuItem(JMenuItem mi, String text, String iconName) {
        kz.tamur.rt.Utils.lookAndFeelMenuItem(mi, text);
        if (mi != null) {
            ImageIcon ic = null;
            try {
                ic = getImageIcon(iconName);
            } catch (Exception e) {
                log.error(e, e);
            }
            mi.setIcon(ic);
        }
    }


    /**
     * Проверяет то что сейчас запущенно
     * 
     * @return true если запущен дизайнер
     */
    public static boolean isDesignerRun() {
        return false;
    }

    public static JButton createBtnEditorIfc(ActionListener parent) {
        return createBtn(parent, "editorIfc");
    }

    public static JButton createBtnEditor(ActionListener parent) {
        return createBtn(parent, "editor");
    }

    public static JButton createBtnOpenIfc(ActionListener parent) {
        return createBtn(parent, "CodeEditor");
    }

    public static JButton createBtn(ActionListener parent, String icon) {
        JButton button = new JButton();
        kz.tamur.rt.Utils.setAllSize(button, Constants.BTN_EDITOR_SIZE);
        if (parent != null) {
            button.addActionListener(parent);
        }
        button.setIcon(getImageIcon(icon));
        button.setMargin(Constants.INSETS_0);
        return button;
    }
    
    public static void setLangId(long langId) {
        ifcLangId = langId;
    }

    public static Locale getLocale() {
        Locale res = new Locale("ru");
        try {
            LangItem li = LangItem.getById(ifcLangId);
            if (li != null) {
                if ("KZ".equals(li.code))
                    res = new Locale("kk");
                else
                    res = new Locale("ru");
            }
        } catch (Exception e) {
            log.error(e, e);
        }

        return res;
    }
    
    public static HTMLDocument createHTMLDocument() {
        StyleSheet css = null;
        try {
            ClassLoader cl = Utils.class.getClassLoader();
            BufferedReader br = new BufferedReader(new InputStreamReader(Constants.class.getResourceAsStream("images/or3.css")));
            css = new StyleSheet();
            css.loadRules(br, Utils.class.getResource("images/or3.css"));
            br.close();

        } catch (IOException e) {
            log.error(e, e);
        }
        return new HTMLDocument(css);
    }
    
    public static StyleSheet getOrCSS() {
        StyleSheet css = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream("images/or3.css")));
            css = new StyleSheet();
            css.loadRules(br, Utils.class.getResource("images/or3.css"));
            br.close();

        } catch (IOException e) {
            log.error(e, e);
        }
        return css;
    }
    
    public static String convertToNameFile(String nameFile){
    	nameFile = (nameFile).replaceAll("\"", "'").replaceAll("ә", "А").replaceAll("ң", "н").replaceAll("ғ", "г").replaceAll("ү", "у").replaceAll("ұ", "у").replaceAll("қ", "к").replaceAll("ө", "о").
				replaceAll("һ", "х").replaceAll("Ә", "А").replaceAll("Ғ", "Г").replaceAll("Ү", "У").replaceAll("Ң", "Н").replaceAll("Ұ", "У").replaceAll("Қ", "К").replaceAll("Ө", "О");
        char[] chArray = nameFile.toCharArray();
          for (int i=0; i<chArray.length; i++){
          	 if(chArray[i] == '*'){
          		 chArray[i] = ' ';
          	 }
          	 if(chArray[i] == '|'){
          		 chArray[i] = ' ';
          	 }
          	 if(chArray[i] == '\\'){
          		 chArray[i] = ' ';
          	 }
          	 if(chArray[i] == '/'){
          		 chArray[i] = ' ';
          	 }
          	if(chArray[i] == '?'){
          		 chArray[i] = ' ';
          	 }
          }
        nameFile = String.valueOf(chArray);
    	return nameFile;
    }
    
    public static String convertToText(String d) {
        StringTokenizer st = new StringTokenizer(d, " ");
        String days = st.nextToken();
        String month = st.nextToken().toLowerCase(Constants.OK);
        if (month.endsWith("ь") || month.endsWith("й"))
            month = month.substring(0, month.length() - 1) + "я";
        else
            month = month + "а";
        String year = st.nextToken();
        return days + " " + month + " " + year;
    }

    public static String convertToDateKaz(String d, Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);

        d = d.replaceAll("dd", getDoubleDay(c.get(Calendar.DAY_OF_MONTH)));
        d = d.replaceAll("d", getSingleDay(c.get(Calendar.DAY_OF_MONTH)));
        d = d.replaceAll("MMMM", getMonthKaz(c.get(Calendar.MONTH)));
        d = d.replaceAll("MM", getDoubleDay(c.get(Calendar.MONTH) + 1));
        d = d.replaceAll("yyyy", getSingleDay(c.get(Calendar.YEAR)));

        return d;
    }

    public static String convertToDateRus(String d, Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);

        d = d.replaceAll("dd", getDoubleDay(c.get(Calendar.DAY_OF_MONTH)));
        d = d.replaceAll("d", getSingleDay(c.get(Calendar.DAY_OF_MONTH)));
        d = d.replaceAll("MMMM", getMonthRus(c.get(Calendar.MONTH)));
        d = d.replaceAll("MM", getDoubleDay(c.get(Calendar.MONTH) + 1));
        d = d.replaceAll("yyyy", getSingleDay(c.get(Calendar.YEAR)));

        return d;
    }

    public static String convertToTextDateKaz(String d, Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);

        d = d.replaceAll("dd", getTextDayKaz(c.get(Calendar.DAY_OF_MONTH)));
        d = d.replaceAll("d", getTextDayKaz(c.get(Calendar.DAY_OF_MONTH)));
        d = d.replaceAll("MMMM", getMonthKaz(c.get(Calendar.MONTH)));
        d = d.replaceAll("MM", getDoubleDay(c.get(Calendar.MONTH) + 1));
        d = d.replaceAll("yyyy", yearToStringKaz(c.get(Calendar.YEAR)));

        return d;
    }

    public static String convertToTextDateRus(String d, Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);

        d = d.replaceAll("dd", getTextDayRus(c.get(Calendar.DAY_OF_MONTH)));
        d = d.replaceAll("d", getTextDayRus(c.get(Calendar.DAY_OF_MONTH)));
        d = d.replaceAll("MMMM", getMonthRus(c.get(Calendar.MONTH)));
        d = d.replaceAll("MM", getDoubleDay(c.get(Calendar.MONTH) + 1));
        d = d.replaceAll("yyyy", yearToStringRus(c.get(Calendar.YEAR)));

        return d;
    }

    public static String getTextDayRus(int days) {
        StringBuffer res = new StringBuffer();
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

        return res.toString();
    }

    public static String getTextDayKaz(int days) {
        StringBuffer res = new StringBuffer();
        int highDay = days/10;
        int lowDay = days - highDay*10;
        if (lowDay > 0) {
            switch (highDay) {
                case 0:
                    res.append(dayToTextKaz(lowDay).toLowerCase(Constants.OK));
                    break;
                case 1:
                    res.append("он ");
                    res.append(dayToTextKaz(lowDay).toLowerCase(Constants.OK));
                    break;
                case 2:
                    res.append("жиырма ");
                    res.append(dayToTextKaz(lowDay).toLowerCase(Constants.OK));
                    break;
                case 3:
                    res.append("отыз ");
                    res.append(dayToTextKaz(lowDay).toLowerCase(Constants.OK));
                    break;
            }
        } else {
            switch (highDay) {
                case 1:
                    res.append("оныншы");
                    break;
                case 2:
                    res.append("жиырмасыншы");
                    break;
                case 3:
                    res.append("отызыншы");
                    break;
            }
        }

        return res.toString();
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

    private static String dayToTextKaz (int day) {
        switch (day) {
            case 1:
                return "Бірінші";
            case 2:
                return "Екінші";
            case 3:
                return "\u04aeшінші";
            case 4:
                return "Т\u04e9ртінші";
            case 5:
                return "Бесінші";
            case 6:
                return "Алтыншы";
            case 7:
                return "Жетінші";
            case 8:
                return "Сегізінші";
            case 9:
                return "То\u0493ызыншы";
        }
        return "";
    }

    private static String yearToString (String year) {
        int y = Integer.parseInt(year);
        return yearToStringRus(y);
    }

    private static String yearToStringRus (int y) {
        StringBuffer res = new StringBuffer();
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

    private static String yearToStringKaz (int y) {
        StringBuffer res = new StringBuffer();
        int i3 = y/1000;
        if (i3 == 1) {
            res.append("бір мы\u04a3");
        } else if (y == 2000) {
            res.append("екі мы\u04a3ыншы");
        } else if (i3 == 2) {
            res.append("екі мы\u04a3");
        }

        int i2 = (y-i3*1000)/100;
        switch (i2) {
            case 1:
                res.append(" сто");
                break;
            case 9:
                res.append(" то\u0493ыз ж\u04afз");
                break;
            case 8:
                res.append(" сегіз ж\u04afз");
                break;
        }

        int i1 = (y-i3*1000-i2*100) / 10;
        int i0 = y - i3*1000 - i2*100 - i1*10;

        if (i0 > 0) {
            switch (i1) {
                case 9:
                    res.append(" то\u049bсан");
                    break;
                case 8:
                    res.append(" сексен");
                    break;
                case 7:
                    res.append(" жетпіс");
                    break;
                case 6:
                    res.append(" алпыс");
                    break;
                case 5:
                    res.append(" елу");
                    break;
                case 4:
                    res.append(" \u049bыры\u049b");
                    break;
                case 3:
                    res.append(" отыз");
                    break;
                case 2:
                    res.append(" жиырма");
                    break;
                case 1:
                    res.append(" он");
                    break;
            }
        } else {
            switch (i1) {
                case 9:
                    res.append(" то\u049bсаныншы");
                    break;
                case 8:
                    res.append(" сексенінші");
                    break;
                case 7:
                    res.append(" жетпісінші");
                    break;
                case 6:
                    res.append(" алпысыншы");
                    break;
                case 5:
                    res.append(" елуінші");
                    break;
                case 4:
                    res.append(" \u049bыры\u049bыншы");
                    break;
                case 3:
                    res.append(" отызыншы");
                    break;
                case 2:
                    res.append(" жиырмасыншы");
                    break;
                case 1:
                    res.append(" оныншы");
                    break;
            }
        }
        switch (i0) {
            case 1:
                res.append(" бірінші");
                break;
            case 2:
                res.append(" екінші");
                break;
            case 3:
                res.append(" \u04afшінші");
                break;
            case 4:
                res.append(" т\u04e9ртінші");
                break;
            case 5:
                res.append(" бесінші");
                break;
            case 6:
                res.append(" алтыншы");
                break;
            case 7:
                res.append(" жетінші");
                break;
            case 8:
                res.append(" сегізінші");
                break;
            case 9:
                res.append(" то\u0493ызыншы");
                break;
        }
        return res.toString();
    }
    
    public static String getMonthKaz(int month) {
        switch(month) {
            case 0:
                return "\u049bантар";
            case 1:
                return "а\u049bпан";
            case 2:
                return "наурыз";
            case 3:
                return "с\u04d9уір";
            case 4:
                return "мамыр";
            case 5:
                return "маусым";
            case 6:
                return "шілде";
            case 7:
                return "тамыз";
            case 8:
                return "\u049bырк\u04afйек";
            case 9:
                return "\u049bазан";
            case 10:
                return "\u049bараша";
            case 11:
                return "желто\u049bсан";
        }
        return "";
    }

    public static String getMonthRus(int month) {
        switch(month) {
            case 0:
                return "января";
            case 1:
                return "февраля";
            case 2:
                return "марта";
            case 3:
                return "апреля";
            case 4:
                return "мая";
            case 5:
                return "июня";
            case 6:
                return "июля";
            case 7:
                return "августа";
            case 8:
                return "сентября";
            case 9:
                return "октября";
            case 10:
                return "ноября";
            case 11:
                return "декабря";
        }
        return "";
    }

    public static String getDoubleDay(int day) {
        if (day > 9)
            return "" + day;
        else
            return "0" + day;
    }

    public static String getSingleDay(int day) {
        return "" + day;
    }
    
    /**
     * Получить ширину рабочей области браузера
     * 
     * @return the width
     */
    public  static int getWidth() {
        return width;
    }

    /**
     * Запомнить ширину рабочей области браузера
     * 
     * @param width the width to set
     */
    public static void setWidth(String width) {
        Utils.width = new Integer(width);
    }

    /**
     * Получить высоту рабочей области браузера
     * 
     * @return the height
     */
    public static int getHeight() {
        return height;
    }

    /**
     * Запомнить высоту рабочей области браузера
     * 
     * @param height the height to set
     */
    public static void setHeight(String height) {
        Utils.height = new Integer(height);
    }

    /**
     * Приведение высоты компоненты к высоте рабочей области браузера
     *
     * @param height the height
     * @return int
     */
    public static int mergeHeight(int height) {
        int h = Utils.height;
        return height > Utils.height ? h < 0 ? 0 : h : height;
    }

    /**
     * Приведение ширины компоненты к ширине рабочей области браузера
     *
     * @param width the width
     * @return int
     */
    public static int mergeWidth(int width) {
        return width > Utils.width ? Utils.width : width;
    }

    public static void setAllSize(WebComponent component, Dimension size) {
        component.setPreferredSize(size);
        component.setMaximumSize(size);
        component.setMinimumSize(size);
    }
}