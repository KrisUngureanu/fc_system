package kz.tamur.comps;

import com.cifs.or2.util.Funcs;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.ClientCallback;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.*;

import javax.swing.*;

import java.io.*;
import java.net.InetAddress;
import java.util.StringTokenizer;
import java.awt.*;

import kz.tamur.Or3Frame;
import kz.tamur.admin.ClassBrowser;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.or3ee.server.session.SessionOpsOperations;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.ExpressionEditor;
import kz.tamur.util.OpenElementPanel;

public class Report implements Serializable {
    public boolean isInited = false;
    public String Sved = "";
    public String DocPathFile = "";
    public long[] langIds_;
    public String[] langNames_;
    private int count_;
    private String lastClass = "";
    private String lastPath = "";
    private boolean isMultiLingual;
    public int langId = 0;
    
    private String baseName;
    private String jndiInitial;
    private String jndiPkgs;
    private String jndiUrl;
	private String userName;
	private String pd;

    public Report() {
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
    }

    private void init() {
        try {
        	if (Thread.currentThread().getContextClassLoader() == null)
        		Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
        	
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();
            String pcName = address.getHostName();

            SessionOpsOperations ops = Or3Frame.lookup(jndiInitial, kz.tamur.util.Funcs.sanitizeLDAP(jndiPkgs), Integer.parseInt(jndiUrl), "Or3EAR", true);
            Kernel.instance().init(userName, pd, null, null, jndiPkgs, jndiUrl, baseName, Constants.CLIENT_TYPE_REPORT, ip, pcName, 0, ops);

            ClientCallback callback = (ClientCallback) Kernel.instance().getCallback();
            callback.setFrame(null);
            callback.setTypeClient(Constants.CLIENT_TYPE_REPORT);
            callback.start();

            isInited = true;
            getLanguages();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessagesFactory.showMessageDialog((JFrame) null,
                    MessagesFactory.ERROR_MESSAGE, ex.getMessage());
        }
    }
    
    public void setUserName(String name) {
    	this.userName = name;
        System.out.println("Юзер: " + userName);
    }

    public void setPassword(String pd) {
    	this.pd = pd;
    }

    public void setJndiInitial(String jndiInitial) {
        // Реально тип сервера
        this.jndiInitial = jndiInitial;
        System.out.println("Тип сервера: " + jndiInitial);
    }

    public void setJndiPkgs(String jndiPkgs) {
        // Реально host
    	this.jndiPkgs = jndiPkgs;
        System.out.println("Адрес: " + jndiPkgs);
    }

    public void setJndiUrl(String jndiUrl) {
        // Реально port
    	this.jndiUrl = jndiUrl;
        System.out.println("Порт: " + jndiUrl);
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
        System.out.println("База: " + baseName);
    }

    public void Release() {
/*        try {
            connChecker.interrupt();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
*/        try {
            Kernel.instance().release();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public String showFilterBrowser() {
        OpenElementPanel op = new OpenElementPanel(Utils.getFiltersTree());
        DesignerDialog dlg = new DesignerDialog((JFrame) null, "Открытие фильтра", op);
        op.getTree().requestFocusInWindow();
        dlg.show();
        if (dlg.isOK()) {
            AbstractDesignerTreeNode fn = op.getTree().getSelectedNode();
            if (fn != null && op.getNodeObj(fn) != null) {
                return op.getNodeObj(fn).uid;
            }
        }

        return "";
    }

    public String ShowBrowser(String path) {
        try {
            if (!isInited) init();
            Kernel krn = Kernel.instance();
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
                    MessagesFactory.showMessageDialog((JFrame) null,
                            MessagesFactory.ERROR_MESSAGE, "\"" + lastClass +
                            "\" - ошибочное имя класса!");
                }
            }
            
            ClassBrowser cb = new ClassBrowser(cls, true);
            if (path != null && path.length() > 0) {
                cb.setSelectedPath(path);
                lastPath = path;
            } else if (lastPath != null && lastPath.length() > 0) {
                cb.setSelectedPath(lastPath);
            }

            DesignerDialog dlg = new DesignerDialog((JFrame) null, "Классы", cb);
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
                    isMultiLingual = attrs[attrs.length - 1].isMultilingual;
                }
                return path;
            }
        } catch (Exception ex) {
            MessagesFactory.showMessageDialog((JFrame) null,
                    MessagesFactory.ERROR_MESSAGE, ex.getMessage());
        }
        return "";
    }

    public String ShowExpressionEditor(String expr) {
        String result = "";
        try {
            if (!isInited) init();
            ExpressionEditor editor = new ExpressionEditor(Funcs.reverseXmlQuote(expr));
            DesignerDialog dlg = new DesignerDialog((JFrame) null, "Редактор формул", editor);
            dlg.setSize(new Dimension(kz.tamur.comps.Utils.getMaxWindowSizeActDisplay()));
            dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
            dlg.show();
            if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                result = editor.getExpression();
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
            MessagesFactory.showMessageDialog((JFrame) null,
                    MessagesFactory.ERROR_MESSAGE, ex.getMessage());
        }
        return Funcs.xmlQuote(result);
    }

    public void AddSved(String value) {
        Sved = Sved + value + "|";
    }

    public void setSved(String value) {
        Sved = value;
        System.out.println(Sved);
    }

    public void AddFile(String value) {
        DocPathFile = value;
    }

    public void Post(int id) {
        try {
            if (!isInited) init();
            final KrnClass cls = Kernel.instance().getClassByName("ReportPrinter");
            File f = new File(DocPathFile);
            FileInputStream DocFile = new FileInputStream(DocPathFile);
            byte[] buff = new byte[(int) f.length()];
            DocFile.read(buff);
            DocFile.close();
            Kernel.instance().setBlob(id, cls.id, "data", 0, Sved.getBytes(), langId, 0);
            Kernel.instance().setBlob(id, cls.id, "template", 0, buff, langId, 0);
            //Kernel.instance().release();
        } catch (Exception ex) {
            MessagesFactory.showMessageDialog((JFrame) null,
                    MessagesFactory.ERROR_MESSAGE, ex.getMessage());
        }
    }

    private String getClassNameFromPath(String path) {
        StringTokenizer st = new StringTokenizer(path, ".");
        return st.nextToken();
    }
    
    protected void getLanguages() {
        count_ = 0;
        try {
            Kernel krn = Kernel.instance();
            KrnObject[] langs_ = Kernel.LANGUAGES;
            langIds_ = new long[langs_.length];
            langNames_ = new String[langs_.length];

            for (int i = 0; i < langs_.length; i++) {
                String[] s = krn.getStrings(langs_[i], "name", 0, 0);
                if (s.length > 0) langNames_[i] = s[0];
                langIds_[i] = langs_[i].id;
            }
            count_ = langIds_.length;

        } catch (KrnException ex) {
            MessagesFactory.showMessageDialog((JFrame) null,
                    MessagesFactory.ERROR_MESSAGE, ex.getMessage());
        }
    }

    public int getLangsCount() {
        if (!isInited) init();
        return count_;
    }

    public long getLangId(int i) {
        if (!isInited) init();
        return langIds_[i];
    }

    public String getLangName(int i) {
        if (!isInited) init();
        return langNames_[i];
    }

    public boolean isMultiLingual() {
        return isMultiLingual;
    }

    public void setLangId(int langId) {
        this.langId = langId;
    }

	public String getBaseName() {
		return baseName;
	}

	public String getJndiInitial() {
		return jndiInitial;
	}

	public String getJndiPkgs() {
		return jndiPkgs;
	}

	public String getJndiUrl() {
		return jndiUrl;
	}
}
