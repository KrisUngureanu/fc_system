package kz.tamur.guidesigner;

import static kz.tamur.guidesigner.MessagesFactory.ERROR_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.INFORMATION_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.QUESTION_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.showMessageDialog;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import kz.tamur.comps.Constants;
import kz.tamur.comps.EmptyPlace;
import kz.tamur.comps.Factories;
import kz.tamur.comps.FactoryListener;
import kz.tamur.comps.GuiComponentItem;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.OrPanel;
import kz.tamur.comps.OrTabbedPane;
import kz.tamur.comps.OrTable.JOrTable;
import kz.tamur.comps.OrTableColumn;
import kz.tamur.comps.OrTableFooter;
import kz.tamur.comps.OrTableModel;
import kz.tamur.comps.OrTableNavigator;
import kz.tamur.comps.Place;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.or3.client.props.inspector.PropertyInspector;
import kz.tamur.util.MapMap;
import kz.tamur.util.Pair;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.cifs.or2.kernel.KrnException;

public class Controller implements AWTEventListener, PropertyListener, FactoryListener {

    private EventListenerList listeners = new EventListenerList();
    private String currCompClass;
    private boolean isCompCreated = false;
    private Set<OrGuiComponent> selection = new HashSet<OrGuiComponent>();
    private PropertyInspector inspector;
    private DragGestureListener dgLnr;
    private DragSourceListener dsLnr;
    private DropTargetListener dtLnr;

    private JLabel compClassLab;

    private static DesignerFrame df;
    private OrGuiComponent copyInstance;

    public static Map<String, Pair<String, MapMap<Long, String, Object>>> appBuffer = new TreeMap<String, Pair<String, MapMap<Long, String, Object>>>();
    private Point sourcePoint = null;
    private Point targetPoint = null;

