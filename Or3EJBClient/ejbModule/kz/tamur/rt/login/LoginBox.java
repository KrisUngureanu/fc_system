package kz.tamur.rt.login;

import kz.tamur.common.CommonFileFilter;
import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.comps.ui.comboBox.OrAutoComboBox;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.guidesigner.*;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.CENTER;
import static kz.tamur.rt.Utils.getImageIcon;

public class LoginBox extends JDialog implements ActionListener {

    public int result = -1;
    private boolean isRuntime;
    private Element xml;

    private ResourceBundle resource;
    private JLabel lblServer = new JLabel();
    private JLabel lblUser = new JLabel();
    private JLabel lblKeyFile = new JLabel();
    private JLabel lblPd = new JLabel();
    private JTextField edtKeyFile = new JTextField();
    private JPasswordField edtPdFld = new JPasswordField("");
    private java.util.List<String> userList = new ArrayList<String>();
    private OrAutoComboBox autoUserName = new OrAutoComboBox(userList);

    private JButton btnChoose = new JButton();
    private JButton btnOK = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_OK);
    private JButton btnCancel = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_CANCEL);
    private boolean isQuickStartShow = true;
    private String currLang = "RU";
    private String currDataLang = "RU";
    private Properties props = new Properties();
    private String selSrv = "";
    private String sel_srv;
    private String serverType = "";
    private String host = "";
    private String port = "";
    private String baseName = "";
    private String webUrl = "";
    private String earName = "";
    private String initPath = "";
    private ServerTree srvTree;
    private static Font font = new Font("Tahoma", 0, 12);
    private File dir = new File(Utils.getUserWorkingDir());
    // Объявление лейбла для вывода заставки
    private JLabel lblImage = new JLabel();
    /** Кнопка для выбора и отображения текущего сервера. */
    private OrTransparentButton lblChoose = new OrTransparentButton() {
        @Override
        public void setText(String text) {
            if (text == null || text.isEmpty()) {
                text = "-----------";
            }
            super.setText(text);
        }
        
    };

    private static ImageIcon LoginImg_ = kz.tamur.rt.Utils.getImageIconJpg("LoginBox");

    public static final boolean LOGIN_WITH_ECP = "1".equals(System.getProperty("ecp_login"));

    private void jbInit() throws Exception {
        setIconImage(getImageIcon("icon").getImage());
        File dir = new File(Utils.getUserWorkingDir());
        dir.mkdirs();

        File f = new File(dir, "propsJboss");
        File fs = new File(dir, "serversJboss.xml");
        
        sel_srv = Funcs.getSystemProperty("selSrv");
        sel_srv = ("1".equals(sel_srv) || "1".equals(sel_srv)) ? "1" : "0";

        if (f.exists()) {
            try {
                FileInputStream fis = new FileInputStream(f);
                props.load(fis);
                fis.close();
                initUserList();
                isQuickStartShow = "1".equals(props.getProperty("quickstart"));
                String currlang_ = props.getProperty("currlang");
                if (currlang_ != null)
                    currLang = currlang_;
                String currdatalang_ = props.getProperty("currdatalang");
                if (currdatalang_ != null)
                    currDataLang = currdatalang_;
                edtPdFld.requestFocusInWindow();
                if ("1".equals(sel_srv)) {
                    selSrv = props.getProperty("selSrv");
                    if (fs.exists()) {
                        xml = getXmlFromFile(fs);
                    } else {
                        xml = createXml();
                    }
                    Element e = (Element) XPath.selectSingleNode(xml, "*[@name='" + selSrv + "']");
                    if (e == null)
                        e = (Element) XPath.selectSingleNode(xml, "//*[@name='" + selSrv + "']");
                    if (e != null) {
                        serverType = e.getAttributeValue("serverType");
                        host = e.getAttributeValue("host");
                        port = e.getAttributeValue("port");
                        baseName = e.getAttributeValue("baseName");
                        webUrl = e.getAttributeValue("webUrl");
                        earName = e.getAttributeValue("ear");
                        initUserList();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        resource = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale(isRuntime ? "RU".equals(currLang) ? "ru" : "kk"
                : "ru"));

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setTitle(resource.getString("loginTitle"));
        this.getRootPane().setDefaultButton(btnOK);

        lblChoose.setOpaque(false);
        lblChoose.addActionListener(this);
        lblChoose.addMouseListener(new MouseListener() {
            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
                lblChoose.setForeground(Utils.getLightSysColor());
            }

            public void mouseEntered(MouseEvent e) {
                lblChoose.setForeground(Color.BLACK);
            }

            public void mouseClicked(MouseEvent e) {
            }
        });

        lblChoose.setFont(font);
        lblChoose.setForeground(Utils.getLightSysColor());
        lblChoose.setHorizontalAlignment(JLabel.LEFT);

        lblServer.setText("Сервер:");
        lblServer.setFont(Utils.getDefaultFont());
        lblServer.setForeground(Utils.getLightSysColor());
        lblServer.setHorizontalAlignment(JLabel.RIGHT);

        lblUser.setText(resource.getString("login"));
        lblUser.setFont(Utils.getDefaultFont());
        lblUser.setForeground(Utils.getLightSysColor());
        lblUser.setHorizontalAlignment(JLabel.RIGHT);

        if (LOGIN_WITH_ECP) {
            lblKeyFile.setText(resource.getString("key-choose-label"));
            lblKeyFile.setFont(Utils.getDefaultFont());
            lblKeyFile.setForeground(Utils.getLightSysColor());
            lblKeyFile.setHorizontalAlignment(JLabel.RIGHT);

            edtKeyFile.setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor())); // TODO убрать в новом UI для TextField
            edtKeyFile.setFont(font);
            edtKeyFile.setPreferredSize(new Dimension(150, 20));
            
            btnChoose.setText("...");
            btnChoose.setBackground(Utils.getSysColor());
            btnChoose.setFont(font);
            btnChoose.setPreferredSize(new Dimension(10, 20));
            btnChoose.setToolTipText(resource.getString("key-choose-tooltip"));
            btnChoose.addActionListener(this);
        }

        lblPd.setText(resource.getString("password"));
        lblPd.setFont(Utils.getDefaultFont());
        lblPd.setForeground(Utils.getLightSysColor());
        lblPd.setHorizontalAlignment(JLabel.RIGHT);

        lblImage.setIcon(LoginImg_);

        autoUserName.setFont(font);
        autoUserName.addKeyListener(new com.cifs.or2.client.gui.OrKazakhAdapter());
        autoUserName.setPreferredSize(new Dimension(150, 20));

        edtPdFld.setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor()));
        edtPdFld.setFont(new Font("Tahoma", 1, 12));
        edtPdFld.addKeyListener(new com.cifs.or2.client.gui.OrKazakhAdapter());
        // закрыть форму ввода пароля при нажатии на кнопку ESC
        edtPdFld.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        btnCancel.doClick();
                    }
                }
            }
        });
        edtPdFld.setPreferredSize(new Dimension(150, 20));
        
        btnOK.setBackground(Color.white);
        btnOK.setIcon(getImageIcon("checkOk"));
        btnOK.addActionListener(this);

        btnCancel.setText(resource.getString("cancel"));
        btnCancel.setIcon(getImageIcon("Delete"));
        btnCancel.addActionListener(this);
        lblImage.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 1, 1, 1, 1, 0, CENTER, HORIZONTAL, new Insets(10, 0, 0, 10), 0, 0);
        if ("1".equals(sel_srv)) {
            autoUserName.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                }

                public void focusLost(FocusEvent e) {
                    try {
                        Element el = (Element) XPath.selectSingleNode(xml, "*[@name='" + selSrv + "']");
                        if (el == null) {
                            el = (Element) XPath.selectSingleNode(xml, "//*[@name='" + selSrv + "']");
                        }
                        if (el != null) {
                            el.setAttribute(isRuntime ? "login" : "dlogin", autoUserName.getText());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            lblChoose.setText(selSrv);

            lblImage.add(new JLabel(" "), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0;
            lblImage.add(lblServer, gbc);
            gbc.gridx = 2;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.fill = GridBagConstraints.NONE;
            lblImage.add(lblChoose, gbc);
            gbc.anchor = CENTER;
            gbc.fill = HORIZONTAL;
        }
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        lblImage.add(new JLabel(" "), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        lblImage.add(lblUser, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        lblImage.add(autoUserName, gbc);

        int i = 3;
        if (LOGIN_WITH_ECP) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.weightx = 1;
            lblImage.add(new JLabel(" "), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0;
            lblImage.add(lblKeyFile, gbc);
            gbc.gridx = 2;
            gbc.gridwidth = 2;
            lblImage.add(edtKeyFile, gbc);
            gbc.gridx = 4;
            gbc.gridwidth = 1;
            gbc.weightx = 1;
            lblImage.add(btnChoose, gbc);
            i++;
        }
        gbc.gridx = 0;
        gbc.gridy = i;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        lblImage.add(new JLabel(" "), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        lblImage.add(lblPd, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        lblImage.add(edtPdFld, gbc);
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(40, 0, 0, 5);
        lblImage.add(btnOK, gbc);
        gbc.gridx = 3;
        lblImage.add(btnCancel, gbc);
        
        getContentPane().add(lblImage);
        pack();
        setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
        edtPdFld.requestFocusInWindow();
        
        String pd = Funcs.getSystemProperty("password");
        
        if (pd != null && Funcs.isValid(pd)) {
            edtPdFld.setText(pd);
            btnOK.requestFocusInWindow();
        }
    }

    /**
     * Создание нового окна авторизации
     * 
     * @param title
     *            заголовок окна
     * @param isRuntime
     *            режим выполнения?
     */
    public LoginBox(JFrame owner, boolean isRuntime) {
        super(owner, true);
        /**
         * добавление слушателя для отслеживания позиции окна на мониторах
         */
        addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                kz.tamur.comps.Utils.isChangeScreen(e);
            }
        });
        this.isRuntime = isRuntime;
        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getUserName() {
        return autoUserName.getText();
    }

    public String getPassword() {
         return new String(edtPdFld.getPassword());
    }

    public String getKeyFilePath() {
    	try {
    		return (LOGIN_WITH_ECP) ? Funcs.getCanonicalFile(edtKeyFile.getText()).getCanonicalPath() : "";
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return null;
    }

    /**
     * Очищает содержимое поля ввода пароля и передаёт ему фокус
     * Используется при неправильном вводе пароля
     * 
     * @return
     */
    public void clearPassword() {
        edtPdFld.setText("");
        edtPdFld.requestFocusInWindow();
    }

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            result = ButtonsFactory.BUTTON_CANCEL;
            dispose();
            endModal();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == lblChoose) {
            Element xml_ = xml != null ? (Element) xml.clone() : createXml();
            ServerParams selSrvParams = new ServerParams(this, currLang, resource, xml_);

            selSrvParams.setVisible(true, selSrv);
            edtPdFld.requestFocusInWindow();
            
            
            String srv = selSrvParams.getServer();
            if (srv != null && !srv.equals(selSrv)) {
                lblChoose.setText(selSrv = srv);
            }
            if (selSrvParams.getResult()) {
                this.xml = xml_;
                srvTree = selSrvParams.getServerTree();
                ServerNode node = (ServerNode) srvTree.getSelectedNode();
                if (node != null) {
                    Element el = node.getXml();
                    serverType = el.getAttributeValue("serverType");
                    host = el.getAttributeValue("host");
                    port = el.getAttributeValue("port");
                    baseName = el.getAttributeValue("baseName");
                    webUrl = el.getAttributeValue("webUrl");
                    earName = el.getAttributeValue("ear");
                    initUserList();
                }
                File dir = new File(Utils.getUserWorkingDir());
                dir.mkdirs();

                File file = new File(dir, "serversJboss.xml");
                saveXmlToFile(file, xml);
            }
        } else if (e.getSource() == btnOK) {
            result = ButtonsFactory.BUTTON_OK;
            if ("1".equals(sel_srv)) {
                File file = new File(dir, "serversJboss.xml");
                saveXmlToFile(file, xml);
                props.setProperty("selSrv", selSrv);
                String pr = props.getProperty(host + "_pkgs");
                if (pr == null) {
                    props.setProperty(host + "_pkgs", "org.jboss.naming:org.jnp.interfaces");
                }
                pr = props.getProperty(host + "_initial");
                if (pr == null) {
                    props.setProperty(host + "_initial", "org.jnp.interfaces.NamingContextFactory");
                }

            }
            setVisible(false);
            endModal();
        } else if (e.getSource() == btnChoose) {
            try {
                String path = selectFile(initPath);
                if (path != null) {
                    initPath = path;
                    edtKeyFile.setText(initPath);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == btnCancel) {
            result = ButtonsFactory.BUTTON_CANCEL;
            dispose();
            endModal();
        }
    }

    private Element createXml() {
        Element xml = new Element("root");
        xml.setAttribute("name", "Серверы");
        Element e = new Element("node");
        e.setAttribute("name", "local");
        e.setAttribute("isLeaf", "true");
        e.setAttribute("serverType", "JBossServer");
        e.setAttribute("url", "localhost:1099");
        e.setAttribute("baseName", "");
        e.setAttribute("webUrl", "http://localhost:8080/Or3WAR");
        e.setAttribute("ear", "Or3EAR");
        e.setAttribute("login", "sys_admin");
        e.setAttribute("dlogin", "sys_admin");
        xml.addContent(e);
        return xml;
    }

    private synchronized void endModal() {
        this.notify();
    }

    public boolean isQuickStartShow() {
        return isQuickStartShow;
    }

    public String getServerType() {
        return serverType != null ? serverType : "";
    }

    public String getHost() {
        return host != null ? host : "";
    }

    public String getWebUrl() {
        return webUrl != null ? webUrl : "";
    }

    public String getEarName() {
        return earName != null ? earName : "";
    }

    public String getPort() {
        return port != null ? port : "";
    }

    public String getBaseName() {
        return baseName != null ? baseName : "";
    }

    private void saveXmlToFile(File file, Element xml) {
        XMLOutputter opr = new XMLOutputter();
        opr.setFormat(opr.getFormat().setEncoding("UTF-8"));
        xml.detach();
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            opr.output(new Document(xml), os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Element getXmlFromFile(File file) {
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(is);
            return doc.getRootElement();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Инициализация списка пользователей
     */
    private void initUserList() {
        userList = Utils.getUserList(host + ":" + port, baseName, isRuntime, props);
        if (userList == null) {
            userList = new ArrayList<String>();
            userList.add(isRuntime ? "" : "sys_admin");
        }
        autoUserName.setDataList(userList);

        initPath = Utils.getKeyFilePath(host + ":" + port, baseName, isRuntime, props);
        edtKeyFile.setText(initPath);
    }

    private String selectFile(final String initPath) throws Exception {
        return selectFile(resource.getString("key-choose-dialog-title"), resource.getString("key-choose-button-title"), "p12",
                resource.getString("key-choose-p12-desc"), initPath);
    }

    private String selectFile(final String dialogTitle, final String buttonTitle, final String extensions,
            final String description, final String initPath) throws Exception {
        JFileChooser chooser = new JFileChooser();
        String filePath = null;
        int selectionMode = 0;
        chooser.setDialogTitle(dialogTitle);
        if (initPath != null && initPath.indexOf(File.separator) > 0) {
            File f = new File(initPath);
            if (f.exists())
                chooser.setSelectedFile(f);
        }
        chooser.setFileSelectionMode(selectionMode);
        String extArray[] = extensions.split(",");
        for (int i = 0; i < extArray.length; i++) {
            extArray[i] = (String) extArray[i];
        }

        CommonFileFilter keyFileFilter = new CommonFileFilter(extArray, description);
        keyFileFilter.setExtensionListInDescription(false);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(keyFileFilter);

        int result = chooser.showDialog(null, buttonTitle);
        if (result == 0) {
            File selectedFile = chooser.getSelectedFile();
            if (selectedFile.isFile()) {
                filePath = null;
                try {
                    filePath = selectedFile.getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    /**
     * Сохранение данных авторизации в файле свойств.
     * 
     */
    public void setUserData() {
        userList = Utils.updateUserList(autoUserName.getText().trim(), userList);
        Utils.setUserList(userList, host + ":" + port, baseName, isRuntime, props);

        initPath = edtKeyFile.getText();
        Utils.setKeyFilePath(initPath, host + ":" + port, baseName, isRuntime, props);
        dir.mkdirs();
        File f = new File(dir, "propsJboss");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            props.store(fos, "Properties");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @return the currLang
     */
    public String getCurrLang() {
        return currLang;
    }
}