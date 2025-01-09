package kz.tamur.guidesigner;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.VERTICAL;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_CANCEL;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_NO;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_OK;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_SEND;
import static kz.tamur.guidesigner.ButtonsFactory.BUTTON_YES;
import static kz.tamur.guidesigner.ButtonsFactory.createDialogButton;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.text.BadLocationException;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.rt.MainFrame;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.Or3DialogTitleBar;

import com.cifs.or2.client.Kernel;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 14.05.2004
 * Time: 18:10:10
 * To change this template use File | Settings | File Templates.
 */
public class MessagesFactory {

    public static final int ERROR_MESSAGE = 0;
    public static final int CONFIRM_MESSAGE = 1;
    public static final int QUESTION_MESSAGE = 2;
    public static final int EXCLAMATION_MESSAGE = 3;
    public static final int INFORMATION_MESSAGE = 4;
    public static final int OPTION_MESSAGE = 5;
    public static final int ENTER_PASSWORD_MESSAGE = 6;

    private static final String ERROR_TITLE = "Ошибка";
    private static final String CONFIRM_TITLE = "Подтверждение";
    private static final String QUESTION_TITLE = "Сообщение";
    private static final String EXCLAMATION_TITLE = "Предупреждение";
    private static final String ENTER_PASSWORD_TITLE = "Введите пароль";

    private static JLabel image = new JLabel();
    private static final String messNotFound = "Поиск завершён. Совпадений не найдено.";
    private static final String messSearchFinished = "Поиск завершён.";

