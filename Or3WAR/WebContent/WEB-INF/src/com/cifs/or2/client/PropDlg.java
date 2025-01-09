package com.cifs.or2.client;

import javax.swing.*;

import kz.tamur.comps.Constants;
import kz.tamur.util.Funcs;

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;


public class PropDlg extends JDialog implements ActionListener {

    public static final int MR_OK     = 0;
    public static final int MR_CANCEL = 1;
    public static final int MR_CLEAR  = 2;

    public static final int NONE_BARS = 0;
    public static final int HAS_FIND_BAR = 1;
    public static final int HAS_SORT_BAR = 2;

    public static final String[] NAME_PREFIX = {"Форма", "Справочник", "Архив"};

    private Dimension prevSize = new Dimension(0,0);
    private Point prevLoc = new Point(0,0);

    private static final Icon refreshIcon =
            new ImageIcon (PropDlg.class.getResource("gui/images/refresh.gif"));
    private ImageIcon imFullScreen = new ImageIcon(
            PropDlg.class.getResource("constructor/images/fullScreen.gif"));
    private ImageIcon imPrevScreen = new ImageIcon(
            PropDlg.class.getResource("constructor/images/prevScreen.gif"));
    private ImageIcon imSortByTitle = new ImageIcon(
            PropDlg.class.getResource("constructor/images/sortByTitle.gif"));
    private ImageIcon imSortByCrit = new ImageIcon(
            PropDlg.class.getResource("constructor/images/sortByCrit.gif"));

    public int result;

    public JButton okBtn;
    public JButton cancelBtn;
    public JButton clearBtn;
    JComponent currContent = null;
    public final JButton refreshBtn = new JButton("Обновить", refreshIcon);

    private boolean initialized_ = false;

    private JComboBox cb_;
    private JTextField tf_ = new JTextField();
    private int toolsBar_ = 0;
    private String prefix_;
    //private int startIdx_ = 0;
    private JList list_;
    private JCheckBox cBox_ = new JCheckBox("С учётом регистра символов");

    private ButtonGroup sortBg = new ButtonGroup();
    private JToggleButton sortByTitleBut = new JToggleButton("По сообщению", imSortByTitle);
    private JToggleButton sortByCritBut = new JToggleButton("По критичности", imSortByCrit);

    public void setListForSort(JList list) {
        list_ = list;
    }

