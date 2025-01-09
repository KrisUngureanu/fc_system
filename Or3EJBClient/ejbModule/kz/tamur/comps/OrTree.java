package kz.tamur.comps;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;

import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreePropertyRoot;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.TreeAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**                           
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 28.03.2004
 * Time: 15:52:58
 * To change this template use File | Settings | File Templates.
 */
public class OrTree extends JTree implements OrGuiComponent {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new TreePropertyRoot();

    private int mode;
    protected Element xml;
    private boolean isSelected;
    protected OrFrame frame;
    protected Cache cash;
    protected static final Kernel krn_ = Kernel.instance();
    protected KrnAttribute valueAttr;
    protected KrnAttribute childrenAttr;
    protected KrnAttribute[] titleAttrs;
    private OrGuiContainer guiParent;
    /** идентификатор строки с подсказкой. */
    private String toolTipUid;
    /** Формула для вывода вспл. подсказки */
    private String toolTipExpr = null;
    /** Текст вспл. подсказки, сформированной по формуле */
    private String toolTipExprText = null;

    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private byte[] description;
    private JButton treeFieldButton = null;

    private TreeAdapter adapter;
    private String descriptionUID;

    private String varName;
    boolean firstOpen = true;
    private boolean transparent = false;
    private Font font = null;
    private int viewType = Constants.FILES; 
    private int sortType=0;
    private boolean isFolderAsLeaf = false;
    private boolean isFolderSelect = true;
    protected boolean showSearchLine = false;

    public OrTree() {
        PropertyValue pv;
        
        if (mode == Mode.RUNTIME) {
            pv = getPropertyValue(getProperties().getChild("extended").getChild("transparent"));
            transparent = pv.booleanValue();
            
            pv = getPropertyValue(getProperties().getChild("view").getChild("font").getChild("fontG"));
            if (pv !=null) {
                font = pv.fontValue();
            }
            pv = getPropertyValue(getProperties().getChild("view").getChild("folderAsLeaf"));
            if (pv.isNull()) {
            	isFolderAsLeaf = ((Boolean) getProperties().getChild("view").getChild("folderAsLeaf").getDefaultValue()).booleanValue();
            } else {
            	isFolderAsLeaf = pv.booleanValue();
            }
        }   
        setCellRenderer(new OrTreeCellRenderer(false,transparent,isFolderAsLeaf,font));
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        updateProperties();
    }
    
    protected OrTree(Element xml, int mode, OrFrame frame) {
        this(xml, mode, frame, false);
    }
    
