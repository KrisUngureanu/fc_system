package kz.tamur.or3.client.props.inspector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import kz.tamur.Or3Frame;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.filters.FiltersPanel;
import kz.tamur.guidesigner.service.ServicePropertyEditor;
import kz.tamur.or3.client.props.ComboProperty;
import kz.tamur.or3.client.props.ComboPropertyItem;
import kz.tamur.or3.client.props.ExprProperty;
import kz.tamur.or3.client.props.FolderProperty;
import kz.tamur.or3.client.props.Inspectable;
import kz.tamur.or3.client.props.InspectorOwner;
import kz.tamur.or3.client.props.Property;
import kz.tamur.or3.client.props.StringProperty;
import kz.tamur.or3.client.props.TreeOrExprProperty;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

public class PropertyInspector extends JPanel implements ActionListener {
    private PropertyTable table = new PropertyTable();
    private JToggleButton plainBtn = ButtonsFactory.createCompButton("", kz.tamur.rt.Utils.getImageIcon("Plain"));
    private JButton floatButton = new JButton();
    private boolean plainMode = false;
    private boolean isInspectorFloat = false;
    private String sizeLocation = "";
    private Properties props = new Properties();
    private InspectorOwner owner;
    private JDialog dialog;
    private String ownerStr = "";
    private String dialogTitle = "";
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public boolean isPlainMode() {
        return plainMode;
    }

    /**
     * Метод
     * возвращает информацию о режиме инспектора свойст
     * находится ли инспектор в плавающем режиме, или прикреплён к фрейму
     */
    public boolean isInspectorFloat() {
        return isInspectorFloat;
    }

