package kz.tamur.rt;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MaskFormatter;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.server.sgds.HexStringOutputStream;

import kz.tamur.comps.Constants;
import kz.tamur.comps.models.ColorAct;
import kz.tamur.lang.MathOp;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.Value;
import kz.tamur.or3.util.PathElement;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.ServiceControlNode;

/**
 * 
 * @author Sergey Lebedev
 * 
 */
public class Utils {
	private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Utils.class.getName());
	private static Map<Long, byte[]> saltMap = new HashMap<>();
	
	static {
		saltMap.put(0L, "Salt@123@".getBytes());
		saltMap.put(1L, "Salt@123@".getBytes());
		saltMap.put(2L, "Salt@123@".getBytes());
		saltMap.put(3L, "Salt@123@".getBytes());
		saltMap.put(4L, "Salt@123@".getBytes());
	}
	
    // Цвета, которые использует система в текущий момент
    private static Color mainColor = Constants.MAIN_COLOR;
    private static Color blueSysColor = Constants.BLUE_SYS_COLOR;
    private static Color darkShadowSysColor = Constants.DARK_SHADOW_SYS_COLOR;
    private static Color midSysColor = Constants.MID_SYS_COLOR;
    private static Color lightYellowColor = Constants.LIGHT_YELLOW_COLOR;
    private static Color redColor = Constants.RED_COLOR;
    private static Color lightRedColor = Constants.LIGHT_RED_COLOR;
    private static Color lightGreenColor = Constants.LIGHT_GREEN_COLOR;
    private static Color shadowYellowColor = Constants.SHADOW_YELLOW_COLOR;
    private static Color sysColor = Constants.SYS_COLOR;
    private static Color lightSysColor = Constants.LIGHT_SYS_COLOR;
    private static Color defaultFontColor = Constants.DEFAULT_FONT_COLOR;
    private static Color silverColor = Constants.SILVER_COLOR;
    private static Color shadowsGreyColor = Constants.SHADOWS_GREY_COLOR;
    private static Color keywordColor = Constants.KEYWORD_COLOR;
    private static Color variableColor = Constants.VARIABLE_COLOR;
    private static Color clientVariableColor = Constants.CLIENT_VARIABLE_COLOR;
    private static Color commentColor = Constants.COMMENT_COLOR;

    // Шрифты
    private static Font defaultFont = new Font("Dialog", Font.PLAIN, 11);
    private static Font defaultComponentFont = new Font("Dialog", Font.PLAIN, 12);
    private static Font appTitleFont = new Font("Dialog", Font.BOLD, 11);
    private static long ifcLangId = 0;
    public static Cursor helpCursor;
    public static GraphicsDevice[] screenDevices = null;
    public static AreaDevice[] areaDevices = null;

	private static Comparator<AbstractDesignerTreeNode> flComparator;
	private static Comparator<AbstractDesignerTreeNode> scnComparator;
	
    public static KrnAttribute[] getAttributesForPath(String path) throws KrnException {
        return getAttributesForPath(path, Kernel.instance());
    }
    
    public static KrnAttribute[] getAttributesForPath(String path, Kernel krn) throws KrnException {
        if (path == null)
            return null;
        StringTokenizer st = new StringTokenizer(path, ".");
        int count = st.countTokens();
        KrnAttribute[] res = new KrnAttribute[(count == 0) ? 0 : count - 1];
        if (count > 0) {
            ClassNode cnode = krn.getClassNodeByName(st.nextToken());
            if(cnode==null) {
            	return null;
            }
            for (int i = 0; i < count - 1; ++i) {
                PathElement pe = Funcs.parseAttrName(st.nextToken());
                KrnAttribute attr = cnode.getAttribute(pe.name);
                res[i] = attr;
                if (attr == null)
                    return null;
                if (pe.castClassName != null) {
                    cnode = krn.getClassNodeByName(pe.castClassName);
                } else {
                    cnode = krn.getClassNode(attr.typeClassId);
                }
            }
        }
        return res;
    }

    public static void drawRects(Component comp, Graphics g) {
        int w = comp.getWidth();
        int h = comp.getHeight();
        Color oldColor = g.getColor();
        g.setColor(Utils.getDefaultFontColor());
        g.drawRect(0, 0, w - 1, h - 1);
        g.fillRect(0, 0, 5, 5);
        g.fillRect(0, h - 5, 5, 5);
        g.fillRect(w - 5, 0, 5, 5);
        g.fillRect(w - 5, h - 5, 5, 5);
        g.setColor(oldColor);
    }
    
    public static Integer[] getDynamicNodeIds(Kernel krn, List<KrnObject> objs) {
    	List<Integer> res = new ArrayList<>();
    	if(objs != null) {
    		try {
        		for(KrnObject obj: objs) {
        			KrnAttribute attr = krn.getAttributeByName(krn.getClassByName("HiperTree"), "runtimeIndex");
        			int id = (int)krn.getLongsSingular(obj, attr, false);
        			res.add(id);        			
        		}
        		return res.toArray(new Integer[0]);
        	} catch(KrnException e) {
        		e.printStackTrace();
        	}
    	}
    	return null;
    }
    
    public static List<KrnObject> getDynamicNodeUis(Kernel krn, List<KrnObject> objs){
    	List<KrnObject> res = new ArrayList<>();    	
    	if(objs != null) {
    		try {
    			for(KrnObject obj : objs) {
    				KrnAttribute attr = krn.getAttributeByName(krn.getClassByName("HiperTree"), "hiperObj");
    				KrnObject[] val = krn.getObjects(obj, attr, 0);
    				if(val != null && val.length > 0) {
        				res.add(val[0]);
    				} else {
    					res.add(null);
    				}
    			}
    		} catch(KrnException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return res;
    }
    
    public static List<KrnObject> getDynamicNodeObjs(){
    	return getDynamicNodeObjs(null);
    }
    
    public static byte[][] getDynamicNodeIcons(Kernel krn, List<KrnObject> objs){
    	if(objs != null) {   
        	List<byte[]> res = new ArrayList<>();
        	try {
        		for(KrnObject obj: objs) {
        			KrnAttribute attr = krn.getAttributeByName(krn.getClassByName("HiperTree"), "uiIcon");
        			byte[] icon = krn.getBlob(obj, "uiIcon", 0, 0, 0);
        			res.add(icon);        			
        		}
        		return res.toArray(new byte[0][0]);
        	} catch(KrnException e) {
        		e.printStackTrace();
        	}
    	}
    	
    	return null;
    }
    
    public static List<KrnObject> getDynamicNodeObjs(Kernel krn){
    	if(krn == null) {
    		krn = Kernel.instance();
    	}
    	if(!krn.hasSessionInitialized()) return null;
    	List<KrnObject> dynamicObjs = new ArrayList<>();
        try {
        	KrnObject obj = krn.getObjectByUid("9.30198536", 0);
        	log.info("Dynamic root: " + obj);
        	KrnAttribute attr = krn.getAttributeByName(krn.getClassByName("HiperTree"), "hipers");
        	log.info("Attr hipers: " + attr);
        	if (obj != null && attr != null) {
	        	TreeSet<Value> values = (TreeSet<Value>) krn.getValues(new long[]{obj.id}, attr.id, 0, 0);
	        	for(Value val : values) {
	        		dynamicObjs.add((KrnObject)val.value);
	        	}
	        	Comparator<KrnObject> c = new Comparator<KrnObject>() {
					@Override
					public int compare(KrnObject o1, KrnObject o2) {
						return o1.uid.compareTo(o2.uid);
					}
	        	};
	        	Collections.sort(dynamicObjs, c);
        	}
        } catch (KrnException e) {
        	e.printStackTrace();
        }
        return dynamicObjs;
    }
    
    
    
    public static String[] getDynamicNodeUids(Kernel krn, List<KrnObject> objs) {
    	List<String> res = new ArrayList<>();
    	if(krn == null)
    		krn = Kernel.instance();
    	if(objs != null) {
    		for(KrnObject obj: objs) {
    			res.add(obj.uid);
    		}
    	}
    	
    	return res.size() > 0 ? (String[])res.toArray(new String[0]): null;
    }
    
    public static String[] getDynamicNodeTitles(Kernel krn, List<KrnObject> objs, long langId){
    	List<String> res = new ArrayList<>();
    	if(krn == null)
    		krn = Kernel.instance();
    	if(objs != null) {
    		try {
    			for(KrnObject obj : objs) {
    				KrnAttribute attr = krn.getAttributeByName(krn.getClassByName("HiperTree"), "title");
    				String val = krn.getStringValues(new long[] {obj.id}, attr, langId, false, 0).length > 0 ? 
    						krn.getStringValues(new long[] {obj.id}, attr, langId, false, 0)[0].value : 
    							krn.getStringValues(new long[] {obj.id}, attr, 0, false, 0)[0].value;
    				res.add(val);
    			}
    		} catch(KrnException e) {
    			e.printStackTrace();
    		}
    	}

    	return res.size() > 0 ? (String[])res.toArray(new String[0]): null;
    }

    public static void drawRect(Component comp, Graphics g) {
        int w = comp.getWidth();
        int h = comp.getHeight();
        Color oldColor = g.getColor();
        g.setColor(Utils.getDefaultFontColor());
        g.drawRect(0, 0, w - 1, h - 1);
        g.setColor(oldColor);
    }

    public static Map<String, ImageIcon> icons = new HashMap<String, ImageIcon>();

    public static BufferedImage toBufferedImage(Image image) { // FIXME переделать используя ImageUtils(там неправильно работает - не учитывает прозрачность)
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        boolean hasAlpha = hasAlpha(image);
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
        }

        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    public static boolean hasAlpha(Image image) {
        if (image instanceof BufferedImage) {
            return ((BufferedImage) image).getColorModel().hasAlpha();
        }
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
        return pg.getColorModel().hasAlpha();
    }

    public static URL getAboutURL() {
        URL url = null;
        try {
            url = Utils.class.getResource("images/a.html");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return url;
    }

    public static Font getDefaultFont() {
        return defaultFont;
    }

    public static Font getDefaultComponentFont() {
        return defaultComponentFont;
    }

    public static Font getAppTitleFont() {
        return appTitleFont;
    }

    public static Color getDefaultFontColor() {
        return defaultFontColor;
    }

    public static int getAbsolutX(Component comp) {
        if (comp.getParent() != null) {
            return comp.getX() + getAbsolutX(comp.getParent());
        }
        return 0;
    }

    public static int getAbsolutY(Component comp) {
        if (comp.getParent() != null) {
            return comp.getY() + getAbsolutY(comp.getParent());
        }
        return 0;
    }

    public static Color getLightGraySysColor() {
        return UIManager.getColor("Button.background");
    }

    /**
     * Клонирование цвета
     * @param originalColor цвет, который необхидимо клонировать
     * @return
     */
    public static Color newColor(Color originalColor) {
        return new Color(originalColor.getRGB());
    }
    
    public static Color getDarkShadowSysColor() {
        return darkShadowSysColor;
    }

    /**
     * @return the mainColor
     */
    public static Color getMainColor() {
        return mainColor;
    }

    public static Color getBlueSysColor() {
        return blueSysColor;
    }

    public static Color getMidSysColor() {
        return midSysColor;
    }

    public static Color getLightYellowColor() {
        return lightYellowColor;
    }

    public static Color getLightRedColor() {
        return lightRedColor;
    }

    public static Color getRedColor() {
        return redColor;
    }

    public static Color getLightGreenColor() {
        return lightGreenColor;
    }

    public static Color getShadowYellowColor() {
        return shadowYellowColor;
    }

    public static Color getSysColor() {
        return sysColor;
    }

    public static Color getLightSysColor() {
        return lightSysColor;
    }

    public static Color getSilverColor() {
        return silverColor;
    }

    public static Color getShadowsGreyColor() {
        return shadowsGreyColor;
    }

    public static Color getKeywordColor() {
        return keywordColor;
    }

    public static Color getVariableColor() {
        return variableColor;
    }

    public static Color getClientVariableColor() {
        return clientVariableColor;
    }

    public static Color getCommentColor() {
        return commentColor;
    }

    public static int getChildrenCount(Element xml) {
        int inc = 0;
        Stack<Element> s = new Stack<Element>();
        s.push(xml);
        while (!s.isEmpty()) {
            Element el = s.pop();
            if ("Component".equals(el.getName())) {
                inc++;
            }
            List children = el.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Element child = (Element) children.get(i);
                s.push(child);
            }
        }
        return inc;
    }

    public static Font getTabbedFont() {
        return new Font("SansSerif", Font.PLAIN, 12);
    }

    public static JLabel createLabel(String text) {
        JLabel lab = createLabel();
        if (text != null) {
            lab.setText(text);
        }
        return lab;
    }

    public static JLabel createLabel() {
        JLabel lab = new JLabel();
        lab.setFont(getDefaultFont());
        lab.setForeground(getDarkShadowSysColor());
        return lab;
    }

    public static JEditorPane createEditorPane() {
        JEditorPane pane = new JEditorPane();
        pane.setFont(getDefaultFont());
        pane.setForeground(getDarkShadowSysColor());
        pane.setEditable(false);
        pane.setAutoscrolls(true);
        pane.setEditorKit(pane.getEditorKitForContentType("text/html"));
        return pane;
    }

    public static JLabel createLabel(String text, int alignment) {
        JLabel lab = new JLabel();
        if (text != null) {
            lab.setText(text);
        }
        lab.setFont(getDefaultFont());
        lab.setForeground(getDarkShadowSysColor());
        lab.setHorizontalAlignment(alignment);
        return lab;
    }

    public static JComboBox createCombo() {
        JComboBox combo = new JComboBox();
        combo.setFont(getDefaultFont());
        combo.setForeground(getDarkShadowSysColor());
        combo.setBackground(getLightSysColor());
        return combo;
    }

    public static JCheckBox createCheckBox(String text, boolean isSelected) {
        JCheckBox check = new JCheckBox();
        check.setSelected(isSelected);
        if (text != null) {
            check.setText(text);
        }
        check.setFont(getDefaultFont());
        check.setForeground(getDarkShadowSysColor());
        return check;
    }

    // Создание обычного листа
    public static JList createListBox() {
        JList list = new JList();
        initListBox(list);
        return list;
    }

    public static JList createListBox(ListModel listModel, int i) {
        JList list = new JList(listModel);
        initListBox(list);
        list.setSelectionMode(i);
        return list;
    }

    public static JList createListBox(ListModel listModel) {
        return createListBox(listModel, 2);
    }

    // Инициализация листа
    private static void initListBox(JList list) {
        // Приведение листа к единому дизайну
        list.setFont(Utils.getDefaultFont());
        list.setBackground(Utils.getSilverColor());
        list.setForeground(Utils.getDarkShadowSysColor());
    }

    public static TitledBorder createTitledBorder(Border b, String title) {
        return BorderFactory.createTitledBorder(b, title, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                Utils.getDefaultFont(), Utils.getDarkShadowSysColor());
    } 
    
    public static String getFontToString(Font f) {
        if (f != null) {
            String styleStr = "PLAIN";
            switch (f.getStyle()) {
            case Font.ITALIC:
                styleStr = "ITALIC";
                break;
            case Font.BOLD:
                styleStr = "BOLD";
                break;
            case Font.BOLD + Font.ITALIC:
                styleStr = "BOLDITALIC";
                break;
            }
            return f.getFamily() + "-" + styleStr + "-" + f.getSize();
        }
        return "";
    }

    public static String getBorderToString(Border b) {
        String btxt = "";
        if (b instanceof EtchedBorder) {
            btxt = "Etched";
        } else if (b instanceof BevelBorder) {
            if (((BevelBorder) b).getBevelType() == BevelBorder.RAISED) {
                btxt = "Bevel Rised";
            } else {
                btxt = "Bevel Lowred";
            }
        } else if (b instanceof LineBorder) {
            btxt = "Line";
        } else if (b instanceof TitledBorder) {
            btxt = getTitledBorderToString((TitledBorder) b);
        }
        return btxt;
    }

    private static String getTitledBorderToString(TitledBorder b) {
        Border bord = b.getBorder();
        String btxt = "";
        if (bord instanceof EtchedBorder) {
            btxt = "Etched";
        } else if (bord instanceof BevelBorder) {
            if (((BevelBorder) bord).getBevelType() == BevelBorder.RAISED) {
                btxt = "Bevel Rised";
            } else {
                btxt = "Bevel Lowred";
            }
        } else if (bord instanceof LineBorder) {
            btxt = "Line";
        }
        Font font = b.getTitleFont();
        btxt = btxt + "|" + getFontToString(font);
        Color color = b.getTitleColor();
        btxt = btxt + "|" + color.getRGB();
        int just = b.getTitleJustification();
        int pos = b.getTitlePosition();
        btxt = btxt + "|" + pos + "|" + just;
        if (bord instanceof LineBorder) {
            btxt = btxt + "|" + ((LineBorder) bord).getThickness();
            btxt = btxt + "|" + ((LineBorder) bord).getLineColor().getRGB();
        }
        return btxt;
    }

    public static Border decodeBorder(String str) {
        String[] sts = str.split("\\|");
        Border b = null;
        Font f = getDefaultFont();
        Color c = getDarkShadowSysColor();
        int just = 0;
        int pos = 0;
        int idx = 0;
        int thick = 1;
        Color lineColor = Color.black;
        String s = sts[0];
        if ("Etched".equals(s)) {
            b = BorderFactory.createEtchedBorder();
        } else if ("Bevel Rised".equals(s)) {
            b = BorderFactory.createBevelBorder(BevelBorder.RAISED);
        } else if ("Bevel Lowred".equals(s)) {
            b = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        } else if ("Line".equals(s)) {
            b = BorderFactory.createLineBorder(Color.black);
        }
        while (++idx < sts.length) {
            String token = sts[idx];
            switch (idx) {
            case 1:
                f = Font.decode(token);
                break;
            case 2:
                c = Color.decode(token);
                break;
            case 3:
                pos = Integer.parseInt(token);
                if (pos < 0) pos = 0;
                break;
            case 4:
                just = Integer.parseInt(token);
                if (just < 0) just = 0;
                break;
            case 5:
                thick = Integer.parseInt(token);
                if (thick < 0) thick = 1;
                break;
            case 6:
                lineColor = Color.decode(token);
                break;
            }
        }
        if (b instanceof LineBorder) {
            b = BorderFactory.createLineBorder(lineColor, thick);
        }
        Border bord = new TitledBorder(b, "", just, pos, f, c);
        return bord;
    }
    
    public static byte[] decodeImage(String str) {
        return HexStringOutputStream.fromHexString(str);
    }

    public static String getImageToString(byte[] b) {
        return HexStringOutputStream.toHexString(b, 0, b.length);
    }

    public static ImageIcon processCreateImage(byte[] b) {
        ImageIcon image = null;
        if (b != null) {
            image = new ImageIcon(b);
        }
        return image;
    }

    protected static class DesignerTextField extends JTextField {

        public DesignerTextField() {
            super();
            setBorder(BorderFactory.createLineBorder(getDarkShadowSysColor())); // TODO убрать в новом UI для TextField 
            setFont(getDefaultFont());
            setPreferredSize(new Dimension(110, 20));
            setMinimumSize(new Dimension(110, 20));
            setMaximumSize(new Dimension(400, 20));
        }
    }

    public static JTextField createDesignerTextField() {
        return new DesignerTextField();
    }

    public static JPasswordField createDesignerPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setBorder(BorderFactory.createLineBorder(getDarkShadowSysColor()));
        pf.setFont(getDefaultFont());
        pf.setPreferredSize(new Dimension(100, 20));
        pf.setMinimumSize(new Dimension(100, 20));
        pf.setMaximumSize(new Dimension(400, 20));
        return pf;
    }

	public static char[] getPD(JPasswordField fld) {
        Document doc = fld.getDocument();
        Segment txt = new Segment();
        try {
            doc.getText(0, doc.getLength(), txt); // use the non-String API
        } catch (BadLocationException e) {
            return null;
        }
        char[] retValue = new char[txt.count];
        System.arraycopy(txt.array, txt.offset, retValue, 0, txt.count);
        return retValue;
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
            e.printStackTrace();
        }

        return res;
    }

    public static Style getStyle(String name) {
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

        s = styles.addStyle("Arial", null);
        StyleConstants.setFontFamily(s, "Arial");

        s = styles.addStyle("Tahoma", null);
        StyleConstants.setFontFamily(s, "Tahoma");

        s = styles.addStyle("Times New Roman", null);
        StyleConstants.setFontFamily(s, "Times New Roman");

        s = styles.addStyle("Monospaced", null);
        StyleConstants.setFontFamily(s, "Monospaced");

        s = styles.addStyle("10", null);
        StyleConstants.setFontSize(s, 10);

        s = styles.addStyle("12", null);
        StyleConstants.setFontSize(s, 12);

        s = styles.addStyle("14", null);
        StyleConstants.setFontSize(s, 14);

        s = styles.addStyle("16", null);
        StyleConstants.setFontSize(s, 16);

        s = styles.addStyle("func", null);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, new Color(0, 0, 128));
        StyleConstants.setItalic(s, false);

        s = styles.addStyle("none", null);
        StyleConstants.setBold(s, false);
        StyleConstants.setForeground(s, Color.BLACK);
        StyleConstants.setItalic(s, false);

        s = styles.addStyle("commit", null);
        StyleConstants.setForeground(s, Color.GRAY);
        StyleConstants.setItalic(s, true);

        s = styles.addStyle("maskUID", null);
        StyleConstants.setForeground(s, new Color(153, 0, 0));
        StyleConstants.setBold(s, true);

        s = styles.addStyle("leftAlign", null);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_LEFT);

        s = styles.addStyle("centerAlign", null);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);

        s = styles.addStyle("rightAlign", null);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_RIGHT);

        s = styles.addStyle("vars", null);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, new Color(102, 14, 122));

        s = styles.addStyle("myvars", null);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, new Color(0, 102, 0));

        s = styles.addStyle("info", null);
        StyleConstants.setBold(s, true);
        StyleConstants.setBackground(s, new Color(153, 0, 0));
        StyleConstants.setForeground(s, Color.WHITE);

        s = styles.addStyle("error", null);
        // StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, new Color(153, 0, 0));
        return styles.getStyle(name);
    }

    public static Document processCreateDocument(byte[] b) {
        Document doc = null;
        if (b != null) {
            try {
                ByteArrayInputStream is = new ByteArrayInputStream(b);
                ObjectInputStream istrm = null;
                istrm = new ObjectInputStream(is);
                doc = (Document) istrm.readObject();
                istrm.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return doc;
    }

    public static Document processCreateHTMLDocument(byte[] b) {
        Document doc = null;
        if (b != null) {
            try {
                ByteArrayInputStream is = new ByteArrayInputStream(b);
                ObjectInputStream istrm = null;
                istrm = new ObjectInputStream(is);
                doc = (Document) istrm.readObject();
                istrm.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return doc;
    }

    public static List moveElementTo(List list, int index, int newIndex) {
        Object o = list.remove(index);
        Vector v = new Vector(list);
        v.insertElementAt(o, newIndex);
        List res = new ArrayList();
        for (int i = 0; i < v.size(); i++) {
            res.add(v.get(i));
        }
        return res;
    }

    public static void moveListElementTo(List list, int index, int newIndex) {
        Object o = list.remove(index);
        list.add(newIndex, o);
    }

    public static MaskFormatter DateMask() {
        MaskFormatter mask = null;
        try {
            mask = new MaskFormatter("AA.AA.AAAA");
            mask.setValidCharacters("0123456789гдм");
            mask.setAllowsInvalid(false);
            mask.setPlaceholder("дд.мм.гггг");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mask;
    }

    public static String[] wrap(String str, FontMetrics fm, int width) {
        int w = 0;
        StringBuffer sb = new StringBuffer();
        List res = new ArrayList();
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
        return (String[]) res.toArray(new String[res.size()]);
    }

    public static void lookAndFeelMenuItem(JMenuItem mi) {
        if (mi != null) {
            mi.setFont(getDefaultFont());
            mi.setForeground(getDarkShadowSysColor());
        }
    }

    public static void lookAndFeelMenuItem(JMenuItem mi, String text) {
        lookAndFeelMenuItem(mi);
        if (mi != null) {
            mi.setText(text);
        }
    }

    public static KrnObject getObjectByUid(String uid, int trId) throws KrnException {
        Kernel krn = Kernel.instance();
        KrnObject resObj = null;
        KrnObject[] objs = krn.getObjectsByUid(new String[] { uid }, trId);
        if (objs.length > 0)
            resObj = objs[0];
        return resObj;
    }

    public static KrnObject getObjectById(long id, int trId) throws KrnException {
        Kernel krn = Kernel.instance();
        KrnObject resObj = null;
        KrnObject[] objs = krn.getObjectsByIds(new long[] { id }, trId);
        if (objs.length > 0)
            resObj = objs[0];
        return resObj;
    }

    public static KrnObject[] getObjectsByIds(String className, long[] ids) {
        Kernel krn = Kernel.instance();
        KrnObject[] res = null;
        List<KrnObject> list = new ArrayList<KrnObject>();
        try {
            KrnObject[] objs = krn.getClassObjects(krn.getClassByName(className), 0);
            for (int i = 0; i < objs.length; i++) {
                KrnObject obj = objs[i];
                for (int j = 0; j < ids.length; j++) {
                    if (obj.id == ids[j]) {
                        list.add(obj);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            res = new KrnObject[list.size()];
            for (int i = 0; i < list.size(); i++) {
                res[i] = list.get(i);
            }
        }
        return res;
    }

    public static String getMonthKaz(int month) {
        switch (month) {
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
        switch (month) {
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
        int highDay = days / 10;
        int lowDay = days - highDay * 10;
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
        int highDay = days / 10;
        int lowDay = days - highDay * 10;
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
        int highDay = days / 10;
        int lowDay = days - highDay * 10;
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
        if (month.endsWith("ь") || month.endsWith("й"))
            month = month.substring(0, month.length() - 1) + "я";
        else
            month = month + "а";
        res.append(" " + month);
        String year = st.nextToken();
        res.append(" " + yearToString(year) + " года");
        return res.toString();
    }

    private static String dayToText(int day) {
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

    private static String dayToTextKaz(int day) {
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

    private static String yearToString(String year) {
        int y = Integer.parseInt(year);
        return yearToStringRus(y);
    }

    private static String yearToStringRus(int y) {
        StringBuffer res = new StringBuffer();
        int i3 = y / 1000;
        if (i3 == 1) {
            res.append("одна тысяча");
        } else if (y == 2000) {
            res.append("двухтысячного");
        } else if (i3 == 2) {
            res.append("две тысячи");
        }

        int i2 = (y - i3 * 1000) / 100;
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

        int i1 = (y - i3 * 1000 - i2 * 100) / 10;
        int i0 = y - i3 * 1000 - i2 * 100 - i1 * 10;

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
                i0 += 10;
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
                i0 += 10;
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

    private static String yearToStringKaz(int y) {
        StringBuffer res = new StringBuffer();
        int i3 = y / 1000;
        if (i3 == 1) {
            res.append("бір мы\u04a3");
        } else if (y == 2000) {
            res.append("екі мы\u04a3ыншы");
        } else if (i3 == 2) {
            res.append("екі мы\u04a3");
        }

        int i2 = (y - i3 * 1000) / 100;
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

        int i1 = (y - i3 * 1000 - i2 * 100) / 10;
        int i0 = y - i3 * 1000 - i2 * 100 - i1 * 10;

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

    public static Set getFunctions() {
        Set<String> functions = new HashSet<String>(12);
        functions.add("if");
        functions.add("else");
        functions.add("foreach");
        functions.add("while");
        functions.add("set");
        functions.add("if");
        functions.add("else");
        functions.add("end");

        functions.add("ERRMSG");
        functions.add("RETURN");
        functions.add("SELOBJ");
        functions.add("Interface");
        return functions;
    }

    public static void setComponentFocusCircle(Component comp) {
        Set fwdKeys = new HashSet();
        Set bkwdKeys = new HashSet();
        fwdKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0, false));
        fwdKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0, false));
        bkwdKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, java.awt.event.InputEvent.SHIFT_DOWN_MASK, false));
        bkwdKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, java.awt.event.InputEvent.SHIFT_DOWN_MASK, false));
        comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, fwdKeys);
        comp.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, bkwdKeys);
    }

    public static void setMemoComponentFocusCircle(Component comp) {
        Set fwdKeys = new HashSet();
        Set bkwdKeys = new HashSet();
        fwdKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, java.awt.event.InputEvent.CTRL_DOWN_MASK, false));
        bkwdKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, java.awt.event.InputEvent.CTRL_DOWN_MASK
                | java.awt.event.InputEvent.SHIFT_DOWN_MASK, false));
        comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, fwdKeys);
        comp.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, bkwdKeys);
    }

    public static void setComponentTabFocusCircle(Component comp) {
        Set fwdKeys = new HashSet();
        Set bkwdKeys = new HashSet();
        fwdKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0, false));
        bkwdKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, java.awt.event.InputEvent.SHIFT_DOWN_MASK, false));
        comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, fwdKeys);
        comp.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, bkwdKeys);
    }

    public static int getFilterFlag(String expr) {
        int res = 0;
        if (expr != null && expr.length() >= 4) {
            res = Character.digit(expr.charAt(3), 10);
        }
        return res;
    }

    public static String cutTextMessage(String aTextField) {
        String strFillCurrent = "Нет данных";
        String strResultCurrent = "";
        if (aTextField != null) {
            strFillCurrent = aTextField;
            int n = strFillCurrent.trim().length();
            int j = strFillCurrent.trim().indexOf(" ");
            boolean bl = false;
            int l = 0;
            if (j > 0)
                while (n > 0) {
                    j = strFillCurrent.trim().indexOf(" ");

                    if (j > 0 && strFillCurrent.trim().length() > 0) {
                        if (l <= 2) {
                            strResultCurrent = strResultCurrent + " " + strFillCurrent.trim().substring(0, j);
                            l++;
                        } else {
                            strResultCurrent = strResultCurrent + " " + strFillCurrent.trim().substring(0, j) + "\n";
                            l = 0;
                        }
                        strFillCurrent = strFillCurrent.trim().substring(j + 1);
                        n = strFillCurrent.length();
                        bl = true;
                    } // if (j > 0 && strFillCurrent.trim().length() > 0)
                    if (bl == true && j == -1 && strFillCurrent.trim().length() > 0) {
                        strResultCurrent = strResultCurrent + " " + strFillCurrent.trim();
                        n = 0;
                    } // if (bl == true && j==-1 &&
                      // strFillCurrent.trim().length() >0)
                }// while (n>0) {
            else
                strResultCurrent = strFillCurrent;

        } // if (aTextField != null)

        return strResultCurrent;

    }

    public static FuncPopupItem createFuncPopupItem(String title, ASTStart template) {
        return new FuncPopupItem(title, template);
    }

    public static class FuncPopupItem extends JMenuItem {
        String title;
        ASTStart template;

        FuncPopupItem(String title, ASTStart template) {
            super(title);
            this.title = title;
            this.template = template;
            setFont(Utils.getDefaultFont());
        }

        public String getTitle() {
            return title;
        }

        public ASTStart getTemplate() {
            return template;
        }

    }

    public static KrnObject createFilterFolder(KrnObject process, String title, long langId) {
        KrnObject obj_f = null;
        try {
            final Kernel krn = Kernel.instance();
            final KrnClass cls_filter = krn.getClassByName("FilterRoot");
            final KrnClass cls_filter_f = krn.getClassByName("FilterFolder");
            final KrnAttribute attr_filter = krn.getAttributeByName(cls_filter, "children");
            KrnObject[] objs = krn.getClassObjects(cls_filter, 0);
            objs = krn.getObjects(objs[0], attr_filter, 0);
            long[] ids = Funcs.makeObjectIdArray(objs);
            StringValue[] svs = krn.getStringValues(ids, cls_filter_f.id, "title", langId, false, 0);
            long objId = -1;
            for (int i = 0; i < svs.length; ++i) {
                if (svs[i].value.equals("Процессы")) {
                    objId = svs[i].objectId;
                    break;
                }
            }
            if (objId > 0) {
                obj_f = krn.createObject(cls_filter_f, 0);
                krn.setString(obj_f.id, cls_filter_f.id, "title", 0, langId, title, 0);
                krn.setObject(objId, attr_filter.id, 0, obj_f.id, 0, false);
                krn.setObject(process.id, process.classId, "filters", 0, obj_f.id, 0, false);
            }
        } catch (KrnException ex) {
            ex.printStackTrace();
        }
        return obj_f;
    }

    public static Dimension getScreenSize(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        screenSize.width = screenSize.width - insets.left - insets.right;
        screenSize.height = screenSize.height - insets.top - insets.bottom;
        return screenSize;
    }

    public static Dimension getScreenSize(JDialog frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        screenSize.width = screenSize.width - insets.left - insets.right;
        screenSize.height = screenSize.height - insets.top - insets.bottom;
        return screenSize;
    }

    public static final char[] WORD_SEPARATORS = { ' ', '\t', '\n', '\r', '\f', '.', ',', ':', '-', '(', ')', '[', ']', '{', '}',
            '<', '>', '/', '|', '\\', '\'', '\"' };

    public static boolean isSeparator(char ch) {
        for (int k = 0; k < WORD_SEPARATORS.length; k++)
            if (ch == WORD_SEPARATORS[k])
                return true;
        return false;
    }

    public static Color getColorByName(String name) {
        if ("red".equals(name)) {
            return Color.red;
        } else if ("blue".equals(name)) {
            return Color.blue;
        } else if ("black".equals(name)) {
            return Color.black;
        } else if ("white".equals(name)) {
            return Color.white;
        } else if ("gray".equals(name)) {
            return Color.gray;
        } else if ("lightgray".equals(name)) {
            return Color.lightGray;
        } else if ("green".equals(name)) {
            return Color.green;
        } else {
            return Color.white;
        }
    }

    public static List getPathToRoot(List res, AbstractDesignerTreeNode node) {
        res.add(node);
        AbstractDesignerTreeNode parent = (AbstractDesignerTreeNode) node.getParent();
        if (parent != null) {
            return getPathToRoot(res, parent);
        } else {
            return res;
        }

    }

    public static HTMLDocument createHTMLDocument() {
        StyleSheet css = null;
        try {
            ClassLoader cl = Utils.class.getClassLoader();
            // URL url = new URL("http","localhost","/kz/tamur/comps/images/or3.css");
            BufferedReader br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream("/kz/tamur/comps/images/or3.css")));
            css = new StyleSheet();
            css.loadRules(br, Utils.class.getResource("/kz/tamur/comps/images/or3.css"));
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HTMLDocument(css);
    }

    public static StyleSheet getOrCSS() {
        StyleSheet css = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream("/kz/tamur/comps/images/or3.css")));
            css = new StyleSheet();
            css.loadRules(br, Utils.class.getResource("/kz/tamur/comps/images/or3.css"));
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return css;
    }

    public static JRadioButton createRadioButton(String text) {
        JRadioButton res = new JRadioButton(text);
        res.setFont(getDefaultFont());
        res.setForeground(getDarkShadowSysColor());
        return res;
    }

    public static String toURIString(String str) {
        if (str == null)
            return "";
        StringBuffer res = new StringBuffer();
        char[] arr = str.toCharArray();

        for (int i = 0; i < arr.length; ++i) {
            if (arr[i] < 'A') {
                String s = Integer.toString((int) arr[i], 16);
                if (s.length() == 1)
                    s = "0" + s;
                res.append("%" + s);
            } else {
                res.append(arr[i]);
            }
        }
        return res.toString();
    }

    public static String getUserWorkingDir() {
        String res = Funcs.getSystemProperty("user.home") + File.separator + "Or3EE" + File.separator;
        return Funcs.validate(res);
    }

    /**
     * Приведение типа <code>boolean</code> к <code>long</code>
     * 
     * @param bool
     *            логическое значение для конвертации
     * @return long приведённое значение
     */
    public static long toLong(boolean bool) {
        return (bool) ? 1l : 0l;
    }

    /**
     * Приведение строки к логическому выражению
     * 
     * @param string
     *            строка
     * @return true, в случае если строка равно <code>1</code> или <code>true</code> (игнорируя регистр)
     */
    public static boolean stringToBoolean(String string) {
        return (string != null && (string.equalsIgnoreCase("true") || string.equals("1")));
    }

    /**
     * Приведение <code>Object</code> к строке вида <i>1</i> или <i>0</i>
     * 
     * @param obj
     *            <code>Object</code> типа <code>Boolean</code> который нужно преобразовать в строку
     * @return string <i>1</i> или <i>0</i> соответствует <i>true</i> или <i>false</i>
     */
    public static String objectToString(Object obj) {
        if (obj instanceof Boolean) {
            return (Boolean) obj ? "1" : "0";
        }
        return null;
    }

    public static Boolean toBoolean(Object o) {
        return (o instanceof Boolean) ? (Boolean) o : o.toString().equals("1") || o.toString().toUpperCase(Locale.ROOT).equals("TRUE");
    }

    /**
     * Устанавливает размеры компонента
     * применяется к максимальному, предпочтительному и минимальному
     * 
     * @param component
     *            компонент для установки размеров
     * @param size
     *            размер
     */
    public static void setAllSize(JComponent component, Dimension size) {
        component.setPreferredSize(size);
        component.setMaximumSize(size);
        component.setMinimumSize(size);
    }

