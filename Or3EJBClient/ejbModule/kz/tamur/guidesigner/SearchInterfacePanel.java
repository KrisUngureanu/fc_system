package kz.tamur.guidesigner;

import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static kz.tamur.rt.Utils.createCombo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kz.tamur.comps.Constants;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 18.10.2004
 * Time: 17:32:59
 * To change this template use File | Settings | File Templates.
 */
public class SearchInterfacePanel extends JPanel {

    private KrnAttribute attribute;
    private JLabel label = Utils.createLabel("Введите текст для поиска:");
    private JTextField textField = Utils.createDesignerTextField();
    private boolean isAttr = true;
    private boolean isMethod = false;
    private final Dimension size = new Dimension(300, 70);
    private JComboBox modeCombo = Utils.createCombo();
    private JComboBox conditionCombo = createCombo();
    private JComboBox langCombo = Utils.createCombo();
    private JComboBox typeCombo = Utils.createCombo();
    private JComboBox paramCombo = Utils.createCombo();
    private JComboBox hiperCombo = Utils.createCombo();
    private JCheckBox paramCheck = Utils.createCheckBox("Поиск привязанных интерфейсов", false);
    private int mode = 0;
    
    
    /**
	 * @return the paramCheck
	 */
    public boolean paramChecked() {
		return paramCheck.isSelected();
	}
    
    public void setParamCheck(boolean check) {
    	paramCheck.setSelected(check);
    }

	public SearchInterfacePanel() {
        super(new GridBagLayout());
        Utils.setAllSize(this, size);
        init();
    }
    
    public SearchInterfacePanel(int mode) {
        super(new GridBagLayout());
        if(mode == 2)        	
        	Utils.setAllSize(this, new Dimension(450,70));
        else
        	Utils.setAllSize(this, size);
        
        this.mode = mode;
        init();
    }
    
    public SearchInterfacePanel(boolean isMethod) {
        super(new GridBagLayout());
        this.isMethod = isMethod;
        Utils.setAllSize(this, size);
        init();
    }

    public SearchInterfacePanel(String labelText, KrnAttribute attribute) {
        super(new GridBagLayout());
        this.attribute = attribute;
        Utils.setAllSize(this, size);
        label.setText(labelText);
        init();
    }

    public SearchInterfacePanel(String labelText, boolean isAttr) {
        super(new GridBagLayout());
        this.isAttr = isAttr;
        Utils.setAllSize(this, size);
        label.setText(labelText);
        init();
    }  

    private void init() {
        setOpaque(false);
        add(label, new GridBagConstraints(0, 0, 4, 1, 0, 0, WEST, HORIZONTAL, Constants.INSETS_5, 0, 0));
        if(mode == 2)
        	add(textField, new GridBagConstraints(0, 1, 5, 1, 1, 0, WEST, HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
        else
        	add(textField, new GridBagConstraints(0, 1, 4, 1, 1, 0, WEST, HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));

        modeCombo.setOpaque(false);
        modeCombo.addItem("Поиск класса");
        modeCombo.addItem("Поиск атрибута");
        modeCombo.addItem("Поиск метода");
        Utils.setAllSize(modeCombo, new Dimension(105, 25));
        modeCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                typeCombo.setEnabled(true);
                if (modeCombo.getSelectedIndex() == 2)
                	typeCombo.removeItem("ID");
                if (modeCombo.getSelectedIndex() != 2 && typeCombo.getItemAt(1) != "ID")
                	typeCombo.insertItemAt("ID", 1);
                
                
                if(modeCombo.getSelectedIndex() != 0)
                	typeCombo.removeItem("Ссылка");
                if(modeCombo.getSelectedIndex() == 0 && typeCombo.getItemAt(3) != "Ссылка")
                	typeCombo.insertItemAt("Ссылка", 3);
                
                setVisible(true);
            }
        });

        langCombo.setOpaque(false);
        if (attribute != null && attribute.isMultilingual) {
            langCombo.addItem("RU");
            langCombo.addItem("KZ");
        } else {
            langCombo.addItem("");
        }
        Utils.setAllSize(langCombo, new Dimension(90, 25));
        langCombo.setForeground(Color.BLUE);
        langCombo.setToolTipText("Выбор языка");
        

        typeCombo.setOpaque(false);
        typeCombo.addItem("Название");
        
        typeCombo.addItem("ID");
        typeCombo.addItem("UID");
        typeCombo.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		conditionCombo.setEnabled(typeCombo.getSelectedIndex() == 0 || typeCombo.getSelectedIndex() == 3);
        	}
        });
        
        if(mode != 1)
        	typeCombo.addItem("Ссылка");
        Utils.setAllSize(typeCombo, new Dimension(90, 25));

        conditionCombo.setOpaque(false);
        conditionCombo.addItem("С начала");
        conditionCombo.addItem("Совпадает");
        conditionCombo.addItem("Содержит");
        Utils.setAllSize(conditionCombo, new Dimension(90, 25));
        conditionCombo.setToolTipText("Выбор условия совпадения");

        paramCombo.setOpaque(false);
        paramCombo.addItem("Поиск по ID");
        paramCombo.addItem("Поиск по UID");
        Utils.setAllSize(paramCombo, new Dimension(100, 25));
        
        paramCheck.setEnabled(false);
        
        hiperCombo.setOpaque(false);
        hiperCombo.addItem("Название");
        hiperCombo.addItem("ID");
        hiperCombo.addItem("UID");
        Utils.setAllSize(hiperCombo, new Dimension(100, 25));
        hiperCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				conditionCombo.setEnabled(hiperCombo.getSelectedIndex() == 0);
			}
		});
