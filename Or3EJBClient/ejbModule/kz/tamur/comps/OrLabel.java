package kz.tamur.comps;

import static kz.tamur.comps.Mode.RUNTIME;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

import kz.tamur.comps.models.LabelPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.ComponentAdapter;
import kz.tamur.rt.adapters.LabelAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

/**
 * The Class OrLabel.
 * 
 */
public class OrLabel extends JLabel implements OrGuiComponent {

    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    public static PropertyNode PROPS = new LabelPropertyRoot();

    protected int mode;
    protected Element xml;
    protected boolean isSelected;
    private OrGuiContainer guiParent;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder = BorderFactory.createLineBorder(Utils.getMidSysColor());
    private OrFrame frame;
    private GridBagConstraints constraints;
    private Dimension prefSize;
    private Dimension maxSize;
    private Dimension minSize;
    private String webNameIcon = null;
    private String varName;
    private LabelAdapter adapter;

    /**
     * Конструктор класса or label.
     * 
     * @param xml
     *            xml.
     * @param mode
     *            mode.
     * @param frame
     *            frame.
     * @throws KrnException
     *             the krn exception
     */
    OrLabel(Element xml, int mode, OrFrame frame, boolean isEditor) throws KrnException {
        super("OrLabel");
        this.mode = mode;
        this.xml = xml;
        this.frame = frame;
        if (xml.getChildren().size() == 0) {
            this.setPropertyValue(new PropertyValue(5, (PROPS.getChild("pos").getChild("insets").getChild("rightInsets"))));
        }
        UUID = PropertyHelper.getUUID(this, isEditor);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        if (mode == Mode.RUNTIME) {
            adapter = new LabelAdapter(frame, this);
            setFocusable(false);
        } else {
            setFocusable(true);
        }
        constraints = PropertyHelper.getConstraints(PROPS, xml);
        prefSize = PropertyHelper.getPreferredSize(this);
        maxSize = PropertyHelper.getMaximumSize(this);
        minSize = PropertyHelper.getMinimumSize(this);
        updateText();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (mode == Mode.DESIGN && isSelected) {
            Utils.drawRects(this, g);
        }
    }

    @Override
    public Element getXml() {
        return xml;
    }

    @Override
    public PropertyNode getProperties() {
        return PROPS;
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

    @Override
    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    @Override
    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        kz.tamur.comps.Utils.processStdCompProperties(this, value);
        PropertyNode prop = value.getProperty();
        if ("title".equals(prop.getName())) {
            setText((String) value.resourceStringValue().second);
        } else if ("fontG".equals(prop.getName())) {
            setFont(value.fontValue());
        } else if ("fontGExpr".equals(prop.getName())) {
        	Object res=evalExpr(value);
            if (res!=null && res instanceof Font) {
                setFont((Font)res);
            }
        } else if ("fontColor".equals(prop.getName())) {
            Color val = value.isNull() ? Color.BLACK : value.colorValue();
            setForeground(val);
        } else if ("fontExpr".equals(prop.getName())) {
        	Object res=evalExpr(value);
            if (res instanceof Number) {
                setForeground(new Color(((Number)res).intValue()));
            } else if (res instanceof String) {
            	setForeground(Utils.getColorByName(res.toString()));
            }
        } else if ("image".equals(prop.getName())) {
            setIcon(Utils.processCreateImage(value.getImageValue()));
        } else if ("alignmentText".equals(prop.getName())) {
            setHorizontalAlignment(value.intValue());
        }
    }

    @Override
    public int getComponentStatus() {
        return Constants.STANDART_COMP;
    }

    @Override
    public void setLangId(long langId) {
        updateText();
    }

