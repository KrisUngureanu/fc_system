package kz.tamur.comps;

import static kz.tamur.comps.Utils.getFullPathComponent;
import static kz.tamur.guidesigner.InterfaceActionsConteiner.getInterfaceActions;
import static kz.tamur.rt.Utils.createMenuItem;

import java.awt.AWTEvent;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;

import kz.tamur.Or3Frame;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PanelPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.InterfaceFrame;
import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.guidesigner.serviceControl.ServiceControl;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.PanelAdapter;
import kz.tamur.rt.adapters.UIFrame;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

import org.jdom.Element;
import org.jdom.IllegalAddException;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.expr.Editor;
/**
 * The Class OrPanel.
 */
public class OrPanel extends JPanel implements OrGuiContainer {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    
    protected String UUID;
    public static PropertyNode PROPS = new PanelPropertyRoot();
    private ControlTabbedContent tabbedContent;

    /** The gui parent. */
    private OrGuiContainer guiParent;

    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;

    /** The is copy. */
    private boolean isCopy;

    /** The standart border. */
    private Border standartBorder;

    /** The copy border. */
    private Border copyBorder = BorderFactory.createLineBorder(Utils.getMidSysColor());

    /** The Constant X_AXIS. */
    private static final int X_AXIS = 0;

    /** The Constant Y_AXIS. */
    private static final int Y_AXIS = 1;

    /** The gbl. */
    private GridBagLayout gbl = new GridBagLayout();

    /** The hspacers. */
    private List<Spacer> hspacers = new ArrayList<Spacer>();

    /** The vspacers. */
    private List<Spacer> vspacers = new ArrayList<Spacer>();

