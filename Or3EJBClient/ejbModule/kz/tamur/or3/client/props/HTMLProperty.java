package kz.tamur.or3.client.props;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.HTMLEditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;

import javax.swing.*;

/**
 * The Class HTMLProperty.
 */
public class HTMLProperty extends Property {

	/**
	 * Instantiates a new hTML property.
	 *
	 * @param parent the parent
	 * @param id the id
	 * @param title the title
	 */
	public HTMLProperty(Property parent, PropertyNode node) {
		super(parent, node);
	}

	
	public EditorDelegate createEditorDelegate(JTable table) {
		 return new HTMLEditorDelegate(table); 
		
	}

	
	public RendererDelegate createRendererDelegate(JTable table) {
		return new HTMLEditorDelegate(table); 
	}

}
