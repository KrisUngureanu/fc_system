package kz.tamur.or3.client.props.inspector;

import java.awt.Component;

public interface RendererDelegate {

	void setValue(Object value);
	Component getRendererComponent();
}
