package com.cifs.or2.client;

import static kz.tamur.common.ErrorCodes.*;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.util.ResourceBundle;

import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.rt.Utils;
import kz.tamur.util.LangItem;
import kz.tamur.util.PasswordService;

/**
 * Created by IntelliJ IDEA. User: Vale Date: 27.12.2004 Time: 11:25:03 To
 */
public class PathWordChange extends JDialog implements ActionListener {
    private JLabel loldPd = new JLabel();
    private JLabel lnewPd = new JLabel();
    private JLabel lcnfPd = new JLabel();
    private JPasswordField oldPdFld = new JPasswordField("");
    private JPasswordField newPdFld = new JPasswordField("");
    private JPasswordField cnfPdFld = new JPasswordField("");
    private JButton okBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_OK);
    private JButton cancelBtn = ButtonsFactory.createDialogButton(ButtonsFactory.BUTTON_CANCEL);

    // Объявление лейбла для вывода заставки
    private JLabel lblImage = new JLabel();
    private ResourceBundle res;

    private static final ImageIcon LoginImg = kz.tamur.rt.Utils.getImageIconJpg("LoginBox");
    private Kernel krn = Kernel.instance();
    private LangItem langItem;
    private String lang;
    private boolean changePass = false;
    private KrnObject object;
    private int codeError;
    private String dsName;
    private String nameUs;
    private String typeClient;
    private String ip;
    private String pcName;

    public PathWordChange(JFrame owner, LangItem langItem, ResourceBundle resource, KrnObject object) {
        super(owner, true);
        this.langItem = langItem;
        res = resource;
        jbInit();
        this.object = object;
        pack();
    }

    public PathWordChange(String dsName, String nameUs, String typeClient, String ip, String pcName, JFrame owner, String lang,
            ResourceBundle resource, KrnObject object, int codeError) {
        super(owner, true);
        this.dsName = dsName;
        this.nameUs = dsName;
        this.typeClient = dsName;
        this.ip = dsName;
        this.pcName = dsName;

        this.lang = lang;
        res = resource;
        this.object = object;
        this.codeError = codeError;
        jbInit();
        pack();
    }

    public PathWordChange(String dsName, String nameUs, String typeClient, String ip, String pcName, JDialog owner, String lang,
            ResourceBundle resource, KrnObject object, int codeError) {
        super(owner, true);
        this.dsName = dsName;
        this.nameUs = dsName;
        this.typeClient = dsName;
        this.ip = dsName;
        this.pcName = dsName;

        this.lang = lang;
        res = resource;
        this.object = object;
        this.codeError = codeError;
        jbInit();
        pack();
    }

    private void jbInit() {
        GridBagLayout gbl = new GridBagLayout();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setTitle(res.getString("passChangeTitle"));
        getRootPane().setDefaultButton(okBtn);
        Font dFont = Utils.getDefaultFont();
        Font pdFont = new Font("Tahoma", 1, 12);

        loldPd.setText(res.getString("oldPass"));
        loldPd.setFont(dFont);
        loldPd.setForeground(Utils.getLightSysColor());
        loldPd.setHorizontalAlignment(JLabel.RIGHT);

        lnewPd.setText(res.getString("newPass"));
        lnewPd.setFont(dFont);
        lnewPd.setForeground(Utils.getLightSysColor());
        lnewPd.setHorizontalAlignment(JLabel.RIGHT);

        lcnfPd.setText(res.getString("confPass"));
        lcnfPd.setFont(dFont);
        lcnfPd.setForeground(Utils.getLightSysColor());
        lcnfPd.setHorizontalAlignment(JLabel.RIGHT);

        lblImage.setIcon(LoginImg);

        oldPdFld.setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor())); // TODO убрать в новом UI для TextField
        oldPdFld.setFont(pdFont);
        oldPdFld.addKeyListener(new com.cifs.or2.client.gui.OrKazakhAdapter());
        oldPdFld.setPreferredSize(new Dimension(150, 20));

        newPdFld.setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor())); // TODO убрать в новом UI для TextField
        newPdFld.setFont(pdFont);
        newPdFld.addKeyListener(new com.cifs.or2.client.gui.OrKazakhAdapter());
        newPdFld.setPreferredSize(new Dimension(150, 20));

        cnfPdFld.setBorder(BorderFactory.createLineBorder(Utils.getDarkShadowSysColor())); // TODO убрать в новом UI для TextField
        cnfPdFld.setFont(pdFont);
        cnfPdFld.addKeyListener(new com.cifs.or2.client.gui.OrKazakhAdapter());
        cnfPdFld.setPreferredSize(new Dimension(150, 20));

        okBtn.setBackground(Color.white);
        okBtn.setIcon(kz.tamur.rt.Utils.getImageIcon("checkOk"));
        okBtn.addActionListener(this);

        cancelBtn.setIcon(kz.tamur.rt.Utils.getImageIcon("Delete"));
        cancelBtn.addActionListener(this);
        lblImage.setLayout(gbl);

        GridBagConstraints c = new GridBagConstraints();
        // инициализация параметров
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 10, 5);
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;

        if (langItem == null) {
            JLabel lbl = new JLabel();
            switch (codeError) {
            case USER_IS_EXPIRED:
                lbl.setText(res.getString("passwordExpired"));
                break;
            case USER_IS_ENDED:
                lbl.setText(res.getString("messPassEnded"));
                break;
            case USER_NOT_LOGIN:
                lbl.setText(res.getString("messFirstLogin"));
                break;
            }
            lbl.setFont(dFont);
            lbl.setForeground(Utils.getLightRedColor());
            lbl.setHorizontalAlignment(JLabel.CENTER);
            c.gridwidth = 4;
            gbl.setConstraints(lbl, c);
            lblImage.add(lbl);
        }

        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        c.gridwidth = 1;
        c.gridy = 1;
        gbl.setConstraints(loldPd, c);
        lblImage.add(loldPd);

        c.gridy = 2;
        gbl.setConstraints(lnewPd, c);
        lblImage.add(lnewPd);

        c.gridy = 3;
        gbl.setConstraints(lcnfPd, c);
        lblImage.add(lcnfPd);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2;
        c.gridy = 4;
        gbl.setConstraints(okBtn, c);

        lblImage.add(okBtn);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 1;
        c.gridy = 1;
        gbl.setConstraints(oldPdFld, c);
        lblImage.add(oldPdFld);

        c.gridy = 2;
        gbl.setConstraints(newPdFld, c);
        lblImage.add(newPdFld);

        c.gridy = 3;
        gbl.setConstraints(cnfPdFld, c);
        lblImage.add(cnfPdFld);

        c.fill = GridBagConstraints.NONE;
        c.gridx = 2;
        c.gridy = 4;
        gbl.setConstraints(cancelBtn, c);
        lblImage.add(cancelBtn);

        getContentPane().add(lblImage);

        FocusListener fl = new FocusListener() {
            public void focusGained(FocusEvent e) {
                getRootPane().setDefaultButton((JButton) e.getSource());
            }

            public void focusLost(FocusEvent e) {
                getRootPane().setDefaultButton(okBtn);
            }
        };
        cancelBtn.addFocusListener(fl);
        okBtn.addFocusListener(fl);
        pack();
    }

    public void setVisible(boolean visible) {
        if (visible) {
            setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
            getRootPane().setDefaultButton(okBtn);
            oldPdFld.setText("");
            newPdFld.setText("");
            cnfPdFld.setText("");
        }
        super.setVisible(visible);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == okBtn) {
            changePass = false;
            try {
                String oldPD = PasswordService.getInstance().encrypt(new String(Utils.getPD(oldPdFld)));
                if (dsName == null) {
                    UserSessionValue us = krn.getUserSession();
                    krn.changePassword(us.dsName, us.name, us.typeClient, us.ip, us.pcName, object, oldPD.toCharArray(), Utils.getPD(newPdFld), Utils.getPD(cnfPdFld));
                } else {
                    krn.changePassword(dsName, nameUs, typeClient, ip, pcName, object, oldPD.toCharArray(),
                    		Utils.getPD(newPdFld), Utils.getPD(cnfPdFld));
                }
            } catch (KrnException e1) {
                String mess = "";
                switch (e1.code) {
                case PASS_NOT_COMPLETE:
                    mess = res.getString("notCompleteMessage");
                    break;
                case PASS_PASS_NOT_EQUALS:
                    mess = res.getString("passNotEqualsMessage");
                    break;
                case PASS_PASS_IDENT:
                    mess = res.getString("messPassIdent");
                    break;
                case PASS_OLD_PASS_INVALID:
                    mess = res.getString("oldPassInvalidMessage");
                    break;
                case PASS_MIN_PERIOD_PASS:
                    mess = res.getString("messMinPeriodPass");
                    mess = mess.replaceFirst("X", e1.getMessage());
                    break;
                case PASS_VALID_PWD_MIN_LOGIN:
                    mess = res.getString("validPwdMinLogin");
                    mess = mess.replaceFirst("X", e1.getMessage());
                    break;
                case PASS_VALID_PWD_MAX_LOGIN:
                    mess = res.getString("validPwdMaxLogin");
                    mess = mess.replaceFirst("X", e1.getMessage());
                    break;
                case PASS_VALID_PWD_MIN_PASS:
                    mess = res.getString("validPwdmMinPass");
                    mess = mess.replaceFirst("X", e1.getMessage());
                    break;
                case PASS_VALID_PWD_MIN_PASS_ADM:
                    mess = res.getString("validPwdMinPassAdm");
                    mess = mess.replaceFirst("X", e1.getMessage());
                    break;
                case PASS_VALID_PWD_MAX_PASS:
                    mess = res.getString("validPwdmMaxPass");
                    mess = mess.replaceFirst("X", e1.getMessage());
                    break;
                case PASS_VALID_PWD_NO_NUMB:
                    mess = res.getString("validPwdNoNumb");
                    break;
                case PASS_VALID_PWD_NO_ALL_NUMB:
	                mess = res.getString("validPwdNoAllNumb");
	                break;
                case PASS_VALID_PWD_NO_SYMB:
                    mess = res.getString("validPwdNoSymb");
                    break;
                case PASS_VALID_PWD_NO_REG:
                    mess = res.getString("validPwdNoReg");
                    break;
                case PASS_VALID_PWD_NO_SPEC:
                    mess = res.getString("validPwdNoSpec");
                    break;
                case PASS_VALID_PWD_NOT_NAME:
                    mess = res.getString("validPwdNotName");
                    break;
                case PASS_VALID_PWD_NOT_SURN:
                    mess = res.getString("validPwdNotSurn");
                    break;
                case PASS_VALID_PWD_NOT_TEL:
                    mess = res.getString("validPwdNotTel");
                    break;
                case PASS_VALID_PWD_NOT_WORD:
                    mess = res.getString("validPwdNotWord");
                    break;
                case PASS_VALID_PWD_NOT_KEYBOARD:
	                mess = res.getString("validPwdNotKeyboard");
	                break;
                case PASS_VALID_PWD_NOT_LOGIN:
                    mess = res.getString("validPwdNotLogin");
                    break;
                case PASS_VALID_PWD_NOT_REP:
                    mess = res.getString("validPwdNotRep");
                    break;
                case PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO:
	                mess = res.getString("validPwdNotRepAnyMoreTwo");
	                break;
                case PASS_MESS_PASS_DUPL:
                    mess = res.getString("messPassDupl");
                    mess = mess.replaceFirst("X", e1.getMessage());
                    break;
                }

                if (mess.isEmpty()) {
                    e1.printStackTrace();
                } else {
                    showMesssage(mess, MessagesFactory.ERROR_MESSAGE);
                }
                return;
            }
            // подтверждение смены пароля
            changePass = true;
            showMesssage(res.getString("completeMessage"), MessagesFactory.INFORMATION_MESSAGE);
            dispose();

        } else if (src == cancelBtn) {
            dispose();
        }
    }

    private void showMesssage(String mess, int type) {
        if (langItem != null) {
            MessagesFactory.showMessageDialog(this, type, mess, langItem);
        } else {
            MessagesFactory.showMessageDialog(this, type, mess, lang);
        }
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            String mess = res.getString("passwordNotChanged");
            showMesssage(mess, MessagesFactory.INFORMATION_MESSAGE);
            dispose();
        }
    }

    public void setRes(ResourceBundle res) {
        this.res = res;
        setTitle(res.getString("passChangeTitle"));
        loldPd.setText(res.getString("oldPass"));
        lnewPd.setText(res.getString("newPass"));
        lcnfPd.setText(res.getString("confPass"));
        cancelBtn.setText(res.getString("cancel"));
    }

    /**
     * @return the changePass
     */
    public boolean isChangePass() {
        return changePass;
    }

    public String getNewPassword() {
        return new String(Utils.getPD(newPdFld));
    }
}
