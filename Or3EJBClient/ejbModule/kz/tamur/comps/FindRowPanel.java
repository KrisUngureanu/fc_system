package kz.tamur.comps;

import kz.tamur.rt.MainFrame;
import kz.tamur.rt.adapters.ColumnAdapter;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;

import javax.swing.*;
import javax.swing.text.BadLocationException;

import java.awt.*;
import java.util.ResourceBundle;
import java.util.Locale;
import kz.tamur.rt.Utils;
/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 18.08.2004
 * Time: 10:52:11
 * To change this template use File | Settings | File Templates.
 */
public class FindRowPanel extends JPanel {

    private ColumnAdapter columnAdapter;

    private JLabel imageLabel = new JLabel(kz.tamur.rt.Utils.getImageIcon("FindRow48"));
    private ButtonGroup bg = new ButtonGroup();
    private ResourceBundle res = ResourceBundle.getBundle(
            Constants.NAME_RESOURCES, new Locale("ru"));
	private JLabel captionLabel = Utils.createLabel(res.getString("findRowInColumn") + " [");
	private JTextField findText = Utils.createDesignerTextField();
    private JCheckBox startWithCheck = Utils.createCheckBox(res.getString("firstSymbol"), false);
    private JCheckBox fullTextCheck = Utils.createCheckBox(res.getString("fullAnalog"), false);
    private JCheckBox containsTextCheck = Utils.createCheckBox(res.getString("containsStr"), true);
	private JCheckBox checkRegister = Utils.createCheckBox(res.getString("case"), false);
	private long langId;
        private static FindRowPanel panel;
        private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
        
    public void setLangId(long langId) {
        if (langId != this.langId) {
            this.langId = langId;
            LangItem langItem = LangItem.getById(langId);
            if (langItem!=null && "KZ".equals(langItem.code)) {
                res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("kk"));
            } else {
                res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
            }
            setCaptions();
        }
    }

    public FindRowPanel(ColumnAdapter columnAdapter, long langId) {
        this.columnAdapter = columnAdapter;
        this.langId = langId;
        LangItem langItem = LangItem.getById(langId);
        if (langItem!=null && "KZ".equals(langItem.code)) {
            res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("kk"));
			setCaptions();
        } else {
            res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
			setCaptions();
        }
        setLayout(new GridBagLayout());
        init();
    }

	private void setCaptions() {
            LangItem langItem = LangItem.getById(langId);
            if (langItem!=null && "KZ".equals(langItem.code)) {
                captionLabel.setText(res.getString("findRowInColumn"));
            } else {
                captionLabel.setText(res.getString("findRowInColumn") + " [");
            }
            checkRegister.setText(res.getString("case"));
            fullTextCheck.setText(res.getString("fullAnalog"));
            startWithCheck.setText(res.getString("firstSymbol"));
            containsTextCheck.setText(res.getString("containsStr"));
	}

	private void init() {
        String title = columnAdapter.getColumn().getTitle();
        setPreferredSize(new Dimension(680, 150));

        bg.add(fullTextCheck);
        bg.add(startWithCheck);
        bg.add(containsTextCheck);

        add(imageLabel, new GridBagConstraints(0, 0, 1, 2, 0, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 10), 0, 0));
            LangItem langItem = LangItem.getById(langId);
            if (langItem!=null && "KZ".equals(langItem.code)) {
                captionLabel.setText("[" + title + "] " + captionLabel.getText() + ":");
            } else {
                captionLabel.setText(captionLabel.getText() + title + "] :");
            }
            add(captionLabel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(25, 5, 0, 0), 0, 0));
        add(findText, new GridBagConstraints(1, 1, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 10), 0, 0));
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
        p.add(containsTextCheck, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                Constants.INSETS_0, 0, 0));
        p.add(startWithCheck, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                Constants.INSETS_0, 0, 0));
        p.add(fullTextCheck, new GridBagConstraints(2, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                Constants.INSETS_0, 0, 0));

        p.add(checkRegister, new GridBagConstraints(3, 0, 1, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    Constants.INSETS_0, 0, 0));

        add(p, new GridBagConstraints(1, 2, 2, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 20, 0), 0, 0));
        setOpaque(isOpaque);
        startWithCheck.setOpaque(isOpaque);
        fullTextCheck.setOpaque(isOpaque);
        containsTextCheck.setOpaque(isOpaque);
        checkRegister.setOpaque(isOpaque);
    }

    public String getFindText() {
        return Funcs.normalizeInput(findText.getText());
    }

    public void setFindText(String text) {
    	findText.setText("");
        try {
        	findText.getDocument().insertString(0, text, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
    }

    public boolean isCheckRegister() {
        return checkRegister.isSelected();
    }
    
    public void setCheckRegister(boolean b) {
        checkRegister.setSelected(b);
    }

    public boolean isCheckStart() {
        return startWithCheck.isSelected();
    }

    public void setCheckStart(boolean b) {
        startWithCheck.setSelected(b);
    }

    public boolean isCheckFull() {
        return fullTextCheck.isSelected();
    }

    public void setCheckFull(boolean b) {
        fullTextCheck.setSelected(b);
    }

    public boolean isCheckContains() {
        return containsTextCheck.isSelected();
    }

    public void setCheckContains(boolean b) {
        containsTextCheck.setSelected(b);
    }

    public static FindRowPanel getInstance(long langId, ColumnAdapter ca) {
        if (panel == null) {
            panel = new FindRowPanel(ca, langId);
        } else {
            panel.setLangId(langId);
            panel.setColumnAdapter(ca);
        }
        return panel;
    }

    public void setColumnAdapter(ColumnAdapter ca) {
        this.columnAdapter = ca;
        String title = columnAdapter.getColumn().getTitle();
        LangItem langItem = LangItem.getById(langId);
        if (langItem!=null && "KZ".equals(langItem.code)) {
            captionLabel.setText("[" + title + "] " + res.getString("findRowInColumn") + ":");
        } else {
            captionLabel.setText(res.getString("findRowInColumn") + " [" + title + "] :");
        }
    }
}
