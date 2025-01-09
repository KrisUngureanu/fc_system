package kz.tamur.or3.client.props.inspector;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import kz.tamur.guidesigner.ButtonsFactory;

public class ObjectOrExprRendererDelegate extends JPanel implements RendererDelegate {

	public ObjectOrExprRendererDelegate() {
		setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));
		JLabel label = new JLabel("Label");
		label.setHorizontalTextPosition(SwingConstants.LEFT);
		JButton btn = ButtonsFactory.createEditorButton(ButtonsFactory.DEFAULT_EDITOR);
		btn.setToolTipText("Button");
		add(label);
		add(btn);
		setToolTipText("sdsds");
	}

	public Component getRendererComponent() {
		return this;
	}

	public void setValue(Object value) {
	}

}
