package kz.tamur.or3.client.props;

import javax.swing.JTable;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.Types;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.MenuEditorDelegate;
import kz.tamur.or3.client.props.inspector.ProcessesEditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;

public class GuiComponentProperty extends Property {

    private int guiPropertyType;
    private RendererDelegate rendererDelegate;
    private EditorDelegate editorDelegate;

    public GuiComponentProperty(Property parent, PropertyNode node, Integer guiPropertyType) {
        super(parent, node);
        this.guiPropertyType = guiPropertyType;
    }

    @Override
    public EditorDelegate createEditorDelegate(JTable table) {
        return editorDelegate;
    }

    @Override
    public RendererDelegate createRendererDelegate(JTable table) {
        if (rendererDelegate == null) {
            if (guiPropertyType == Types.PMENUITEM) {
                MenuEditorDelegate delegate = new MenuEditorDelegate(table);
                rendererDelegate = delegate;
                editorDelegate = delegate;
            } else if (guiPropertyType == Types.PROCESSES) {
                ProcessesEditorDelegate delegate = new ProcessesEditorDelegate(table);
                rendererDelegate = delegate;
                editorDelegate = delegate;
            }
        }
        return rendererDelegate;
    }

}
