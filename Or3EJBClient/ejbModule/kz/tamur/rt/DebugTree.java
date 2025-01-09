package kz.tamur.rt;

import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

import kz.tamur.comps.Constants;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.awt.*;
import java.io.ByteArrayInputStream;

/**
 * Created by IntelliJ IDEA. User: Vale Date: 26.05.2005 Time: 15:02:40 To
 * change this template use File | Settings | File Templates.
 */
public class DebugTree extends JTree {

	protected DefaultTreeModel model;
	private boolean isDebug = false;
	private long flowId;
	private long trId;
	
	public DebugTree(Element xml, long flowId, long trId, boolean isDebug) {
		super();
		this.isDebug = isDebug;
		this.flowId = flowId;
		this.trId = trId;
		DefaultMutableTreeNode root = loadNode(xml);
		model = new DefaultTreeModel(root);
		setModel(model);
		setRootVisible(true);
		setCellRenderer(new CellRenderer());
		setBackground(kz.tamur.rt.Utils.getLightGraySysColor());
	}
	private DefaultMutableTreeNode loadNode(Element xml) {
		StringBuilder rootTitle = new StringBuilder("debug(flowId:");
		rootTitle.append(flowId);
		if (trId != 0)
			rootTitle.append(",trId:").append(trId);
		rootTitle.append(')');
		DefaultMutableTreeNode res = new DefaultMutableTreeNode(rootTitle.toString());
		if (isDebug) {
			List nodes = xml.getChildren("node");
			for (int i = 0; i < nodes.size(); ++i) {
				Element e = (Element) nodes.get(i);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(e
						.getText());
				res.insert(node, i);
				List events = e.getChildren("event");
				for (int j = 0; j < events.size(); ++j) {
					Element ee = (Element) events.get(j);
					DefaultMutableTreeNode event = new DefaultMutableTreeNode(
							ee.getText());
					node.insert(event, j);
					List vars = ee.getChildren("var");
					for (int k = 0; k < vars.size(); ++k) {
						Element eee = (Element) vars.get(k);
						String type = eee.getAttributeValue("type");
						String name = eee.getAttributeValue("name");
						String value = name + "(" + type + ")";
						DefaultMutableTreeNode var;
						if ("element".equals(type)) {
							var = new DefaultMutableTreeNode(value);
							try {
								SAXBuilder builder = new SAXBuilder();
								Document doc = builder.build(
										new ByteArrayInputStream(eee.getText()
												.getBytes("UTF-8")), "UTF-8");
								Element root = doc.getRootElement();
								parsXml(root, var);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						} else if ("map".equals(type)) {
							List vs = eee.getChildren("pair");
							var = new DefaultMutableTreeNode(value);
							for (int l = 0; l < vs.size(); ++l) {
								Element pair = (Element) vs.get(l);
								Element key = pair.getChild("key");
								String key_s = key.getText();
								Element val = pair.getChild("value");
								String val_s = val.getText();
								DefaultMutableTreeNode var_ = new DefaultMutableTreeNode(
										"key=" + key_s + ";value=" + val_s);
								var.insert(var_, l);
							}
						} else if ("list".equals(type)) {
							List vs = eee.getChildren("value");
							var = new DefaultMutableTreeNode(value);
							for (int l = 0; l < vs.size(); ++l) {
								Element val = ((Element) vs.get(l))
										.getChild("value");
								String val_s = val.getText();
								DefaultMutableTreeNode var_ = new DefaultMutableTreeNode(
										"value=" + val_s);
								var.insert(var_, l);
							}
						} else {
							value += "=" + eee.getText();
							var = new DefaultMutableTreeNode(value);
						}
						event.insert(var, k);
					}
				}
			}
		} else {
			List vars = xml.getChildren("var");
			for (int k = 0; k < vars.size(); ++k) {
				Element eee = (Element) vars.get(k);
				String type = eee.getAttributeValue("type");
				String name = eee.getAttributeValue("name");
				String value = name + "(" + type + ")";
				DefaultMutableTreeNode var;
				if ("element".equals(type)) {
					var = new DefaultMutableTreeNode(value);
					try {
						SAXBuilder builder = new SAXBuilder();
						Document doc = builder.build(new ByteArrayInputStream(
								eee.getText().getBytes("UTF-8")), "UTF-8");
						Element root = doc.getRootElement();
						parsXml(root, var);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else if ("map".equals(type)) {
					List vs = eee.getChildren("pair");
					var = new DefaultMutableTreeNode(value);
					for (int j = 0; j < vs.size(); ++j) {
						Element pair = (Element) vs.get(j);
						Element key = pair.getChild("key");
						String key_s = key.getText();
						Element val = pair.getChild("value");
						String val_s = val.getText();
						DefaultMutableTreeNode var_ = new DefaultMutableTreeNode(
								"key=" + key_s + ";value=" + val_s);
						var.insert(var_, j);
					}
				} else if ("list".equals(type)) {
					List vs = eee.getChildren("value");
					var = new DefaultMutableTreeNode(value);
					for (int j = 0; j < vs.size(); ++j) {
						Element val = (Element) vs.get(j);
						String val_s = val.getText();
						DefaultMutableTreeNode var_ = new DefaultMutableTreeNode(
								"value=" + val_s);
						var.insert(var_, j);
					}
				} else {
					value += "=" + eee.getText();
					var = new DefaultMutableTreeNode(value);
				}
				res.insert(var, k);
			}
		}
		normalizer(res);
		return res;
	}
	private void normalizer(DefaultMutableTreeNode node) {
        ArrayList children = Collections.list(node.children());
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> cNames = new ArrayList<String>();
        DefaultMutableTreeNode temParent = new DefaultMutableTreeNode();
        for(Object child:children) {
            DefaultMutableTreeNode ch = (DefaultMutableTreeNode)child;
            temParent.insert(ch,0);
            cNames.add(ch.toString().toUpperCase(Constants.OK));
            names.add(ch.toString().toUpperCase(Constants.OK));
        }
        Collections.sort(cNames);
        for(String name:cNames) {
            int indx = names.indexOf(name);
            node.add((DefaultMutableTreeNode)children.get(indx));
        }
    }
	private void parsXml(Element xml, DefaultMutableTreeNode parent) {
		List vars = xml.getContent();
		int sz = vars.size();
		for (int i = 0; i < vars.size(); ++i) {
			DefaultMutableTreeNode var = null;
			Content cnt = (Content) vars.get(i);
			if (cnt instanceof Element) {
				Element e = (Element) cnt;
				String name = e.getName();
				String pfx = e.getNamespacePrefix();
				if (pfx.length() > 0) {
					name = pfx + ":" + name;
				}
				var = new DefaultMutableTreeNode(name);
				parsXml(e, var);
			} else if (cnt instanceof Text) {
				Text t = (Text) cnt;
				if (sz == 1) {
					String title = (String)parent.getUserObject();
					parent.setUserObject(title + "=" + t.getTextTrim());
				} else {
					var = new DefaultMutableTreeNode(t.getTextTrim());
				}
			}
			if (var != null) {
				parent.add(var);
			}
		}
	}
	private class CellRenderer extends JLabel implements TreeCellRenderer {

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			setOpaque(true);
			if (hasFocus && selected) {
				setBackground(kz.tamur.rt.Utils.getDarkShadowSysColor());
				setForeground(Color.white);
			} else {
				setBackground(kz.tamur.rt.Utils.getLightGraySysColor());
				setForeground(Color.black);
			}
			if (!leaf) {
				if (expanded) {
					setIcon(kz.tamur.rt.Utils.getImageIcon("Open"));
				} else {
					setIcon(kz.tamur.rt.Utils.getImageIcon("CloseFolder"));
				}
			} else {
				setIcon(null);
			}
			setFont(kz.tamur.rt.Utils.getDefaultFont());
			setText(value.toString());
			return this;
		}

	}
}
