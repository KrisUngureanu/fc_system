package kz.tamur.or3.client.props;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.inspector.EditorDelegate;
import kz.tamur.or3.client.props.inspector.RendererDelegate;
import kz.tamur.or3.client.props.inspector.ReportEditorDelegate;

import javax.swing.*;

public class ReportProperty extends Property {

	public ReportProperty(Property parent, PropertyNode node) {
		super(parent, node);
	}

	@Override
	public EditorDelegate createEditorDelegate(JTable table) {
		return new ReportEditorDelegate(table);
	}

	@Override
	public RendererDelegate createRendererDelegate(JTable table) {
		return new ReportEditorDelegate(table);
	}

}