    public Controller(PropertyInspector inspector) {
        this.inspector = inspector;
        dndInit();
        Factories.instance().addFactoryListener(this);
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addKeyEventDispatcher( new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                Object component = keyEvent.getComponent();
                if (component instanceof OrGuiComponent || component instanceof OrGuiContainer || component instanceof JTableHeader || component instanceof JOrTable) {
                	if (keyEvent.getID() == KeyEvent.KEY_PRESSED && keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
                		Controller.this.deleteComponent();
                	}
                }
                return false;
            }
        });
    }

    public void setTopLevelAncestor(JPanel f) {
        if (f instanceof DesignerFrame) {
            df = (DesignerFrame)f;
        }
    }

    public void setClassLabel(JLabel l) {
        compClassLab = l;
    }

    public void setCurrCompCalss(String compClass) {
        this.currCompClass = compClass;
    }

    public OrGuiComponent getSelectedComponent() {
        return (selection.size() > 0) ? selection.iterator().next() : null;
    }

    public void clearSelection() {
        for (Iterator<OrGuiComponent> it = selection.iterator(); it.hasNext();) {
            OrGuiComponent gc = it.next();
            gc.setSelected(false);
        }
        selection.clear();
    }
    
    private boolean isEdenticalTypes(OrGuiComponent comp) {
    	for (Iterator<OrGuiComponent> it = selection.iterator(); it.hasNext();) {
            OrGuiComponent gc = it.next();
            if (!comp.getClass().equals(gc.getClass()) || comp == gc) {
            	return false;
			}
    	}
    	return true;
    }
    
    public void addSelection(OrGuiComponent comp, boolean isShiftDown) {
    	if (selection.size() > 0) {
    		if (isShiftDown) {
    			if (isEdenticalTypes(comp)) {
    		        selection.add(comp);
    		        inspector.addObject(new GuiComponentItem(comp, DesignerFrame.instance()));
                    selectComponent(comp);
    			}
    		} else {
    	        clearSelection();
    			selection.add(comp);
    	        inspector.setObject(new GuiComponentItem(comp, DesignerFrame.instance()));
                selectComponent(comp);
    	        updateProperties(comp);
    	        firePropertyModified(comp, PropertyListener.SELECT_EVENT);
    		}
    	} else {
    		selection.add(comp);
	        inspector.setObject(new GuiComponentItem(comp, DesignerFrame.instance()));
            selectComponent(comp);
	        updateProperties(comp);
	        firePropertyModified(comp, PropertyListener.SELECT_EVENT);
    	}
    }

    public void updateProperties(OrGuiComponent comp) {
        inspector.updateObject(new GuiComponentItem(comp, DesignerFrame.instance()));
    }
    
    public void createComponent(OrGuiContainer container, int x, int y) throws KrnException {
        if (currCompClass != null) {
            OrGuiComponent component = Factories.instance().create(currCompClass, ControlTabbedContent.instance().getSelectedFrame());
            component.setLangId(df.getInterfaceLang().id);
            container.addComponent(component, x, y);
            currCompClass = null;
            isCompCreated = true;
            addSelection(component, false);
            long interfaceID = DesignerFrame.getTabbedContent().getKrnObjectIfr().id;
            String componentID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
            InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(componentID, component);
            InterfaceActionsConteiner.getInterfaceActions(interfaceID).addComponent(componentID, component);
            firePropertyModified(container);
        }
    }
    
    public void createComponent(OrGuiContainer container, OrGuiComponent component) throws KrnException {       
        component.setLangId(df.getInterfaceLang().id);
        container.addComponent(component, 0, 0);
        currCompClass = null;
        isCompCreated = true;
        addSelection(component, false);
        long interfaceID = DesignerFrame.getTabbedContent().getKrnObjectIfr().id;
        String componentID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
        InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(componentID, component);
        InterfaceActionsConteiner.getInterfaceActions(interfaceID).addComponent(componentID, component);
        firePropertyModified(container);
    }
    
    public String copyToBuffer() {
        return copyToBuffer(selection);
    }

    public void cutComponent() {
        OrGuiComponent component = getSelectedComponent();
        OrGuiContainer container;
        if (component != null) {
        	if (component instanceof OrTableColumn) {
        		container = ((OrTableColumn) component).getOrTable();
        	} else {
                container = getContainer((Component) component, false);
        	}
            if (container != null) {
            	String componentID = copyToBuffer();
            	if (!(component instanceof OrTableColumn)) {
                    long interfaceID = DesignerFrame.getTabbedContent().getKrnObjectIfr().id;
	                PropertyValue titleValue = component.getPropertyValue(component.getProperties().getChild("title"));
		            titleValue = new PropertyValue(titleValue.getValue(), titleValue.getProperty());
	                String titleValueID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
		    		InterfaceActionsConteiner.getInterfaceActions(interfaceID).getTitleValues().put(titleValueID, titleValue);
			    	String containerID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
		            InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(containerID, container);
		            InterfaceActionsConteiner.getInterfaceActions(interfaceID).cutComponent(componentID, component, containerID, container, titleValueID);
            	}
	            container.removeComponent(component);
	            InterfaceFrame frame = ControlTabbedContent.instance().getSelectedFrame();
	            removeStrings(component.getXml(), frame);
	            addSelection(container, false);
	            firePropertyModified(container);
            }
        }
    }    

    private String copyToBuffer(Set<OrGuiComponent> components) {
        if (components == null || components.size() == 0) {
            showMessageDialog((JFrame) df.getTopLevelAncestor(), ERROR_MESSAGE, "Нет выделенного элемента!");
        } else {
            OrGuiComponent component = getSelectedComponent();
            if (!(component instanceof OrTableColumn)) {
                appBuffer.remove("copy");
                copyInstance = component;
                Element newXml = (Element) component.getXml().clone();
                // Удалить все UUID компонентов, для того чтобы в копии была новая генерация
                removeChild(newXml, "UUID");
                try {
                    StringWriter sw = new StringWriter();
                    XMLOutputter out = new XMLOutputter();
                    out.getFormat().setEncoding("UTF-8");
                    out.output(newXml, sw);
                    sw.close();
                    MapMap<Long, String, Object> strings = new MapMap<Long, String, Object>();
                    InterfaceFrame frame = ControlTabbedContent.instance().getSelectedFrame();
                    loadStringMap(newXml, strings, frame);
                    appBuffer.put("copy", new Pair<String, MapMap<Long, String, Object>>(sw.toString(), strings));
                    long interfaceID = DesignerFrame.getTabbedContent().getKrnObjectIfr().id;
                    String componentID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
                    InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(componentID, component);
                    return componentID;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showMessageDialog((JFrame) df.getTopLevelAncestor(), INFORMATION_MESSAGE, "Копирование столбцов запрещено!");
            }
        }
        return null;
    }

    
    /**
     * Рекурсивный перебор всего элемента и удаление нужного тега.
     *
     * @param newXml хмл для перебора
     * @param string удаляемый тег
     */
    private void removeChild(Element newXml, String string) {
        newXml.removeChild(string);
        List<Element> children = newXml.getChildren();
        for (Element child: children) {
            removeChild(child, string);
        }
    }

    private void loadStringMap(Element xml, MapMap<Long, String, Object> map, InterfaceFrame frame) {
        if ("Component".equals(xml.getName())) {
            String cls = xml.getAttributeValue("class");
            if ("Panel".equals(cls)
                    || "Label".equals(cls)
                    || "HyperLabel".equals(cls)
                    || "Button".equals(cls)
                    || "PopUpPanel".equals(cls)
                    || "CollapsiblePanel".equals(cls)
                    || "Accordion".equals(cls)
                    || "CheckBox".equals(cls)
                    || "HyperPopup".equals(cls)
                    || "TabbedPane".equals(cls)
                    || "Table".equals(cls)
                    || "SplitPane".equals(cls)
                    || "ScrollPane".equals(cls)
                    || "LayoutPane".equals(cls)
                    || "Note".equals(cls)
                    || "DocField".equals(cls)
                    || "ChartPanel".equals(cls)
                    || "LayoutPane".equals(cls)
                    || "Map".equals(cls)
                    || "MemoField".equals(cls)
                    || "RichTextEditor".equals(cls)
                    || "TreeTable".equals(cls)
                    || "TreeTable2".equals(cls)
                    ) {
                Element e = xml.getChild("title");
                if (e != null) {
                    putToMap(e, map, frame);
                }
                e = xml.getChild("treeTitle");
                if (e != null) {
                    putToMap(e, map, frame);
                }
        	} else if (cls.endsWith("Column")) {
                Element e = xml.getChild("header").getChild("text");
                if (e != null) {
                    putToMap(e, map, frame);
                }
	            e = xml.getChild("header").getChild("editor");
	            if (e != null) {
                    putToMap(e, map, frame);
	            }
	        }
	        Element e = xml.getChild("description");
	        if (e != null) {
                putToMap(e, map, frame);
	        }
	        e = xml.getChild("toolTip");
	        if (e != null) {
                putToMap(e, map, frame);
	        }
            Element e1 = xml.getChild("obligation");
            if (e1 != null) {
                Element e2 = e1.getChild("message");
                if (e2 != null) {
                    putToMap(e2, map, frame);
                }
            }
            e1 = xml.getChild("view");
            if (e1 != null) {
                Element e2 = e1.getChild("border");
                if (e2 != null) {
                    putToMap(e2.getChild("borderTitle"), map, frame);
                }
            }
            e1 = xml.getChild("pov");
            if (e1 != null) {
                Element e2 = e1.getChild("copy");
                if (e2 != null) {
                    putToMap(e2.getChild("copyTitle"), map, frame);
                }
                e2 = e1.getChild("maxObjectCountMessage");
                if (e2 != null) {
                    putToMap(e2, map, frame);
                }
            }
            e1 = xml.getChild("constraints");
            if (e1 != null) {
                Element e2 = e1.getChild("formula");
                if (e2 != null) {
                    putToMap(e2.getChild("message"), map, frame);
                }
            }
            e1 = xml.getChild("children");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }

            e1 = xml.getChild("panel");
            if (("CollapsiblePanel".equals(cls)||"PopUpPanel".equals(cls)) && e1 != null) {
                loadStringMap((Element) e1.getChildren().get(0), map, frame);
            }
            
            e1 = xml.getChild("panels");
            if ("Accordion".equals(cls) && e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }
            
            e1 = xml.getChild("columns");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }
            e1 = xml.getChild("viewComp");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }
            e1 = xml.getChild("left");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }
            e1 = xml.getChild("right");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    loadStringMap((Element) children.get(i), map, frame);
                }
            }
        }
    }

    private void putToMap(Element tag, MapMap<Long, String, Object> map, InterfaceFrame frame) {
        if (tag != null) {
            Long[] langs = frame.frameLangs();
            for (int i = 0; i < langs.length; i++) {
                Long lang = langs[i];
                map.put(lang, tag.getText(),
                        frame.getString(lang, tag.getText()));
            }
        }
    }

    private void putCopyToString(Element e, InterfaceFrame frame, MapMap<Long, String, Object> strings) {
        String oldUid = e.getText();
        String uid = frame.getNextUid();
        e.setText(uid);
        Set<Long> keySet = strings.keySet();
        for (Iterator<Long> it = keySet.iterator(); it.hasNext();) {
            Long lang = it.next();
            Object text = strings.get(lang, oldUid);
            try {
                frame.setCopyString(lang, uid, text);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void convertUids(Element xml, InterfaceFrame frame, MapMap<Long, String, Object> strings) throws Exception {
        if ("Component".equals(xml.getName())) {
            String cls = xml.getAttributeValue("class");
            Element uidElement = xml.getChild("UUID");
            if (uidElement != null) {
            	uidElement.setText(UUID.randomUUID().toString());
            }
            if ("Panel".equals(cls)
                  || "Label".equals(cls)
                  || "HyperLabel".equals(cls)
                  || "Button".equals(cls)
                  || "PopUpPanel".equals(cls)
                  || "CollapsiblePanel".equals(cls)
                  || "Accordion".equals(cls)
                  || "CheckBox".equals(cls)
                  || "HyperPopup".equals(cls)
                  || "TabbedPane".equals(cls)
                  || "Table".equals(cls)
                  || "SplitPane".equals(cls)
                  || "ScrollPane".equals(cls)
                  || "LayoutPane".equals(cls)
                  || "Note".equals(cls)
                  || "DocField".equals(cls)
                  || "ChartPanel".equals(cls)
                  || "LayoutPane".equals(cls)
                  || "Map".equals(cls)
                  || "MemoField".equals(cls)
                  || "RichTextEditor".equals(cls)
                  || "TreeTable".equals(cls)
                  || "TreeTable2".equals(cls)
                  ) {
                Element e = xml.getChild("title");
                if (e != null) {
                    putCopyToString(e, frame, strings);
                }
                e = xml.getChild("treeTitle");
                if (e != null) {
                    putCopyToString(e, frame, strings);
                }
            } else if (cls.endsWith("Column")) {
                Element e = xml.getChild("header").getChild("text");
                if (e != null) {
                    putCopyToString(e, frame, strings);
                }
                e = xml.getChild("header").getChild("editor");
                if (e != null) {
                    putCopyToString(e, frame, strings);
                }
            }
            Element e = xml.getChild("description");
            if (e != null) {
                putCopyToString(e, frame, strings);
            }
            e = xml.getChild("toolTip");
            if (e != null) {
                putCopyToString(e, frame, strings);
            }
            Element e1 = xml.getChild("obligation");
            if (e1 != null) {
                Element e2 = e1.getChild("message");
                if (e2 != null) {
                    putCopyToString(e2, frame, strings);
                }
            }
            e1 = xml.getChild("view");
            if (e1 != null) {
                Element e2 = e1.getChild("border");
                if (e2 != null) {
                    Element e3 = e2.getChild("borderTitle");
                    if (e3 != null) {
                        putCopyToString(e3, frame, strings);
                    }
                }
            }
            e1 = xml.getChild("constraints");
            if (e1 != null) {
                Element e2 = e1.getChild("formula");
                if (e2 != null) {
                    Element e3 = e2.getChild("message");
                    if (e3 != null) {
                        putCopyToString(e3, frame, strings);
                    }
                }
            }
            e1 = xml.getChild("pov");
            if (e1 != null) {
                Element e2 = e1.getChild("copy");
                if (e2 != null) {
                    Element e3 = e2.getChild("copyTitle");
                    if (e3 != null) {
                        putCopyToString(e3, frame, strings);
                    }
                }
                e2 = e1.getChild("maxObjectCountMessage");
                if (e2 != null) {
                    putCopyToString(e2, frame, strings);
                }
            }
            e1 = xml.getChild("children");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    Element element = (Element) children.get(i);
                    convertUids(element, frame, strings);
                }
            }
                   
            e1 = xml.getChild("panel");
            if (("CollapsiblePanel".equals(cls)||"PopUpPanel".equals(cls)) && e1 != null) {
                convertUids((Element) e1.getChildren().get(0), frame, strings);
            }
            
            e1 = xml.getChild("panels");
            if ("Accordion".equals(cls) && e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    convertUids((Element) children.get(i), frame, strings);
                }
            }
            
            e1 = xml.getChild("columns");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    convertUids((Element) children.get(i), frame, strings);
                }
            }
            e1 = xml.getChild("viewComp");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    convertUids((Element) children.get(i), frame, strings);
                }
            }
            e1 = xml.getChild("left");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    Element element = (Element) children.get(i);
                    convertUids(element, frame, strings);
                }
            }
            e1 = xml.getChild("right");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    convertUids((Element) children.get(i), frame, strings);
                }
            }
        }

    }

    public void pasteComponent(OrGuiContainer cnt) {
        OrGuiComponent newComp = null;
        try {
            InterfaceFrame frm = ControlTabbedContent.instance().getSelectedFrame();
            Pair<String, MapMap<Long, String, Object>> clipContent = appBuffer.get("copy");
            if (clipContent != null) {
                ByteArrayInputStream data = new ByteArrayInputStream(clipContent.first.toString().getBytes("UTF-8"));
                Document doc = new SAXBuilder().build(data);
                Element xml = doc.detachRootElement();
                MapMap<Long, String, Object> map = clipContent.second;
                convertUids(xml, frm, map);
                newComp = Factories.instance().create(xml, Mode.DESIGN, frm);
                if (newComp != null) {
                    newComp.setCopy(true);
                    newComp.setGuiParent(cnt);
                    JLayeredPane lp = ((JFrame)Controller.df.getTopLevelAncestor()).getLayeredPane();
                    Point p = new Point(0, 0);
                    Dimension d = new Dimension(0, 0);
                    if (copyInstance != null) {
                        int x = kz.tamur.rt.Utils.getAbsolutX(((Component)copyInstance));
                        int y = kz.tamur.rt.Utils.getAbsolutY(((Component)copyInstance));
                        x = (x <= 0 ) ? 300 : x;
                        y = (y <= 0 ) ? 300 : y;
                        p = new Point(x, y);
                        d = new Dimension(((Component)copyInstance).getWidth(), ((Component) copyInstance).getHeight());
                    }
                    ((Component)newComp).setBounds(p.x + 5, p.y - 10, d.width, d.height);
                    lp.add((Component)newComp, new Integer(1));
                    int componentIndex = lp.getIndexOf((Component) newComp);
                    long interfaceID = DesignerFrame.tabbedContent.getKrnObjectIfr().id;
                    String componentID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
                    InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(componentID, newComp);
                    String containerID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
                    InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(containerID, cnt);
                    InterfaceActionsConteiner.getInterfaceActions(interfaceID).pasteComponent(componentID, newComp, containerID, componentIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OrGuiContainer getContainer(Component c, boolean selfCheck) {
        Component parent = (selfCheck) ? c : c.getParent();
        while(parent != null && !(parent instanceof OrGuiContainer)) {
            parent = parent.getParent();
        }
        return (OrGuiContainer)parent;
    }

    private OrGuiContainer getContainer(Place place) {
        Component parent = (Component)place;
        while(parent != null && !(parent instanceof OrGuiContainer)) {
            parent = parent.getParent();
        }
        return (OrGuiContainer)parent;
    }

    public void eventDispatched(AWTEvent event) {
        switch (event.getID()) {
            case MouseEvent.MOUSE_PRESSED :
                mousePressed((MouseEvent)event);
                break;
            case MouseEvent.MOUSE_RELEASED :
                mouseReleased((MouseEvent)event);
                break;
            case MouseEvent.MOUSE_MOVED :
                mouseMoved((MouseEvent)event);
                break;
            case KeyEvent.KEY_PRESSED :
                keyPressed((KeyEvent)event);
                break;
        }
    }

    private void mouseMoved(MouseEvent e) {
        if (compClassLab != null) {
            Component c = e.getComponent();
            if (c instanceof OrGuiComponent) {
                if (((OrGuiComponent)c).getMode() == Mode.DESIGN) {
                    compClassLab.setText(c.getClass().getName());
                }
            } else {
                compClassLab.setText("Интерфейсы...");
            }
        }
    }


    private void mousePressed(MouseEvent e) {
        Component c = e.getComponent();
        if (c instanceof Place) {
            c = (Component)getContainer((Place)c);
        }
        if (c instanceof OrGuiContainer) {
            c.requestFocusInWindow();
        }
    }

    private void mouseReleased(MouseEvent e) {
        Component c = e.getComponent();
        if (c instanceof OrGuiContainer) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                OrGuiContainer cnt = (OrGuiContainer)c;
                if (cnt.canAddComponent(e.getX(), e.getY())) {
                    try {
						createComponent(cnt, e.getX(), e.getY());
					} catch (KrnException e1) {
						e1.printStackTrace();
					}
                }
            }
        }
        if (c instanceof Place) {
            c = (Component)getContainer((Place)c);
        }
        if (c instanceof OrGuiComponent) {
            OrGuiComponent gc = (OrGuiComponent)c;
            if (gc.getMode() != Mode.RUNTIME) {
            	if (!isCompCreated) {
            		if (gc instanceof OrTabbedPane) {
            			int tabIndex = ((OrTabbedPane)gc).getUI().tabForCoordinate((OrTabbedPane)gc, e.getX(), e.getY());
            			if (tabIndex > -1)
            				gc = (OrGuiComponent)((OrTabbedPane)gc).getSelectedComponent();
            		}
                    addSelection(gc, e.isShiftDown());
                } else {
                    isCompCreated = false;
                }
            }
            c.requestFocusInWindow();
        } else if (c instanceof JTableHeader && !(c instanceof OrTableFooter)) {
            JTableHeader h = (JTableHeader)c;
            TableModel tm = h.getTable().getModel();
            if (tm instanceof OrTableModel) {
                TableColumnModel columnModel = h.getColumnModel();
                int viewIndex = columnModel.getColumnIndexAtX(e.getX());
                if (viewIndex != -1) {
                    OrTableColumn tc = ((OrTableModel)tm).getColumn(viewIndex);
                    if (tc != null) {
                        tc.setModelIndex(viewIndex);
                        if (tc.getMode() != Mode.RUNTIME) {
                            tc.setSelected(true);
                            addSelection(tc, e.isShiftDown());
                        }
                        h.requestFocusInWindow();
                    }
                }
            }
        } else if (c instanceof OrTableNavigator) {
            addSelection(((OrTableNavigator)c).getTable(), e.isShiftDown());
        }
    }

    private void keyPressed(KeyEvent e) {
        Component component = e.getComponent();
        if (component instanceof OrGuiComponent && ((OrGuiComponent) component).getMode() == Mode.DESIGN) {
        	if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()) {
                DesignerFrame.instance().setCursor(Constants.WAIT_CURSOR);
                copyToBuffer();
                DesignerFrame.instance().setCursor(Constants.DEFAULT_CURSOR);
            } else if (e.getKeyCode() == KeyEvent.VK_V && e.isControlDown()) {
                DesignerFrame.instance().setCursor(Constants.WAIT_CURSOR);
                pasteComponent(getContainer(component, true));
                DesignerFrame.instance().setCursor(Constants.DEFAULT_CURSOR);
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                removeCopy();
            } else if (e.getKeyCode() == KeyEvent.VK_X && e.isControlDown()) {
                cutComponent();
            } else if ((e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) && !(e.getSource() instanceof InterfaceTree)) {
                df.showSearchDialog();
            } else if ((e.getKeyCode() == KeyEvent.VK_N && e.isControlDown()) && !(e.getSource() instanceof InterfaceTree)) {
                df.searchComponentByTitle();
            } else if (e.getKeyCode() == KeyEvent.VK_F3) {
                df.searchNext();
            }
        }
    }

    private void removeCopy() {
        JLayeredPane layeredPane = ((JFrame)Controller.df.getTopLevelAncestor()).getLayeredPane();
        int count = layeredPane.getComponentCount();
        for (int i = count - 1; i >= 0; --i) {
            Component c = layeredPane.getComponent(i);
            if (c instanceof OrGuiComponent && ((OrGuiComponent)c).isCopy()) {
                layeredPane.remove(c);
            }
        }
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    public void deleteComponent() {
        OrGuiComponent component = getSelectedComponent();
        if (component != null && component.getMode() == Mode.DESIGN) {
            if (component instanceof OrTableColumn) {
                deleteColumn();
            } else {
                if (component instanceof OrPanel && !((OrPanel) component).isDelete()) {
                    showMessageDialog(((JComponent)component).getTopLevelAncestor(), INFORMATION_MESSAGE, "Компонент зарезервированн и не подлежит удалению!");
                } else {
                    deleteComponent(component);
                }
            }
        }
    }

    private void deleteComponent(OrGuiComponent component) {
        if (component.getMode() == Mode.RUNTIME || component.isCopy() || getContainer((Component) component, false) == null) {
            return;
        }
        Element xml = component.getXml();
        PropertyValue value = component.getPropertyValue(component.getProperties().getChild("title"));
        String cls = xml.getAttributeValue("class");
        String componentMess = "Удалeние компонента " + value.stringValue() + " [" + cls + "]!\n Продолжить?";
        int res = ButtonsFactory.BUTTON_NOACTION;
        if (!(component instanceof OrTableColumn)) {
            res = showMessageDialog(((JComponent)component).getTopLevelAncestor(), QUESTION_MESSAGE, componentMess);
        } else {
            String columnMess = "Удалeние колонки " + value.stringValue() + " [" + cls + "]!\n Продолжить?";
            Container cont = ((OrTableColumn)component).getOrTable().getJTable().getTopLevelAncestor();
            res = showMessageDialog(cont,QUESTION_MESSAGE, columnMess);
        }
        if (res == ButtonsFactory.BUTTON_YES) {            
        	dropComponent(component);          
        }
    }
     
    public void dropComponent(OrGuiComponent component) {
    	if (!(component instanceof EmptyPlace) && !(component instanceof OrTableColumn)) {   
    		try {    			
    			if (component instanceof OrGuiContainer) {
    				if (((Container) component).getComponentCount() > 0) {
    					Component[] components = ((Container) component).getComponents();
    					for (int i = 0; i < components.length; i++) {
    						if (components[i] instanceof OrGuiComponent) 
    						    dropComponent((OrGuiComponent) components[i]);    
    					}
    				}
    			}
	    		PropertyValue titleValue = component.getPropertyValue(component.getProperties().getChild("title"));    	
	    		titleValue = new PropertyValue(titleValue.getValue(), titleValue.getProperty());
	    		OrGuiContainer container = getContainer((Component) component, false);
	    		long interfaceID = DesignerFrame.getTabbedContent().getKrnObjectIfr().id;
                String titleValueID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
	    		InterfaceActionsConteiner.getInterfaceActions(interfaceID).getTitleValues().put(titleValueID, titleValue);
	    		String componentID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
	            InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(componentID, component);
		    	String containerID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
	            InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(containerID, container);         
	            InterfaceActionsConteiner.getInterfaceActions(interfaceID).dropComponent(componentID, component, containerID, container, titleValueID);
				container.removeComponent(component);
				InterfaceFrame frame = ControlTabbedContent.instance().getSelectedFrame();
				removeStrings(component.getXml(), frame);
				firePropertyModified(container);	
    		} catch (NullPointerException e) {
    			e.printStackTrace();
    		}
    	}
    }
 
    private void removeStrings(Element xml, InterfaceFrame frame) {
        if ("Component".equals(xml.getName())) {
            String cls = xml.getAttributeValue("class");
            if ("Panel".equals(cls)
                    || "Label".equals(cls)
                    || "HyperLabel".equals(cls)
                    || "Button".equals(cls)
                    || "PopUpPanel".equals(cls)
                    || "CollapsiblePanel".equals(cls)
                    || "Accordion".equals(cls)
                    || "CheckBox".equals(cls)
                    || "HyperPopup".equals(cls)
                    || "TabbedPane".equals(cls)
                    || "Table".equals(cls)
                    || "SplitPane".equals(cls)
                    || "ScrollPane".equals(cls)
                    || "LayoutPane".equals(cls)
                    || "Note".equals(cls)
                    || "DocField".equals(cls)
                    || "ChartPanel".equals(cls)
                    || "LayoutPane".equals(cls)
                    || "Map".equals(cls)
                    || "MemoField".equals(cls)
                    || "RichTextEditor".equals(cls)
                    || "TreeTable".equals(cls)
                    || "TreeTable2".equals(cls)
                    ) {
                Element e = xml.getChild("title");
                if (e != null) {
                    frame.removeStrings(e.getText());
                }
                e = xml.getChild("treeTitle");
                if (e != null) {
                    frame.removeStrings(e.getText());
                }
            } else if (cls.endsWith("Column")) {
                Element e = xml.getChild("header").getChild("text");
                if (e != null) {
                    frame.removeStrings(e.getText());
                }
                e = xml.getChild("header").getChild("editor");
                if (e != null) {
                    frame.removeStrings(e.getText());
                }
            }
            Element e = xml.getChild("description");
            if (e != null) {
                frame.removeStrings(e.getText());
            }
            e = xml.getChild("toolTip");
            if (e != null) {
                frame.removeStrings(e.getText());
            }

            Element e1 = xml.getChild("obligation");
            if (e1 != null) {
                Element e2 = e1.getChild("message");
                if (e2 != null) {
                    frame.removeStrings(e2.getText());
                }
            }
            e1 = xml.getChild("view");
            if (e1 != null) {
                Element e2 = e1.getChild("border");
                if (e2 != null) {
                    Element e3 = e2.getChild("borderTitle");
                    if (e3 != null) {
                        frame.removeStrings(e3.getText());
                    }
                }
            }
            e1 = xml.getChild("constraints");
            if (e1 != null) {
                Element e2 = e1.getChild("formula");
                if (e2 != null) {
                    Element e3 = e2.getChild("message");
                    if (e3 != null) {
                        frame.removeStrings(e3.getText());
                    }
                }
            }
            e1 = xml.getChild("pov");
            if (e1 != null) {
                Element e2 = e1.getChild("copy");
                if (e2 != null) {
                    Element e3 = e2.getChild("copyTitle");
                    if (e3 != null) {
                        frame.removeStrings(e3.getText());
                    }
                }
                e2 = e1.getChild("maxObjectCountMessage");
                if (e2 != null) {
                    frame.removeStrings(e2.getText());
                }
            }

            e1 = xml.getChild("panel");
            if (("CollapsiblePanel".equals(cls)||"PopUpPanel".equals(cls)) && e1 != null) {
                removeStrings((Element) e1.getChildren().get(0), frame);
            }
            
            e1 = xml.getChild("panels");
            if ("Accordion".equals(cls) && e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    removeStrings((Element) e1.getChildren().get(i), frame);
                }
            }
            
            e1 = xml.getChild("children");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    removeStrings((Element) children.get(i), frame);
                }
            }
            e1 = xml.getChild("columns");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    removeStrings((Element) children.get(i), frame);
                }
            }
            e1 = xml.getChild("viewComp");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    removeStrings((Element) children.get(i), frame);
                }
            }
            e1 = xml.getChild("left");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    removeStrings((Element) children.get(i), frame);
                }
            }
            e1 = xml.getChild("right");
            if (e1 != null) {
                List children = e1.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    removeStrings((Element) children.get(i), frame);
                }
            }
        }
    }

    private void deleteColumn() {
    	OrGuiComponent component = getSelectedComponent();
        if (component.getMode() == Mode.RUNTIME) {
            return;
        }
        Element xml = component.getXml();
        PropertyValue value = component.getPropertyValue(component.getProperties().getChild("header").getChild("text"));
        String cls = xml.getAttributeValue("class");
        String mess = "Удалeние колонки " + value.stringValue() + " [" + cls + "]!\n Продолжить?";
        int res = ButtonsFactory.BUTTON_NOACTION;
        Container cont = ((OrTableColumn)component).getOrTable().getJTable().getTopLevelAncestor();
        res = showMessageDialog(cont,QUESTION_MESSAGE, mess);
        if (res == ButtonsFactory.BUTTON_YES) {
            OrTableColumn column = (OrTableColumn) component;
            column.getOrTable().removeComponent(column);
            InterfaceFrame frame = ControlTabbedContent.instance().getSelectedFrame();
            removeStrings(xml, frame);
            firePropertyModified(column.getOrTable());
        }
    }

    public void addPropertyListener(PropertyListener l) {
        listeners.add(PropertyListener.class, l);
    }

    public void removePropertyListener(PropertyListener l) {
        listeners.remove(PropertyListener.class, l);
    }

    public void firePropertyModified(OrGuiComponent c) {
        Object[] list = listeners.getListeners(PropertyListener.class);
        try {
	        for (int i = 0; i < list.length; i++) {
	            if (list[i] != this) {
	              ((PropertyListener)list[i]).propertyModified(c);
	            }    
	        }
        } catch (IndexOutOfBoundsException e) {
        	e.printStackTrace();
        } catch (IllegalArgumentException e) {
        	e.printStackTrace();
        }
    }

    public void firePropertyModified(OrGuiComponent c, int propertyEvent) {
        Object[] list = listeners.getListeners(PropertyListener.class);
        for (int i = 0; i < list.length; i++) {
            if (list[i] != this) {
                ((PropertyListener) list[i]).propertyModified(c, propertyEvent);
            }
        }
    }
    public void propertyModified(OrGuiComponent component) {}

    public void propertyModified(OrGuiComponent component, PropertyNode property) {}

    public void propertyModified(OrGuiComponent component, int propertyEvent) {
        switch(propertyEvent) {
            case PropertyListener.SELECT_EVENT:
                if (component instanceof Component) {
                    ((Component) component).requestFocusInWindow();
                }
                addSelection(component, false);
                break;
            case PropertyListener.DELETE_EVENT:
                deleteComponent();
                break;
        }
    }

    private void dndInit() {
        dgLnr = new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent e) {
                Component component = e.getComponent();
                if (component instanceof JComponent) {
                    if (((OrGuiComponent) component).getMode() == Mode.DESIGN) {
                        e.startDrag(DragSource.DefaultCopyDrop, new Clip(component), dsLnr);
                        sourcePoint = e.getDragOrigin();
                    }
                }
            }
        };

        dsLnr = new DragSourceAdapter() {};
        dtLnr = new DropTargetAdapter() {
            public void drop(DropTargetDropEvent e) {
                Component component = e.getDropTargetContext().getComponent();
                if (component instanceof EmptyPlace) {
                	component = (Component) getContainer(component, false);
                }
                if (component instanceof OrGuiContainer) {
                    OrGuiContainer container = (OrGuiContainer) component;
                    Transferable transfer = e.getTransferable();
                    if (transfer.isDataFlavorSupported(Clip.flavors[0])) {
                        try {
                            OrGuiComponent guiComponent = (OrGuiComponent) transfer.getTransferData(Clip.flavors[0]);
                            if (guiComponent == container ) {
                            	return;
                            }
                            OrGuiContainer sourceContainer = null;
                            if (guiComponent.isCopy()) {
                            	sourceContainer = guiComponent.getGuiParent();
                            } else {
                            	sourceContainer = getContainer(((Component) guiComponent), false);
                            }
                            targetPoint = e.getLocation();
                            moveComponent(guiComponent, container, sourceContainer, targetPoint, sourcePoint, false, 0, 0, 0, 0);                           
                        } catch (UnsupportedFlavorException exception) {
                        	exception.printStackTrace();
                        } catch (IOException exception) {
                        	exception.printStackTrace();
                        } catch (NullPointerException exception) {
                        	exception.printStackTrace();
                        }
                    }
                }
            }
        };
    }
    
    public void moveComponent(OrGuiComponent component, OrGuiContainer targetContainer, OrGuiContainer sourceContainer, Point targetPoint, Point sourcePoint, boolean canUse, int xValue, int yValue, int heightValue, int widthValue) {
    	if ((targetContainer != null) && (sourceContainer != null)) {
   		 	long interfaceID = DesignerFrame.getTabbedContent().getKrnObjectIfr().id;
        	if (component.isCopy()) {
        		String componentID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
                InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(componentID, component);
                InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).dropMovedComponent(componentID, component);
                if (sourceContainer == targetContainer) {
                	 targetContainer.moveComponent(component, targetPoint.x, targetPoint.y);
                } else {                        	  
                	 sourceContainer.removeComponent(component);
                	 targetContainer.addComponent(component, targetPoint.x, targetPoint.y);
                }
        	} else {
        		PropertyNode position = component.getProperties().getChild("pos");
        		int x = component.getPropertyValue(position.getChild("x")).intValue();
        		int y = component.getPropertyValue(position.getChild("y")).intValue();
        		int width = component.getPropertyValue(position.getChild("width")).intValue();
        		int height = component.getPropertyValue(position.getChild("height")).intValue();
        		String operationID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
                String componentID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
                InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(componentID, component);
                String sourceContainerID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
                InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(sourceContainerID, sourceContainer);
                String targetContainerID = InterfaceActionsConteiner.getInterfaceActions(interfaceID).getNextID();
                InterfaceActionsConteiner.getInterfaceActions(interfaceID).getGUIComponents().put(targetContainerID, targetContainer);
                
                if (sourceContainer == targetContainer) {
            		targetContainer.moveComponent(component, targetPoint.x, targetPoint.y);
                } else {                        	  
               	 	sourceContainer.removeComponent(component);
               	 	targetContainer.addComponent(component, targetPoint.x, targetPoint.y);
                }
                if (canUse) {
	                component.setPropertyValue(new PropertyValue(xValue, component.getProperties().getChild("pos").getChild("x")));
					component.setPropertyValue(new PropertyValue(yValue, component.getProperties().getChild("pos").getChild("y")));
					component.setPropertyValue(new PropertyValue(heightValue, component.getProperties().getChild("pos").getChild("width")));
					component.setPropertyValue(new PropertyValue(widthValue, component.getProperties().getChild("pos").getChild("height")));
                }
                InterfaceActionsConteiner.getInterfaceActions(DesignerFrame.tabbedContent.getKrnObjectIfr().id).moveComponent(operationID, componentID, sourceContainerID, targetContainerID, sourcePoint, targetPoint, x, y, width, height);
        	}        	
        	firePropertyModified(targetContainer);
        }
   }

    public void componentCreated(OrGuiComponent c) {
        if (c instanceof Component) {
            Component comp = (Component)c;
            DragSource dragSource = DragSource.getDefaultDragSource();
            dragSource.createDefaultDragGestureRecognizer(
                    comp, DnDConstants.ACTION_COPY_OR_MOVE, dgLnr);
            new DropTarget(comp, DnDConstants.ACTION_COPY_OR_MOVE, dtLnr);
        }
    }

    public void componentCreating(String className) {

    }

    class ControllerMouseEvent extends MouseEvent {

        private OrTableColumn o;

        public ControllerMouseEvent(OrTableColumn source) {
            super(new JLabel(), MouseEvent.MOUSE_PRESSED, 0,
                    MouseEvent.BUTTON1_MASK, 0, 0, 1, false, MouseEvent.BUTTON1);
            o = source;
        }

        public OrTableColumn getColumnObject() {
            return o;
        }
    }

    private static class Clip implements Transferable {
        public static DataFlavor[] flavors;
        static {
            try {
                flavors = new DataFlavor[] {
                    new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType)
                };
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private Object obj;

        public Clip(Object obj) {
            this.obj = obj;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(flavors[0]);
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            return obj;
        }

    }
    
    private void selectComponent(OrGuiComponent c) {
        c.setSelected(true);
        OrGuiContainer parent = c.getGuiParent();
        OrGuiComponent child = c;
        while (parent != null) {
        	if (parent instanceof OrTabbedPane)
        		((OrTabbedPane)parent).setSelectedComponent((Component)child);
        	child = parent;
        	parent = parent.getGuiParent();
        }
    }
}
