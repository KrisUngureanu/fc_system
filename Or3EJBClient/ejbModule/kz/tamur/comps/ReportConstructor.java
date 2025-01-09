package kz.tamur.comps;

import com.cifs.or2.util.Funcs;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.ReportConstructorListener;
import com.cifs.or2.kernel.*;

import javax.swing.*;

import org.jdom.Element;

import java.io.*;
import java.util.StringTokenizer;
import java.awt.*;

import kz.tamur.Or3Frame;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.LangItem;
import kz.tamur.util.OpenElementPanel;
import kz.tamur.util.XmlUtil;

public class ReportConstructor implements ReportConstructorListener {
	private String xml = "";
	private String templatePath = "";
	private long[] langIds_;
	private String[] langNames_;
	private int count_;
	private String lastClass = "";
	private String lastPath = "";

	private Kernel krn;

	public ReportConstructor(Kernel krn) {
		this.krn = krn;
		getLanguages();
	}

	@Override
	public String executeCommand(ReportNote note) {
		String cmd = note.getCmd();
		
		String res = "";
		
		if ("filter".equals(cmd))
			res = showFilterBrowser();
		else if ("ref".equals(cmd))
			res = showBrowser(note.getStringParam());
		else if ("expr".equals(cmd))
			res = showExpressionEditor(note.getStringParam());
		else if ("xml".equals(cmd))
			setXml(note.getStringParam());
		else if ("file".equals(cmd))
			setFile(note.getStringParam());
		else if ("save".equals(cmd))
			save(note.getStringParam());
		else if ("langs".equals(cmd))
			res = getLangsXml();
		
		return note.getReportId() + "|" + res;
	}

	public String showFilterBrowser() {
		Or3Frame.instance().setAlwaysOnTop(true);
		Or3Frame.instance().setAlwaysOnTop(false);

		OpenElementPanel op = new OpenElementPanel(Utils.getFiltersTree());
		DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Открытие фильтра", op);
		op.getTree().requestFocusInWindow();
		dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
		dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
		dlg.show();
		if (dlg.isOK()) {
			AbstractDesignerTreeNode fn = op.getTree().getSelectedNode();
			if (fn != null && op.getNodeObj(fn) != null) {
				return op.getNodeObj(fn).uid;
			}
		}

		return "";
	}

	public String showBrowser(String path) {
		try {
			ClassNode cls = null;
			if ("".equals(path)) {
				if ("".equals(lastClass)) {
					cls = krn.getClassNodeByName("Объект");
				} else {
					cls = krn.getClassNodeByName(lastClass);
				}
			} else {
				try {
					lastClass = getClassNameFromPath(path);
					cls = krn.getClassNodeByName(lastClass);
				} catch (KrnException e) {
					MessagesFactory.showMessageDialog((JFrame) null, MessagesFactory.ERROR_MESSAGE,
							"\"" + lastClass + "\" - ошибочное имя класса!");
				}
			}

			ClassBrowser cb = new ClassBrowser(cls, true);
			if (path != null && path.length() > 0) {
				cb.setSelectedPath(path);
				lastPath = path;
			} else if (lastPath != null && lastPath.length() > 0) {
				try {
					cb.setSelectedPath(lastPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Or3Frame.instance().setAlwaysOnTop(true);
			Or3Frame.instance().setAlwaysOnTop(false);
			DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Классы", cb);
			dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
			dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
			dlg.show();
			if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
				path = cb.getSelectedPath();
				if (path != null && path.length() > 0) {
					StringTokenizer st = new StringTokenizer(path, ".");
					lastClass = st.nextToken();
				}
				lastPath = path;

				KrnAttribute[] attrs = cb.getSelectedAttributes();
				if (attrs != null && attrs.length > 0) {
					path += "|" + attrs[attrs.length - 1].isMultilingual;
				}
				return path;
			}
		} catch (Exception ex) {
			MessagesFactory.showMessageDialog((JFrame) null, MessagesFactory.ERROR_MESSAGE, ex.getMessage());
		}
		return "";
	}

	public String showExpressionEditor(String expr) {
		String result = "";
		try {
			Or3Frame.instance().setAlwaysOnTop(true);
			Or3Frame.instance().setAlwaysOnTop(false);
	    	
			ExpressionEditor editor = new ExpressionEditor(Funcs.reverseXmlQuote(expr));
			DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Редактор формул", editor);
			dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
			dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
			dlg.show();
			if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
				result = editor.getExpression();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			MessagesFactory.showMessageDialog((JFrame) null, MessagesFactory.ERROR_MESSAGE, ex.getMessage());
		}
		return Funcs.xmlQuote(result);
	}

	public void setXml(String value) {
		this.xml = value;
		System.out.println(value);
	}

	public void setFile(String value) {
		this.templatePath = value;
	}

	public void save(String pars) {
		String[] params = pars.split("\\|");
		long id = Long.parseLong(params[0]);
		long lid = Long.parseLong(params[1]);
		try {
			final KrnClass cls = krn.getClassByName("ReportPrinter");
			File f = kz.tamur.util.Funcs.getCanonicalFile(templatePath);
			byte[] b = kz.tamur.util.Funcs.read(f);
			krn.setBlob(id, cls.id, "data", 0, xml.getBytes("UTF-8"), lid, 0);
			krn.setBlob(id, cls.id, "template", 0, b, lid, 0);
		} catch (Exception ex) {
			MessagesFactory.showMessageDialog((JFrame) null, MessagesFactory.ERROR_MESSAGE, ex.getMessage());
		}
	}

	private String getClassNameFromPath(String path) {
		StringTokenizer st = new StringTokenizer(path, ".");
		return st.nextToken();
	}

	private void getLanguages() {
		count_ = 0;
		java.util.List<LangItem> langs =  LangItem.getAll();
		langIds_ = new long[langs.size()];
		langNames_ = new String[langs.size()];

		for (int i = 0; i < langs.size(); i++) {
			langNames_[i] = langs.get(i).name;
			langIds_[i] = langs.get(i).obj.id;
		}
		count_ = langIds_.length;
	}

	private String getLangsXml() {
		Element res = new Element("langs");
		Element count = new Element("count");
		count.setText(String.valueOf(count_));
		res.addContent(count);

		for (int i = 0; i < count_; i++) {
			Element lang = new Element("lang");
			Element name = new Element("name");
			name.setText(langNames_[i]);
			lang.addContent(name);
			Element id = new Element("id");
			id.setText(String.valueOf(langIds_[i]));
			lang.addContent(id);
			res.addContent(lang);
		}

		try {
			byte[] b = XmlUtil.write(res);
	        return new String(b, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "<langs><count>0</count></langs>";
	}
}
