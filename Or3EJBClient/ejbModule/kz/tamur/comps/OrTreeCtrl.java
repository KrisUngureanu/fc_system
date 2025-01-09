package kz.tamur.comps;

import static kz.tamur.comps.Utils.getExpReturn;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.util.CursorToolkit;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreePropertyRoot;
import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.NodeFinder;
import kz.tamur.guidesigner.SearchInterfacePanel;
import kz.tamur.guidesigner.StringPattern;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.adapters.OrRef;
import kz.tamur.rt.adapters.TreeAdapter;
import kz.tamur.rt.adapters.TreeAdapter.Node;
import kz.tamur.rt.data.Cache;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import java.awt.*;
//import com.cifs.or2.util.MapMap;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 28.03.2004
 * Time: 15:52:58
 * To change this template use File | Settings | File Templates.
 */
public class OrTreeCtrl extends OrTree implements OrGuiComponent {

    public static PropertyNode PROPS = new TreePropertyRoot();
    private String searchString;
    protected NodeFinder finder = new NodeFinder();

    private int mode;
    private boolean isSelected;
    protected Cache cash;
    protected static final Kernel krn_ = Kernel.instance();
    protected KrnAttribute valueAttr;
    protected KrnAttribute childrenAttr;
    protected KrnAttribute[] titleAttrs;
    public OrRef rootRef;
    private OrGuiContainer guiParent;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private boolean transparent = false;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;
    private String toolTipContent = null;
    private boolean multiSelection = false;
    
