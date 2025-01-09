package kz.tamur.comps;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.models.HyperTreePropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.ComponentAdapter;

import org.jdom.Element;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 22.04.2004
 * Time: 16:16:31
 * To change this template use File | Settings | File Templates.
 */
public class OrHyperTree extends JPanel implements OrGuiComponent {
    
    private java.util.List<OrGuiComponent> listListeners = new ArrayList<OrGuiComponent>();
    protected String UUID;
    private static PropertyNode PROPS = new HyperTreePropertyRoot();

    private kz.tamur.comps.OrHiperTree htree;
    private Element xml;
    private boolean isSelected;
    private int mode;
    private OrFrame frame;
    private OrGuiContainer guiParent;
    private boolean isCopy;
    private Border standartBorder;
    private Border copyBorder =
            BorderFactory.createLineBorder(kz.tamur.rt.Utils.getMidSysColor());

    public OrHyperTree(Element xml, int mode, OrFrame frame) {
        this.xml = xml;
        this.mode = mode;
        this.frame = frame;
        UUID = PropertyHelper.getUUID(this);
        setFocusable(true);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setLayout(new BorderLayout());
        //PropertyNode prop = getProperties().getChild("hipertree");
        //PropertyValue pv = getPropertyValue(prop);
        //initHiperTree(pv);
    }



    public void paint(Graphics g) {
        super.paint(g);
        if (mode == Mode.DESIGN && isSelected) {
            kz.tamur.rt.Utils.drawRects(this, g);
        }
    }

    public Element getXml() {
        return xml;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public GridBagConstraints getConstraints() {
        return PropertyHelper.getConstraints(PROPS, xml);
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

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, xml, frame);
    }

    public void setPropertyValue(PropertyValue value) {
        PropertyHelper.setPropertyValue(value, xml, frame);
        Utils.processStdCompProperties(this, value);
        if (value.getProperty().getName().equals("hipertree")) {
            initHiperTree(value);
        }
    }

    public OrHiperTree getHiperTree() {
        return htree;
    }

    private void initHiperTree(PropertyValue pv) {
        try {
            KrnObject obj = null;
            if (!pv.isNull()) {
                long id = Long.parseLong(pv.getKrnObjectId());
                if (htree == null || htree.getKrnObject().id != id) {
                    htree = HiperTreeHack.getHiperTree(id);
                    if (htree == null) {
                        final Kernel krn = Kernel.instance();
                        KrnClass cls = krn.getClassByName(pv.getKrnClassName());
                        obj = new KrnObject(id, "", cls.id);
                        Map map = Collections.EMPTY_MAP;
                        htree = new OrHiperTree(null, obj, null, "", map, map, map, mode, map, null);
                        HiperTreeHack.setHiperTree(htree);
                    }
                    add(htree, BorderLayout.CENTER);
                    validate();
                    repaint();
                }
            } else if (htree != null) {
                remove(htree);
                htree = null;
                validate();
                repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getComponentStatus() {
        return Constants.HYPER_COMP;
    }

    public void setLangId(long langId) {
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
        return null;
    }

    public Dimension getMaxSize() {
        return null;
    }

    public Dimension getMinSize() {
        return null;
    }

    public String getBorderTitleUID() {
        return null;
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

    public byte[] getDescription() {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ComponentAdapter getAdapter() {
        return null;
    }

    public String getVarName() {
        return null;
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
}