//        hiperCombo.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent e) {
//            	paramCheck.setEnabled(true);                
//            }
//        });
        
        if (isAttr) {
            if (isMethod) {
                add(modeCombo, new GridBagConstraints(0, 2, 2, 1, 0, 0, WEST, NONE, Constants.INSETS_5, 0, 0));
                add(typeCombo, new GridBagConstraints(2, 2, 1, 1, 1, 0, EAST, NONE, Constants.INSETS_5, 0, 0));
            } else if (mode == 1) {
                add(typeCombo, new GridBagConstraints(2, 2, 1, 1, 1, 0, EAST, NONE, Constants.INSETS_5, 0, 0));
            } else if (mode == 2) {
                add(paramCheck, new GridBagConstraints(2, 2, 1, 1, 1, 0, EAST, NONE, Constants.INSETS_5, 0, 0));
                add(hiperCombo, new GridBagConstraints(3, 2, 1, 1, 1, 0, EAST, NONE, Constants.INSETS_5, 0, 0));
                paramCheck.setEnabled(true);
            }
            if (attribute != null) {
                add(langCombo, new GridBagConstraints(2, 2, 1, 1, 1, 0, EAST, NONE, Constants.INSETS_5, 0, 0));
            }
            if(mode == 2)
            	add(conditionCombo, new GridBagConstraints(4, 2, 1, 1, 1, 0, EAST, NONE, Constants.INSETS_5, 0, 0));
            else 
            	add(conditionCombo, new GridBagConstraints(3, 2, 1, 1, 1, 0, EAST, NONE, Constants.INSETS_5, 0, 0));
        } else {
            add(paramCombo, new GridBagConstraints(3, 2, 1, 1, 1, 0, EAST, NONE, Constants.INSETS_5, 0, 0));
        }
    }

    public boolean findByID() {
        return typeCombo.getSelectedIndex() == 1;
    }
    
    public boolean findByUID() {
    	return typeCombo.getSelectedIndex() == 2;
    }
       
    public String getSearchText() {
        return Funcs.normalizeInput(textField.getText()).trim();
    }

    public JTextField getSearchField() {
        return textField;
    }

    public boolean isMethod() {
        return isMethod && modeCombo.getSelectedIndex() == 2;
    }

    public boolean isAttr() {
        return modeCombo.getSelectedIndex() == 1;
    }
    public long getLanguageID() {
        if (langCombo.getSelectedItem().equals("RU")) {
            return 122;
        } else if (langCombo.getSelectedItem().equals("KZ")) {
            return 123;
        } else {
            return 0;
        }
    }

    public int getSearchMethod() {
        if (isAttr) {
            if (conditionCombo.getSelectedIndex() == 0) {
                return ComparisonOperations.SEARCH_START_WITH;
            } else if (conditionCombo.getSelectedIndex() == 1) {
                return ComparisonOperations.CO_EQUALS;
            } else if (conditionCombo.getSelectedIndex() == 2) {
                return ComparisonOperations.CO_CONTAINS;
            } else {
                return ComparisonOperations.CO_CONTAINS;
            }
        } else {
            if (paramCombo.getSelectedIndex() == 0) {
                return Constants.SEARCH_ID;
            } else if (paramCombo.getSelectedIndex() == 1) {
                return Constants.SEARCH_UID;
            } else {
                return Constants.SEARCH_ID;
            }
        }
    }

    public void setSearchMethod(int index) {
        if (isAttr) {
            if (ComparisonOperations.SEARCH_START_WITH == index) {
                conditionCombo.setSelectedIndex(0);
            } else if (ComparisonOperations.CO_EQUALS == index) {
                conditionCombo.setSelectedIndex(1);
            } else if (ComparisonOperations.CO_CONTAINS == index) {
                conditionCombo.setSelectedIndex(2);
            }
        } else {
            if (Constants.SEARCH_ID == index) {
                paramCombo.setSelectedIndex(0);
            } else if (Constants.SEARCH_UID == index) {
                paramCombo.setSelectedIndex(1);
            }
        }
    }

    public void setSearchText(String text) {
        textField.setText(text);
        textField.selectAll();
    }
    
    public void setModeIndex(int index) {
    	modeCombo.setSelectedIndex(index);
    }
    
    public void setTypeIndex(int index) {
    	typeCombo.setSelectedIndex(index);
    }
    
    public void setConditionIndex(int index) {
    	conditionCombo.setSelectedIndex(index);
    }
    
    public int getType() {
        return typeCombo.getSelectedIndex();
    }
    
    public int getMode() {
    	return modeCombo.getSelectedIndex();
    }
    
    public int getCondition() {
    	return conditionCombo.getSelectedIndex();
    }
    
    public int getHiperParam() {
        return hiperCombo.getSelectedIndex();
    }
    
    public void setHiperIndex(int index) {
    	hiperCombo.setSelectedIndex(index);
    }
}