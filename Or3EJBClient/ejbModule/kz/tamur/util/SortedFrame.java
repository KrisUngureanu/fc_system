package kz.tamur.util;

import static kz.tamur.rt.Utils.getImageIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Comparator;
import java.util.Arrays;
import java.util.ArrayList;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.OrGradientToolBar;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerModalFrame;
import kz.tamur.rt.MainFrame;

/**
 * Created by Vitaly A. Pronin
 * Date: 30.01.2004
 * Time: 15:34:30
 */
public class SortedFrame extends DesignerModalFrame implements ActionListener {

    private static JPanel content_ = new JPanel(new BorderLayout());
    private Component contentComp_;
    private OrGradientToolBar toolBar = kz.tamur.comps.Utils.createGradientToolBar();

    private JToggleButton sortByStringBut = ButtonsFactory.createCompButton("Сортировка по алфавиту", getImageIcon("sortAZ"));
    private JToggleButton sortByIntBut = ButtonsFactory.createCompButton("Сортировка по значимости", getImageIcon("sortCrit"));
    private JToggleButton filterByCritBut = ButtonsFactory.createCompButton("Фильтр по обязательным", getImageIcon("FilterNodeMod"));
    private JToggleButton filterByConstrBut = ButtonsFactory.createCompButton("Фильтр по ограничениям", getImageIcon("filterByConstr1"));
    private JToggleButton filterByOptBut = ButtonsFactory.createCompButton("Фильтр по необязательным", getImageIcon("filterByConstr"));

    private ReqMsgsList.MsgListItem[] allMsgs_;

    public SortedFrame(Frame owner, String title) {
        super(owner, title, content_);
        init_();
    }

    public SortedFrame(Dialog owner, String title) {
        super(owner, title, content_);
        init_();
    }

    public void setOption(String[] options) {
        if (options.length > 0) {
            okBtn.setText(options[0]);
            if (options.length > 1) {
                if(options[1].equals("hide")) {
                    setOnlyOkButton();
                }else {
                    cancelBtn.setText(options[1]);
                }
            }
        }
    }

    public void setContent(Component comp) {
        content_.removeAll();
        content_.add(toolBar, BorderLayout.NORTH);
        content_.add(comp, BorderLayout.CENTER);
        contentComp_ = comp;
        setSize(comp.getSize());
        pack();
    }

    void init_() {
        content_.setOpaque(isOpaque);
        sortByStringBut.setMargin(Constants.INSETS_1);
        sortByStringBut.setToolTipText("Сортировка по сообщению");
        sortByStringBut.setSelected(true);
        sortByStringBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JToggleButton src = (JToggleButton) e.getSource();
                sort(new ReqMsgsList.SortOnTitles(src.isSelected() ? ReqMsgsList.SORT_UP : ReqMsgsList.SORT_DOWN));
            }
        });

        sortByIntBut.setMargin(Constants.INSETS_1);
        sortByIntBut.setToolTipText("Сортировка по значимости");
        sortByIntBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JToggleButton src = (JToggleButton) e.getSource();
                if (src.isSelected()) {
                    sort(new ReqMsgsList.SortOnMessType(ReqMsgsList.SORT_UP));
                } else {
                    sort(new ReqMsgsList.SortOnMessType(ReqMsgsList.SORT_DOWN));
                }
            }
        });

        filterByCritBut.setMargin(Constants.INSETS_1);
        filterByCritBut.setToolTipText("Фильтровать по обязательным атрибутам");
        filterByCritBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JToggleButton src = (JToggleButton) e.getSource();
                filtered(src.isSelected(), Constants.BINDING);
            }
        });

        filterByConstrBut.setMargin(Constants.INSETS_1);
        filterByConstrBut.setToolTipText("Фильтровать по ограничениям");
        filterByConstrBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JToggleButton src = (JToggleButton) e.getSource();
                filtered(src.isSelected(), Constants.CONSTRAINT);
            }
        });

        filterByOptBut.setMargin(Constants.INSETS_1);
        filterByOptBut.setToolTipText("Фильтровать по факультативным атрибутам");
        filterByOptBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JToggleButton src = (JToggleButton) e.getSource();
                filtered(src.isSelected(), Constants.OPTIONAL);
            }
        });

        toolBar.add(new JLabel(kz.tamur.rt.Utils.getImageIcon("decor")));
        toolBar.add(sortByStringBut);
        toolBar.add(sortByIntBut);
        toolBar.add(filterByCritBut);
        toolBar.add(filterByConstrBut);
        toolBar.add(filterByOptBut);

        if (!isOpaque) {
            toolBar.setGradient(MainFrame.GRADIENT_CONTROL_PANEL);
        }

        content_.add(toolBar, BorderLayout.NORTH);
        sortByStringBut.setOpaque(isOpaque);
        sortByIntBut.setOpaque(isOpaque);
        filterByCritBut.setOpaque(isOpaque);
        filterByConstrBut.setOpaque(isOpaque);
        filterByOptBut.setOpaque(isOpaque);
    }

    void sort(Comparator comparator) {
        ReqMsgsList reqMsgList = null;
        if (contentComp_ instanceof ReqMsgsList) {
            reqMsgList = (ReqMsgsList) contentComp_;
        }
        if (reqMsgList != null) {
            JList list = reqMsgList.getList();
            ListModel lm = list.getModel();
            ReqMsgsList.MsgListItem[] objs = new ReqMsgsList.MsgListItem[lm.getSize()];
            for (int i = 0; i < lm.getSize(); i++) {
                objs[i] = (ReqMsgsList.MsgListItem) lm.getElementAt(i);
            }
            Arrays.sort(objs, comparator);
            reqMsgList.addToList(objs);
            reqMsgList.revalidate();
        }
    }

    void filtered(boolean exec, int type) {
        ReqMsgsList reqMsgList = null;
        if (contentComp_ instanceof ReqMsgsList) {
            reqMsgList = (ReqMsgsList) contentComp_;
        }
        if (reqMsgList != null) {
            if (allMsgs_ == null) {
                JList list = reqMsgList.getList();
                ListModel lm = list.getModel();
                allMsgs_ = new ReqMsgsList.MsgListItem[lm.getSize()];
                for (int i = 0; i < lm.getSize(); i++) {
                    allMsgs_[i] = (ReqMsgsList.MsgListItem) lm.getElementAt(i);
                }
            }
        }
        if (exec) {
            ArrayList critMsgs = new ArrayList();
            for (int i = 0; i < allMsgs_.length; i++) {
                if (allMsgs_[i].getType() == type) {
                    critMsgs.add(allMsgs_[i]);
                }
            }
            ReqMsgsList.MsgListItem[] objs = new ReqMsgsList.MsgListItem[critMsgs.size()];
            for (int i = 0; i < critMsgs.size(); i++) {
                objs[i] = (ReqMsgsList.MsgListItem) critMsgs.get(i);
            }
            reqMsgList.addToList(objs);
        } else {
            if (allMsgs_.length > 0) {
                reqMsgList.addToList(allMsgs_);
            }
        }
    }

    public void show() {
        super.show();
        startModal();
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            okBtn.doClick();
        } else {
            super.processWindowEvent(e);
        }
    }

}