    /** The hspacer cts. */
    private GridBagConstraints hspacerCts = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, 1, new Insets(0,
            0, 0, 0), 0, 0);

    /** The vspacer cts. */
    private GridBagConstraints vspacerCts = new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.CENTER, 1, new Insets(0,
            0, 0, 0), 0, 0);

    /** The mode. */
    private int mode;

    /** The xml. */
    private Element xml;

    /** The is selected. */
    private boolean isSelected;

    /** The frame. */
    public OrFrame frame;

    /** The title. */
    private String title;

    /** The title uid. */
    private String titleUID;

    /** The listeners. */
    private EventListenerList listeners = new EventListenerList();

    /** The constraints. */
    private GridBagConstraints constraints;

    /** The pref size. */
    private Dimension prefSize;

    /** The max size. */
    private Dimension maxSize;

    /** The min size. */
    private Dimension minSize;

    /** The enabled. */
    private boolean enabled;

    /** The border type. */
    private Border borderType;

    /** The border title uid. */
    private String borderTitleUID;

    /** The description. */
    private byte[] description;

    /** The adapter. */
    private PanelAdapter adapter;

    /** The after save template. */
    private ASTStart beforeOpenTemplate, afterOpenTemplate, beforeCloseTemplate, afterCloseTemplate, createXmlTemplate,
            afterSaveTemplate, afterTaskListUpdateTemplate, onNotificationTemplate, messageRecievedTemplate;

    /** The description uid. */
    private String descriptionUID;

    /** The default button. */
    private OrButton defaultButton;

    /** The var name. */
    private String varName;

    /** Начальный цвет градиента. */
    private Color startColor;

    /** конечный цвет градиента. */
    private Color endColor;

    /** Ориентация градиента. */
    private int orientation = 0;

    /** Цикличность градиента. */
    private boolean isCycle = true;

    /** позиция отсчёта градиента для начального цвета. */
    private int positionStartColor = 0;

    /** позиция отсчёта градиента для конечного цвета. */
    private int positionEndColor = 50;

    private boolean isEnableGradient = true;
    private boolean isFoundGradient = false;
    /** Иконка, служит для маркера вкладки на <code>OrTabbedPanel</code> при использовании <code>OrPanel</code> на <code>OrTabbedPanel</code> */
    private ImageIcon icon;
    /**Фоновое изображение компонента*/
    private ImageIcon backgroundPict = null;
    /**местоположение фонового изображения*/
    private int positionPict = GridBagConstraints.CENTER;
    /**изменять ли размеры изображения под размыры компонента*/
    private boolean autoResizePict = true;
    /** Прорисованно изображение на панели */
    private boolean isBackgroundPict;
    private String webNameIcon = null;
    private String webNameBg = null;
    private String titleAlign = "center";
    /** Разрешено ли удаление компонента. */
    private boolean isDelete = true;
    
    private boolean isHideBreadCrumps;
    
    /** Видима ли панель на вэбе если все кнопки с панели перенесены на тулбар. */
    public boolean isWebWisible = true; 
   /**
     * Конструктор панели
     * 
     * @param xml
     *            the xml
     * @param mode
     *            the mode
     * @param fm
     *            the fm
     * @param frame
     *            the frame
     * @throws KrnException
     *             the krn exception
     */
    OrPanel(Element xml, int mode, Factory fm, OrFrame frame) throws KrnException {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        init(fm, mode);
    }

    /**
     * Инициализация панели
     * 
     * @param fm
     *            the fm
     * @param mode
     *            the mode
     * @throws KrnException
     *             the krn exception
     */
    private void init(Factory fm, int mode) throws KrnException {
        setLayout(gbl);
        if (mode == Mode.DESIGN) {
            if(ServiceControl.instance()==null) {
                Or3Frame.instance().initControl();
            }
            tabbedContent = ServiceControl.instance().getContentTabs();
            enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
            setFocusable(true);
            // setBorder(BorderFactory.createLineBorder(Color.black));
            Spacer hspacer = new Spacer(X_AXIS);
            GridBagConstraints spacerCs = hspacerCts;
            spacerCs.gridx = 0;
            spacerCs.gridy = 1;
            add(hspacer, spacerCs);
            hspacers.add(hspacer);
            Spacer vspacer = new Spacer(Y_AXIS);
            spacerCs = vspacerCts;
            spacerCs.gridx = 1;
            spacerCs.gridy = 0;
            add(vspacer, spacerCs);
            vspacers.add(vspacer);
            doLayout();
        } else if (mode != Mode.PREVIEW) {
            adapter = new PanelAdapter((UIFrame) frame, this, false);
            Utils.setComponentFocusCircle(this);
        }

        PropertyNode pn = PROPS.getChild("alignmentText");
        PropertyValue pv;
        if (pn != null) {
            pv = getPropertyValue(pn);
            if (!pv.isNull()) {
                switch (pv.enumValue()) {
                case GridBagConstraints.CENTER:
                    titleAlign  = "center";
                    break;
                case GridBagConstraints.WEST:
                    titleAlign  = "left";
                    break;
                case GridBagConstraints.EAST:
                    titleAlign  = "right";
                    break;
                }
            }else {
                setPropertyValue(new PropertyValue(pn.getDefaultValue(), PROPS.getChild("alignmentText")));
            }
        }
        
        pn = PROPS.getChild("pov").getChild("activity");
        pv = getPropertyValue(pn.getChild("enabled"));
        if (pv.isNull()) {
            setPropertyValue(new PropertyValue(true, pn.getChild("enabled")));
        }

        // получить иконку
        pv = getPropertyValue(PROPS.getChild("view").getChild("icon"));
        if (!pv.isNull()) {
            setIcon(pv.getImageValue());
        }
        // получить фоное изображение
        pv = getPropertyValue(PROPS.getChild("view").getChild("backgroundPict"));
        if (!pv.isNull()) {
            setBackgroundPict(pv.getImageValue());
        }

        pv = getPropertyValue(PROPS.getChild("view").getChild("positionPict"));
        if (!pv.isNull()) {
            setPositionPict(pv.intValue());
        } else {
            setPropertyValue(new PropertyValue(positionPict, PROPS.getChild("view").getChild("positionPict")));
        }

        pv = getPropertyValue(PROPS.getChild("view").getChild("autoResizePict"));
        if (!pv.isNull()) {
            setAutoResizePict(pv.booleanValue());
        }
        
        pv = getPropertyValue(PROPS.getChild("view").getChild("hideBreadCrumps"));
        if (!pv.isNull()) {
        	isHideBreadCrumps = pv.booleanValue();
        }

        pv = getPropertyValue(PROPS.getChild("children"));
        if (!pv.isNull()) {
            List<Element> children = pv.elementValue().getChildren();
            List<Element> childrenCopy = new ArrayList<Element>(children);
            Collections.sort(childrenCopy, new Comparator<Element>() {
				@Override
				public int compare(Element e1, Element e2) {
					String uuid1 = e1.getChild("UUID") != null ? e1.getChild("UUID").getValue() : "no UUID";
					String uuid2 = e2.getChild("UUID") != null ? e2.getChild("UUID").getValue() : "no UUID";
					if (e1.getChild("pos").getChild("y") == null) {
						System.out.println(uuid1 + " - null y");
					}
					if (e1.getChild("pos").getChild("x") == null) {
						System.out.println(uuid1 + " - null x");
					}
					if (e2.getChild("pos").getChild("y") == null) {
						System.out.println(uuid2 + " - null y");
					}
					if (e2.getChild("pos").getChild("x") == null) {
						System.out.println(uuid2 + " - null x");
					}
					int res = e1.getChild("pos").getChild("y").getText().compareTo(e2.getChild("pos").getChild("y").getText());
					if (res == 0) {
						return e1.getChild("pos").getChild("x").getText().compareTo(e2.getChild("pos").getChild("x").getText());
					}
					return res;
				}
			});
            for (int i = 0; i < childrenCopy.size(); i++) {
                Element child = (Element) childrenCopy.get(i);
                OrGuiComponent c = fm.create(child, mode, frame);
                c.setGuiParent(this);
                GridBagConstraints cs = c.getConstraints();
                if (mode == Mode.DESIGN) {
                	int[][] dims = gbl.getLayoutDimensions();
                    int d = cs.gridx + cs.gridwidth - (dims[0].length - 1);
                    if (d > 0) {
                        insertCols((dims[0].length - 1), d);
                    }
                    d = cs.gridy + cs.gridheight - (dims[1].length - 1);
                    if (d > 0) {
                        insertRows((dims[1].length - 1), d);
                    }
                    addComponent(c, cs.gridx, cs.gridy, true);
                    try {
                        doLayout();
                    } catch (Exception e) {
                        String name = c.getVarName() == null ? "" : c.getVarName();
                        System.out.println("Property is not valid! Component:" + name + " " + c.getUUID());
                    }

                    if (cs.gridx < 0 || cs.gridy < 0) {
                        System.out.println("Coordinats is not valid! Component:" + c.getVarName() + " " + c.getUUID());
                    } else {
//                    	System.out.println("xxx " + c.getUUID() + "  " + c.getVarName());
                        setSpacerDeleteEnabled(cs.gridx, cs.gridy, false);
                    }
                } else {
                    setPreferredSize(c);
                    setMinimumSize(c);
                    setMaximumSize(c);
                    add((Component) c, cs);
                    if (c instanceof OrButton) {
                        OrButton button = (OrButton) c;
                        if (button.isDefaultButton())
                            defaultButton = button;
                    } else if (c instanceof OrPanel) {
                        OrButton button = ((OrPanel) c).getDefaultButton();
                        if (button != null)
                            defaultButton = button;
                    }
                }
            }
            // Если на панели есть кнопка по умолчанию, то убрать Enter из клавиш
            // перехода фокуса, т.к. по Enter должна будет нажиматься эта кнопка.
            if (defaultButton != null) {
                removeEnterKs();
            }
            validate();
            repaint();
        }
        // Utils.processBorder(this, frame, borderProps);
        updateProperties();
        if (mode != Mode.DESIGN) {
            setSpacers(false);
            constraints = PropertyHelper.getConstraints(PROPS, xml);
            prefSize = PropertyHelper.getPreferredSize(this);
            maxSize = PropertyHelper.getMaximumSize(this);
            minSize = PropertyHelper.getMinimumSize(this);
            // description = PropertyHelper.getDescription(this);
            PropertyValue titleVal = getPropertyValue(PROPS.getChild("title"));
            if (!titleVal.isNull()) {
                Pair p = titleVal.resourceStringValue();
                titleUID = (String) p.first;
                title = frame.getString(titleUID);
            }
            if (title == null || title.length() == 0) {
				PropertyValue titleExprVal = getPropertyValue(PROPS.getChild("title1").getChild("expr"));
				if (!titleExprVal.isNull()) {
	                String titleExpr = (String) titleExprVal.objectValue();
	                title = kz.tamur.comps.Utils.getExpReturn(titleExpr, frame, getAdapter());
				}
			}
            if (title == null) title = "";
            // setFocusCycleRoot(true);
            // setFocusTraversalPolicy(new FocusPolicy(this));
        }
        pn = getProperties().getChild("pov").getChild("activity").getChild("enabled");
        if (pn != null) {
            pv = getPropertyValue(pn);
            if (!pv.isNull()) {
                enabled = pv.booleanValue();
            } else {
                enabled = ((Boolean) pn.getDefaultValue()).booleanValue();
            }
        }
        if (mode == Mode.RUNTIME) {
            // всплывающая подсказка
            pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
                if (pv.objectValue() instanceof Expression) {
                    try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = kz.tamur.comps.Utils.getExpReturn(toolTipExpr, frame, getAdapter());
                        if (toolTipExprText != null && !toolTipExprText.isEmpty()) {
                            setToolTipText(toolTipExprText);
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка в формуле\r\n" + toolTipExpr + "\r\n" + e);
                    }
                } else {
                    toolTipUid = Funcs.normalizeInput((String) pv.resourceStringValue().first);
                    byte[] toolTip = frame.getBytes(toolTipUid);
                    if (toolTip != null) {
                        setToolTipText(new String(toolTip));
                    }
                }
            }
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    updateToolTip();
                }
            });
            pn = getProperties().getChild("pov").getChild("beforeOpen");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    beforeOpenTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            String path = paths.get(j);
                            OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            pn = getProperties().getChild("pov").getChild("afterOpen");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    afterOpenTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            String path = paths.get(j);
                            OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            pn = getProperties().getChild("pov").getChild("beforeClose");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    beforeCloseTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            String path = paths.get(j);
                            OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            pn = getProperties().getChild("pov").getChild("afterClose");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    afterCloseTemplate = OrLang.createStaticTemplate(expr);
                }
            }
            pn = getProperties().getChild("pov").getChild("afterSave");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    afterSaveTemplate = OrLang.createStaticTemplate(expr);
                }
            }
            pn = getProperties().getChild("pov").getChild("afterTaskListUpdate");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    afterTaskListUpdateTemplate = OrLang.createStaticTemplate(expr);
                }
            }
            pn = getProperties().getChild("pov").getChild("onNotification");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                	onNotificationTemplate = OrLang.createStaticTemplate(expr);
                }
            }
            pn = getProperties().getChild("pov").getChild("onMessageReceived");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                	messageRecievedTemplate = OrLang.createStaticTemplate(expr);
                }
            }
            pn = getProperties().getChild("pov").getChild("createXml");
            if (pn != null) {
                String expr = null;
                pv = getPropertyValue(pn);
                if (!pv.isNull()) {
                    expr = pv.stringValue();
                }
                if (expr != null && expr.length() > 0) {
                    createXmlTemplate = OrLang.createStaticTemplate(expr);
                    try {
                        Editor e = new Editor(expr);
                        ArrayList<String> paths = e.getRefPaths();
                        for (int j = 0; j < paths.size(); ++j) {
                            String path = paths.get(j);
                            OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(), OrRef.TR_CLEAR, frame);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            // Не храним XML в режиме выполнения компонента
            this.xml = null;
        }
        pn = null;
        /*
         * добавление слушателя, который будет перерисовывать родителя компонента если компонент прозрачен
         * необходимо для удаления артефактов прорисовки при изменении размеров прозрачных компонентов
         */

        addComponentListener(new ComponentListener() {

            public void componentShown(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {
                if (isOpaque() && getTopLevelAncestor() != null) {
                    getTopLevelAncestor().repaint();
                }
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    /**
     * Checks if is panel enabled.
     * 
     * @return true, if is panel enabled
     */
    public boolean isPanelEnabled() {
        return enabled;
    }
    
    public List<Spacer> getHSpacers() {
    	return hspacers;
    }
    
    public List<Spacer> getVSpacers() {
    	return vspacers;
    }    

    @Override
    public String getTitle() {
        return title;
    }
    
    public boolean isHideBreadCrumps() {
    	return isHideBreadCrumps;
    }

    @Override
    public Dimension getPrefSize() {
        return (mode == Mode.RUNTIME) ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    @Override
    public Dimension getMaxSize() {
        return (mode == Mode.RUNTIME) ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    @Override
    public Dimension getMinSize() {
        return (mode == Mode.RUNTIME) ? minSize : PropertyHelper.getMinimumSize(this);
    }

    /**
     * Добавление строки
     * 
     * @param inserting
     *            the inserting
     * @param sp
     *            the sp
     */
    public void addRow(boolean inserting, Spacer sp) {
        GridBagConstraints cs = gbl.getConstraints(sp);
        if (!inserting) {
            cs.gridy++;
        }
        insertRows(cs.gridy, 1);
        validate();
        repaint();
    }

    /**
     * Удаление строки
     * 
     * @param sp
     *            the sp
     */
    public void removeRow(Spacer sp) {
        GridBagConstraints cs = gbl.getConstraints(sp);
        insertRows(cs.gridy, -1);
        validate();
        repaint();
    }

    /**
     * Добавление столбца
     * 
     * @param inserting
     *            the inserting
     * @param sp
     *            the sp
     */
    public void addColumn(boolean inserting, Spacer sp) {
        GridBagConstraints cs = gbl.getConstraints(sp);
        if (!inserting) {
            cs.gridx++;
        }
        insertCols(cs.gridx, 1);
        validate();
        repaint();
    }

    /**
     * Удаление столбца
     * 
     * @param sp
     *            the sp
     */
    public void removeColumn(Spacer sp) {
        GridBagConstraints cs = gbl.getConstraints(sp);
        insertCols(cs.gridx, -1);
        validate();
        repaint();
    }

    
    public boolean canAddComponent(int x, int y) {
        return true;
    }

    
    public void addComponent(OrGuiComponent c, int x, int y) {
        // Выделить слушателей на удаление
        java.util.List<OrGuiComponent> copyList = new ArrayList<OrGuiComponent>(c.getListListeners());
        // Добавить слушателей родителя в добавляемый компонент
        c.setListListeners(listListeners, copyList);
        
        Point o = gbl.getLayoutOrigin();
        int[][] dims = gbl.getLayoutDimensions();
        int gridx = dims[0].length - 2;
        int gridy = dims[1].length - 2;
        int t = o.x;
        for (int i = 0; i < dims[0].length - 1; i++) {
            t += dims[0][i];
            if (x < t) {
                gridx = i;
                break;
            }
        }
        t = o.y;
        for (int i = 0; i < dims[1].length - 1; i++) {
            t += dims[1][i];
            if (y < t) {
                gridy = i;
                break;
            }
        }
        addComponent(c, gridx, gridy, false);
        setSpacerDeleteEnabled(gridx, gridy, false);
    }

    /**
     * Sets the spacer delete enabled.
     * 
     * @param gridx
     *            the gridx
     * @param gridy
     *            the gridy
     * @param isDeleted
     *            the is deleted
     */
    public void setSpacerDeleteEnabled(int gridx, int gridy, boolean isDeleted) {
    	if (gridx < hspacers.size() && gridy < vspacers.size()) {
	        Spacer vs = vspacers.get(gridy);
	        Spacer hs = hspacers.get(gridx);
	        if (isDeleted) {
	            boolean delR = isDeleted;
	            boolean delC = isDeleted;
	            Component[] components = getComponents();
	            for (int i = 0; i < components.length; i++) {
	                Component comp = components[i];
	                if (!(comp instanceof Spacer)) {
	                    GridBagConstraints cs = gbl.getConstraints(comp);
	                    if (cs.gridx == gridx) {
	                        delR = false;
	                    }
	                    if (cs.gridy == gridy) {
	                        delC = false;
	                    }
	                }
	            }
	            vs.setDeleteEnabled(delC);
	            hs.setDeleteEnabled(delR);
	
	        } else {
	            hs.setDeleteEnabled(isDeleted);
	            vs.setDeleteEnabled(isDeleted);
	        }
    	}
    }

    /**
     * Adds the component.
     * 
     * @param c
     *            the c
     * @param gridx
     *            the gridx
     * @param gridy
     *            the gridy
     * @param isLoading
     *            the is loading
     */
    public void addComponent(OrGuiComponent c, int gridx, int gridy, boolean isLoading) {
        if (!isLoading) {
            PropertyNode pos = c.getProperties().getChild("pos");
            c.setPropertyValue(new PropertyValue(gridx, pos.getChild("x")));
            c.setPropertyValue(new PropertyValue(gridy, pos.getChild("y")));
            c.setPropertyValue(new PropertyValue(1, pos.getChild("width")));
            c.setPropertyValue(new PropertyValue(1, pos.getChild("height")));
            try {
            	PropertyHelper.addProperty(new PropertyValue(c.getXml(), PROPS.getChild("children")), xml);
            } catch (IllegalAddException e) {
            	
            }
        }
        if (c.isCopy()) {
            c.setCopy(false);
        }
        if (mode != Mode.DESIGN) {
            PropertyValue pv = getPropertyValue(getProperties().getChild("pov").getChild("activity").getChild("enabled"));
            if (!pv.isNull()) {
                if (pv.booleanValue() == false)
                    ((Component) c).setEnabled(false);
            }
        }
        try {
        	add((Component) c);
        } catch (IllegalArgumentException e) {}
        setConstraints(c);
        setPreferredSize(c);
        setMinimumSize(c);
        setMaximumSize(c);
        validate();
        repaint();
    }

    /**
     * Установить constraints.
     * 
     * @param comp
     *            the new constraints
     */
    public void setConstraints(OrGuiComponent comp) {
        gbl.setConstraints((Component) comp, comp.getConstraints());
        try {
            doLayout();
        } catch (Exception e) {
        }
        updatePlaces();
    }

    /**
     * Установить предпочитаемый размер
     * 
     * @param comp
     *            новый предпочитаемый размер
     */
    public void setPreferredSize(OrGuiComponent comp) {
        Dimension sz = comp.getPrefSize();
        if (sz != null) {
            ((JComponent) comp).setPreferredSize(sz);
        }
    }

    /**
     * Установить минимальный размер
     * 
     * @param comp
     *            новый минимальный размер
     */
    public void setMinimumSize(OrGuiComponent comp) {
        Dimension sz = comp.getMinSize();
        if (sz != null) {
            ((JComponent) comp).setMinimumSize(sz);
        }
    }

    /**
     * Установить максимальный размер
     * 
     * @param comp
     *            новый максимальный размер
     */
    public void setMaximumSize(OrGuiComponent comp) {
        Dimension sz = comp.getMaxSize();
        if (sz != null) {
            ((JComponent) comp).setMaximumSize(sz);
        }
    }

    
    public void removeComponent(OrGuiComponent c) {
        if (c.isCopy()) {
            removeComponentCopy(c);
        } else {
            GridBagConstraints cs = gbl.getConstraints((Component) c);
            removeComponentImpl(c);
            setSpacerDeleteEnabled(cs.gridx, cs.gridy, true);
            updatePlaces();
        }
        validate();
        repaint();
    }

    public void removeComponentForMassHtmlGen(OrGuiComponent c){
        if (c.isCopy()) {
            removeComponentCopy(c);
        } else {
            removeComponentImpl(c);
        }
        revalidate();
    }
    
    /**
     * Удаление компонента с панели и чистка свойств
     * 
     * @param c
     *            компонент
     */
    private void removeComponentImpl(OrGuiComponent c) {
        Component comp = (Component) c;
        remove(comp);
        PropertyHelper.removeProperty(new PropertyValue(c.getXml(), PROPS.getChild("children")), xml);
    }

    /**
     * Removes the component copy.
     * 
     * @param c
     *            the c
     */
    private void removeComponentCopy(OrGuiComponent c) {
        Container cont = getTopLevelAncestor();
        Component comp = (Component) c;
        if (cont instanceof JFrame) {
            JLayeredPane layeredPane = ((JFrame) cont).getLayeredPane();
            layeredPane.remove(comp);
            layeredPane.validate();
            layeredPane.repaint();
        }
    }

    
    public void moveComponent(OrGuiComponent c, int x, int y) {
        GridBagConstraints cs = gbl.getConstraints((Component) c);
        if (c.isCopy()) {
            removeComponentCopy(c);
        } else {
            removeComponentImpl(c);
            setSpacerDeleteEnabled(cs.gridx, cs.gridy, true);
        }
        addComponent(c, x, y);
    }

    /**
     * Обновление местоположения компонента
     */
    private void updatePlaces() {
        int mx = 0;
        int my = 0;
        int[][] dims = gbl.getLayoutDimensions();
        int[] ws = new int[dims[0].length];
        int[] hs = new int[dims[1].length];
        Component[] children = getComponents();
        for (int i = 0; i < children.length; i++) {
            Component child = children[i];
            GridBagConstraints cs = gbl.getConstraints(child);
            if (child instanceof OrGuiComponent) {
                for (int j = 0; j < cs.gridwidth; j++) {
                    try {
                    ws[cs.gridx + j] = 1;
                    } catch (Exception e) {
                        System.out.println("Invalid property interface! Name сomponent '" + ((OrGuiComponent)child).getVarName() + "' Path: " + getFullPathComponent(child));
                    }
                }
                for (int j = 0; j < cs.gridheight; j++) {
                    try {
                        hs[cs.gridy + j] = 1;
                    } catch (Exception e) {
                        System.out.println("Invalid property interface! Name сomponent '" + ((OrGuiComponent)child).getVarName() + "' Path: " + getFullPathComponent(child));
                    }
                    
                    
                }
                if (mx < cs.gridx + cs.gridwidth - 1) {
                    mx = cs.gridx + cs.gridwidth - 1;
                }
                if (my < cs.gridy + cs.gridheight - 1) {
                    my = cs.gridy + cs.gridheight - 1;
                }
            }
        }
        // корректировка горизонтальных спейсеров
        for (int i = 0; i < hspacers.size(); i++) {
            Spacer sp = hspacers.get(i);
            GridBagConstraints cs = gbl.getConstraints(sp);
            try {
                cs.weightx = ws[i] == 1 ? 0 : 1;
            } catch (Exception e) {
                System.out.println("Invalid property interface! Name сomponent '" + sp.getName() + "' Path: " + getFullPathComponent(sp));
            }
            gbl.setConstraints(sp, cs);
        }
        // корректировка вертикальных спейсеров
        for (int i = 0; i < vspacers.size(); i++) {
            Spacer sp = vspacers.get(i);
            GridBagConstraints cs = gbl.getConstraints(sp);
            try {
                cs.weighty = hs[i] == 1 ? 0 : 1;
            } catch (Exception e) {
                System.out.println("Invalid property interface! Name сomponent '" + sp.getName() + "' Path: " + getFullPathComponent(sp));
            }
            gbl.setConstraints((Component) vspacers.get(i), cs);
        }
    }

    /**
     * Вставка строк
     * 
     * @param pos
     *            позиция
     * @param count
     *            количество
     */
    public void insertRows(int pos, int count) {
        Map<Long, InterfaceFrame> interfaces = null;
        if (mode == Mode.DESIGN) {
            interfaces = tabbedContent.getInterfaces();
        }
        ArrayList<Long> loadingInterfacesID = (ArrayList<Long>) DesignerFrame.getLoadingInterfaceID();
        Component[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component comp = comps[i];
            GridBagConstraints cs = gbl.getConstraints(comp);
            if (cs.gridy >= pos) {
                cs.gridy += count;
                gbl.setConstraints(comp, cs);
                if (comp instanceof OrGuiComponent) {
                    savePosition((OrGuiComponent) comp, cs.gridx, cs.gridy);
                }
            }
        }
        if (count > 0) {
            GridBagConstraints spacerCs = gbl.getConstraints(vspacers.get(0));
            spacerCs.weighty = 1;
            for (int i = 0; i < count; i++) {
                Spacer s = new Spacer(Y_AXIS);
                spacerCs.gridy = pos + i;
                vspacers.add(pos + i, s);
                add(s, spacerCs);
                if (mode == Mode.DESIGN) {
                    int match = 0;
                    for (Long a : loadingInterfacesID) {
                        if (interfaces.containsKey(a)) {
                            match++;
                        }
                    }
                    if (match == DesignerFrame.getLoadingInterfaceID().size() && match != 0) {
                        tabbedContent.propertyModified(this);
                        String componentID = getInterfaceActions(DesignerFrame.getTabbedContent().getKrnObjectIfr().id) .getNextID();
                        getInterfaceActions(tabbedContent.getSelectedFrame().getUiObject().id).getGUIComponents().put( componentID, this);
                        getInterfaceActions(tabbedContent.getKrnObjectIfr().id).addRow(componentID, this, spacerCs.gridx, spacerCs.gridy);
                    }
                }
            }
        } else if (count < 0) {
            for (int i = 0; i < -count; i++) {
                Spacer s = vspacers.remove(pos - i);
                GridBagConstraints spacerCs = gbl.getConstraints(s);
                if (mode == Mode.DESIGN) {
                    int match = 0;
                    for (Long a : loadingInterfacesID) {
                        if (interfaces.containsKey(a)) {
                            match++;
                        }
                    }
                    if (match == DesignerFrame.getLoadingInterfaceID().size() && match != 0) {
                        tabbedContent.propertyModified(this);
                        String componentID = getInterfaceActions(DesignerFrame.getTabbedContent().getKrnObjectIfr().id)
                                .getNextID();
                        getInterfaceActions(tabbedContent.getSelectedFrame().getUiObject().id).getGUIComponents().put(
                                componentID, this);
                        getInterfaceActions(tabbedContent.getKrnObjectIfr().id).dropRow(componentID, this, spacerCs.gridx,
                                spacerCs.gridy + 1);
                    }
                }
                remove(s);
            }
        }
    }

    /**
     * Вставка столбцов
     * 
     * @param pos
     *            позиция
     * @param count
     *            количество
     */
    public void insertCols(int pos, int count) {
        Map<Long, InterfaceFrame> interfaces = null;
        if (mode == Mode.DESIGN) {
            interfaces = tabbedContent.getInterfaces();
        }
        ArrayList<Long> loadingInterfacesID = (ArrayList<Long>) DesignerFrame.getLoadingInterfaceID();
        Component[] comps = getComponents();
        for (int i = 0; i < comps.length; i++) {
            Component comp = comps[i];
            GridBagConstraints cs = gbl.getConstraints(comp);
            if (cs.gridx >= pos) {
                cs.gridx += count;
                gbl.setConstraints(comp, cs);
                if (comp instanceof OrGuiComponent) {
                    savePosition((OrGuiComponent) comp, cs.gridx, cs.gridy);
                }
            }
        }
        if (count > 0) {
            GridBagConstraints spacerCs = gbl.getConstraints(hspacers.get(0));
            spacerCs.weightx = 1;
            for (int i = 0; i < count; i++) {
                Spacer s = new Spacer(X_AXIS);
                spacerCs.gridx = pos + i;
                hspacers.add(pos + i, s);
                add(s, spacerCs);
                if (mode == Mode.DESIGN) {
                    int match = 0;
                    for (Long a : loadingInterfacesID) {
                        if (interfaces.containsKey(a)) {
                            match++;
                        }
                    }
                    if (match == DesignerFrame.getLoadingInterfaceID().size() && match != 0) {
                        tabbedContent.propertyModified(this);
                        String componentID = getInterfaceActions( DesignerFrame.getTabbedContent().getKrnObjectIfr().id).getNextID();
                        getInterfaceActions(tabbedContent.getSelectedFrame().getUiObject().id) .getGUIComponents().put(componentID, this);
                        getInterfaceActions(tabbedContent.getKrnObjectIfr().id).addColumn( componentID, this, spacerCs.gridx, spacerCs.gridy);
                    }
                }
            }
        } else if (count < 0) {
            for (int i = 0; i < -count; i++) {
                Spacer s = hspacers.remove(pos - i);
                GridBagConstraints spacerCs = gbl.getConstraints(s);
                if (mode == Mode.DESIGN) {
                    int match = 0;
                    for (Long a : loadingInterfacesID) {
                        if (interfaces.containsKey(a)) {
                            match++;
                        }
                    }
                    if (match == DesignerFrame.getLoadingInterfaceID().size() && match != 0) {
                        tabbedContent.propertyModified(this);
                        String componentID = getInterfaceActions(DesignerFrame.getTabbedContent().getKrnObjectIfr().id) .getNextID();
                        getInterfaceActions(tabbedContent.getSelectedFrame().getUiObject().id).getGUIComponents().put( componentID, this);
                        getInterfaceActions(tabbedContent.getKrnObjectIfr().id).dropColumn(componentID, this, spacerCs.gridx + 1, spacerCs.gridy);
                    }
                }
                remove(s);
            }
        }
    }

    
    protected void paintChildren(Graphics g) {
        if (mode == Mode.DESIGN) {
            Color oldColor = g.getColor();
            g.setColor(Color.gray);
            Point o = gbl.getLayoutOrigin();
            int[][] dims = gbl.getLayoutDimensions();
            int fullWidth = 0;
            for (int i = 0; i < dims[0].length; i++) {
                fullWidth += dims[0][i];
            }
            int fullHeight = 0;
            for (int i = 0; i < dims[1].length; i++) {
                fullHeight += dims[1][i];
            }
            for (int i = 0, w = o.x; i < dims[0].length - 1; i++) {
                w += dims[0][i];
                g.drawLine(w, o.y, w, o.y + fullHeight);
            }
            for (int i = 0, h = o.y; i < dims[1].length - 1; i++) {
                h += dims[1][i];
                g.drawLine(o.x, h, o.x + fullWidth, h);
            }
            g.drawRect(o.x, o.y, fullWidth - 1, fullHeight - 1);
            g.setColor(oldColor);
        }
        super.paintChildren(g);
    }

    /*
     * private boolean checkNotEnabled(OrGuiComponent comp) {
     * PropertyNode pn = comp.getProperties().getChild("editable");
     * if (pn != null) {
     * PropertyValue pv = comp.getPropertyValue(pn);
     * if (!pv.isNull()) {
     * return pv.booleanValue();
     * }
     * }
     * return false;
     * }
     */

    
    public void paint(Graphics g) {
        super.paint(g);
        // Прорисовка рамки компонента. Необходимо в режиме дизайнера для визуального выделения компонента когда он выбран
        if (mode == Mode.DESIGN && isSelected) {
            Utils.drawRects(this, g);
        }
    }

    @Override
    public PropertyNode getProperties() {
        return PROPS;
    }

    @Override
    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    @Override
    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        kz.tamur.comps.Utils.processStdCompProperties(this, value);
        updateProperties();
        final String name = value.getProperty().getName();
        PropertyNode pn;
        PropertyValue pv;
        if ("title".equals(name)) {
            firePropertyModified();
            pv = getPropertyValue(getProperties().getChild("title"));
            setTitle(pv.toString());
            
        } else if ("gradient".equals(name)) {
            // если градиентная заливка отключена, необходимо перерисовать компоеннт основным его цветом
            pv = getPropertyValue(getProperties().getChild("extended").getChild("gradient"));
            if (pv.isNull()) {
                pn = getProperties().getChild("view").getChild("background");
                pv = getPropertyValue(pn.getChild("backgroundColor"));
                setBackground(pv.isNull() ? (Color) pn.getChild("backgroundColor").getDefaultValue() : pv.colorValue());
                repaintAll();
            }
        } else if ("transparent".equals(name)) {
            // перерисовка
            repaintAll();
        } else if ("icon".equals(name)) {
            // получить иконку
            pv = getPropertyValue(getProperties().getChild("view").getChild("icon"));
            setIcon(pv.getImageValue());
            Container parent = getParent();
            if (parent instanceof OrTabbedPane) {
                ((OrTabbedPane) parent).updateIcon();
            }
        } else if ("backgroundPict".equals(name)) {
            // получить фоное изображение
            pv = getPropertyValue(PROPS.getChild("view").getChild("backgroundPict"));
            if (!pv.isNull()) {
                setBackgroundPict(pv.getImageValue());
            }else {
                setBackgroundPict(null); 
            }
            repaintAll();
        } else if ("positionPict".equals(name)) {
            setPositionPict(getPropertyValue(PROPS.getChild("view").getChild("positionPict")).intValue());
            repaintAll();
        } else if ("autoResizePict".equals(name)) {
            setAutoResizePict(getPropertyValue(PROPS.getChild("view").getChild("autoResizePict")).booleanValue());
            repaintAll();
        }
    }

    /**
     * Обновление свойств компонента
     */
    private void updateProperties() {
        PropertyNode pn = getProperties().getChild("view").getChild("background");
        PropertyValue pv = getPropertyValue(pn.getChild("backgroundColor"));

        setBackground(pv.isNull() ? (Color) pn.getChild("backgroundColor").getDefaultValue() : pv.colorValue());

        pn = getProperties().getChild("view").getChild("border");
        if (pn != null) {
            pv = getPropertyValue(pn.getChild("borderType"));
            borderType = pv.isNull() ? (Border) pn.getChild("borderType").getDefaultValue() : pv.borderValue();
            pv = getPropertyValue(pn.getChild("borderTitle"));
            if (!pv.isNull()) {
                borderTitleUID = (String) pv.resourceStringValue().first;
            }
        }
        pv = getPropertyValue(PROPS.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String) p.first;
            description = (byte[]) p.second;
        }

        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }

        pn = getProperties().getChild("extended");
        pv = getPropertyValue(pn.getChild("gradient"));
        if (!pv.isNull()) {
            isFoundGradient = true;
            // градиентная заливка компонента
            setGradient((GradientColor) pv.objectValue());
        } else {
            isFoundGradient = false;
        }
        pv = getPropertyValue(pn.getChild("transparent"));
        if (!pv.isNull()) {
            // прозрачность компонента(да/нет)
            setOpaque(!pv.booleanValue());
        }
        repaintAll();

        kz.tamur.comps.Utils.processBorderProperties(this, frame);
    }

    @Override
    public Element getXml() {
        return xml;
    }

    @Override
    public GridBagConstraints getConstraints() {
        return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml);
    }

    @Override
    public void setSelected(boolean isSelected) {
        if (mode == Mode.DESIGN && isSelected) {
            for (OrGuiComponent listener : listListeners) {
                if (listener instanceof OrCollapsiblePanel) {
                    ((OrCollapsiblePanel) listener).expand();
                } else if (listener instanceof OrAccordion) {
                    ((OrAccordion) listener).expand();
                } else if (listener instanceof OrPopUpPanel) {
                    ((OrPopUpPanel) listener).showEditor(true);
                }
            }
        }
        this.isSelected = isSelected;
        repaint();
    }

    public void updateConstraints(OrGuiComponent c) {
        invalidate();
        setConstraints(c);
        setPreferredSize(c);
        setMinimumSize(c);
        setMaximumSize(c);
        validate();
        repaint();
    }

    @Override
    public int getComponentStatus() {
        return Constants.CONTAINER_COMP;
    }

    @Override
    public void setLangId(long langId) {
        kz.tamur.comps.Utils.processBorderProperties(this, frame);
        title = frame.getString(titleUID);
        if (title == null || title.length() == 0) {
			PropertyValue titleExprVal = getPropertyValue(PROPS.getChild("title1").getChild("expr"));
			if (!titleExprVal.isNull()) {
                String titleExpr = (String) titleExprVal.objectValue();
                title = kz.tamur.comps.Utils.getExpReturn(titleExpr, frame, getAdapter());
			}
		}
        if (title == null) title = "";

        if (mode == Mode.RUNTIME) {
            if (descriptionUID != null)
                description = frame.getBytes(descriptionUID);
            if (toolTipUid != null) {
                byte[] toolTip = frame.getBytes(toolTipUid);
                setToolTipText(toolTip == null ? null : new String(toolTip));
            } else {
                updateToolTip();
            }
        } else {
            PropertyValue pv = getPropertyValue(PROPS.getChild("description"));
            if (!pv.isNull()) {
                Pair p = pv.resourceStringValue();
                description = (byte[]) p.second;
            }
        }
        Component[] cs = getComponents();
        for (int i = 0; i < cs.length; i++) {
            Component c = cs[i];
            if (c instanceof OrGuiComponent) {
                ((OrGuiComponent) c).setLangId(langId);
            }
        }
    }

    /**
     * The Class Spacer.
     */
    public class Spacer extends JLabel implements ComponentListener, ActionListener {

        /** The ins row bef but. */
        private JButton insRowBefBut = ButtonsFactory.createSpaserButton(ButtonsFactory.INS_ROW_BEFORE);

        /** The ins row aft but. */
        private JButton insRowAftBut = ButtonsFactory.createSpaserButton(ButtonsFactory.INS_ROW_AFTER);

        /** The ins col bef but. */
        private JButton insColBefBut = ButtonsFactory.createSpaserButton(ButtonsFactory.INS_COL_BEFORE);

        /** The ins col aft but. */
        private JButton insColAftBut = ButtonsFactory.createSpaserButton(ButtonsFactory.INS_COL_AFTER);

        /** The del row but. */
        private JButton delRowBut = ButtonsFactory.createSpaserButton(ButtonsFactory.DELETE_RC);

        /** The del col but. */
        private JButton delColBut = ButtonsFactory.createSpaserButton(ButtonsFactory.DELETE_RC);

        /** The down arr but. */
        private JButton downArrBut = ButtonsFactory.createSpaserButton(ButtonsFactory.ARROW_DOWN);

        /** The left arr but. */
        private JButton leftArrBut = ButtonsFactory.createSpaserButton(ButtonsFactory.ARROW_LEFT);

        /** The grid ops. */
        private JPopupMenu gridOps = new JPopupMenu();

        /** The row ops. */
        private JPopupMenu rowOps = new JPopupMenu("Строка");

        /** The ins before row. */
        private JMenuItem insBeforeRow = createMenuItem("Вставить строку до");

        /** The ins after row. */
        private JMenuItem insAfterRow = createMenuItem("Вставить строку после");

        /** The del row. */
        private JMenuItem delRow = createMenuItem("Удалить строку");

        /** The col ops. */
        private JPopupMenu colOps = new JPopupMenu("Колонка");

        /** The ins before col. */
        private JMenuItem insBeforeCol = createMenuItem("Вставить колонку до");

        /** The ins after col. */
        private JMenuItem insAfterCol = createMenuItem("Вставить колонку после");

        /** The del col. */
        private JMenuItem delCol = createMenuItem("Удалить колонку");

        /** The dir. */
        private int dir;

        /** The is popup. */
        private boolean isPopup = false;

        /**
         * Instantiates a new spacer.
         * 
         * @param dir
         *            the dir
         */
        public Spacer(int dir) {
            this.dir = dir;
            setOpaque(false);
            BoxLayout hLay = new BoxLayout(this, BoxLayout.X_AXIS);
            BoxLayout vLay = new BoxLayout(this, BoxLayout.Y_AXIS);
            if (dir == X_AXIS) {
                addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            colOps.show(Spacer.this, e.getX(), e.getY());
                        }
                    }
                });
                setPreferredSize(new Dimension(10, 12));
                setMinimumSize(new Dimension(10, 12));
                setMaximumSize(new Dimension(10, 12));
                setLayout(hLay);
            } else if (dir == Y_AXIS) {
                addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            rowOps.show(Spacer.this, e.getX(), e.getY());
                        }
                    }
                });
                setPreferredSize(new Dimension(12, 10));
                setMinimumSize(new Dimension(12, 10));
                setMaximumSize(new Dimension(12, 10));
                setLayout(vLay);
            }
            insColBefBut.addActionListener(Spacer.this);
            insColAftBut.addActionListener(Spacer.this);
            insRowBefBut.addActionListener(Spacer.this);
            insRowAftBut.addActionListener(Spacer.this);
            delColBut.addActionListener(Spacer.this);
            delRowBut.addActionListener(Spacer.this);
            downArrBut.addActionListener(Spacer.this);
            leftArrBut.addActionListener(Spacer.this);

            insBeforeRow.addActionListener(Spacer.this);
            insAfterRow.addActionListener(Spacer.this);
            delRow.addActionListener(Spacer.this);

            insBeforeCol.addActionListener(Spacer.this);
            insAfterCol.addActionListener(Spacer.this);
            delCol.addActionListener(Spacer.this);
            gridOps.setFont(Utils.getDefaultFont());
            rowOps.setFont(Utils.getDefaultFont());
            rowOps.add(insBeforeRow);
            rowOps.add(insAfterRow);
            rowOps.add(delRow);
            gridOps.add(rowOps);
            colOps.setFont(Utils.getDefaultFont());
            colOps.add(insBeforeCol);
            colOps.add(insAfterCol);
            colOps.add(delCol);
            gridOps.add(colOps);
            initButtons(isPopup);
            addComponentListener(this);
            setBorder(null);
            if (mode == Mode.RUNTIME) {
                setFocusable(false);
            } else {
                setFocusable(true);
            }

            // setVisible(false);
        }

        /**
         * Установить возможность удаления
         * 
         * @param isEnabled
         *            можно ли удалять?
         */
        public void setDeleteEnabled(boolean isEnabled) {
            if (dir == X_AXIS) {
                delColBut.setEnabled(isEnabled);
                delCol.setEnabled(isEnabled);
            } else if (dir == Y_AXIS) {
                delRowBut.setEnabled(isEnabled);
                delRow.setEnabled(isEnabled);
            }
        }

        /**
         * Инициализация кнопок
         * 
         * @param p
         *            the p
         */
        private void initButtons(boolean p) {
            if (dir == X_AXIS) {
                add(Box.createHorizontalGlue());
                if (p) {
                    add(downArrBut);
                } else {
                    add(insColBefBut);
                    add(Box.createRigidArea(new Dimension(5, 0)));
                    add(insColAftBut);
                    add(Box.createRigidArea(new Dimension(5, 0)));
                    add(delColBut);
                }
                add(Box.createHorizontalGlue());
            }
            if (dir == Y_AXIS) {
                add(Box.createVerticalGlue());
                if (p) {
                    add(leftArrBut);
                } else {
                    add(insRowBefBut);
                    add(Box.createRigidArea(new Dimension(0, 5)));
                    add(insRowAftBut);
                    add(Box.createRigidArea(new Dimension(0, 5)));
                    add(delRowBut);
                }
                add(Box.createVerticalGlue());
            }
            repaint();
        }

        /**
         * Удаление кнопок
         */
        private void removeButtons() {
            removeAll();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
         */
        public void componentResized(ComponentEvent e) {
            boolean p = false;
            if (dir == X_AXIS) {
                p = getWidth() <= 30;
            } else if (dir == Y_AXIS) {
                p = getHeight() <= 30;
            }
            if (p != isPopup) {
                removeButtons();
                initButtons(p);
                isPopup = p;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
         */
        public void componentMoved(ComponentEvent e) {

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
         */
        public void componentShown(ComponentEvent e) {

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
         */
        public void componentHidden(ComponentEvent e) {

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == insColBefBut || src == insBeforeCol) {
                addColumn(true, this);
            } else if (src == insColAftBut || src == insAfterCol) {
                addColumn(false, this);
            } else if (src == delColBut || src == delCol) {
                removeColumn(this);
            } else if (src == delRowBut || src == delRow) {
                removeRow(this);
            } else if (src == insRowBefBut || src == insBeforeRow) {
                addRow(true, this);
            } else if (src == insRowAftBut || src == insAfterRow) {
                addRow(false, this);
            } else if (src == downArrBut) {
                Point p = downArrBut.getLocation();
                colOps.show(Spacer.this, p.x, p.y);
            } else if (src == leftArrBut) {
                Point p = leftArrBut.getLocation();
                rowOps.show(Spacer.this, p.x, p.y);
            }

        }
    }

    /**
     * Сохранение позиции
     * 
     * @param c
     *            компонент
     * @param gridx
     *            номер столбца
     * @param gridy
     *            номер строки
     */
    private void savePosition(OrGuiComponent c, int gridx, int gridy) {
        PropertyNode pos = c.getProperties().getChild("pos");
        c.setPropertyValue(new PropertyValue(gridx, pos.getChild("x")));
        c.setPropertyValue(new PropertyValue(gridy, pos.getChild("y")));
    }

    /**
     * Установить пружины.
     * 
     * @param isVisible
     *            the new spacers
     */
    private void setSpacers(boolean isVisible) {
        for (int i = 0; i < hspacers.size(); i++) {
            Spacer s = hspacers.get(i);
            s.setVisible(isVisible);
        }
        for (int i = 0; i < vspacers.size(); i++) {
            Spacer s = vspacers.get(i);
            s.setVisible(isVisible);
        }
    }

    @Override
    public int getMode() {
        return mode;
    }

    
    public void addPropertyListener(PropertyListener l) {
        listeners.add(PropertyListener.class, l);
    }

    
    public void removePropertyListener(PropertyListener l) {
        listeners.remove(PropertyListener.class, l);
    }

    public void firePropertyModified() {
        EventListener[] list = listeners.getListeners(PropertyListener.class);
        for (int i = 0; i < list.length; i++) {
            ((PropertyListener) list[i]).propertyModified(this);
        }
    }

    @Override
    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    @Override
    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    @Override
    public void setXml(Element xml) {
        this.xml = xml;
    }

    //
    /**
     * Получить tab index.
     * 
     * @return the tab index
     */
    public int getTabIndex() {
        return -1;
    }

    @Override
    public boolean isCopy() {
        return isCopy;
    }

    @Override
    public void setCopy(boolean copy) {
        isCopy = copy;
        if (isCopy) {
            standartBorder = getBorder();
            setBorder(copyBorder);
        } else {
            setBorder(standartBorder);
        }
    }

    /**
     * Получить border type.
     * 
     * @return the border type
     */
    public Border getBorderType() {
        return borderType;
    }

    /**
     * Получить border title uid.
     * 
     * @return the border title uid
     */
    public String getBorderTitleUID() {
        return borderTitleUID;
    }

    /**
     * Получить or frame.
     * 
     * @return the or frame
     */
    public OrFrame getOrFrame() {
        return frame;
    }

    @Override
    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

    @Override
    public kz.tamur.rt.adapters.ComponentAdapter getAdapter() {
        return adapter;
    }

    /**
     * Получить before open template.
     * 
     * @return the before open template
     */
    public ASTStart getBeforeOpenTemplate() {
        return beforeOpenTemplate;
    }

    /**
     * Получить after open template.
     * 
     * @return the after open template
     */
    public ASTStart getAfterOpenTemplate() {
        return afterOpenTemplate;
    }

    /**
     * Получить before close template.
     * 
     * @return the before close template
     */
    public ASTStart getBeforeCloseTemplate() {
        return beforeCloseTemplate;
    }

    /**
     * Получить after close template.
     * 
     * @return the after close template
     */
    public ASTStart getAfterCloseTemplate() {
        return afterCloseTemplate;
    }

    /**
     * Получить after save template.
     * 
     * @return the after save template
     */
    public ASTStart getAfterSaveTemplate() {
        return afterSaveTemplate;
    }

    /**
     * Получить действия после обновления списка задач.
     * 
     * @return the after save template
     */
    public ASTStart getAfterTaskListUpdateTemplate() {
        return afterTaskListUpdateTemplate;
    }
    
    public ASTStart getOnNotificationTemplate() {
        return onNotificationTemplate;
    }

    public ASTStart getMessageRecievedTemplate() {
        return messageRecievedTemplate;
    }

    /**
     * Получить creates the xml template.
     * 
     * @return the creates the xml template
     */
    public ASTStart getCreateXmlTemplate() {
        return createXmlTemplate;
    }

    /**
     * Получить default button.
     * 
     * @return the default button
     */
    public OrButton getDefaultButton() {
        return defaultButton;
    }

    /**
     * Removes the enter ks.
     */
    private void removeEnterKs() {
        AWTKeyStroke enterKs = AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0, false);
        for (Component comp : getComponents()) {
            Set<AWTKeyStroke> keys = new HashSet<AWTKeyStroke>(
                    comp.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
            keys.remove(enterKs);
            comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, keys);
            if (comp instanceof OrPanel)
                ((OrPanel) comp).removeEnterKs();
        }
    }

    
    public OrGuiComponent getComponent(String title) {
        if (title.equals(getVarName()))
            return this;

        int count = getComponentCount();

        for (int i = 0; i < count; i++) {
            Component c = getComponent(i);
            if (c instanceof OrGuiContainer) {
                OrGuiComponent cc = ((OrGuiContainer) c).getComponent(title);
                if (cc != null)
                    return cc;
            } else if (c instanceof OrGuiComponent) {
                OrGuiComponent gc = (OrGuiComponent) c;
                if (title.equals(gc.getVarName()))
                    return gc;
            }
        }
        return null;
    }

    @Override
    public String getVarName() {
        return varName;
    }

    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        final int height = getHeight();
        final int wigth = getWidth();
        boolean paintGradient = true;

        // если для компонента НЕ установлена градиентная заливка
        if (!isEnableGradient || !isFoundGradient) {
            if (backgroundPict != null) {
                paintGradient = false;
            } else {
                return;
            }
        }

        // защита он неверных параметров
        if (startColor == null && endColor == null) {
            startColor = Color.WHITE;
            endColor = Utils.getLightGraySysColor();
        } else if (startColor == null) {
            setBackground(endColor);
            if (backgroundPict != null) {
                paintGradient = false;
            } else {
                return;
            }
        } else if (endColor == null) {
            setBackground(startColor);
            if (backgroundPict != null) {
                paintGradient = false;
            } else {
                return;
            }
        }

        if (paintGradient) {
            // расчёт переменных для градиента
            // позиция по горизонтали начального цвета
            final int startH = (int) (wigth / 100f * positionStartColor);
            // позиция по горизонтали конечного цвета
            final int endH = (int) (wigth / 100f * positionEndColor);
            // позиция по вертикали начального цвета
            final int startV = (int) (height / 100f * positionStartColor);
            // позиция по вертикали конечного цвета
            final int endV = (int) (height / 100f * positionEndColor);
            // градиент
            GradientPaint gp;
            // задание градиентной заливки, в зависимости от его ориентации
            switch (orientation) {
            case Constants.HORIZONTAL:
                gp = new GradientPaint(startH, 0, startColor, endH, 0, endColor, isCycle);
                break;
            case Constants.VERTICAL:
                gp = new GradientPaint(0, startV, startColor, 0, endV, endColor, isCycle);
                break;
            case Constants.DIAGONAL:
                gp = new GradientPaint(startH, height - startV, startColor, endH, height - endV, endColor, isCycle);
                break;
            case Constants.DIAGONAL2:
                gp = new GradientPaint(startH, startV, startColor, endH, endV, endColor, isCycle);
                break;
            default:
                gp = new GradientPaint(startH, 0, startColor, endH, 0, endColor, isCycle);
            }

            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        if (isBackgroundPict && backgroundPict == null) {
            // очистка
            g.drawImage(null, 0, 0, null);
            isBackgroundPict = false;
        }
        if (backgroundPict != null) {
            if (mode == Mode.DESIGN) {
                // очистка
                g.drawImage(null, 0, 0, null);
            }
            isBackgroundPict = true;
            if (autoResizePict) {
                ImageIcon img = null;
                img = Utils.setSize(backgroundPict, wigth, height);
                g.drawImage(img.getImage(), 0, 0, null);
                
            } else {
                int x = 0;
                int y = 0;
                int heightIcon = backgroundPict.getIconHeight();
                int widthIcon = backgroundPict.getIconWidth();
                switch (positionPict) {
                default:
                case GridBagConstraints.CENTER: // центр
                    x = wigth / 2 - widthIcon / 2;
                    y = height / 2 - heightIcon / 2;
                    break;
                case GridBagConstraints.WEST: // Слева
                    x = 0;
                    y = height / 2 - heightIcon / 2;
                    break;
                case GridBagConstraints.EAST: // Справа
                    x = wigth - widthIcon;
                    y = height / 2 - heightIcon / 2;
                    break;
                case GridBagConstraints.NORTH: // Сверху
                    x = wigth / 2 - widthIcon / 2;
                    y = 0;
                    break;
                case GridBagConstraints.SOUTH: // Снизу
                    x = wigth / 2 - widthIcon / 2;
                    y = height - heightIcon;
                    break;
                case GridBagConstraints.NORTHWEST: // Сверху слева
                    x = 0;
                    y = 0;
                    break;
                case GridBagConstraints.NORTHEAST: // Сверху справа
                    x = wigth - widthIcon;
                    y = 0;
                    break;
                case GridBagConstraints.SOUTHWEST: // Снизу слева
                    x = 0;
                    y = height - heightIcon;
                    break;
                case GridBagConstraints.SOUTHEAST: // Снизу справа
                    x = wigth - widthIcon;
                    y = height - heightIcon;
                    break;
                }
                g.drawImage(backgroundPict.getImage(), x, y, null);
            }
        }

    }

    /**
     * Установить gradient.
     * 
     * @param gradient
     *            the new gradient
     */
    public void setGradient(GradientColor gradient) {
        startColor = gradient.getStartColor();
        endColor = gradient.getEndColor();
        orientation = gradient.getOrientation();
        isCycle = gradient.isCycle();
        positionStartColor = gradient.getPositionStartColor();
        positionEndColor = gradient.getPositionEndColor();
        isEnableGradient = gradient.isEnabled();
        repaint();
    }

    /**
     * Перерисовать компонент и всех его потомков
     */
    private void repaintAll() {
        repaint();
        if (isFoundGradient || backgroundPict != null) {
            Component[] comps = getComponents();
            for (Component comp : comps) {
                comp.repaint();
            }
        }
    }

    void updateToolTip() {
        if (toolTipExpr != null && !toolTipExpr.isEmpty()) {
            String toolTipExprText_ = kz.tamur.comps.Utils.getExpReturn(toolTipExpr, frame, getAdapter());
            if (toolTipExprText_ != null && !toolTipExprText_.equals(toolTipExprText)) {
                if (toolTipExprText_.isEmpty()) {
                    toolTipExprText_ = null;
                }
                setToolTipText(toolTipExprText_);
                toolTipExprText = toolTipExprText_;
            }
        }
    }

    /**
     * Устанавливает иконку панели
     * 
     * @param icon
     */

    private void setIcon(byte[] imageValue) {
        icon = Utils.processCreateImage(imageValue);
        StringBuilder name = new StringBuilder();
        name.append("icoPanel");
        kz.tamur.rt.Utils.getHash(imageValue, name);
        name.append(".").append(kz.tamur.rt.Utils.getSignature(imageValue));
        webNameIcon = name.toString();
    }
    
    
    
    /**
     * Возвращает иконку панели
     * 
     * @return
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * @return the backgroundPict
     */
    public ImageIcon getBackgroundPict() {
        return backgroundPict;
    }

    /**
     * @param backgroundPict
     *            the backgroundPict to set
     */
    public void setBackgroundPict(byte[] imageValue) {
        backgroundPict = Utils.processCreateImage(imageValue);
        StringBuilder name = new StringBuilder();
        name.append("bgPanel");
        kz.tamur.rt.Utils.getHash(imageValue, name);
        name.append(".").append(kz.tamur.rt.Utils.getSignature(imageValue));
        webNameBg = name.toString();
    }

    /**
     * @return the positionPict
     */
    public int getPositionPict() {
        return positionPict;
    }

    /**
     * @param positionPict
     *            the positionPict to set
     */
    public void setPositionPict(int positionPict) {
        this.positionPict = positionPict;
    }

    /**
     * @return the autoResizePict
     */
    public boolean isAutoResizePict() {
        return autoResizePict;
    }

    /**
     * @param autoResizePict
     *            the autoResizePict to set
     */
    public void setAutoResizePict(boolean autoResizePict) {
        this.autoResizePict = autoResizePict;
    }
    
    @Override
    public String getUUID() {
        return UUID;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public void setComponentChange(OrGuiComponent comp) {
        listListeners.add(comp);
    }
    
    @Override
    public void setListListeners(java.util.List<OrGuiComponent> listListeners,  java.util.List<OrGuiComponent> listForDel) {
        for (OrGuiComponent orGuiComponent : listForDel) {
            this.listListeners.remove(orGuiComponent);
        }
        for (int i = 0; i < listListeners.size(); i++) {
            this.listListeners.add(i, listListeners.get(i));
        }
        Component[] comps = getComponents();
        for (Component c : comps) {
            if (c instanceof OrGuiComponent) {
                ((OrGuiComponent) c).setListListeners(listListeners, listForDel);
            }
        }
    }  
    
    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }
    
    public String getWebNameIcon() {
        return webNameIcon;
    }

    /**
     * @param webNameIcon the webNameIcon to set
     */
    public void setWebNameIcon(String webNameIcon) {
        this.webNameIcon = webNameIcon;
    }
    
    public String getWebNameBg() {
        return webNameBg;
    }

    /**
     * @param webNameIcon the webNameIcon to set
     */
    public void setWebNameBg(String webNameBg) {
        this.webNameBg = webNameBg;
    }

    /**
     * @return the alignmentText
     */
    public String getTitleAlign() {
        return titleAlign;
    }
    
    @Override
    public String getToolTip() {
        return null;
    }

    @Override
    public void updateDynProp() {
    }

    @Override
    public int getPositionOnTopPan() {
        return -1;
    }

    @Override
    public boolean isShowOnTopPan() {
        return false;
    }

    @Override
    public void setAttention(boolean attention) {
    }

    /**
     * @return the isDelete
     */
    public boolean isDelete() {
        return isDelete;
    }

    /**
     * @param isDelete the isDelete to set
     */
    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }
}