	private void updateText() {
		PropertyValue pv = null;
		pv = getPropertyValue(getProperties().getChild("title"));
		if (!pv.isNull()) {
			setText((String) ((Pair) pv.resourceStringValue()).second);
		}
		pv = getPropertyValue(PROPS.getChild("varName"));
		if (!pv.isNull()) {
			varName = pv.stringValue();
		}
		PropertyNode pn = getProperties().getChild("view");
		pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
		if (!pv.isNull()) {
			setFont(pv.fontValue());
		}
		pv = getPropertyValue(pn.getChild("image"));
		if (!pv.isNull()) {
			setIcon(Utils.processCreateImage(pv.getImageValue()));
			byte[] b = pv.getImageValue();
			if (b != null && b.length > 0) {
				StringBuilder name = new StringBuilder();
				name.append("imc");
				kz.tamur.rt.Utils.getHash(b, name);
				name.append(".").append(kz.tamur.rt.Utils.getSignature(b));
				webNameIcon = name.toString();
			}
		}
		pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
		if (!pv.isNull()) {
			setForeground(pv.colorValue());
		}
        if (mode == Mode.RUNTIME) {
    		pv = getPropertyValue(pn.getChild("font").getChild("fontGExpr"));
    		if (!pv.isNull()) {
    			Object res = evalExpr(pv);
    			if (res != null && res instanceof Font) {
    				setFont((Font) res);
    			}
    		}

			pv = getPropertyValue(pn.getChild("font").getChild("fontExpr"));
			if (!pv.isNull()) {
				Object res = evalExpr(pv);
				if (res instanceof Number) {
					setForeground(new Color(((Number) res).intValue()));
				} else if (res instanceof String) {
					setForeground(Utils.getColorByName(res.toString()));
				}
			}
        }
		pv = getPropertyValue(pn.getChild("alignmentText"));
		if (!pv.isNull()) {
			setHorizontalAlignment(pv.intValue());
		}
	}

	private Object evalExpr(PropertyValue value) {
		String expr = "";
		Object res = null;
		if (value.objectValue() instanceof Expression) {
			expr = ((Expression) value.objectValue()).text;
		} else if (value.objectValue() instanceof String) {
			expr = (String) value.objectValue();
		}
		if (!"".equals(expr)) {
			try {
				res = kz.tamur.comps.Utils.evalExp(expr, frame, getAdapter());
			} catch (Exception e) {
				System.out.println("Ошибка в формуле\r\n" + expr + "\r\n" + e);
			}
		}
		return res;
	}
	
	@Override
    public int getMode() {
        return mode;
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

    @Override
    public Dimension getPrefSize() {
        return mode == RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this);
    }

    @Override
    public Dimension getMaxSize() {
        return mode == RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this);
    }

    @Override
    public Dimension getMinSize() {
        return mode == RUNTIME ? minSize : PropertyHelper.getMinimumSize(this);
    }

    @Override
    public GridBagConstraints getConstraints() {
        return mode == RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml);
    }

    //
    /**
     * Получить tab index.
     * 
     * @return tab index.
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

    @Override
    public byte[] getDescription() {
        return new byte[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ComponentAdapter getAdapter() {
        return adapter;
    }

    @Override
    public String getVarName() {
        return varName;
    }

    /**
     * Установить var name.
     * 
     * @param varName
     *            новое значение var name.
     */
    public void setVarName(String varName) {
        this.varName = varName;
    }

    @Override
    public void setText(String text) {
        super.setText(Utils.castToHTML(text, this));
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
    public void setListListeners(java.util.List<OrGuiComponent> listListeners, java.util.List<OrGuiComponent> listForDel) {
        for (OrGuiComponent orGuiComponent : listForDel) {
            this.listListeners.remove(orGuiComponent);
        }
        for (int i = 0; i < listListeners.size(); i++) {
            this.listListeners.add(i, listListeners.get(i));
        }
    }

    @Override
    public List<OrGuiComponent> getListListeners() {
        return listListeners;
    }

    /**
     * Получить web name icon.
     * 
     * @return web name icon.
     */
    public String getWebNameIcon() {
        return webNameIcon;
    }

    /**
     * Установить web name icon.
     * 
     * @param webNameIcon
     *            the webNameIcon to set
     */
    public void setWebNameIcon(String webNameIcon) {
        this.webNameIcon = webNameIcon;
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
}
