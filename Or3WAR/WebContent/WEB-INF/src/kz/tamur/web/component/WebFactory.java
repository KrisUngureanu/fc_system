package kz.tamur.web.component;

import org.jdom.Element;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.FactoryListener;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.web.OrWebBarcode;
import kz.tamur.web.OrWebImage;

import kz.tamur.web.common.webgui.WebComponent;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import javax.swing.event.EventListenerList;
import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: �������������
 * Date: 12.07.2006
 * Time: 16:26:38
 * To change this template use File | Settings | File Templates.
 */
public class WebFactory {
    private EventListenerList listeners = new EventListenerList();
    private int componentNumber = 0;
    private KrnObject obj;
    
    public WebFactory(KrnObject obj) {
    	this.obj = obj;
    }
    

    public void addFactoryListener(FactoryListener l) {
        listeners.add(FactoryListener.class, l);
    }

    public void removeFactoryListener(FactoryListener l) {
        listeners.remove(FactoryListener.class, l);
    }

    private void fireComponentCreated(WebComponent c) {
        EventListener[] ls = listeners.getListeners(FactoryListener.class);
        for (int i = 0; i < ls.length; i++) {
            ((FactoryListener)ls[i]).componentCreated((OrGuiComponent)c);
        }
    }

    private void fireComponentCreating(String className) {
        EventListener[] ls = listeners.getListeners(FactoryListener.class);
        for (int i = 0; i < ls.length; i++) {
            ((FactoryListener)ls[i]).componentCreating(className);
        }
    }

    public WebComponent create(Element xml, int mode, OrFrame frame) throws KrnException {
        String name = xml.getAttributeValue("class");
        fireComponentCreating(name);
        WebComponent c = createImpl(name, xml, mode, frame);
        fireComponentCreated(c);
        return c;
    }
    
