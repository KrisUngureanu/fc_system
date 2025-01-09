package kz.tamur.guidesigner;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import kz.tamur.rt.Utils;
import kz.tamur.comps.*;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 27.05.2004
 * Time: 16:40:53
 * To change this template use File | Settings | File Templates.
 */
public class DesignerPreviewFrame extends JFrame {

    private DesignerStatusBar statusBar = new DesignerStatusBar();
    private JLabel infoLabel = Utils.createLabel("");

    public DesignerPreviewFrame(Element xml, String title, long langId) throws HeadlessException, KrnException {
        super(title + " - [Просмотр интерфейса]");
        setIconImage(kz.tamur.rt.Utils.getImageIcon("Preview").getImage());
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                infoLabel.setText(getWidth() + " : " + getHeight());
            }
        });
        statusBar.addAnyComponent(infoLabel);
        statusBar.addEmptySpace();
        statusBar.addCorner();
        OrGuiComponent c = Factories.instance().create(xml, Mode.PREVIEW,
                ControlTabbedContent.instance().getSelectedFrame());
        c.setLangId(langId);
        Container container = getContentPane();
        container.setLayout(new GridBagLayout());
        container.add((Component)c, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_0, 0, 0));
        container.add(statusBar, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                Constants.INSETS_0, 0, 0));
        PropertyNode pn = c.getProperties().getChild("pos").getChild("pref");
        PropertyValue pvWidth = c.getPropertyValue(pn.getChild("width"));
        PropertyValue pvHeight = c.getPropertyValue(pn.getChild("height"));
        if (!pvWidth.isNull() && !pvHeight.isNull()) {
            int width = pvWidth.intValue();
            int height = pvHeight.intValue();
            if (width > 0 && height > 0) {
                setSize(pvWidth.intValue(), pvHeight.intValue());
                setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(width, height));
            } else {
                setSize(800, 600);
            }
        } else {
            setSize(800, 600);
        }
        //pack();
        setLocation(0, 0);
        infoLabel.setText(getWidth() + " : " + getHeight());
        show();
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            dispose();
        } else {
            super.processWindowEvent(e);
        }    
    }
}