    OrTreeCtrl(Element xml, int mode, OrFrame frame) {
        super(xml, mode, frame);
        this.xml = xml;
        this.mode = mode;
        setRootVisible(false);
        setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        if (mode == Mode.RUNTIME) {
            kz.tamur.rt.Utils.setComponentTabFocusCircle(this);
            addFocusListener(new DefaultFocusAdapter(this));

            MouseAdapter ma = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (e.getClickCount() == 2) {
                        Container cnt = getTopLevelAncestor();
                        if (cnt instanceof DesignerDialog && getAdapter().getSelectedNode().isLeaf()) {
                            boolean b = getAdapter().isOnlyChildren();
                            if (b) {
                                ((DesignerDialog) cnt).processOkClicked();
                            }
                        }
                    }
                }
            };
            addMouseListener(ma);
            KeyAdapter ka = new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    final Window cnt = (Window) getTopLevelAncestor();
                    if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                        find(cnt);
                    } else if (e.getKeyCode() == KeyEvent.VK_F3 && e.isShiftDown()) {
                        Thread t = new Thread(new Runnable() {
                            public void run() {
                                TreeNode fnode = finder.findPrev();
                                if (fnode != null) {
                                    TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                                    if (path != null) {
                                        setSelectionPath(path);
                                        scrollPathToVisible(path);
                                    }
                                } else {
                                    MessagesFactory.showMessageSearchFinished(cnt);
                                }
                            }
                        });
                        t.start();
                    } else if (e.getKeyCode() == KeyEvent.VK_F3) {
                        Thread t = new Thread(new Runnable() {
                            public void run() {
                                TreeNode fnode = finder.findNext();
                                if (fnode != null) {
                                    TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                                    if (path != null) {
                                        setSelectionPath(path);
                                        scrollPathToVisible(path);
                                    }
                                } else {
                                    MessagesFactory.showMessageSearchFinished(cnt);
                                }
                            }
                        });
                        t.start();

                    }
                }
            };
            addKeyListener(ka);
        } else if(this.mode == Mode.DESIGN) {
        	PropertyValue pv = getPropertyValue(PROPS.getChild("toolTip"));
            if (!pv.isNull()) {
            	if (pv.objectValue() instanceof Expression) {
            		try {
                        toolTipExpr = ((Expression) pv.objectValue()).text;
                        toolTipExprText = getExpReturn(toolTipExpr, frame, getAdapter());
                        if (toolTipExprText != null && !toolTipExprText.isEmpty()) {
                            setToolTipText(toolTipExprText);
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка в формуле\r\n" + toolTipExpr + "\r\n" + e);
                    }
            	} else {
	                toolTipUid = (String) pv.resourceStringValue().first;
	                byte[] toolTip = frame.getBytes(toolTipUid);
	                if (toolTip != null && toolTip.length > 0) {
	                    setToolTipText(new String(toolTip));
		                SAXBuilder builder = new SAXBuilder();
		                InputStream is = new ByteArrayInputStream(toolTip);
		                try {
							Element var_doc = builder.build(is).getRootElement();
							if(var_doc.getName().equals("html")) {
								XMLOutputter outp = new XMLOutputter();
		
							    outp.setFormat(Format.getCompactFormat());
							    StringWriter sw = new StringWriter();
							    outp.output(var_doc.getChild("body").getContent(), sw);
							    StringBuffer sb = sw.getBuffer();
							    toolTipContent = sb.toString();
								toolTipExprText = var_doc.getChild("body").getValue();
							}
						} catch (JDOMException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
	                }
            	}
            }
            setEnabled(false);
        }

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
        PropertyNode pn = PROPS.getChild("extended");
        // прозрачность компонента(да/нет)
        PropertyValue pv = getPropertyValue(pn.getChild("transparent"));
        transparent = pv.booleanValue();
        setTransparent(!transparent);
        
        multiSelection = getPropertyValue(PROPS.getChild("pov").getChild("multiselection")).booleanValue();
     
        // Формула для вычисления потомков
       /* pn = PROPS.getChild("ref").getChild("childrenExpr");
        if (pn != null) {
            pv = getPropertyValue(pn);
            if (!pv.isNull()) {
                String str = pv.stringValue();
                getAdapter().getRef().setChildrenExpr(str);
            }
        }*/
    }

    @Override
    public void expandPath(TreePath path) {
        Container cnt = getTopLevelAncestor();
        if (cnt != null) {
            cnt.setCursor(Constants.WAIT_CURSOR);
        }
        super.expandPath(path);
        if (cnt != null) {
            cnt.setCursor(Constants.DEFAULT_CURSOR);
        }
    }

    public void find(final Window parent) {
        final SearchInterfacePanel sip = new SearchInterfacePanel();
        sip.setSearchMethod(ComparisonOperations.CO_CONTAINS);
        if (searchString != null)
            sip.setSearchText(searchString);
        DesignerDialog dlg = new DesignerDialog(parent, "Поиск элемента", sip);
        dlg.show();
        if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    CursorToolkit.startWaitCursor(parent);
                    searchString = sip.getSearchText();
                    Node node = (Node) getAdapter().getRoot();
                    TreeNode fnode = finder.findFirst(node, new StringPattern(searchString, sip.getSearchMethod()));
                    CursorToolkit.stopWaitCursor(parent);
                    if (fnode != null) {
                        TreePath path = new TreePath(((DefaultMutableTreeNode) fnode).getPath());
                        if (path != null) {
                            setSelectionPath(path);
                            scrollPathToVisible(path);
                        }
                    } else {
                        MessagesFactory.showMessageNotFound(parent);
                    }

                }
            });
            t.start();
        }
    }

    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
        }
    }

    public Element getXml() {
        return xml;
    }

    public GridBagConstraints getConstraints() {
        return PropertyHelper.getConstraints(PROPS, xml);
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        repaint();
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        String name = value.getProperty().getName();
        if ("transparent".equals(name)) {
            PropertyNode pn = getProperties().getChild("extended");
            // прозрачность компонента(да/нет)
            PropertyValue pv = getPropertyValue(pn.getChild("transparent"));
            transparent = pv.booleanValue();
            setTransparent(!transparent);
        } else if ("toolTip".equals(name)) {
            if (value.isNull()) {
                toolTipUid = null;
                setToolTipText("");
                toolTipContent = null;
                toolTipExprText = null;
            } else {
                toolTipUid = (String) value.resourceStringValue().first;
                byte[] toolTip = frame.getBytes(toolTipUid);
                if (toolTip != null && toolTip.length > 0) {
                    setToolTipText(new String(toolTip));
                    SAXBuilder builder = new SAXBuilder();
                    InputStream is = new ByteArrayInputStream(toolTip);
                    try {
                        Element var_doc = builder.build(is).getRootElement();
                        if (var_doc.getName().equals("html")) {
                            XMLOutputter outp = new XMLOutputter();
                            outp.setFormat(Format.getPrettyFormat());
                            StringWriter sw = new StringWriter();
                            outp.output(var_doc.getChild("body").getContent(), sw);
                            StringBuffer sb = sw.getBuffer();
                            toolTipContent = sb.toString();
                            toolTipExprText = var_doc.getChild("body").getValue();
                        }
                    } catch (JDOMException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if ("multiselection".equals(name)) {
            multiSelection = value.booleanValue();
        }
        updateProperties();
    }

    protected void updateProperties() {
    	super.updateProperties();
    }

    public int getComponentStatus() {
        return Constants.TREES_COMP;
    }

    public void setLangId(long langId) {
        super.setLangId(langId);
        TreeNode root = (TreeNode) getModel().getRoot();
        if (root.getChildCount() > 0) {
            TreePath path = getSelectionPath();
            TreeAdapter.Node sn = (path != null) ? (TreeAdapter.Node) path.getLastPathComponent() : null;
            TreeAdapter.Node n = (TreeAdapter.Node) root.getChildAt(0);
            n.reset();
            if (sn != null) {
                path = n.find(sn.getObject(), false);
                setSelectionPath(path);
            }
        }
        setTransparent(!transparent);
        Utils.processBorderProperties(this, frame);
    }

    public int getMode() {
        return mode;
    }

    public OrGuiContainer getGuiParent() {
        return guiParent;
    }

    public void setGuiParent(OrGuiContainer guiParent) {
        this.guiParent = guiParent;
    }

    public boolean isCopy() {
        return isCopy;
    }

    public void setCopy(boolean copy) {
        isCopy = copy;
        if (isCopy) {
            standartBorder = getBorder();
            setBorder(copyBorder);
        } else {
            setBorder(standartBorder);
        }
    }
    /*public String getToolTipText() {
    	return toolTipExprText;
    }*/
    public String getToolTip() {
    	return (toolTipContent != null && toolTipContent.trim().length() > 0) ? toolTipContent : toolTipExprText;
    }
    
    /**
     * Компонент поддерживает мультивыбор значений?
     * @return <code>true</code> или <code>false</code>
     */
    public boolean isMultiSelection() {
        return multiSelection;
    }
}
