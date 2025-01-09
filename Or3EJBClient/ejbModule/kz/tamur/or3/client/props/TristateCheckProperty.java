package kz.tamur.or3.client.props;

import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;
import kz.tamur.or3.client.props.inspector.TristateCheckEditorDelegate;

import javax.swing.*;

/**
 * The Class TristateCheckProperty.
 * Данный класс реализует свойсво, в виде обычного чекбокса, но с программмной возможностью установить ему третье(неопределённое) состояние
 * 
 * @author Sergey Lebedev
 */
public class TristateCheckProperty extends Property {

    /**
     * Создание нового tristate check property.
     * 
     * @param parent
     *            the parent
     * @param id
     *            the id
     * @param title
     *            the title
     */
    public TristateCheckProperty(Property parent, String id, String title) {
        super(parent, id, title);
    }

    public RendererDelegate createRendererDelegate(JTable table) {
        return new TristateCheckEditorDelegate(table);
    }

    public EditorDelegate createEditorDelegate(JTable table) {
        return new TristateCheckEditorDelegate(table);
    }

}