    private WebComponent createImpl(String name, Element xml, int mode, OrFrame frame) throws KrnException {
    	String id = obj.id + "_" + componentNumber++;
    	WebComponent comp = null;
        if ("Panel".equals(name)) {
            comp = new OrWebPanel(xml, mode, this, frame, id);
        } else if ("GISPanel".equals(name)) {
            comp = new OrWebGISPanel(xml, mode, this, frame, id);
        } else if ("AnalyticPanel".equals(name)) {
            comp = new OrWebAnalyticPanel(xml, mode, this, frame, id);
        } else if ("ImagePanel".equals(name)) {
            comp = new OrWebImagePanel(xml, mode, this, frame, id);
        } else if ("ScrollPane".equals(name)) {
            comp = new OrWebScrollPane(xml, mode, this, frame, id);
        } else if ("SplitPane".equals(name)) {
            comp = new OrWebSplitPane(xml, mode, this, frame, id);
        } else if ("LayoutPane".equals(name)) {
            comp = new OrWebLayoutPane(xml, mode, this, frame, id);
        } else if ("TabbedPane".equals(name)) {
            comp = new OrWebTabbedPane(xml, mode, this, frame, id);
        } else if ("Label".equals(name)) {
            comp = new OrWebLabel(xml, mode, frame, false, id);
        } else if ("TextField".equals(name)) {
            comp = new OrWebTextField(xml, mode, frame, false, id);
        } else if ("PasswordField".equals(name)) {
            comp = new OrWebPasswordField(xml, mode, frame, false, id);
        } else if ("DateField".equals(name)) {
            comp = new OrWebDateField(xml, mode, frame, false, id);
        } else if ("ComboBox".equals(name)) {
            comp = new OrWebComboBox(xml, mode, frame, false, id);
        } else if ("MemoField".equals(name)) {
            comp = new OrWebMemoField(xml, mode, frame, false, id);
        } else if ("RichTextEditor".equals(name)) {
            comp = new OrWebRichTextEditor(xml, mode, frame, false, id);
        } else if ("Table".equals(name)) {
            comp = new OrWebTable(xml, mode, this, frame, id);
        } else if ("TreeTable".equals(name)) {
            comp = new OrWebTreeTable(xml, mode, this, frame, id);
        } else if ("TreeTable2".equals(name)) {
            comp = new OrWebTreeTable2(xml, mode, this, frame, id);
        } else if ("CheckBox".equals(name)) {
            comp = new OrWebCheckBox(xml, mode, frame, false, id);
        } else if ("IntField".equals(name)) {
            comp = new OrWebIntField(xml, mode, frame, false, id);
        } else if ("FloatField".equals(name)) {
            comp = new OrWebFloatField(xml, mode, frame, false, id);
        } else if ("HyperLabel".equals(name)) {
            comp = new OrWebHyperLabel(xml, mode, frame, false, id);
        } else if ("HyperPopup".equals(name)) {
            comp = new OrWebHyperPopup(xml, mode, frame, false, id);
        } else if ("Tree".equals(name)) {
            comp = new OrWebTreeCtrl(xml, mode, frame, false, id);
        } else if ("Tree2".equals(name)) {
            comp = new OrWebTreeControl2(xml, mode, frame, false, id);
        } else if ("TreeField".equals(name)) {
            comp = new OrWebTreeField(xml, mode, frame, false, id);
        } else if ("Button".equals(name)) {
            comp = new OrWebButton(xml, mode, frame, id);
        } else if ("Image".equals(name)) {
            comp = new OrWebImage(xml, mode, frame, false, id);
        } else if ("Map".equals(name)) {
            comp = new OrWebMap(xml, mode, frame, id);
        } else if ("DocField".equals(name)) {
            comp = new OrWebDocField(xml, mode, frame, false, id);
        } else if ("TextColumn".equals(name)) {
            comp = new OrWebTextColumn(xml, mode, frame, id);
        } else if ("IntColumn".equals(name)) {
            comp = new OrWebIntColumn(xml, mode, frame, id);
        } else if ("DateColumn".equals(name)) {
            comp = new OrWebDateColumn(xml, mode, frame, id);
        } else if ("FloatColumn".equals(name)) {
            comp = new OrWebFloatColumn(xml, mode, frame, id);
        }else if ("ComboColumn".equals(name)) {
            comp = new OrWebComboColumn(xml, mode, frame, id);
        } else if ("MemoColumn".equals(name)) {
            comp = new OrWebMemoColumn(xml, mode, frame, id);
        } else if ("CheckColumn".equals(name)) {
            comp = new OrWebCheckColumn(xml, mode, frame, id);
        } else if ("HyperColumn".equals(name)) {
            comp = new OrWebHyperColumn(xml, mode, frame, id);
        } else if ("PopupColumn".equals(name)) {
            comp = new OrWebPopupColumn(xml, mode, frame, id);
        } else if ("DocFieldColumn".equals(name)) {
            comp = new OrWebDocFieldColumn(xml, mode, frame, id);
        } else if ("ImageColumn".equals(name)) {
            comp = new OrWebImageColumn(xml, mode, frame, id);
        } else if ("TreeColumn".equals(name)) {
            comp = new OrWebTreeColumn(xml, mode, frame, id);
        } else if ("HSpacer".equals(name)) {
            comp = new OrWebSpacer(xml, OrWebSpacer.HORIZONTAL, mode, frame, id);
        } else if ("VSpacer".equals(name)) {
            comp = new OrWebSpacer(xml, OrWebSpacer.VERTICAL, mode, frame, id);
        } else if ("RadioBox".equals(name)) {
            comp = new OrWebRadioBox(xml, mode, frame, id);
        } else if ("Note".equals(name)) {
            comp = new OrWebNote(xml, mode, frame, id);
        } else if ("PopUpPanel".equals(name)) {
            comp = new OrWebPopUpPanel(xml, mode, this, frame, id);
        } else if ("CollapsiblePanel".equals(name)) {
            comp = new OrWebCollapsiblePanel(xml, mode, this, frame, id);
        } else if ("Accordion".equals(name)) {
            comp = new OrWebAccordion(xml, mode, this, frame, id);
        } else if ("Barcode".equals(name)) {
        	comp = new OrWebBarcode(xml, mode, frame, id);
        }

        if (comp != null) {
        	((WebFrame) frame).registerComponent(comp.uuid, comp);
        }
        return comp;
    }
}