    public static int showMessageDialog(Container owner, int type, String text) {
        final MessageDialog dlg;
        if (owner instanceof Frame) {
            dlg = new MessageDialog((Frame) owner, type, (LangItem) null);
        } else {
            dlg = new MessageDialog((Dialog) owner, type, (LangItem) null);
        }
        dlg.getTextMessage().setText(Funcs.sanitizeMessage(text));
        dlg.setResizable(true);
        dlg.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dlg.dispose();
                }
            }
        });
        dlg.setFocusable(true);
        dlg.setVisible(true);
        return dlg.getResult();
    }

    public static int showMessageDialog(Frame owner, int type, String text) {
        MessageDialog dlg = new MessageDialog(owner, type, (LangItem) null);
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        dlg.setResizable(true);
        dlg.setVisible(true);
        return dlg.getResult();
    }

    public static int showMessageDialog(Container owner, int type, String text, int width, int height) {
        MessageDialog dlg = null;
        if (owner instanceof Frame) {
            dlg = new MessageDialog((Frame) owner, type, (LangItem) null);
        } else if (owner instanceof Dialog) {
            dlg = new MessageDialog((Dialog) owner, type, (LangItem) null);
        }
        
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        
        dlg.getTextMessage().setPreferredSize(new Dimension(width, height));
        dlg.setResizable(true);
        // dlg.pack();
        dlg.setVisible(true);
        return dlg.getResult();
    }

    public static int showMessageDialog(Frame owner, int type, String text, LangItem li) {
        MessageDialog dlg = new MessageDialog(owner, type, li);
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        dlg.setVisible(true);
        return dlg.getResult();
    }

    public static int showOptionDialog(Frame owner, int type, String[] values, LangItem li) {
        MessageDialog dlg = new MessageDialog(owner, type, li, values);
        dlg.setVisible(true);
        return dlg.getOptionResult();
    }

    public static String showPasswordDialog(Frame owner, int type, String cod) {
        MessageDialog dlg = new MessageDialog(owner, type, cod);
        dlg.getTextMessage().setText(dlg.getTitle());
        dlg.pdFld.requestFocus();
        dlg.pdFld.requestFocusInWindow();
        dlg.setVisible(true);
        char[] res = dlg.getPasswordResult();
        return (res != null) ? new String(res) : null;
    }

    public static int showMessageDialog(Frame owner, int type, String text, String cod) {
        MessageDialog dlg = new MessageDialog(owner, type, cod);
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        dlg.setVisible(true);
        return dlg.getResult();
    }

    public static int showMessageDialog(Dialog owner, int type, String text, LangItem li) {
        MessageDialog dlg = new MessageDialog(owner, type, li);
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        dlg.setVisible(true);
        return dlg.getResult();
    }

    public static int showMessageDialog(Dialog owner, int type, String text) {
        MessageDialog dlg = new MessageDialog(owner, type);
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        dlg.setVisible(true);
        return dlg.getResult();
    }

    public static int showMessageDialog(Dialog owner, int type, String text, String cod) {
        MessageDialog dlg = new MessageDialog(owner, type, cod);
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        dlg.setVisible(true);
        return dlg.getResult();
    }

    public static int showMessageDialog(Dialog owner, int type, String text, int activeButton) {
        MessageDialog dlg = new MessageDialog(owner, type, activeButton);
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        dlg.setVisible(true);
        return dlg.getResult();
    }

    public static int showMessageDialog(Frame owner, int type, String text, int activeButton) {
        MessageDialog dlg = new MessageDialog(owner, type, activeButton);
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        dlg.setVisible(true);
        return dlg.getResult();
    }

    public static int showMessageNotFound(Container owner) {
        return showMessageNotFound(owner, null);
    }

    public static int showMessageSearchFinished(Container owner) {
        return showMessageSearchFinished(owner, null);
    }

    public static int showMessageNotFound(Container owner, String cod) {
        if (owner instanceof Frame)
            return showMessageDialog((Frame) owner, MessagesFactory.INFORMATION_MESSAGE, messNotFound, cod);
        else if (owner instanceof Dialog)
            return showMessageDialog((Dialog) owner, MessagesFactory.INFORMATION_MESSAGE, messNotFound, cod);
        else
            return -1;
    }

    public static int showMessageSearchFinished(Container owner, String cod) {
        if (owner instanceof Frame)
            return showMessageDialog((Frame) owner, MessagesFactory.INFORMATION_MESSAGE, messSearchFinished, cod);
        else if (owner instanceof Dialog)
            return showMessageDialog((Dialog) owner, MessagesFactory.INFORMATION_MESSAGE, messSearchFinished, cod);
        else
            return -1;
    }
    
    
    public static int showMessageDialogBig(Container owner, int type, String text) {
        MessageDialog dlg = null;
        if (owner instanceof Frame)
            dlg = new MessageDialog((Frame) owner, type, (LangItem) null);
        else if (owner instanceof Dialog)
            dlg = new MessageDialog((Dialog) owner, type, (LangItem) null);
        else
            return -1;
        dlg.setSize(new Dimension(600, 250));
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        dlg.setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(dlg.getSize()));
        dlg.setVisible(true);
        return dlg.getResult();
    }

    private static final int dist = 8;

    private static int locations[] = { SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.WEST, SwingConstants.EAST,
            SwingConstants.NORTH_WEST, SwingConstants.NORTH_EAST, SwingConstants.SOUTH_WEST, SwingConstants.SOUTH_EAST };

    private static int cursors[] = { Cursor.N_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR,
            Cursor.E_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR,
            Cursor.SE_RESIZE_CURSOR };

    private static Rectangle getRectangle(int x, int y, int w, int h, int location) {
        switch (location) {
        case SwingConstants.NORTH:
            return new Rectangle(x + dist / 2, y, w - dist, dist);
        case SwingConstants.SOUTH:
            return new Rectangle(x + dist / 2, y + h - dist, w - dist, dist);
        case SwingConstants.WEST:
            return new Rectangle(x, y + dist / 2, dist, h - dist);
        case SwingConstants.EAST:
            return new Rectangle(x + w - dist, y + dist / 2, dist, h - dist);
        case SwingConstants.NORTH_WEST:
            return new Rectangle(x, y, dist / 2, dist / 2);
        case SwingConstants.NORTH_EAST:
            return new Rectangle(x + w - dist / 2, y, dist / 2, dist / 2);
        case SwingConstants.SOUTH_WEST:
            return new Rectangle(x, y + h - dist / 2, dist / 2, dist / 2);
        case SwingConstants.SOUTH_EAST:
            return new Rectangle(x + w - dist / 2, y + h - dist / 2, dist / 2, dist / 2);
        }
        return null;
    }

    public static int getCursor(MouseEvent me) {
        Component c = me.getComponent();
        int w = c.getWidth();
        int h = c.getHeight();

        for (int i = 0; i < locations.length; i++) {
            Rectangle rect = getRectangle(0, 0, w, h, locations[i]);
            if (rect.contains(me.getPoint()))
                return cursors[i];
        }

        return Cursor.DEFAULT_CURSOR;
    }

    public static void showMessageDialog(Dialog owner, int type, String text, LangItem li, long showPeriod) {
        MessageDialog dlg = new MessageDialog(owner, type, li);
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        dlg.setVisible(true);
        try {
            Thread.sleep(showPeriod);
        } catch (InterruptedException e) {
        }

        dlg.dispose();
    }

    public static void showMessageDialog(Dialog owner, int type, String text, String cod, long showPeriod) {
        MessageDialog dlg = new MessageDialog(owner, type, cod);
        dlg.getTextMessage().setText("");
        try {
			dlg.getTextMessage().getDocument().insertString(0, Funcs.sanitizeMessage(text), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        dlg.setVisible(true);

        try {
            Thread.sleep(showPeriod);
        } catch (InterruptedException e) {
        }

        dlg.dispose();
    }

    protected static class MessageDialog extends JDialog implements ActionListener {

        ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));

        private int result = 0;
        private int result_ = 0;
        private JButton btnOk = createDialogButton(BUTTON_OK);
        private JButton btnYes = createDialogButton(BUTTON_YES);
        private JButton btnNo = createDialogButton(BUTTON_NO);
        private JButton btnCancel = createDialogButton(BUTTON_CANCEL);
        private JButton btnSend = createDialogButton(BUTTON_SEND);
        private int type;
        private int activeButton = -1;
        private LangItem lang;
        private String cod;
        private String[] values = null;
        private JPasswordField pdFld;
        private JTextArea textMessage;

        public MessageDialog(Frame owner, int type, LangItem lang) {
            this(owner, type, lang, null, null, -1);
        }

        public MessageDialog(Frame owner, int type, LangItem lang, String[] values) {
            this(owner, type, lang, values, null, -1);
        }

        public MessageDialog(Frame owner, int type, String cod) {
            this(owner, type, null, null, cod, -1);
        }

        public MessageDialog(Frame owner, int type, int activeButton) {
            this(owner, type, null, null, null, activeButton);
        }

        public MessageDialog(Frame owner, int type, LangItem lang, String[] values, String cod, int activeButton) {
            super(owner, true);
            this.type = type;
            this.lang = lang;
            this.values = values;
            this.cod = cod;
            this.activeButton = activeButton;
            initMessage();
        }

        public MessageDialog(Dialog owner, int type, LangItem lang) {
            this(owner, type, lang, null, -1);
        }

        public MessageDialog(Dialog owner, int type, String cod) {
            this(owner, type, null, cod, -1);
        }

        public MessageDialog(Dialog owner, int type) {
            this(owner, type, null, null, -1);
        }

        public MessageDialog(Dialog owner, String title, int type) {
            this(owner, type, null, null, -1);
            setTitle(title);
        }

        public MessageDialog(Dialog owner, int type, int activeButton) {
            this(owner, type, null, null, activeButton);
        }

        public MessageDialog(Dialog owner, int type, LangItem lang, String cod, int activeButton) {
            super(owner, true);
            this.type = type;
            this.lang = lang;
            this.cod = cod;
            this.activeButton = activeButton;
            initMessage();
        }

        private void setActiveButton() {
            switch (activeButton) {
            case BUTTON_OK:
                getRootPane().setDefaultButton(btnOk);
                break;
            case BUTTON_YES:
                getRootPane().setDefaultButton(btnYes);
                break;
            case BUTTON_NO:
                getRootPane().setDefaultButton(btnNo);
                break;
            case BUTTON_CANCEL:
                getRootPane().setDefaultButton(btnCancel);
                break;
            }
        }

        void initMessage() {
            setUndecorated(true);
            getRootPane().setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            textMessage = new JTextArea();
            
            textMessage.setOpaque(false);

            if (lang != null) {
                res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("KZ".equals(lang.code) ? "kk" : "ru"));
            } else if (cod != null && !cod.equals("")) {
                res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("KZ".equals(cod) ? "kk" : "ru"));
            } else if (Utils.getLocale() != null) {
                res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, Utils.getLocale());
            }

            refreshButtonsTitle(res);
            btnOk.addActionListener(this);
            btnYes.addActionListener(this);
            btnNo.addActionListener(this);
            btnCancel.addActionListener(this);
            btnSend.addActionListener(this);

            GradientPanel content = new GradientPanel();

            if (Kernel.instance().getUser() != null && !MainFrame.GRADIENT_MAIN_FRAME.isEmpty()) {
                content.setGradient(MainFrame.GRADIENT_MAIN_FRAME);
            } else {
                content.setGradient(Constants.GLOBAL_DEF_GRADIENT);
            }

            content.setLayout(new GridBagLayout());
            content.add(image, new GridBagConstraints(0, 0, 1, 1, 0, 0, CENTER, BOTH, new Insets(25, 25, 4, 25), 0, 0));
            content.add(new JLabel(""), new GridBagConstraints(0, 1, 1, 2, 0, 2, CENTER, VERTICAL, Constants.INSETS_0, 0, 0));
            if (values == null) {
                if (type == ENTER_PASSWORD_MESSAGE) {
                    Dimension sz = new Dimension(200, 20);
                    textMessage.setPreferredSize(sz);
                    textMessage.setMaximumSize(sz);
                    pdFld = new JPasswordField();
                    pdFld.setPreferredSize(sz);
                    pdFld.setMaximumSize(sz);
                    JPanel p = new JPanel();
                    // данную переменную необходимо использовать только так, иначе возможна досрочная инициализациа
                    // переменной при неправильной авторизации пользователя и некоррекстная работа в дальнейшем
                    p.setOpaque(!MainFrame.TRANSPARENT_DIALOG);
                    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
                    p.add(textMessage);
                    p.add(pdFld);
                    content.add(p, new GridBagConstraints(1, 0, 4, 2, 0, 0, CENTER, NONE, new Insets(25, 4, 4, 4), 0, 0));
                } else {
                    textMessage.setPreferredSize(new Dimension(300, 140));
                    content.add(textMessage, new GridBagConstraints(1, 0, 4, 2, 1, 10, CENTER, BOTH, new Insets(25, 4, 4, 4), 0,
                            0));
                }

                textMessage.setEditable(false);
                textMessage.setFont(Utils.getDefaultFont());
                textMessage.setLineWrap(true);
                textMessage.setWrapStyleWord(true);
            } else {
                JPanel rgPanel = new JPanel();
                // данную переменную необходимо использовать только так, иначе возможна досрочная инициализациа
                // переменной при неправильной авторизации пользователя и некоррекстная работа в дальнейшем
                rgPanel.setOpaque(!MainFrame.TRANSPARENT_DIALOG);
                rgPanel.setLayout(new GridLayout(0, 1));
                ButtonGroup bgroup = new ButtonGroup();
                for (int i = 0; i < values.length; i++) {
                    JRadioButton rb = new JRadioButton(values[i]);
                    if (i == 0) {
                        rb.setSelected(true);
                        result_ = 0;
                    }
                    bgroup.add(rb);
                    rgPanel.add(rb);
                    rb.setFont(Utils.getDefaultFont());
                    rb.setName("" + i);
                    rb.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Object o = e.getSource();
                            if (o instanceof JRadioButton)
                                result_ = Integer.valueOf(((JRadioButton) o).getName());
                        }
                    });
                }
                content.add(rgPanel, new GridBagConstraints(1, 0, 4, 2, 1, 1, CENTER, BOTH, new Insets(25, 4, 4, 4), 0, 0));
            }
            switch (type) {
            case ERROR_MESSAGE:
                setTitle(res != null || (cod != null && !cod.equals("")) ? res.getString("error") : ERROR_TITLE);
                content.add(new JLabel(), new GridBagConstraints(1, 1, 2, 0, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_0, 0, 0));
                content.add(btnSend, new GridBagConstraints(3, 3, 1, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                content.add(btnOk, new GridBagConstraints(4, 3, 1, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                image.setIcon(kz.tamur.rt.Utils.getImageIcon("Error48"));
                if (activeButton == -1) {
                    getRootPane().setDefaultButton(btnOk);
                } else {
                    setActiveButton();
                }
                break;
            case EXCLAMATION_MESSAGE:
                setTitle(res != null ? res.getString("alert") : EXCLAMATION_TITLE);
                content.add(new JLabel(""),
                        new GridBagConstraints(1, 1, 3, 0, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_0, 0, 0));
                content.add(btnOk, new GridBagConstraints(4, 3, 0, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                image.setIcon(kz.tamur.rt.Utils.getImageIcon("Exclam48"));
                if (activeButton == -1) {
                    getRootPane().setDefaultButton(btnOk);
                } else {
                    setActiveButton();
                }
                break;
            case CONFIRM_MESSAGE:
                setTitle(res != null ? res.getString("confirmation") : CONFIRM_TITLE);
                content.add(new JLabel(""),
                        new GridBagConstraints(1, 1, 1, 0, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_0, 0, 0));
                content.add(btnYes, new GridBagConstraints(2, 3, 1, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                content.add(btnNo, new GridBagConstraints(3, 3, 1, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                content.add(btnCancel, new GridBagConstraints(4, 3, 1, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                image.setIcon(kz.tamur.rt.Utils.getImageIcon("Confirm48"));
                if (activeButton == -1) {
                    getRootPane().setDefaultButton(btnCancel);
                } else {
                    setActiveButton();
                }
                break;
            case QUESTION_MESSAGE:
                setTitle(res != null ? res.getString("message") : QUESTION_TITLE);
                image.setIcon(kz.tamur.rt.Utils.getImageIcon("Question48"));
                content.add(new JLabel(""),
                        new GridBagConstraints(1, 1, 1, 0, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_0, 0, 0));
                content.add(btnYes, new GridBagConstraints(2, 3, 1, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                content.add(btnNo, new GridBagConstraints(3, 3, 1, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                if (activeButton == -1) {
                    getRootPane().setDefaultButton(btnNo);
                } else {
                    setActiveButton();
                }
                break;
            case INFORMATION_MESSAGE:
                setTitle(res != null ? res.getString("message") : QUESTION_TITLE);
                content.add(new JLabel(""),
                        new GridBagConstraints(1, 1, 3, 0, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_0, 0, 0));
                content.add(btnOk, new GridBagConstraints(4, 3, 0, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                image.setIcon(kz.tamur.rt.Utils.getImageIcon("Information48"));
                if (activeButton == -1) {
                    getRootPane().setDefaultButton(btnOk);
                } else {
                    setActiveButton();
                }
                break;
            case OPTION_MESSAGE:
                setTitle(res != null ? res.getString("option") : QUESTION_TITLE);
                image.setIcon(kz.tamur.rt.Utils.getImageIcon("Question48"));
                content.add(new JLabel(""),
                        new GridBagConstraints(1, 1, 1, 0, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_0, 0, 0));
                content.add(btnYes, new GridBagConstraints(2, 3, 1, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                content.add(btnNo, new GridBagConstraints(3, 3, 1, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                if (activeButton == -1) {
                    getRootPane().setDefaultButton(btnOk);
                } else {
                    setActiveButton();
                }
                break;
            case ENTER_PASSWORD_MESSAGE:
                setTitle(res != null ? res.getString("enterPassword") : ENTER_PASSWORD_TITLE);
                image.setIcon(kz.tamur.rt.Utils.getImageIcon("Question48"));
                content.add(new JLabel(""),
                        new GridBagConstraints(1, 1, 1, 0, 1, 0, CENTER, HORIZONTAL, Constants.INSETS_0, 0, 0));
                content.add(btnOk, new GridBagConstraints(2, 3, 1, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                content.add(btnCancel, new GridBagConstraints(3, 3, 1, 0, 0, 0, CENTER, NONE, new Insets(5, 10, 5, 5), 0, 0));
                if (activeButton == -1) {
                    getRootPane().setDefaultButton(btnOk);
                } else {
                    setActiveButton();
                }
                break;
            default:
                setTitle(res != null ? res.getString("message") : QUESTION_TITLE);
            }

            Container cont = getContentPane();
            cont.setLayout(new BorderLayout());
            Or3DialogTitleBar titleBar = new Or3DialogTitleBar(this, getTitle());
            cont.add(titleBar, BorderLayout.NORTH);
            cont.add(content, BorderLayout.CENTER);
            pack();
            setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
            updateDefaultButton();

            addMouseListener(resizeListener);
            addMouseMotionListener(resizeListener);

        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public int getOptionResult() {
            return result == BUTTON_OK || result == BUTTON_YES ? result_ : -1;
        }

        public char[] getPasswordResult() {
            return result == BUTTON_OK || result == BUTTON_YES ? pdFld.getPassword() : null;
        }

        public void setOptionResult(int result) {
            this.result_ = result;
        }

        public void actionPerformed(ActionEvent e) {
            JButton src = (JButton) e.getSource();
            if (src == btnOk) {
                result = BUTTON_OK;
                dispose();
            } else if (src == btnYes) {
                result = BUTTON_YES;
                dispose();
            } else if (src == btnNo) {
                result = BUTTON_NO;
                dispose();
            } else if (src == btnCancel) {
                result = BUTTON_CANCEL;
                dispose();
            } else if (src == btnSend) {
                sendToDeveloper();
            }
        }
        
        private JTextArea getTextMessage() {
        	return textMessage;
        }

        private void updateDefaultButton() {
            JButton btn = getRootPane().getDefaultButton();
            btn.setText(Funcs.underline(btn.getText()));
        }

        private void refreshButtonsTitle(ResourceBundle res) {
            btnOk.setText(res.getString("ok"));
            btnCancel.setText(res.getString("cancel"));
            btnYes.setText(res.getString("yes"));
            btnNo.setText(res.getString("no"));
            btnSend.setText(res.getString("sendToDeveloper"));
        }
        
        private void sendToDeveloper() {
            if (!java.awt.Desktop.isDesktopSupported()) {
                System.err.println("Desktop is not supported (fatal)");
            } else {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

                if (!desktop.isSupported(java.awt.Desktop.Action.MAIL)) {
                    System.err.println("Desktop doesn't support the mail action (fatal)");
                } else {
                    try {
                        String email = System.getProperty("email");
                        URI uri = new URI("mailto:" + (email == null ? "daulet@tamur.kz" : email) + "?SUBJECT=OR3%20ошибка!&BODY="
                                + Utils.toURIString(textMessage.getText()));
                        desktop.mail(uri);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }

        MouseInputListener resizeListener = new MouseInputAdapter() {
            public void mouseMoved(MouseEvent me) {
                setCursor(Cursor.getPredefinedCursor(MessagesFactory.getCursor(me)));
            }

            public void mouseExited(MouseEvent mouseEvent) {
                setCursor(Cursor.getDefaultCursor());
            }

            private int cursor;
            private Point startPos = null;

            public void mousePressed(MouseEvent me) {
                cursor = MessagesFactory.getCursor(me);
                startPos = me.getPoint();
                requestFocus();
                repaint();
            }

            public void mouseDragged(MouseEvent me) {

                if (startPos != null) {

                    int x = getX();
                    int y = getY();
                    int w = getWidth();
                    int h = getHeight();

                    int dx = me.getX() - startPos.x;
                    int dy = me.getY() - startPos.y;

                    switch (cursor) {
                    case Cursor.N_RESIZE_CURSOR:
                        if (!(h - dy < 50)) {
                            setLocation(x, y + dy);
                            setSize(w, h - dy);
                        }
                        break;

                    case Cursor.S_RESIZE_CURSOR:
                        if (!(h + dy < 50)) {
                            setSize(w, h + dy);
                            startPos = me.getPoint();
                        }
                        break;

                    case Cursor.W_RESIZE_CURSOR:
                        if (!(w - dx < 50)) {
                            setLocation(x + dx, y);
                            setSize(w - dx, h);
                        }
                        break;

                    case Cursor.E_RESIZE_CURSOR:
                        if (!(w + dx < 50)) {
                            setSize(w + dx, h);
                            startPos = me.getPoint();
                        }
                        break;

                    case Cursor.NW_RESIZE_CURSOR:
                        if (!(w - dx < 50) && !(h - dy < 50)) {
                            setLocation(x + dx, y + dy);
                            setSize(w - dx, h - dy);
                        }
                        break;

                    case Cursor.NE_RESIZE_CURSOR:
                        if (!(w + dx < 50) && !(h - dy < 50)) {
                            setLocation(x, y + dy);
                            setSize(w + dx, h - dy);
                            startPos = new Point(me.getX(), startPos.y);
                        }
                        break;

                    case Cursor.SW_RESIZE_CURSOR:
                        if (!(w - dx < 50) && !(h + dy < 50)) {
                            setLocation(x + dx, y);
                            setSize(w - dx, h + dy);
                            startPos = new Point(startPos.x, me.getY());
                        }
                        break;

                    case Cursor.SE_RESIZE_CURSOR:
                        if (!(w + dx < 50) && !(h + dy < 50)) {
                            setSize(w + dx, h + dy);
                            startPos = me.getPoint();
                        }
                        break;
                    }

                    setCursor(Cursor.getPredefinedCursor(cursor));
                }
            }

            public void mouseReleased(MouseEvent mouseEvent) {
                startPos = null;
            }
        };

        protected void processWindowEvent(WindowEvent e) {
            if (e.getID() == WindowEvent.WINDOW_CLOSING) {
                textMessage.setPreferredSize(new Dimension(300, 140));
            }
            super.processWindowEvent(e);
        }
    }
}