    protected OrTree(Element xml, int mode, OrFrame frame,boolean isTransparent) {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        PropertyValue pv;
        if (mode == Mode.RUNTIME) {
            transparent = isTransparent ? true : getPropertyValue(PROPS.getChild("extended").getChild("transparent")).booleanValue();
            pv = getPropertyValue(PROPS.getChild("view").getChild("font").getChild("fontG"));
            if (pv != null) {
                font = pv.fontValue();
            }
            pv = getPropertyValue(getProperties().getChild("view").getChild("folderAsLeaf"));
            if (pv.isNull()) {
            	isFolderAsLeaf = ((Boolean) getProperties().getChild("view").getChild("folderAsLeaf").getDefaultValue()).booleanValue();
            } else {
            	isFolderAsLeaf = pv.booleanValue();
            }
        }  
        //description = PropertyHelper.getDescription(this);
        pv = getPropertyValue(PROPS.getChild("description"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            descriptionUID = (String)p.first;
            description = (byte[])p.second;
        }
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue();
        }
        setCellRenderer(new OrTreeCellRenderer(false,transparent,isFolderAsLeaf,font));
        
        if (mode == Mode.RUNTIME) {
            // всплывающая подсказка
               pv = getPropertyValue(PROPS.getChild("toolTip"));
               if (!pv.isNull()) {
                   if (pv.objectValue() instanceof Expression) {
                       try {
                           toolTipExpr = ((Expression) pv.objectValue()).text;
                           toolTipExprText = Utils.getExpReturn(toolTipExpr, frame, getAdapter());
                           if (toolTipExprText != null && !toolTipExprText.isEmpty()) {
                               setToolTipText(toolTipExprText);
                           }
                       } catch (Exception e) {
                           System.out.println("Ошибка в формуле\r\n" + toolTipExpr + "\r\n" + e);
                       }
                   } else {
                       toolTipUid = (String) pv.resourceStringValue().first;
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
           }
        PropertyNode pn = getProperties().getChild("extended");
        // прозрачность компонента(да/нет)
         pv = getPropertyValue(pn.getChild("transparent"));
        setTransparent(!pv.booleanValue());
        //setBackground(Utils.getLightSysColor());
        OrTreeCellRenderer rend = (OrTreeCellRenderer)getCellRenderer();
        //rend.setBackground(Utils.getLightSysColor());
        pv = getPropertyValue(PROPS.getChild("view").getChild("viewType"));
        if (!pv.isNull()) {
            viewType = pv.intValue();
        } else {
            viewType = ((EnumValue)PROPS.getChild("view").getChild("viewType").getDefaultValue()).code;
        }
        pv = getPropertyValue(PROPS.getChild("ref").getChild("sortPath"));
        if (!pv.isNull()) {
            sortType = 1;
        }
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
        updateProperties();
    }

    @Override
	public void expandPath(TreePath path) {
		super.expandPath(path);
		// при первом открытии дерева выделяет первый(корневой) элемент
		if (firstOpen) {
			setSelectionPath(getPathForRow(0));
			firstOpen = false;
		}
	}
    
    public void setTreeTableRenderer() {
        setCellRenderer(new OrTreeCellRenderer(true,transparent,isFolderAsLeaf,font));
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

    public PropertyNode getProperties() {
        return PROPS;
    }

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
       /* final String name = value.getProperty().getName();
        if ("transparent".equals(name)) {
            PropertyNode pn = getProperties().getChild("extended");
            // прозрачность компонента(да/нет)
            PropertyValue pv = getPropertyValue(pn.getChild("transparent"));
            setTransparent(!pv.booleanValue());
        }*/
        updateProperties();
    }

    protected void updateProperties() {
        PropertyNode pn = getProperties().getChild("extended");
        // прозрачность компонента(да/нет)
        PropertyValue pv = getPropertyValue(pn.getChild("transparent"));
        setTransparent(!pv.booleanValue());
        
        pv = getPropertyValue(getProperties().getChild("view").getChild("showSearchLine"));
        if (pv.isNull()) {
        	showSearchLine = ((Boolean) getProperties().getChild("view").getChild("showSearchLine").getDefaultValue()).booleanValue();
        } else {
        	showSearchLine = pv.booleanValue();
        }

        pv = getPropertyValue(getProperties().getChild("view").getChild("folderSelect"));
        if (!pv.isNull()) {
            isFolderSelect = pv.booleanValue();
        } else {
            isFolderSelect = ((Boolean)getProperties().getChild("view").getChild("folderSelect").getDefaultValue()).booleanValue();
        }

        Utils.processBorderProperties(this, frame);
    }

    public int getComponentStatus() {
        return Constants.TREES_COMP;
    }

    public void setLangId(long langId) {
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
                description = (byte[])p.second;
            }
        }
        if (adapter != null) adapter.setLangId(langId);
        if (!(this instanceof OrTreeTable.TreeTableCellRenderer)) updateProperties();
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

    public void setXml(Element xml) {
        this.xml = xml;
    }

    public Dimension getPrefSize() {
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
    }

    //
    public int getTabIndex() {
        return -1;
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

    public JButton getTreeFieldButton() {
        return treeFieldButton;
    }

    public void setTreeFieldButton(JButton treeFieldButton) {
        this.treeFieldButton = treeFieldButton;
    }

    public void setBackground(Color bg) {
        super.setBackground(bg);
    }

    public byte[] getDescription() {
        return description != null ? java.util.Arrays.copyOf(description, description.length) : null;
    }

    public TreeAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ComponentAdapter adapter) {
        this.adapter = (TreeAdapter)adapter;
    }

    public String getVarName() {
        return varName;
    }

    void setTransparent(boolean transparent) {
        setOpaque(transparent);
        repaint();
    }
    
    void updateToolTip() {
        if (toolTipExpr != null && !toolTipExpr.isEmpty()) {
            String toolTipExprText_ = Utils.getExpReturn(toolTipExpr, frame, getAdapter());
            if (toolTipExprText_ != null && !toolTipExprText_.equals(toolTipExprText)) {
                if (toolTipExprText_.isEmpty()) {
                    toolTipExprText_ = null;    
                }
                setToolTipText(toolTipExprText_);
                toolTipExprText = toolTipExprText_;
            }
        }
    }
    
    @Override
    public String getUUID() {
        return UUID;
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
    }
    
    /**
     * @return the viewType
     */
    public int getViewType() {
        return viewType;
    }  
    
    public int getSortType(){
    	return sortType;
    }

    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
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

	public boolean isShowSearchLine() {
		return showSearchLine;
	}

	public boolean isFolderSelect() {
		return isFolderSelect;
	}
}