    public void setList(JList list) {
        list_ = list;
        if (!prefix_.equals("")) {
            prefix_ = cb_.getSelectedItem().toString() + ": ";
        } else {
            prefix_ = "";
        }
        if (list_.getSelectedIndex() == -1) {
            list_.setSelectedIndex(0);
        }
        list_.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (list_.getSelectedValue() != null && e.getClickCount() == 2) {
                    okBtn.doClick();
                }
            }
        });
    }

    public void show() {
        if (toolsBar_ == HAS_FIND_BAR && list_ != null) {
            find(prefix_);
            tf_.requestFocus();
        }
        super.show();
    }

    public PropDlg(JFrame owner, boolean hasClearButton) {
        super(owner, true);
        try {
            JComponent comp = (owner != null) ? owner.getRootPane() : null;
	    jbInit(comp, hasClearButton);
	} catch(Exception e) {
	    e.printStackTrace();
	}
        pack();
    }

    public PropDlg(JDialog owner, boolean hasClearButton) {
        super (owner, true);
        try {
	    jbInit(owner.getRootPane(), hasClearButton);
        } catch(Exception e) {
            e.printStackTrace();
        }
        pack();
    }

    public PropDlg(JFrame owner, boolean hasClearButton, int initBar) {
        super(owner, true);
        try {
            JComponent comp = (owner != null) ? owner.getRootPane() : null;
	    jbInit(comp, hasClearButton);
            toolsBar_ = initBar;
            if (toolsBar_ == HAS_FIND_BAR) {
                initFindBar();
            } else if (toolsBar_ == HAS_SORT_BAR) {
                initSortBar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        pack();
    }

    public void setContent(JComponent content) {
        if (currContent != null) {
            remove(currContent);
        }
        currContent = content;
        if (content instanceof JScrollPane) {
            ((JComponent) ((JScrollPane)
                                content).getViewport().getView()).grabFocus();
        }
        getContentPane().add(currContent, BorderLayout.CENTER);
        pack();
        //setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
    }

  // Implementing ActionListener interface
    public void actionPerformed (ActionEvent e) {
        Object src = e.getSource();
        if (src == okBtn) {
            result = MR_OK;
            dispose();
        } else if (src == cancelBtn) {
            result = MR_CANCEL;
            dispose();
        } else if (src == clearBtn) {
            result = MR_CLEAR;
            dispose();
        }
    }

    public void setFocusForFind() {
        tf_.requestFocus();
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (!initialized_) {
            initialized_ = true;
            if (currContent != null) {
                if (currContent instanceof JScrollPane) {
                    JScrollPane sp = (JScrollPane) currContent;
                    ((JComponent) sp.getViewport().getView()).grabFocus();
                } //else {
                  //  currContent.grabFocus();
                //}
            }
        }
    }

    private void jbInit(final JComponent owner, boolean hasClearButton)
            throws Exception {
        result = MR_CANCEL;
        JPanel southPanel = new JPanel(new BorderLayout());
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(btnPanel, BorderLayout.EAST);
        JPanel extraBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southPanel.add(extraBtnPanel, BorderLayout.WEST);
        refreshBtn.addActionListener(this);
        refreshBtn.setEnabled(false);
        okBtn = new JButton("Ok");
        okBtn.addActionListener(this);
        cancelBtn = new JButton("Отмена");
        cancelBtn.addActionListener(this);
        cancelBtn.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                getRootPane().setDefaultButton (cancelBtn);   
            }

            public void focusLost(FocusEvent e) {
                getRootPane().setDefaultButton (okBtn);
            }
        });
        this.setTitle("Открытие интерфейса...");
        extraBtnPanel.add(refreshBtn);
        btnPanel.add(okBtn);
	if (hasClearButton) {
            clearBtn = new JButton("Очистить");
	    clearBtn.addActionListener(this);
	    btnPanel.add(clearBtn);
	}
        btnPanel.add(cancelBtn);
        getRootPane().setDefaultButton (okBtn);
        //setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
    }

    public void setOption(String[] options) {
        if(options.length > 0) {
            okBtn.setText(options[0]);
            if(options.length > 1) {
                cancelBtn.setText(options[1]);
                if(options.length > 2) {
                    clearBtn.setText(options[2]);
                }
            }
        }
    }

    private void initFindBar() {
        JPanel findBar = new JPanel(new BorderLayout());
        cb_ = new JComboBox(NAME_PREFIX);
        cb_.setFont(new Font("Dialog", Font.PLAIN, 12));
        cb_.setPreferredSize(new Dimension(100, 21));
        prefix_ = cb_.getSelectedItem().toString();
        cb_.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (!e.getItem().toString().equals("")) {
                    prefix_ = e.getItem().toString() + ": ";
                } else {
                    prefix_ = "";
                }
                find(prefix_);
                tf_.requestFocus();
            }
        });
        tf_.setPreferredSize(new Dimension(400, 21));
        cBox_.setFont(new Font("Dialog", Font.PLAIN, 12));
        cBox_.setSelected(false);
        findBar.add(cb_, BorderLayout.WEST);
        findBar.add(tf_, BorderLayout.CENTER);
        //findBar.add(cBox_, BorderLayout.SOUTH);
        tf_.requestFocus();
        tf_.addKeyListener(new FindKeyListener());
        getContentPane().add(findBar, BorderLayout.NORTH);
        //getContentPane().addKeyListener(new FindKeyListener());
    }

    public void find(String prefix) {
        if (list_ != null) {
            ListModel model = list_.getModel();
            for(int i = 0; i < model.getSize(); i++) {
                String str = model.getElementAt(i).toString().toLowerCase(Constants.OK);
                if (str.startsWith(prefix.toLowerCase(Constants.OK))) {
                    list_.setSelectedIndex(i);
                    //startIdx_ = i;
                    break;
                }
            }
            setViewPos();
        }
    }

    private void setViewPos() {
        JViewport v = (JViewport)list_.getParent();
        Point pt = v.getViewPosition();
        FontMetrics fm = list_.getFontMetrics(list_.getFont());
        pt.y = fm.getHeight() * list_.getSelectedIndex();
        int maxYExt = v.getView().getHeight() - v.getHeight();
        pt.y = Math.max(0, pt.y);
        pt.y = Math.min(maxYExt, pt.y);
        v.setViewPosition(pt);
    }

    class FindKeyListener extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP ||
                e.getKeyCode() == KeyEvent.VK_DOWN) {
                list_.requestFocus();
                return;
            }
            if (e.getKeyCode() != KeyEvent.VK_SHIFT &&
                    e.getKeyCode() != KeyEvent.VK_BACK_SPACE &&
                    e.getKeyCode() != KeyEvent.VK_CONTROL &&
                    e.getKeyCode() != KeyEvent.VK_ALT &&
                    e.getKeyCode() != KeyEvent.VK_TAB &&
                    e.getKeyCode() != KeyEvent.VK_ENTER) {
                if (!tf_.hasFocus())
                    tf_.requestFocus();

                prefix_ = prefix_ + e.getKeyChar();
            }
            if (e.getKeyCode() == KeyEvent.VK_DELETE ||
                    e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if (!tf_.hasFocus()) {
                    tf_.requestFocus();
                }
                if (cb_.getSelectedItem().toString() != "") {
                    prefix_ = cb_.getSelectedItem().toString() + ": " + Funcs.normalizeInput(tf_.getText());
                } else {
                    prefix_ = Funcs.normalizeInput(tf_.getText());
                }
            }
            findForward(prefix_, 0);
        }



        private void findForward(String prefix, int startIndex) {
            if (list_ != null) {
                ListModel model = list_.getModel();
                for(int i = startIndex; i < model.getSize(); i++) {
                    String str = model.getElementAt(i).toString().toLowerCase(Constants.OK);
                    if (str.startsWith(prefix.toLowerCase(Constants.OK))) {
                        list_.setSelectedIndex(i);
                        //startIdx_ = i;
                        break;
                    }
                }
                setViewPos();
            }
        }
    }

    void initSortBar() {
        JPanel sortPanel = new JPanel(new BorderLayout());
        JPanel butPanel = new JPanel();
        JToggleButton sizeBut = new JToggleButton("Развернуть", imFullScreen);
        sizeBut.setToolTipText("Развернуть во весь экран");
        sizeBut.setFont(new Font("Dialog", Font.PLAIN, 12));
        sizeBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JToggleButton src = (JToggleButton)e.getSource();
                if (src.isSelected()) {
                    prevSize = PropDlg.this.getSize();
                    prevLoc = PropDlg.this.getLocation();
                    Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
                    PropDlg.this.setSize(new Dimension(sz.width, sz.height - 20));
                    PropDlg.this.setLocation(0,0);
                    PropDlg.this.validate();
                    src.setIcon(imPrevScreen);
                    src.setText("Свернуть");
                    src.setToolTipText("Свернуть до исходных размеров");
                    src.setForeground(Color.white);
                } else {
                    PropDlg.this.setSize(prevSize);
                    PropDlg.this.setLocation(prevLoc);
                    PropDlg.this.pack();
                    src.setIcon(imFullScreen);
                    src.setToolTipText("Развернуть во весь экран");
                    src.setText("Развернуть");
                    src.setForeground(Color.black);
                }
            }
        });
        sortByTitleBut.setToolTipText("Сортировка");
        sortByTitleBut.setSelected(true);
        sortByTitleBut.setFont(new Font("Dialog", Font.PLAIN, 12));
        sortByTitleBut.setForeground(Color.white);
        sortByCritBut.setToolTipText("Сортировка");
        sortByCritBut.setFont(new Font("Dialog", Font.PLAIN, 12));
        SortAction sa = new SortAction();
        sortByTitleBut.addActionListener(sa);
        sortByCritBut.addActionListener(sa);
        sortBg.add(sortByTitleBut);
        sortBg.add(sortByCritBut);
        butPanel.add(new JLabel("Сортировка:"));
        butPanel.add(sortByTitleBut);
        butPanel.add(sortByCritBut);
        sortPanel.add(sizeBut, BorderLayout.WEST);
        sortPanel.add(butPanel, BorderLayout.CENTER);
        getContentPane().add(sortPanel, BorderLayout.NORTH);
    }

    class SortAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JToggleButton src = (JToggleButton)e.getSource();
            if (src.equals(sortByTitleBut)) {
                if (src.isSelected()) {
                    src.setForeground(Color.white);
                    sortByCritBut.setForeground(Color.black);
                    //sort(new ReqMsgsList.SortOnTitles());
                }
            } else {
                if (src.isSelected()) {
                    src.setForeground(Color.white);
                    sortByTitleBut.setForeground(Color.black);
                    //sort(new ReqMsgsList.SortOnMessType());
                }
            }
        }
    }

}