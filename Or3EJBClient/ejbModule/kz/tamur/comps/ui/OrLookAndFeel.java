package kz.tamur.comps.ui;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import kz.tamur.comps.ui.button.OrButtonUI;
import kz.tamur.comps.ui.checkBox.OrCheckBoxUI;
import kz.tamur.comps.ui.comboBox.OrComboBoxUI;
import kz.tamur.comps.ui.label.OrLabelUI;
import kz.tamur.comps.ui.radiobutton.OrRadioButtonUI;
import kz.tamur.comps.ui.tabbedPane.OrTabbedPaneUI;
import kz.tamur.comps.ui.toolbar.OrToolBarUI;

/**
 * Используется для визуализации интерфейсов системы
 * 
 * @author Sergey Lebedev
 * 
 */
public class OrLookAndFeel extends MetalLookAndFeel {
    /**
     * Известные константы UI
     */

    public static final String COMPONENT_ORIENTATION_PROPERTY = "componentOrientation";
    public static final String COMPONENT_MARGIN_PROPERTY = "margin";
    public static final String COMPONENT_ENABLED_PROPERTY = "enabled";
    public static final String TOOLBAR_FLOATABLE_PROPERTY = "floatable";
    public static final String WINDOW_DECORATION_STYLE_PROPERTY = "windowDecorationStyle";

    /**
     * Описание LaF
     * 
     * @see javax.swing.plaf.metal.MetalLookAndFeel#getDescription()
     */
    public String getDescription() {
        return "Cross-platform Java Look and Feel for OR3 System";
    }

    /**
     * Имя LaF
     * 
     * @see javax.swing.plaf.metal.MetalLookAndFeel#getName()
     */
    public String getName() {
        return "OrLookAndFeel";
    }

    /**
     * Идентификатор LaF
     * 
     * @see javax.swing.plaf.metal.MetalLookAndFeel#getID()
     */
    public String getID() {
        return getName();
    }

    /**
     * Привязан ли данный LaF к текущей платформе (является ли его нативной имплементацией)
     * 
     * @see javax.swing.plaf.metal.MetalLookAndFeel#isNativeLookAndFeel()
     */
    public boolean isNativeLookAndFeel() {
        return false;
    }

    /**
     * Поддерживается ли данный LaF на текущей платформе
     * 
     * @see javax.swing.plaf.metal.MetalLookAndFeel#isSupportedLookAndFeel()
     */
    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public static boolean isInstalled() {
        return UIManager.getLookAndFeel().getClass().getCanonicalName().equals(OrLookAndFeel.class.getCanonicalName());
    }

    /**
     * Замена необходимых стандартных классов, собственными
     * 
     * @see javax.swing.plaf.metal.MetalLookAndFeel#initClassDefaults(javax.swing.UIDefaults)
     */
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        table.put("ButtonUI", OrButtonUI.class.getCanonicalName());
        table.put("ToggleButtonUI", OrButtonUI.class.getCanonicalName());
        table.put("TabbedPaneUI", OrTabbedPaneUI.class.getCanonicalName());
        table.put("CheckBoxUI", OrCheckBoxUI.class.getCanonicalName());
        table.put("RadioButtonUI", OrRadioButtonUI.class.getCanonicalName());
        table.put("ComboBoxUI", OrComboBoxUI.class.getCanonicalName());
        table.put("LabelUI", OrLabelUI.class.getCanonicalName());
        table.put("ToolBarUI", OrToolBarUI.class.getCanonicalName());
        // table.put("ScrollPaneUI", OrScrollPaneUI.class.getCanonicalName());
        // table.put("ScrollBarUI", OrScrollBarUI.class.getCanonicalName());
        // table.put("TextFieldUI", OrTextFieldUI.class.getCanonicalName());
    }
}