/*    *//**
     * Установить дисплей для вывода приложения
     * 
     * @param screen
     *            номер дисплея
     *//*
    public static void setScreenApplication(int screen) {
        setProperty("screenApplication", screen);
    }

    *//**
     * Установить дисплей для вывода конструктора
     * 
     * @param screen
     *            номер дисплея
     *//*
    public static void setScreenDesigner(int screen) {
        setProperty("screenDesigner", screen);
    }*/

    /**
     * Установка свойства в файле Properties
     * T
     * 
     * @param name
     *            название свойства для записи
     * @param value
     *            значение свойства
     */
    public static void setProperty(String name, String value) {
        Properties props = loadPropertis();
        String workDir = getUserWorkingDir();
        if (Funcs.isValid(workDir)) {
	        File dir = Funcs.getCanonicalFile(workDir);
	        File f = new File(dir, "propsJboss");
	        FileOutputStream fos = null;
	        try {
	            fos = new FileOutputStream(f);
	            props.setProperty(name, value);
	            try {
	                props.store(fos, "Properties");
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                fos.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
        }
    }

    /**
     * Перегрузка метода <code>setProperty(String name, String value)</code> Установить дисплей для вывода конструктора
     * 
     * @param name
     *            название свойства для записи
     * @param value
     *            значение свойства
     */
    public static void setProperty(String name, int value) {
        setProperty(name, value + "");
    }

    /**
     * Получить номер дисплея для отображения дизайнера.
     * 
     * @return номер дисплея
     */
    public static int getScreenDesigner() {
        int screen;
        Properties props = loadPropertis();
        // считать индекс монитора на котором необходимо отобразить окно
        String display = props.getProperty("screenDesigner");
        try {
            screen = display == null ? 0 : Integer.parseInt(display);
            if (screen == -1) {
                screen = 0;
            }
        } catch (Exception e) {
            screen = 0;
        }
        return screen;
    }

    /**
     * Получить номер дисплея для отображения приложения.
     * 
     * @return номер дисплея
     */
    public static int getScreenApplication() {
        int screen;
        Properties props = loadPropertis();
        // считать индекс монитора на котором необходимо отобразить окно
        final String display = props.getProperty("screenApplication");
        try {
            screen = display == null ? 0 : Integer.parseInt(display);
            if (screen == -1) {
                screen = 0;
            }
        } catch (Exception e) {
            screen = 0;
        }
        return screen;
    }

    /**
     * Загружает свойства из файла конфигурации
     * 
     * @return properties объект <code>Properties</code> с содержимым конфигурационного файла
     */
    public static Properties loadPropertis() {
        Properties props = new Properties();
        String workDir = getUserWorkingDir();
        if (Funcs.isValid(workDir)) {
	        File dir = Funcs.getCanonicalFile(workDir);
	        File f = Funcs.getCanonicalFile(dir, "propsJboss");
	        if (!f.exists()) {
	            try {
	                dir.mkdirs();
	                f.createNewFile();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	
	        FileInputStream fis = null;
	        try {
	            fis = new FileInputStream(f);
	            props.load(fis);
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                fis.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
        }
        return props;
    }

    /**
     * Запомнить последнюю директорию, выбранную пользователем
     * 
     * @param path
     *            полное имя директориии
     */
    public static void setLastSelectDir(String path) {
        setProperty("lastSelectDir", path);
    }

    /**
     * Устанавливает размеры картинки в соответствии с заданными в параметрах
     * 
     * @param img
     *            картинка, которую необходимо увеличить/уменьшить
     * @param wigth
     *            новая ширина картинки
     * @param heigt
     *            новая высота картинки
     * @return image новая картинка. Если размеры картинки совпадали с заданными в параметрах - картинка не меняется
     */
    public static ImageIcon setSize(ImageIcon img, int wigth, int heigt) {
        if (img != null && (img.getIconWidth() != wigth || img.getIconHeight() != heigt)) {
            Image newimg = (img.getImage()).getScaledInstance(wigth, heigt, Image.SCALE_SMOOTH);
            newimg.flush();
            img = new ImageIcon(newimg);
        }
        return img;
    }
    
    public static BufferedImage resize(byte[] img, int width, int height) {
    	ByteArrayInputStream is = new ByteArrayInputStream(img);
	    BufferedImage bufImg = null;
	    try {
	    	bufImg = ImageIO.read(is);
	    } catch(Exception e) {
	    	log.error(e, e);
	    }
    	int orgHit = bufImg.getHeight();
    	int orgWd = bufImg.getWidth();
    	double orgRatio = (double) orgHit/ (double)orgWd;
    	double setRatio = (double) height/ (double)width;
    	if(orgRatio > setRatio)
    		width = (int) (height*orgWd)/orgHit;
    	else 
    		height = (int) (width*orgHit)/orgWd;
        Image tmp = bufImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    /**
     * Запоминить новый список пользователей
     * 
     * @param list
     *            список пользователей
     * @param url
     *            адрес сервера
     * @param baseName
     *            имя базы данных
     * @param isRuntime
     *            режим выполенния
     */
    public static void setUserList(List<String> list, String url, String baseName, boolean isRuntime) {
        final String varName = buildNameVar(url, baseName, isRuntime);
        setProperty(varName, listToString(list, ';'));
    }

    /**
     * Sets the user list.
     * 
     * @param userList
     *            the user list
     * @param url
     *            the url
     * @param baseName
     *            the base name
     * @param isRuntime
     *            the is runtime
     * @param props
     *            the props
     */
    public static void setUserList(List<String> userList, String url, String baseName, boolean isRuntime, Properties props) {
        final String varName = buildNameVar(url, baseName, isRuntime);
        props.setProperty(varName, listToString(userList, ';'));
    }

    /**
     * Получить список пользователей.
     * 
     * @param url
     *            адрес сервера
     * @param baseName
     *            имя базы данных
     * @param isRuntime
     *            режим выполенния
     * @return the список пользователей
     */
    public static List<String> getUserList(String url, String baseName, boolean isRuntime, Properties props) {
        final String varName = buildNameVar(url, baseName, isRuntime);
        final String prop = props.getProperty(varName);

        if (prop == null || prop.isEmpty()) {
            return null;
        } else {
            List<String> list = new ArrayList<String>();
            if (prop.contains(";")) {
                final String[] arrDisplay = prop.split(";");
                for (String row : arrDisplay) {
                    list.add(row);
                }
            } else {
                list.add(prop);
            }
            return list;
        }
    }

    /**
     * Sets the user key file path.
     * 
     * @param path
     *            - путь к ключевому файлу
     * @param url
     *            the url
     * @param baseName
     *            the base name
     * @param isRuntime
     *            the is runtime
     * @param props
     *            the props
     */
    public static void setKeyFilePath(String path, String url, String baseName, boolean isRuntime, Properties props) {
        final String varName = "key_" + buildNameVar(url, baseName, isRuntime);
        props.setProperty(varName, path);
    }

    /**
     * Получить путь к ключевому файлу последнего пользователя.
     * 
     * @param url
     *            адрес сервера
     * @param baseName
     *            имя базы данных
     * @param isRuntime
     *            режим выполенния
     * @return путь к ключевому файлу
     */
    public static String getKeyFilePath(String url, String baseName, boolean isRuntime, Properties props) {
        final String varName = "key_" + buildNameVar(url, baseName, isRuntime);
        final String prop = props.getProperty(varName);

        if (prop == null || prop.isEmpty()) {
            return "";
        } else {
            return prop;
        }
    }

    /**
     * Сборка уникального имени переменной для конкретного сервера.
     * Необходимо для запоминания списка пользователей для каждого сервера отдельно.
     * 
     * @param url
     *            адрес сервера
     * @param baseName
     *            имя базы данных
     * @param isRuntime
     *            режим выполнения
     * @return string собранное имя ключа для файла свойств
     */
    private static String buildNameVar(String url, String baseName, boolean isRuntime) {
        String app = isRuntime ? "app" : "des";
        // составление имени параметра, уникального для каждого сервера
        return app + url.replaceAll("\\.", "-").replaceAll(":", ".") + "." + baseName;
    }

    /**
     * Конвертирование <code>List</code> в строку с указанным разделителем
     * 
     * @param list
     *            список
     * @param separator
     *            разделитель
     * @return string полученная строка
     */
    public static String listToString(List<String> list, char separator) {
        StringBuilder sb = new StringBuilder();
        final int ls = list.size();
        final int ls2 = ls - 1;
        for (int i = 0; i < ls; ++i) {
            sb.append(list.get(i));
            if (i < ls2) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * Обновление списка пользователей
     * 
     * @param userName
     *            введенное имя пользователя
     * @param list
     *            список пользователей
     * @return list новый список пользователей
     */
    public static List<String> updateUserList(String userName, List<String> list) {
        final int ls = list.size();
        boolean isFind = false;
        for (int i = 0; i < ls; ++i) {
            if (userName.equals(list.get(i))) {
                isFind = true;
                break;
            }
        }
        List<String> tempList = new ArrayList<String>();
        tempList.add(userName);
        String row = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < ls; ++i) {
                if (list.get(i) != null) {
                    row = (String) list.get(i);
                    if (!row.isEmpty() && !(isFind && userName.equals(row))) {
                        tempList.add((String) list.get(i));
                    }
                }
                if (i == Constants.NUMBER_STORED_USER - 1) {
                    break;
                }
            }
            return tempList;
        }
        return list;
    }

    public static void outErrorCreateAttrUser() {
        System.out.println("Не найдены необходимые атрибуты 'User'!");
    }
    
    public static void outErrorCreateAttrUser(String attr) {
        System.out.println("Не найдены необходимые атрибуты класса 'User':"+attr+"!");
    }

    public static void outErrorCreateAttrPolicy() {
        System.out.println("Не найдены необходимые атрибуты класса 'Политика учетных записей'!");
    }

    /**
     * Перевод первого символа строки в верхний регистр
     * 
     * @param str
     *            строка
     * @return string изменённая строка
     */
    public static String toTitleCase(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
    }

    /**
     * Создание из строки html кода путём замены <code>@</code> на <code><br></code> и добавления разметки тегов<code>html</code>.
     *
     * @param string строка
     * @param comp компонент
     * @return string изменённая строка
     */
    public static String castToHTML(String string, JComponent comp) {
        if (string != null && string.contains("@")) {
            StringBuilder sb = new StringBuilder();
            sb.append("<html>");
            int align = -1;
            if (comp != null) {
                if (comp instanceof JButton) {
                    align = ((JButton) comp).getHorizontalAlignment();
                } else if (comp instanceof JLabel) {
                    align = ((JLabel) comp).getHorizontalAlignment();
                }
                if (!comp.isEnabled()) {
                    sb.append("<font color=\"" + colorToString(getSysColor()) + "\">");
                }
            }

            sb.append(getHTMLAlign(align, string.replaceAll("@", "<br>")));
            if (comp != null && !comp.isEnabled()) {
                sb.append("</font>");
            }
            sb.append("</html>");
            return sb.toString();
        } else {
            return string;
        }
    }
    
    
    public static String delHTML(String string) {
        return string.replaceAll("<html>|</html>|<center>|</center>|<left>|</left>|<right>|</right>|<font.*?>|</font>", "");
    }
    
    /**
     * Преобразование <code>JAVA</code> цвета в строку для HTML 
     */
    public static String colorToString(Color bg) {
        if (bg != null) {
            return String.format("#%02x%02x%02x", bg.getRed(), bg.getGreen(), bg.getBlue());
        }
        return "";
    }


    /**
     * Преобразовать объект <code>java.awt.Font</code> в <code>css</code> стиль.
     * 
     * @param font
     *            шрифт для получения из него <code>css</code> стиля.
     * @param style
     *            составленное свойство стиля.
     * @return <code>css</code> свойство стиля.
     */
    public static void getCSS(Font font, StringBuilder style) {
        if (font != null) {
            style.append("font-size:").append(font.getSize()).append("px;");
            if (font.isBold()) {
                style.append("font-weight:bold;");
            }
            if (font.isItalic()) {
                style.append("font-style:italic;");
            }
            String fn = font.getName();
            // В HTML отсутствует шрифт Dialog, поэтому нужно заменить его, например на похожий Arial.
            if(fn.equals("Dialog")) {
                fn = "Arial";
            }
            style.append("font-family:").append(fn).append(";");
        }
    }
    
    /**
     * Преобразовать объект <code>java.awt.Color</code> в <code>css</code> стиль.
     * 
     * @param color
     *            цвет для получения из него <code>css</code> стиля.
     * @param style
     *            составленное свойство стиля.
     * @return <code>css</code> свойство стиля.
     */
    public static void getCSS(Color color, StringBuilder style) {
        if (color != null) {
            style.append("color:").append(colorToString(color)).append(";");
        }
    }
    
    public static void setMainColor(Color color) {
        Utils.mainColor = color;
    }
    
    /**
     * @param blueSysColor
     *            the blueSysColor to set
     */
    public static void setBlueSysColor(Color color) {
        Utils.blueSysColor = color;
    }

    /**
     * @param darkShadowSysColor
     *            the darkShadowSysColor to set
     */
    public static void setDarkShadowSysColor(Color color) {
        Utils.darkShadowSysColor = color;
    }

    /**
     * @param midSysColor
     *            the midSysColor to set
     */
    public static void setMidSysColor(Color color) {
        Utils.midSysColor = color;
    }

    /**
     * @param lightYellowColor
     *            the lightYellowColor to set
     */
    public static void setLightYellowColor(Color color) {
        Utils.lightYellowColor = color;
    }

    /**
     * @param redColor
     *            the redColor to set
     */
    public static void setRedColor(Color color) {
        Utils.redColor = color;
    }

    /**
     * @param lightRedColor
     *            the lightRedColor to set
     */
    public static void setLightRedColor(Color color) {
        Utils.lightRedColor = color;
    }

    /**
     * @param lightGreenColor
     *            the lightGreenColor to set
     */
    public static void setLightGreenColor(Color color) {
        Utils.lightGreenColor = color;
    }

    /**
     * @param shadowYellowColor
     *            the shadowYellowColor to set
     */
    public static void setShadowYellowColor(Color color) {
        Utils.shadowYellowColor = color;
    }

    /**
     * @param sysColor
     *            the sysColor to set
     */
    public static void setSysColor(Color color) {
        Utils.sysColor = color;
    }

    /**
     * @param lightSysColor
     *            the lightSysColor to set
     */
    public static void setLightSysColor(Color color) {
        Utils.lightSysColor = color;
    }

    /**
     * @param silverColor
     *            the silverColor to set
     */
    public static void setSilverColor(Color color) {
        Utils.silverColor = color;
    }

    /**
     * @param shadowsGreyColor
     *            the shadowsGreyColor to set
     */
    public static void setShadowsGreyColor(Color color) {
        Utils.shadowsGreyColor = color;
    }

    /**
     * @param keywordColor
     *            the keywordColor to set
     */
    public static void setKeywordColor(Color color) {
        Utils.keywordColor = color;
    }

    /**
     * @param variableColor
     *            the variableColor to set
     */
    public static void setVariableColor(Color color) {
        Utils.variableColor = color;
    }

    /**
     * @param clientVariableColor
     *            the clientVariableColor to set
     */
    public static void setClientVariableColor(Color color) {
        Utils.clientVariableColor = color;
    }

    /**
     * @param commentColor
     *            the commentColor to set
     */
    public static void setCommentColor(Color color) {
        Utils.commentColor = color;
    }

    /**
     * @param defaultFontColor
     *            the defaultFontColor to set
     */
    public static void setDefaultFontColor(Color color) {
        Utils.defaultFontColor = color;
    }

    public static boolean isColorActive(ColorAct color) {
        return color != null && color.isEnable();
    }

    /**
     * Метод слияния глобальной конфигурации с конфигурацией пользователя.
     *
     * @param config глобальная конфирурация
     * @return config объединённая конфигурация
     */
    public static Config mergeConfig(Config config) {
        if (Kernel.instance().getUser() != null) {
            Config configUser = Kernel.instance().getUser().config;

            if (configUser.getGradientMainFrame() != null && !configUser.getGradientMainFrame().isEmpty()) {
                config.setGradientMainFrame(configUser.getGradientMainFrame());
            }
            if (configUser.getGradientControlPanel() != null && !configUser.getGradientControlPanel().isEmpty()) {
                config.setGradientControlPanel(configUser.getGradientControlPanel());
            }
            if (configUser.getColorMain() != null) {
                config.setColorMain(configUser.getColorMain());
            }
            if (configUser.getColorHeaderTable() != null) {
                config.setColorHeaderTable(configUser.getColorHeaderTable());
            }
            if (configUser.getColorTabTitle() != null) {
                config.setColorTabTitle(configUser.getColorTabTitle());
            }
            if (configUser.getColorBackTabTitle() != null) {
                config.setColorBackTabTitle(configUser.getColorBackTabTitle());
            }
            if (configUser.getColorFontTabTitle() != null) {
                config.setColorFontTabTitle(configUser.getColorFontTabTitle());
            }
            if (configUser.getColorFontBackTabTitle() != null) {
                config.setColorFontBackTabTitle(configUser.getColorFontBackTabTitle());
            }
        }
        return config;
    }
    
    /**
     * Добавляет в переданную строку <code>html</code> теги разметки горизонтального позиционирования текста 
     * @param align позиционирование (<code>SwingConstants</code>)
     * @param str строка для разметки
     * @return преобразованная строка
     */
    private static String getHTMLAlign(int align, String str) {
        String sAlign;
        StringBuilder sb = new StringBuilder();
        switch (align) {
        case SwingConstants.CENTER: {
            sAlign = "center";
            break;
        }
        case SwingConstants.LEFT: {
            sAlign = "left";
            break;
        }
        case SwingConstants.RIGHT: {
            sAlign = "right";
            break;
        }
        default:
            return str;
        }
        sb.append("<").append(sAlign).append(">").append(str).append("</").append(sAlign).append(">");
        return sb.toString();
    }
    
    public static String getColorState(int state) {
        if (state == Constants.REQ_ERROR) {
            return " background-color: #FFCCCC; ";
        }  
        if (state == Constants.EXPR_ERROR) {
            return " background-color: #CAF7BB; ";
        }
        return null;
    }
    
    public static void getColorState(int state, StringBuilder sb) {
        String c = getColorState(state);
        if (c != null)
            sb.append(c);
    }
    
    public static String getColorStyleState(int state) {
        if (state == Constants.REQ_ERROR) {
            return " style='background-color: #FFCCCC;' ";
        }  
        if (state == Constants.EXPR_ERROR) {
            return " style='background-color: #CAF7BB;' ";
        }
        return null;
    }
    
    /**
     * Получить color style state.
     *
     * @param state the state
     * @param sb the sb
     * @return the color style state
     */
    public static void getColorStyleState(int state, StringBuilder sb) {
        String c = getColorStyleState(state);
        if (c != null)
            sb.append(c);
    }
    
    /**
     *  Определяет, является ли текущая база системной.
     *
     * @return true, если system db
     * @throws KrnException the krn exception
     */
    public static boolean isSystemDB() throws KrnException {
        return isSystemDB(Kernel.instance());
        }
    
    /**
     * Определяет, является ли текущая база системной.
     *
     * @param krn the krn
     * @return <code>true</code> если системная
     * @throws KrnException the krn exception
     */
    public static boolean isSystemDB(Kernel krn) throws KrnException {
        try {
            KrnClass baseCls = krn.getClassByName("Структура баз");
            KrnObject obj = krn.getClassObjects(baseCls, 0).clone()[0];
            AttrRequestBuilder arb = new AttrRequestBuilder(baseCls, krn).add("родитель");
            long[] objIds = { obj.id };
            List<Object[]> rows = krn.getObjects(objIds, arb.build(), 0);
            long objId = -1;
            for (Object[] row : rows) {
                Object parent = arb.getValue("родитель", row);
                if (parent == null) {
                    objId = ((KrnObject) row[0]).id;
                    break;
                }
            }
            // сравнить ID системной БД с ID текущей БД
            return krn.getCurrentDb().id == objId;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Получить XML resource.
     *
     * @param path the path
     * @return the xML resource
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JDOMException the jDOM exception
     */
    public static byte[] getXMLResource(String path) throws IOException {
        path = "/" + path + ".xml";
        if (Utils.class.getResource(path) != null) {
            InputStream is = Utils.class.getResourceAsStream(path);
            byte[] b = Funcs.readStream(is, Constants.MAX_DOC_SIZE);
            is.close();
            return b;
        }
        return null;
    }
    
    public static String getHash(byte[] data) {
        StringBuilder b = new StringBuilder(33);
        getHash(data,b);
        return b.toString();
    }
    
    /**
     * Получить ХЭШ массива байтов.
     *
     * @param data массив, ХЭШ которого необходимо получить
     * @param hexString объект куда пишется результат
     */
    public static void getHash(byte[] data,StringBuilder hexString) {
        MessageDigest mdAlgorithm = null;
        try {
            mdAlgorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert mdAlgorithm != null;
        mdAlgorithm.reset();
        mdAlgorithm.update(getSalt());
        mdAlgorithm.update(data);
        byte[] digest = mdAlgorithm.digest();
        for (byte aDigest : digest) {
            String str = Integer.toHexString(0xFF & aDigest);
            if (str.length() == 1)
                hexString.append('0');
            hexString.append(str);

        }
    }
    
    /**
     * Получить сигнатуру файла, содержащегося в массиве байтов.
     *
     * @param array массив байтов в котором содержится файл
     * @return строка с сигнатурой файла
     */
    public static String getSignature(byte[] array) {
        int l = array.length;
        // выявить маркер АРРО
        if (l>19) {
            byte[] arr = new byte[4];
            for (int i = 0; i < 4; i++) {
                arr[i] = array[i];
            } 
            if ("FFD8FFE0".equals(bytArrayToHex(arr).toUpperCase(Locale.ROOT))) {
                return "JFIF";
            }
        }
        int sl = 3;
        l = l < sl-1 ? l : sl;
        byte[] arr = new byte[l];
        for (int i = 0; i < l; i++) {
            arr[i] = array[i+1];
        }
        return new String(arr);
    }
    
    static long timeStart;

    public static long start() {
        System.out.println("Отсчет!");
        return timeStart = System.currentTimeMillis();
    }
    
    public static void finish(long start) {
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        System.out.println("Затраченное время : " + elapsedTimeMillis / 1000F + " сек."); 
    }
    public static void finish() {
        long elapsedTimeMillis = System.currentTimeMillis() - timeStart;
        System.out.println("Затраченное время : " + elapsedTimeMillis / 1000F + " сек."); 
    }  
    public static void finish2() {
        long elapsedTimeMillis = System.currentTimeMillis() - timeStart;
        System.out.println("Затраченное время : " + elapsedTimeMillis / 1000F + " сек.");
        start();
    } 
    static String bytArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(byte b: a)
           sb.append(String.format("%02x", b&0xff));
        return sb.toString();
     }

    /**
     * Поиск в массиве <code>int[]</code> числа <code>int</code>
     * 
     * @param array
     *            входящий массив
     * @param e
     *            посковый элемент
     * @return индекс элемента или -1
     */
    public static int arrayFinder(int[] array, int e) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == e) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Клонирует список
     */
    public static <T> List<T> clone(List<T> list) {
        List<T> cloned = new ArrayList<T>(list.size());
        cloned.addAll(list);
        return cloned;
    }
    
    /**
     * Конвертирует массив объектов в массив их идентификаторов
     * @param objs - массив объектов (KrnObject)
     * @return массив идентификаторов входящих объектов
     */
    public static long[] toIdsArray(KrnObject[] objs) {
    	long[] res = new long[objs.length];
    	int i = 0;
    	for (KrnObject obj : objs) {
    		res[i++] = obj.id;
    	}
    	return res;
    }

    /**
     * Конвертирует список числе в массив чисел
     * @param ids - список чисел
     * @return массив чисел
     */
    public static long[] toIdsArray(List<Long> ids) {
    	long[] res = new long[ids.size()];

    	for (int k=0; k<ids.size(); k++)
    		res[k] = ids.get(k);
    	
    	return res;
    }
    
    public static synchronized Comparator<AbstractDesignerTreeNode> getFolderAndLeafsComparator() {
    	if (flComparator == null) {
    		flComparator = new Comparator<AbstractDesignerTreeNode>() {
    	        public int compare(AbstractDesignerTreeNode n1, AbstractDesignerTreeNode n2) {
    	            if (n1 == null) {
    	                return -1;
    	            } else if (n2 == null) {
    	                return 1;
    	            } else if (n1.isLeaf() && !n2.isLeaf()) {
    	                return 1;
    	            } else if (!n1.isLeaf() && n2.isLeaf()) {
    	                return -1;
    	            } else if (n1.isLeaf() && n2.isLeaf()) {
    	                return n1.toString().compareTo(n2.toString());
    	            }
    	            return 0;
    	        }
			};
    	}
    	return flComparator;
    }

    public static synchronized Comparator<AbstractDesignerTreeNode> getServiceControlNodeComparator() {
    	if (scnComparator == null) {
    		scnComparator = new Comparator<AbstractDesignerTreeNode>() {
    	        public int compare(AbstractDesignerTreeNode o1, AbstractDesignerTreeNode o2) {
    	            if (o1 == null) {
    	                return -1;
    	            } else if (o2 == null) {
    	                return 1;
    	            } else {
    	            	ServiceControlNode n1 = (ServiceControlNode)o1;
    	            	ServiceControlNode n2 = (ServiceControlNode)o2;
    	            	int i1 = n1.isReport() ? 1 : n1.isFilter() ? 2 : n1.isInterface() ? 3 : n1.isService() ? 4 : 5;
    	            	int i2 = n2.isReport() ? 1 : n2.isFilter() ? 2 : n2.isInterface() ? 3 : n2.isService() ? 4 : 5;
    	            	
    	            	if (i1 == i2)
    	            		return n1.toString().compareTo(n2.toString());
    	            	else
    	            		return i1 > i2 ? -1 : 1;
    	            }
    	        }
			};
    	}
    	return scnComparator;
    }
    
    public static ImageIcon getImageIconFull(String path) {
        ImageIcon icon = icons.get(path);
        try {
            if (icon == null && !"null".equals(path)) {
                if (Constants.class.getResource("images/" + path) != null) {
                    icon = new ImageIcon(Utils.class.getResource("/kz/tamur/comps/images/" + path));
                }
                icons.put(path, icon);
            }
        } catch (NullPointerException e) {
            System.out.println("Изображение не найдено! Имя: (\"" + Funcs.sanitizeHtml(path) + "\")");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icon;
    }

    public static ImageIcon getImageIconExt(String name, String extension) {
        return getImageIconFull(name + extension);
    }

    public static ImageIcon getImageIcon(String name) {
        return getImageIconExt(name, ".gif");
    }

    public static ImageIcon getImageIconJpg(String name) {
        return getImageIconExt(name, ".jpg");
    }

    public static ImageIcon getImageIconForClass(String name) {
        if (name.equals("UI"))
            return getImageIcon("IfcTab");
        if (name.equals("ProcessDef"))
            return getImageIcon("ServiceTab");
        if (name.equals("User"))
            return getImageIcon("userNode");
        if (name.equals("Filter"))
            return getImageIcon("filterNavi");
        if (name.equals("ReportPrinter"))
            return getImageIcon("DocField");
        if (name.equals("<Unaccessible>"))
            return getImageIcon("helpLeaf");
        if (name.equals("<Methods>"))
            return getImageIcon("method");
        if (name.equals("<Class>"))
            return getImageIcon("class");
        if (name.equals("<Attribut>"))
            return getImageIcon("attr");
        if (name.equals("<Changes>"))
            return getImageIcon("change");
        if (name.equals("<Triggers>"))
            return getImageIconExt("trigger", ".png");
        return getImageIcon("class");
    }

    public static ImageIcon getMergedIcon(ImageIcon icon, String overlayIcon) { // FIXME переделать используя ImageUtils
        if (icon != null) {
            try {
                BufferedImage image = toBufferedImage(icon.getImage());
                BufferedImage overlay = ImageIO.read(Utils.class.getResource("/kz/tamur/comps/images/" + overlayIcon));
                int w = Math.max(image.getWidth(), overlay.getWidth());
                int h = Math.max(image.getHeight(), overlay.getHeight());
                BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics g = combined.getGraphics();
                g.drawImage(overlay, 0, 0, null);
                g.drawImage(image, 0, 0, null);
                ImageIcon hoverIcon = new ImageIcon(combined);
                // icons.put(iconName+"_hover", hoverIcon);
                icon = hoverIcon;
            } catch (NullPointerException e) {
                System.out.println("NullPointerException in getHoverImageIcon(\"" + icon.getDescription() + "\")");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return icon;
    }

    /**
     * метод создаёт изображение путём наслоения друг на друга двух других
     * 
     * @param iconName
     *            - изображение которое будет нарисованно поверх первого
     * @param overlayIcon
     *            - изображение, используемое в качестве основы
     * @return иконка, полученная в результате наложения изображений
     */
    public static ImageIcon getMergedIcon(String iconName, String overlayIcon) { // FIXME переделать используя ImageUtils
        ImageIcon icon = null;
        if (iconName != null) {
            icon = icons.get(iconName + "_hover");
            try {
                BufferedImage image = toBufferedImage(getImageIconExt(iconName, ".gif").getImage());
                BufferedImage overlay = ImageIO.read(Utils.class.getResource("/kz/tamur/comps/images/" + overlayIcon));
                // определить габариты изображений
                final int imW = image.getWidth();
                final int imH = image.getHeight();
                // определить максимальные, из двух переданных иконок, габариты
                final int w = Math.max(imW, overlay.getWidth());
                final int h = Math.max(imH, overlay.getHeight());
                // вычислить позицию прорисовки верхнего изображения
                int positionX = w / 2 - imW / 2;
                int positionY = h / 2 - imH / 2;
                // результатирующее изображение
                BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics g = combined.getGraphics();
                // прорисовка основы
                g.drawImage(overlay, 0, 0, null);
                // прорисовка верхнего изображения
                g.drawImage(image, positionX, positionY, null);
                ImageIcon hoverIcon = new ImageIcon(combined);
                icons.put(iconName + "_hover", hoverIcon);
                icon = hoverIcon;
            } catch (NullPointerException e) {
                System.out.println("NullPointerException in getHoverImageIcon(\"" + iconName + "\")");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return icon;
    }
    
    public static ImageIcon getRollOverIcon(String iconName) {
        return getMergedIcon(iconName, "overlay.png");
    }

    public static ImageIcon getRollOverIcon(ImageIcon icon) {
        return getMergedIcon(icon, "overlay.png");
    }

    public static ImageIcon getClickIcon(String iconName) {
        return getMergedIcon(iconName, "click_overlay.png");
    }

    private static class DesignerMenu extends JMenu {
        private static final long serialVersionUID = 1L;

        public DesignerMenu(String title) {
            super(title);
            init();
        }

        private void init() {
            setFont(kz.tamur.rt.Utils.getDefaultFont());
            setForeground(kz.tamur.rt.Utils.getDefaultFontColor());
            String title = getText();
            if (title != null) {
                if (title.equals("Копировать")) {
                    setIcon(getImageIcon("edit"));
                } else if (title.equals("События")) {
                    setIcon(getImageIconFull("Events.png"));
                }
            }
        }
    }

    private static class DesinerMenuItem extends JMenuItem {

        private String title;
        private String iconName;

        public DesinerMenuItem() {
            super();
            init();
        }

        public DesinerMenuItem(Action action) {
            super(action);
            init();
        }

        public DesinerMenuItem(String title) {
            this(title, null);
        }

        public DesinerMenuItem(String title, String iconName) {
            super(title);
            this.title = title;
            this.iconName = iconName;
            init();
        }

        void init() {
            setFont(kz.tamur.rt.Utils.getDefaultFont());
            if (iconName != null && !iconName.isEmpty()) {
                // Если в имени иконки найдено расширение файла, то файл будет вытянут по полному имени
                if (Pattern.compile("\\S+\\.(\\S+)$").matcher(iconName).find()) {
                    setIcon(getImageIconFull(iconName));
                } else {
                    setIcon(getImageIcon(iconName));
                }
            } else if (title != null) {
                if (title.equals("Создать")) {
                    setIcon(getImageIcon("Create"));
                }
                if (title.equals("Открыть")) {
                    setIcon(getImageIcon("Open"));
                }
                if (title.equals("Сохранить всё")) {
                    setIcon(getImageIcon("Save"));
                }
                if (title.equals("Сохранить")) {
                    setIcon(getImageIcon("Save"));
                }
                if (title.equals("Подключиться")) {
                    setIcon(getImageIcon("PortConn"));
                }
                if (title.equals("Печать")) {
                    setIcon(getImageIcon("ReportPrinter"));
                }
                if (title.equals("Закрыть") || title.equals("Выход")) {
                    setIcon(getImageIcon("Delete"));
                }
                if (title.equals("Предыдущий документ")) {
                    setIcon(getImageIcon("Back"));
                }
                if (title.equals("Следующий документ")) {
                    setIcon(getImageIcon("Next"));
                }
                if (title.equals("Просмотр")) {
                    setIcon(getImageIcon("Preview"));
                }
                if (title.equals("Просмотр на браузере")) {
                    setIcon(getImageIcon("PreviewWeb"));
                }
                if (title.equals("Копировать")) {
                    setIcon(getImageIcon("Copy"));
                }
                if (title.equals("Вставить")) {
                    setIcon(getImageIcon("Paste"));
                }
                if (title.equals("Вырезать")) {
                    setIcon(getImageIcon("Cut"));
                }
                if (title.equals("Удалить")) {
                    setIcon(getImageIcon("Trash"));
                }
                if (title.equals("Найти")) {
                    setIcon(getImageIcon("Find"));
                }
                if (title.equals("Заменить")) {
                    setIcon(getImageIcon("S&R"));
                }
                if (title.equals("Вставить строку до")) {
                    setIcon(getImageIcon("InsertColBef"));
                }
                if (title.equals("Вставить строку после")) {
                    setIcon(getImageIcon("InsertColAft"));
                }
                if (title.equals("Удалить строку")) {
                    setIcon(getImageIcon("DelRC"));
                }
                if (title.equals("Вставить колонку до")) {
                    setIcon(getImageIcon("InsertRowBef"));
                }
                if (title.equals("Вставить колонку после")) {
                    setIcon(getImageIcon("InsertRowAft"));
                }
                if (title.equals("Удалить колонку")) {
                    setIcon(getImageIcon("DelRC"));
                }
                if (title.equals("Инспектор свойств")) {
                    setIcon(getImageIcon("inspector"));
                }
                if (title.equals("Создать копию")) {
                    setIcon(getImageIcon("CopyIfc"));
                }
                if (title.equals("Переименовать")) {
                    setIcon(getImageIcon("renameIfc"));
                }
                if (title.equals("Переместить назад")) {
                    setIcon(getImageIcon("MoveTabLeft"));
                }
                if (title.equals("Переместить вперёд")) {
                    setIcon(getImageIcon("MoveTabRight"));
                }
                if (title.equals("Создать копию закладки")) {
                    setIcon(getImageIcon("CopyTabS"));
                }
                if (title.equals("Классы")) {
                    setIcon(getImageIcon("classes"));
                }
                if (title.equals("Отладчик")) {
                    setIcon(getImageIcon("debug"));
                }
                if (title.equals("Помощь")) {
                    setIcon(getImageIcon("help"));
                }
                if (title.equals("О программе")) {
                    setIcon(getImageIcon("aboutS"));
                }
                if (title.equals("Сохранить на диске...")) {
                    setIcon(getImageIcon("SaveOnDisk"));
                }
                if (title.equals("Открыть из...")) {
                    setIcon(getImageIcon("OpenFrom"));
                }
                if (title.equals("Создать класс")) {
                    setIcon(getImageIcon("createClass"));
                }
                if (title.equals("Удалить класс")) {
                    setIcon(getImageIcon("deleteClass"));
                }
                if (title.equals("Свойства класса")) {
                    setIcon(getImageIcon("propClass"));
                }
                if (title.equals("Редактировать таблицу")) {//TODO: Tedit
                    setIcon(getImageIcon("propClass"));
                }
                if (title.equals("Объекты класса")) {
                    setIcon(getImageIcon("objClass"));
                }
                if (title.equals("Поля быстрых отчетов")) {
                    setIcon(getImageIcon("quickRep"));
                }
                if (title.equals("Добавить в избранные")) {
                    setIcon(getImageIconFull("AddToFavoritesIcon.png"));
                }
                if (title.equals("Создать атрибут")) {
                    setIcon(getImageIcon("createAttr"));
                }
                if (title.equals("Удалить атрибут")) {
                    setIcon(getImageIcon("deleteAttr"));
                }
                if (title.equals("Свойства атрибута")) {
                    setIcon(getImageIcon("propAttr"));
                }
                if (title.equals("Обновить связи")) {
                    setIcon(getImageIcon("updateAttr"));
                }
                if (title.equals("Удалить неисползуемые объекты")) {
                    setIcon(getImageIcon("deleteObjs"));
                }
                if (title.equals("Объекты")) {
                    setIcon(getImageIcon("objClass"));
                }
                if (title.equals("Поиск объектов")) {
                    setIcon(getImageIcon("objsSearch"));
                }
                if (title.equals("Доступ")) {
                    setIcon(getImageIcon("access"));
                }
                if (title.equals("Пользователи")) {
                    setIcon(getImageIcon("User"));
                }
                if (title.equals("Службы")) {
                    setIcon(getImageIcon("Services"));
                }
                if (title.equals("Фильтры")) {
                    setIcon(getImageIcon("Filters"));
                }
                if (title.equals("Запуск репликации")) {
                    setIcon(getImageIcon("Repl"));
                }
                if (title.equals("Журнал репликации")) {
                    setIcon(getImageIcon("ReplJournal"));
                }
                if (title.equals("Выборочная репликация")) {
                    setIcon(getImageIcon("ReplSelected"));
                }
                if (title.equals("Сумма")) {
                    setIcon(getImageIcon("summ"));
                }
                if (title.equals("Среднее")) {
                    setIcon(getImageIcon("aver"));
                }
                if (title.equals("Максимальное")) {
                    setIcon(getImageIcon("max"));
                }
                if (title.equals("Минимальное")) {
                    setIcon(getImageIcon("min"));
                }
                if (title.equals("По верхнему краю")) {
                    setIcon(getImageIcon("AlignTops"));
                }
                if (title.equals("По нижнему краю")) {
                    setIcon(getImageIcon("AlignBottoms"));
                }
                if (title.equals("По левому краю")) {
                    setIcon(getImageIcon("AlignRights"));
                }
                if (title.equals("По правому краю")) {
                    setIcon(getImageIcon("AlignLefts"));
                }
                if (title.equals("В центре по горизонтали")) {
                    setIcon(getImageIcon("AlignHCenters"));
                }
                if (title.equals("В центре по вертикали")) {
                    setIcon(getImageIcon("AlignVCenters"));
                }
                if (title.equals("По сетке")) {
                    setIcon(getImageIcon("AlignToGrid"));
                }
                if (title.equals("Отменить")) {
                    setIcon(getImageIcon("UndoBlue"));
                }
                if (title.equals("Повторить")) {
                    setIcon(getImageIcon("RedoBlue"));
                }
                if (title.equals("Найти компонент")) {
                    setIcon(getImageIcon("SearchComp"));
                    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
                }
                if (title.equals("Показать историю")) {
                    setIcon(getImageIcon("ServiceHistory"));
                }
            }
        }
    }

    public static DesinerMenuItem createMenuItem(String title) {
        return new DesinerMenuItem(title);
    }

    public static DesignerMenu createMenu(String title) {
        return new DesignerMenu(title);
    }

    public static DesinerMenuItem createMenuItem(Action action) {
        return new DesinerMenuItem(action);
    }

    public static DesinerMenuItem createMenuItem(String title, String iconName) {
        return new DesinerMenuItem(title, iconName);
    }
    
    public static int addObject(KrnObject obj, String attrName, KrnObject val)
            throws KrnException {
        Kernel krn = Kernel.instance();
        KrnObject[] vals = krn.getObjects(obj, attrName, 0);
        krn.setObject(obj.id, obj.classId, attrName, vals.length, val.id, 0, false);
        return vals.length;
    }

    public static boolean searchObject(Object objs[], Object val) {
        for (Object object : objs) {
            if (object == val) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isEmpty(Time t) {
        return t.day == 0 && t.month == 0 && t.year == 0 && t.hour == 0 && t.min == 0 && t.sec == 0 && t.msec == 0;
    }
    
    public static String getTriggerNameByModelChangeType(int modelChangeType) {
    	String triggerName = "";
    	switch(modelChangeType) {
    		case 4:
    			triggerName = "Перед созданием объекта";
    			break;
    		case 5:
    			triggerName = "После создания объекта";
    			break;
    		case 6:
    			triggerName = "Перед удалением объекта";
    			break;
    		case 7:
    			triggerName = "После удаления объекта";
    			break;
    		case 8:
    			triggerName = "Перед изменением значения атрибута";
    			break;
    		case 9:
    			triggerName = "После изменения значения атрибута";
    			break;
    		case 10:
    			triggerName = "Перед удалением значения атрибута";
    			break;
    		case 11:
    			triggerName = "После удаления значения атрибута";
    			break;
    	}
    	return triggerName;
    }
    
    public static int getOwnerTypeByModelChangeType(int modelChangeType) {
    	switch (modelChangeType) {
    		case 4: case 5: case 6: case 7:
    			return 0;
    		case 8: case 9: case 10: case 11:
    			return 1;
    	}
		return -1;
    }
    
    public static String getTriggerNameByTriggerType(int triggerType, int triggerOwner) {
    	String triggerName = "";
    	switch(triggerType) {
    		case 0:
    			triggerName = triggerOwner == 0 ? "Перед созданием объекта" : "Перед изменением значения атрибута";
    			break;
    		case 1:
    			triggerName = triggerOwner == 0 ? "После создания объекта" : "После изменения значения атрибута";
    			break;
    		case 2:
    			triggerName = triggerOwner == 0 ? "Перед удалением объекта" : "Перед удалением значения атрибута";
    			break;
    		case 3:
    			triggerName = triggerOwner == 0 ? "После удаления объекта" : "После удаления значения атрибута";
    			break;
    	}
    	return triggerName;
    }
    
    public static int getTriggerTypeByName(String triggerName) {
    	int triggerType = -1;
	    if ("Перед созданием объекта".equals(triggerName) || "Перед изменением значения атрибута".equals(triggerName)) {
			triggerType = 0;
		} else if ("После создания объекта".equals(triggerName) || "После изменения значения атрибута".equals(triggerName)) {
			triggerType = 1;
		} else if ("Перед удалением объекта".equals(triggerName) || "Перед удалением значения атрибута".equals(triggerName)) {
			triggerType = 2;
		} else {
			triggerType = 3;
		}
	    return triggerType;
    }
    
    public static int getModelChangeTypeByTriggerType(int triggerType, int triggerOwner) {
    	int modelChangeTypre = -1;
    	switch(triggerType) {
    		case 0:
    			modelChangeTypre = triggerOwner == 0 ? 4 : 8;
    			break;
    		case 1:
    			modelChangeTypre = triggerOwner == 0 ? 5 : 9;
    			break;
    		case 2:
    			modelChangeTypre = triggerOwner == 0 ? 6 : 10;
    			break;
    		case 3:
    			modelChangeTypre = triggerOwner == 0 ? 7 : 11;
    			break;
    	}
    	return modelChangeTypre;
    }
    
    public static void closeQuietly(Reader r) {
    	try {
    		if (r != null) {
    			r.close();
    		}
    	} catch (IOException e) {
    		log.error(e, e);
    	}
    }

    public static void closeQuietly(InputStream r) {
    	try {
    		if (r != null) {
    			r.close();
    		}
    	} catch (IOException e) {
    		log.error(e, e);
    	}
    }

    public static void closeQuietly(OutputStream os) {
    	try {
    		if (os != null) {
    			os.close();
    		}
    	} catch (IOException e) {
    		log.error(e, e);
    	}
    }

    public static void closeQuietly(HttpURLConnection r) {
    	try {
    		if (r != null) {
    			r.disconnect();
    		}
    	} catch (Exception e) {
    		log.error(e, e);
    	}
    }
    
    public static byte[] getSalt() {
    	Long r = MathOp.random(5);
    	return saltMap.get(r);
    }
}