    public PropertyInspector(InspectorOwner owner) {
        this.owner = owner;
        if (owner instanceof FiltersPanel) {
            ownerStr = "Filter";
        } else if (owner instanceof ServicePropertyEditor) {
            ownerStr = "Service";
        }
        table.getTree().setRootVisible(false);

        String workDir = Utils.getUserWorkingDir();
        if (Funcs.isValid(workDir)) {
	        File dir = Funcs.getCanonicalFile(workDir);
	        dir.mkdirs();
	
	        File f = Funcs.getCanonicalFile(dir, "propsJboss");
	        if (f.exists()) {
	            try {
	                FileInputStream fis = new FileInputStream(f);
	                props.load(fis);
	                fis.close();
	                String plainMode_ = props.getProperty("plainMode");
	                if ("true".equals(plainMode_))
	                    plainMode = true;
	                String isFloat_ = props.getProperty("isFloat" + ownerStr);
	                if ("true".equals(isFloat_)) {
	                    String sizeLocation_ = props.getProperty("sizeLocation" + ownerStr);
	                    if (sizeLocation_ != null && !"".equals(sizeLocation_))
	                        sizeLocation = sizeLocation_;
	                    isInspectorFloat = true;
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
        }
        JScrollPane scroller = new JScrollPane(table);
        setLayout(new BorderLayout());
        add(scroller, BorderLayout.CENTER);
        scroller.setOpaque(isOpaque);
        scroller.getViewport().setOpaque(isOpaque);
        JToolBar toolBar = kz.tamur.comps.Utils.createDesignerToolBar();
        toolBar.setLayout(new BorderLayout());
        plainBtn.setToolTipText("Режим таблицы");
        plainBtn.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    plainBtn.setToolTipText("Режим таблицы");
                    plainBtn.setIcon(kz.tamur.rt.Utils.getImageIcon("Plain"));
                } else {
                    plainBtn.setToolTipText("Режим дерева");
                    plainBtn.setIcon(kz.tamur.rt.Utils.getImageIcon("Tree"));
                }
                plainMode = e.getStateChange() == ItemEvent.SELECTED;
                table.setPlainMode(plainMode);

                String workDir = Utils.getUserWorkingDir();
                if (Funcs.isValid(workDir)) {
        	        File dir = Funcs.getCanonicalFile(workDir);

	                File f = new File(dir, "propsJboss");
	                if (f.exists()) {
	                    try {
	                        FileInputStream fis = new FileInputStream(f);
	                        props.load(fis);
	                        fis.close();
	                    } catch (IOException e1) {
	                        e1.printStackTrace();
	                    }
	                }
	                try {
	                    FileOutputStream fos = new FileOutputStream(f);
	                    props.setProperty("plainMode", plainMode ? "true" : "false");
	                    props.store(fos, "Properties");
	                    fos.close();
	                } catch (IOException e1) {
	                    e1.printStackTrace();
	                }
                }
            }
        });
        floatButton.setEnabled(owner != null && !isInspectorFloat ? true : false);
        floatButton.addActionListener(this);
        floatButton.setIcon(kz.tamur.rt.Utils.getImageIcon("FloatMode"));
        floatButton.setToolTipText("Плавающий режим");
        if (plainMode) {
            plainBtn.setSelected(plainMode);
            table.setPlainMode(plainMode);
        }
        toolBar.add(plainBtn, BorderLayout.WEST);
        toolBar.add(floatButton, BorderLayout.EAST);
        add(toolBar, BorderLayout.NORTH);
    }

    public void addObject(Inspectable obj) {
        table.addObject(obj);
    }
    
    public void setObject(Inspectable obj) {
    	int index = table.getSelectedRow();
    	Property lastSelectedProperty = null;
    	if (index > 0) {
    		int index_=table.getPropertyTableModel().getChildren().size();
    		if(index>index_)
    			index=index_;
    		lastSelectedProperty = table.getPropertyTableModel().getSelectedNode(index - 1);
    	}
        table.setObject(obj);
        if (dialog != null) {
            dialog.setTitle(obj.getTitle());
        }
        setOpaque(isOpaque);
        if (lastSelectedProperty != null) {
	        List<Property> children = table.getPropertyTableModel().getChildren();
	        index = -1;
	        for (int i = 0; i < children.size(); i++) {
	        	Property property = children.get(i);
				if (property.toString().equals(lastSelectedProperty.toString())) {
					index = i + 1;
					break;
				}
			}
        }
        if (index > -1) {
        	table.setRowSelectionInterval(index, index);
        }
    }

    /**
     * Обновление объекта в инспекторе объектов
     */
    public void updateObject(Inspectable obj) {
    	int index = table.getSelectedRow();
    	Property lastSelectedProperty = null;
    	if (index > 0) {
    		lastSelectedProperty = table.getPropertyTableModel().getSelectedNode(index - 1);
    	}
        table.updateObject(obj);
        if (dialog != null) {
            dialog.setTitle(obj.getTitle());
        }
        index = -1;
        if (lastSelectedProperty != null) {
	        List<Property> children = table.getPropertyTableModel().getChildren();
	        for (int i = 0; i < children.size(); i++) {
	        	Property property = children.get(i);
				if (property.toString().equals(lastSelectedProperty.toString())) {
					index = i + 1;
					break;
				}
			}
        }
        if (index > -1) {
        	table.setRowSelectionInterval(index, index);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == floatButton) {
            isInspectorFloat = true;
            floatButton.setEnabled(false);
            getDialog(dialogTitle);
        }
    }

    public JDialog getDialog(String title) {
        if (dialog == null) {
            dialog = new JDialog(Or3Frame.instance(), title, false) {
                protected void processWindowEvent(WindowEvent e) {
                    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
                        isInspectorFloat = false;
                        owner.setInspector(false);
                        floatButton.setEnabled(true);
                    }
                    super.processWindowEvent(e);
                }
            };
        }
        if (!"".equals(title)) {
            dialogTitle = title;
        }

        if (isInspectorFloat) {
            owner.setInspector(true);
            dialog.add(this);
            Dimension screen = new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay());
            if (screen.height > 250) {
                screen.height = 250;
            }
            dialog.setSize(screen);
            dialog.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dialog.getSize()));
            dialog.show();
        }
        return dialog;
    }

    public void processExit() {
        String workDir = Utils.getUserWorkingDir();
        if (Funcs.isValid(workDir)) {
	        File dir = Funcs.getCanonicalFile(workDir);

	        File f = new File(dir, "propsJboss");
	        Properties props = new Properties();
	        if (f.exists()) {
	            try {
	                FileInputStream fis = new FileInputStream(f);
	                props.load(fis);
	                fis.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	        }
	        try {
	            FileOutputStream fos = new FileOutputStream(f);
	            props.setProperty("isFloat" + ownerStr, isInspectorFloat ? "true" : "false");
	            if (isInspectorFloat) {
	                sizeLocation = dialog.getSize().width + "," + dialog.getSize().height + "," + dialog.getLocation().x + ","
	                        + dialog.getLocation().y;
	            } else {
	                sizeLocation = "";
	            }
	            props.setProperty("sizeLocation" + ownerStr, sizeLocation);
	            props.store(fos, "Properties");
	            fos.close();
	        } catch (IOException e1) {
	            e1.printStackTrace();
	        }
        }
    }
    
    
    public PropertyTableModel getModel() {
    	PropertyTableModel model = table.getPropertyTableModel();
    	return model;
    }
    
    public PropertyTable getPropTable() {
    	return table;
    }

    public static void main(String[] args) throws Exception {
        JFrame frm = new JFrame("Тест инспектора свойств");
        PropertyInspector inspector = new PropertyInspector(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.add(inspector, BorderLayout.CENTER);
        frm.setSize(new Dimension(400, 400));
        frm.setVisible(true);
        inspector.setObject(new TestItem());
    }

}

class TestItem implements Inspectable {

    private static Property proot;

    private static final String PROP_USER = "TestItem.user";
    private static final String PROP_DATA_PATH = "TestItem.path";
    private static final String PROP_DATA_EXPR = "TestItem.expr";
    private static final String PROP_DATA_PROC_TYPE = "TestItem.procType";

    private String path;
    private String expr;
    private ComboPropertyItem procType;

    public Property getProperties() {
        if (proot == null) {
            proot = new FolderProperty(null, null, "Свойства");

            Property user = new TreeOrExprProperty(proot, PROP_USER, "Пользователь", "User");

            Property size = new FolderProperty(proot, "Размер", "Размер");
            new StringProperty(size, "width", "Ширина");
            new StringProperty(size, "height", "Высота");

            Property data = new FolderProperty(proot, "Данные", "Данные");
            new StringProperty(data, PROP_DATA_PATH, "Путь");
            new ExprProperty(data, PROP_DATA_EXPR, "Формула");

            ComboProperty typeProp = new ComboProperty(data, PROP_DATA_PROC_TYPE, "Тип процесса");
            typeProp.addItem("01", "Процесс").addItem("02", "Подпроцесс (собств. транз.)")
                    .addItem("03", "Подпроцесс (транз. суперпроцесса)");
        }
        return proot;
    }

    public Object getValue(Property prop) {
        String id = prop.getId();

        if (PROP_DATA_PATH.equals(id)) {
            return path;

        } else if (PROP_DATA_EXPR.equals(id)) {
            return expr;

        } else if (PROP_DATA_PROC_TYPE.equals(id)) {
            return procType;
        }

        return null;
    }

    public void setValue(Property prop, Object value) {
        String id = prop.getId();

        if (PROP_DATA_PATH.equals(id)) {
            path = (String) value;

        } else if (PROP_DATA_EXPR.equals(id)) {
            expr = (String) value;

        } else if (PROP_DATA_PROC_TYPE.equals(id)) {
            procType = (ComboPropertyItem) value;
        }
    }
    
	public void setValue(Property prop, Object value, Object oldValue) {
		setValue(prop, value);
	}

    public String getTitle() {
        return "";
    }

    @Override
    public Property getNewProperties() {
        return null;
    }
    
    
}
