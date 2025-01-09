package kz.tamur.or3.client.props.inspector;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;

import kz.tamur.or3.client.props.ExprProperty;
import kz.tamur.or3.client.props.Inspectable;
import kz.tamur.or3.client.props.Property;
import kz.tamur.rt.MainFrame;
import other.treetable.JTreeTable;
import other.treetable.TreeTableModel;
import other.treetable.TreeTableModelAdapter;

public class PropertyTable extends JTreeTable {
	
	private Inspectable obj;
	private PropertyTableModel model = new PropertyTableModel(null);
	private Property prop;
	private PropertyEditor editor;
	private Map<String, EditorDelegate> editorDelegates = new HashMap<String, EditorDelegate>();

	private PropertyRenderer renderer;
	private Map<String, RendererDelegate> rendererDelegates = new HashMap<String, RendererDelegate>();
	
	private boolean plainMode = false;
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
	 
    public PropertyTable() {
        setFont(kz.tamur.rt.Utils.getDefaultFont());
        setModel(model);
        getTableHeader().setReorderingAllowed(false);
        editor = new PropertyEditor(this);
        renderer = new PropertyRenderer(this);
    }
	
    public PropertyEditor getEditor() {
    	return editor;
    }
    
    
	public Inspectable getObject() {
		return obj;
	}
	
	public void addObject(Inspectable obj) {
        model.addObject(obj);
	}
	
	public void setObject(Inspectable obj) {
		this.obj = obj;
        editor.cancelCellEditing();
        model.setObject(obj);
        setModel(model);
	}
	
    /**
     * Обновление свойств объекта в таблице свойств
     */
    public void updateObject(Inspectable obj) {
        this.obj = obj;
        editor.cancelCellEditing();
        model.updateObject(obj);
        setModel(model);
    }
	
	public void setPlainMode(boolean plainMode) {
		this.plainMode = plainMode;
		renderer.setPlainMode(plainMode);
		model.setPlainMode(plainMode);
	}
	
	@Override
	public void setModel(TreeTableModel treeTableModel) {
        // Create the tree. It will be used as a renderer and editor.
        tree = new PropertyTreeRenderer(treeTableModel);

        // Force the JTable and JTree to share their row selection models.
        ListToTreeSelectionModelWrapper selectionWrapper = new
                                ListToTreeSelectionModelWrapper();
        tree.setSelectionModel(selectionWrapper);
        setSelectionModel(selectionWrapper.getListSelectionModel());

        // Install a tableModel representing the visible rows in the tree.
        super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

        setDefaultRenderer(TreeTableModel.class, tree);

        // And update the height of the trees row to match that of
        // the table.
        if (tree.getRowHeight() < 1) {
            // Metal looks better like this.
            setRowHeight(18);
        }
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		prop = (Property)getModel().getValueAt(row, 0);
		if (column == 1) {
			EditorDelegate delegate = getEditorDelegate(prop);
			editor.setDelegate(delegate);
			return editor;
		}
		return super.getCellEditor(row, column);
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == 1) {
			Property prop = (Property)getModel().getValueAt(row, 0);
			RendererDelegate delegate = getRendererDelegate(prop);
			renderer.setDelegate(delegate);
			return renderer;
		} else if (column == 0 && plainMode) {}
		return super.getCellRenderer(row, column);
	}

    public EditorDelegate getEditorDelegate(Property prop) {
        if (prop != null) {
            String propId = prop.getId();
            if (propId != null) {
                if (!editorDelegates.containsKey(propId)) {
                	if(prop instanceof ExprProperty) {
                		editorDelegates.put(propId, ((ExprProperty)prop).createEditorDelegate(this, prop));
                	}
                	else 
                		editorDelegates.put(propId, prop.createEditorDelegate(this));
                }
                return editorDelegates.get(propId);
            }
        }
        return null;
    }

    private RendererDelegate getRendererDelegate(Property prop) {
        if (prop != null) {
            String propId = prop.getId();
            if (propId != null) {
                if (!rendererDelegates.containsKey(propId)) {
                    rendererDelegates.put(propId, prop.createRendererDelegate(this));
                }
                return rendererDelegates.get(propId);
            }
        }
        return null;
    }
	
	private class PropertyTreeRenderer extends TreeTableCellRenderer {

        private Color secondColor = kz.tamur.rt.Utils.getLightSysColor();
        private Color firstColor = PropertyTable.this.getBackground();

        public PropertyTreeRenderer(TreeModel model) {
			super(model);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			Component comp = super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);

			DefaultTreeCellRenderer dtcr = (DefaultTreeCellRenderer)getCellRenderer();
			dtcr.setBackgroundNonSelectionColor(firstColor);

			if (plainMode && !isSelected && value instanceof Property) {
				Property folder = ((Property)value).getParent();
				if (folder != null) {
					Property root = folder.getParent();
					if (root != null) {
						int i = root.getChildren().indexOf(folder);
						if (i != -1) {
							if ((i % 2) == 0) {
								dtcr.setBackgroundNonSelectionColor(secondColor);
								comp.setBackground(secondColor);
							}
						}
					}
				}
			}
			return comp;
			
		}
		
	}
	
	public PropertyTableModel getPropertyTableModel() {
		return model;
	}
	
	public Property getCurentProperty(){
		return prop;
	}
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
        boolean res = super.processKeyBinding(ks, e, condition, pressed);
        TableCellEditor ed = getCellEditor();
        if (ed instanceof PropertyEditor) {
            PropertyEditor ped = (PropertyEditor) ed;

            EditorDelegate edd = ped.getDelegate();
            if (edd instanceof ComboEditorDelegate) {
                ComboEditorDelegate ced = (ComboEditorDelegate)edd;
                if (!ced.hasFocus()) {
                    ced.showPopup();
                    ced.requestFocusInWindow();
                }
            }
        }
        return res;
    }
}