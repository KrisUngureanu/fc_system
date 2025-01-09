package kz.tamur.rt.adapters;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.OrImagePanel;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;

import com.cifs.or2.kernel.KrnException;
import static kz.tamur.rt.Utils.createMenuItem;
public class ImagePanelAdapter extends ComponentAdapter
        implements ActionListener {
    private OrImagePanel imagePanel;
    private RadioGroupManager groupManager = new RadioGroupManager();
    private JPopupMenu pm = new JPopupMenu();
    private JMenuItem miLoad = createMenuItem("Загрузить");
    private String fileNotFoundMsg = "Файл не найден!";

    private boolean autoresize = true;
    private long ifcLangId = 0;
    private OrRef tableRef;
    private InnerImagePanel selected;
    private OrRef fileNameRef;
    private OrRef fileContentRef;

    public ImagePanelAdapter(UIFrame frame, OrImagePanel img, boolean isEditor)
            throws KrnException {
        super(frame, img, isEditor);
        createTableRef(img, isEditor);

        OrRef ref = dataRef;
        while (ref != null && ref != tableRef) {
            ref.setColumn(true);
            ref = ref.getParent();
        }

        if (dataRef != null) {
            String path = dataRef.toString();
            path = path.substring(0, path.lastIndexOf('.'));
            fileNameRef = OrRef.createRef(path + ".filename", false, Mode.RUNTIME,
                frame.getRefs(), frame.getTransactionIsolation(), frame);
            fileContentRef = OrRef.createRef(path + ".file", false, Mode.RUNTIME,
                frame.getRefs(), frame.getTransactionIsolation(), frame);
        }

        PropertyNode proot = img.getProperties();
        PropertyValue pv =
                img.getPropertyValue(proot.getChild("view").getChild("autoresize"));
        if (!pv.isNull()) {
            autoresize = pv.booleanValue();
        }
        this.imagePanel = img;
        img.setAdapter(this);
        if (dataRef != null && !dataRef.isColumn()) {
            kz.tamur.rt.Utils.setComponentTabFocusCircle(this.imagePanel);
            this.imagePanel.addFocusListener(new DefaultFocusAdapter(this.imagePanel));
        }

        pm.add(miLoad);
        miLoad.addActionListener(this);
        this.imagePanel.setXml(null);
    }

    protected void createTableRef(OrGuiComponent c, boolean isEditor) throws KrnException {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("ref").getChild("tableRef");
        PropertyValue pv = c.getPropertyValue(rprop);
        if (!pv.isNull() && pv.stringValue().length() > 0) {
            try {
                propertyName = "Свойство: Данные";

                boolean hasParentRef = false;
                Map<String, OrRef> refs = frame.getRefs();
                Iterator<String> it = refs.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    if (key != null && pv.stringValue().startsWith(key.toString())) {
                        hasParentRef = true;
                        break;
                    }
                }
                if (!hasParentRef)
                    tableRef = OrRef.createContentRef(pv.stringValue(),
                            Constants.RM_ALWAYS, Mode.RUNTIME,
                            frame.getTransactionIsolation(), frame);

                if (tableRef == null)
                    tableRef = OrRef.createRef(pv.stringValue(), false, Mode.RUNTIME,
                            frame.getRefs(), frame.getTransactionIsolation(), frame);

                if (!isEditor) {
                    tableRef.addCheckContext(this);
                    tableRef.addOrRefListener(this);
                }
            } catch (Exception e) {
                showErrorNessage(e.getMessage() + pv.stringValue());
                e.printStackTrace();
            }
        }
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (e.getRef() == dataRef && e.getOriginator() != this
        		&& (OrRefEvent.CHECKING & e.getReason()) == 0) {
            List<OrRef.Item> items = dataRef.getItems(langId);
            int selectedIndex = dataRef.getIndex();
            Component[] comps = imagePanel.getComponents();
            for (Component comp : comps) {
                if (comp instanceof InnerImagePanel) {
                    ((InnerImagePanel) comp).clear();
                    comp = null;
                }
            }
            imagePanel.removeAll();
            int index = 0;
            for (OrRef.Item item : items) {
                if (item == null || item.getCurrent() instanceof File) {
                    File val = (item != null) ? (File) item.getCurrent() : null;
                    if (val != null) {
                        final InnerImagePanel ip = new InnerImagePanel(val, (int) imagePanel.getPrefSize().getWidth(), index++);
                        imagePanel.add(ip);
                        ip.addMouseListener(new MouseAdapter() {
                            public void mouseClicked(MouseEvent e) {
                                popupShow(e);
                                if (e.getClickCount() > 1) {
                                    OrRef.Item item = dataRef.getItem(langId);
                                    if (item == null || item.getCurrent() == null) {
                                        Container cnt = imagePanel.getTopLevelAncestor();
                                        if (cnt instanceof Frame) {
                                            MessagesFactory.showMessageDialog((Frame)cnt,
                                                    MessagesFactory.INFORMATION_MESSAGE, fileNotFoundMsg, LangItem.getById(ifcLangId));
                                        } else {
                                            MessagesFactory.showMessageDialog((Dialog)cnt,
                                                    MessagesFactory.INFORMATION_MESSAGE, fileNotFoundMsg, LangItem.getById(ifcLangId));
                                        }
                                    } else {
                                        open();
                                    }
                                }
                            }

                            public void mousePressed(MouseEvent e) {
                                popupShow(e);
                            }

                            public void mouseReleased(MouseEvent e) {
                                popupShow(e);
                            }

                            void popupShow(MouseEvent e) {
                                if (e.isPopupTrigger() && imagePanel.isShowPopup()) {
                                    pm.show(ip, e.getX(), e.getY());
                                }
                            }
                        });
                    }
                }
            }
            if (imagePanel.getComponentCount() > 0) {
                //imagePanel.setLayout(new GridLayout(imagePanel.getComponentCount() + 1, 1));
                imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
                imagePanel.add(new Box.Filler(new Dimension(0, 0), new Dimension(10, 100), new Dimension(10, 800)));
                if (selectedIndex > -1) select((InnerImagePanel)imagePanel.getComponent(selectedIndex));
                if (radioGroup != null) {
                    groupManager.evaluate(frame, radioGroup);
                }
            }
            imagePanel.repaint();
            imagePanel.getParent().validate();
            //imagePanel.getParent().getParent().invalidate();
        }
    }

    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == miLoad) {
                //if (doBeforeOpen()) {
                    OrRef.Item item = dataRef.getItem(langId);
                    if (item == null || item.getCurrent() == null) {
                        Container cnt = imagePanel.getTopLevelAncestor();
                        if (cnt instanceof Frame) {
                            MessagesFactory.showMessageDialog((Frame)cnt,
                                    MessagesFactory.INFORMATION_MESSAGE, fileNotFoundMsg, LangItem.getById(ifcLangId));
                        } else {
                            MessagesFactory.showMessageDialog((Dialog)cnt,
                                    MessagesFactory.INFORMATION_MESSAGE, fileNotFoundMsg, LangItem.getById(ifcLangId));
                        }
                    } else {
                        open();
                    }
                //}
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void open() {
        try {
        	File f = download();
        	if (f != null) {
                Runtime r = Runtime.getRuntime();
                r.exec("cmd /c \"" + f.getAbsolutePath() + "\"");
        	}
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private File download() {
        try {
        	String fileName = (String)fileNameRef.getValue(langId);
        	File file = (File)fileContentRef.getValue(langId);
            if (fileName != null && file != null) {
    	    	File tmpDir = Constants.TMP_DIRECTORY;
    	    	File tmpFile = Funcs.getCanonicalFile(tmpDir, fileName);

            	Funcs.copy(file, tmpFile);
            	return tmpFile;
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public void setEnabled(boolean isEnabled) {
        imagePanel.setEnabled(isEnabled);
    }

    public void setLangId(long langId) {
        ifcLangId = langId;
        LangItem li = LangItem.getById(langId);
        if (li != null) {
            ResourceBundle res;
            if ("KZ".equals(li.code)) {
                res = ResourceBundle.getBundle(
                        Constants.NAME_RESOURCES, new Locale("kk"));
            } else {
                res = ResourceBundle.getBundle(
                        Constants.NAME_RESOURCES, new Locale("ru"));
            }
            miLoad.setText(res.getString("open"));
            fileNotFoundMsg = res.getString("fileNotFound");
        }
    }

    private static double koef = 1.2;

    private class SelectMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent event) {
        }

        public void mousePressed(MouseEvent e) {
            select((InnerImagePanel) e.getComponent());
        }
    }

    void select(InnerImagePanel image) {
        InnerImagePanel oldSelected = selected;

        selected = image;
        if (selected != null) {
            try {
                tableRef.absolute(selected.getIndex(), this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            selected.repaint();
        }
        if (oldSelected != null)
            oldSelected.repaint();
    }

    private SelectMouseListener sml = new SelectMouseListener();

    private class InnerImagePanel extends JPanel {
        private Image image;
        int width;
        int height;
        int index;

        InnerImagePanel(File imageFile, int width, int index) {
    		Image image = Toolkit.getDefaultToolkit().createImage(imageFile.getAbsolutePath());
        	//float imageWidth = image.getWidth(null);
        	//float imageHeight = image.getHeight(null);
            this.width = width;
            //this.height = (int)(imageHeight * width * koef / imageWidth);
            this.height = (int)(width * 1.41);
            this.image = image.getScaledInstance(width, height, Image.SCALE_FAST);
            image = null;
            this.index = index;
            addMouseListener(sml);
        }

        public void clear() {
            image = null;
        }

        public void paint(Graphics g) {
            super.paint(g);
            if (this.equals(selected)) {
                g.setColor(kz.tamur.rt.Utils.getLightSysColor());
                g.drawRect(0,0,width, height);
                g.drawRect(1,1,width-2, height-2);
            } else {
                g.clearRect(0,0,width, height);
            }
            g.drawImage(image, 3, 3, width - 6, height - 6, this);
        }

        public Dimension getMinimumSize() {
            return new Dimension(width+1, height+1);
        }

        public Dimension getMaximumSize() {
            return new Dimension(width+1, height+1);
        }

        public Dimension getPreferredSize() {
            return new Dimension(width+1, height+1);
        }

        public int getIndex() {
            return index;
        }
    }
}
