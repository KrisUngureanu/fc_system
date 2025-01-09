package kz.tamur.or3.client.props;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.GradientColorEditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;

import javax.swing.*;

/**
 * The Class GradientColorProperty.
 * @author Sergey Lebedev
 */
public class GradientColorProperty extends Property {

	/**
	 * Создание нового gradient color property.
	 *
	 * @param parent the parent
	 * @param id the id
	 * @param title the title
	 */
	public GradientColorProperty(Property parent, PropertyNode node) {
		super(parent, node);
	}

	
	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new GradientColorEditorDelegate(table);
	}

	
	@Override
	public RendererDelegate createRendererDelegate(JTable table) {
		return new GradientColorEditorDelegate(table);
	}

}
