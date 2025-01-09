package kz.tamur.guidesigner;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.label.OrRotationLabel;

import kz.tamur.rt.Utils;
import kz.tamur.util.Or3FrameTemplate;
import kz.tamur.rt.Descriptionable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static kz.tamur.rt.Utils.setAllSize;
import static kz.tamur.comps.Constants.DONT_ROTATE;
import static kz.tamur.comps.Constants.ROTATE_LEFT;
import static kz.tamur.comps.Constants.ROTATE_RIGHT;
import static kz.tamur.rt.Utils.getImageIcon;
import static kz.tamur.rt.Utils.getImageIconFull;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 07.05.2004
 * Time: 10:33:03
 * To change this template use File | Settings | File Templates.
 */
public class ButtonsFactory {

    static ResourceBundle resource = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));

    // Dialog's buttons
    public static final int BUTTON_OK = 0;
    public static final int BUTTON_CANCEL = 1;
    public static final int BUTTON_REFRESH = 2;
    public static final int BUTTON_YES = 3;
    public static final int BUTTON_NO = 4;
    public static final int BUTTON_CLEAR = 5;
    public static final int BUTTON_DEFAULT = 6;
    public static final int BUTTON_EDIT = 7;
    public static final int BUTTON_NOACTION = 99;
    public static final int BUTTON_FIND = 8;
    public static final int BUTTON_CLOSE = 9;
    public static final int BUTTON_REPLACE = 10;
    public static final int BUTTON_REPLACEALL = 11;
    public static final int BUTTON_FIND_NEXT = 12;
    public static final int BUTTON_CANCEL_FILTER = 13;
    public static final int BUTTON_CREATE = 14;
    public static final int BUTTON_SEND = 15;
    public static final int BUTTON_TO_BACKGROUND = 16;

    // Function's buttons
    public static final int FN_TREE = 0;
    public static final int FN_INSPECTOR = 1;
    public static final int FN_DEBUG = 2;
    public static final int FN_CLASSES = 3;
    public static final int FN_AREA = 4;
    public static final int FN_SERVICES = 5;
    public static final int FN_INTERFACES = 6;
    public static final int FN_FILTERS = 7;
    public static final int FN_USERS = 8;
    public static final int FN_REPORTS = 9;
    public static final int FN_HYPERS = 10;
    public static final int FN_BASE = 11;
    public static final int FN_REPLICATIONS = 12;
    public static final int FN_SCHEDULER = 13;
    public static final int FN_BOXES = 14;
    public static final int FN_FUNC = 15;
    public static final int FN_REPL = 16;
    public static final int FN_ACTIVE_USERS = 17;
    public static final int FN_SEARCH = 18;
    public static final int FN_TERMINAL = 19;
    public static final int FN_CONFIG = 20;
    public static final int FN_SERVICES_CONTROL = 21;
    public static final int FN_CHAT = 22;
    public static final int FN_RECYCLE = 23;
    public static final int FN_RIGHTS = 24;
    public static final int FN_CONFIGS = 25;
    public static final int FN_PROC = 26;
    public static final int FN_VCS_CHANGE = 27;

    // Spacer's buttons
    public static final int INS_COL_BEFORE = 0;
    public static final int INS_COL_AFTER = 1;
    public static final int INS_ROW_BEFORE = 2;
    public static final int INS_ROW_AFTER = 3;
    public static final int DELETE_RC = 4;
    public static final int ARROW_LEFT = 5;
    public static final int ARROW_DOWN = 6;

    // Editors's buttons
    public static final int DEFAULT_EDITOR = 0;
    public static final int COLOR_EDITOR = 1;
    public static final int IFC_EDITOR = 2;
    public static final int CREATE_EDITOR = 3;

    // Frame's buttons
    public static final int MINIMIZE = 0;
    public static final int CLOSE = 1;
    public static final int MAXIMIZE = 2;
    public static final int RESTORE = 3;

    public static JButton createDialogButton(int buttonType) {
        return new DialogButton(buttonType);
    }

    public static JButton createDialogButton(int buttonType, boolean isDefault) {
        return new DialogButton(buttonType, isDefault);
    }

    public static JButton createToolButton(String iconName, String toolTip) {
        return new DesignerToolButton(iconName, toolTip);
    }

    public static JButton createToolButtonTransp(String iconName, String toolTip) {
        return createToolButtonTransp(iconName, ".gif", toolTip);
    }

    public static JButton createToolButtonTransp(String iconName, String ext, String toolTip) {
    	return createToolButtonTransp(iconName, ext, toolTip, null);
    }
    
    public static JButton createToolButtonTransp(String iconName, String ext, String toolTip, String title) {
        DesignerToolButton btn = new DesignerToolButton(iconName, ext, toolTip, title);
        btn.setBackground(Color.LIGHT_GRAY);
        btn.setOpaque(false);
        return btn;
    }

    public static JButton createToolButton(String iconName, String ext, String toolTip) {
        return new DesignerToolButton(iconName, ext, toolTip);
    }

    public static JButton createToolButton(Action action) {
        return new DesignerToolButton(action);
    }

    public static FontColorButton createFontColorButton() {
        return new FontColorButton(null, "Цвет шрифта");
    }

    public static JButton createToolButton(String iconName, String toolTip, boolean isFree) {
        return new DesignerToolButton(iconName, toolTip, isFree);
    }

    public static JButton createToolButton(String text) {
        return createToolButton(null, text, "", true);
    }
    
    public static JButton createToolButton(String iconName, String text, String toolTip, boolean isFree) {
        return new DesignerToolButton(iconName, text, toolTip, isFree);
    }

    public static DesignerCompButton createCompButton(String className, ImageIcon icon) {
        return new DesignerCompButton(className, icon);
    }

    public static JToggleButton createCompButton(Action a, String className, ImageIcon icon) {
        return new DesignerCompButton(a, className, icon);
    }

    public static JRadioButton createRadioButton() {
        return new DesignerRadioButton();
    }

    public static JRadioButton createRadioButton(String title) {
        return new DesignerRadioButton(title);
    }

    public static FunctionToolButton createFunctionButton(int type) {
        return new FunctionToolButton(type);
    }

    public static FunctionToolButton createFunctionButton(String title, int type, int rotation) {
        return new FunctionToolButton(title, type, rotation);
    }
    public static FunctionToolButton createFunctionButton(String title, int type) {
        return new FunctionToolButton(title, type);
    }

    public static JButton createFunctionButton1(int type) {
        return new FunctionToolButton1(type);
    }

    public static JButton createSpaserButton(int type) {
        return new SpacerButton(type);
    }

    public static EditorButton createEditorButton(int editorType) {
        return new EditorButton(editorType);
    }

    public static ToggleButton createToggleButton(boolean selected, String img, String tip) {
        return new ToggleButton(selected, img, tip);
    }

    // Кнопка для диалога
    protected static class DialogButton extends JButton {

        private int btnType;
        private String text;
       // private boolean underlined;

        public DialogButton(int buttonType) {
            this(buttonType, false);
        }

        public DialogButton(int buttonType, boolean isDefault) {
            super();
            btnType = buttonType;
         //   underlined = isDefault;
            initButton();
        }

        void initButton() {
            setFont(Utils.getDefaultFont());
            setAllSize(this, new Dimension(100, 30));
            switch (btnType) {
            case BUTTON_OK:
                setText(resource.getString("ok"));
                break;
            case BUTTON_CANCEL:
                setText(resource.getString("cancel"));
                break;
            case BUTTON_REFRESH:
                setText(resource.getString("refresh"));
                break;
            case BUTTON_YES:
                setText(resource.getString("yes"));
                break;
            case BUTTON_NO:
                setText(resource.getString("no"));
                break;
            case BUTTON_CLEAR:
                setText(resource.getString("clear"));
                break;
            case BUTTON_EDIT:
                setText(resource.getString("edit"));
                break;
            case BUTTON_DEFAULT:
                setText("По умолчанию");
                break;
            case BUTTON_CLOSE:
                setText(resource.getString("close"));
                break;
            case BUTTON_FIND:
                setText(resource.getString("findBtn"));
                break;
            case BUTTON_REPLACE:
                setText("Заменить");
                break;
            case BUTTON_REPLACEALL:
                setText("Заменить все");
                break;
            case BUTTON_FIND_NEXT:
                setText("Найти далее");
                break;
            case BUTTON_CANCEL_FILTER:
                setText(resource.getString("cancelApplyFilter"));
                break;
            case BUTTON_CREATE:
                setText(resource.getString("create"));
                break;
            case BUTTON_TO_BACKGROUND:
                setText(resource.getString("toBackground"));
                break;
            case BUTTON_SEND:
                setText(resource.getString("sendToDeveloper"));
                setAllSize(this, new Dimension(160, 30));
                break;
            }
            addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                //    underlined = true;
                    updateLabelText();
                }

                public void focusLost(FocusEvent e) {
                 //   underlined = false;
                    updateLabelText();
                }
            });
        }

        public void setText(String text) {
            this.text = text;
            updateLabelText();
        }

        private void updateLabelText() {
            super.setText(/*underlined ? Funcs.underline(text) :*/ text);
        }
    }

    public static class FontColorButton extends DesignerToolButton {

        private JLabel colorLabel = new JLabel("A");

        public FontColorButton(String iconName, String toolTip) {
            super(iconName, toolTip);
            setLayout(new GridBagLayout());
            // colorLabel.setOpaque(true);
            colorLabel.setFont(new Font("Arial", Font.BOLD, 16));
            colorLabel.repaint();
            add(colorLabel);
        }

        public void setColorLabel(Color color) {
            colorLabel.setForeground(color);
            colorLabel.repaint();
        }
    }

    // Стандартная кнопка для панели инструментов
    public static class DesignerToolButton extends JButton {

        Border defaultBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        Border defaultFreeBorder = BorderFactory.createLineBorder(Utils.getDarkShadowSysColor());
        Border defaultFreeBorderDis = BorderFactory.createLineBorder(Color.gray);

        Border mouseMoveBorder = BorderFactory.createLineBorder(new Color(46, 105, 124), 0);
        Border mouseMoveBorderIsOpaque = BorderFactory.createLineBorder(new Color(46, 105, 124), 2);
        Border pressedBorder = BorderFactory.createLineBorder(new Color(46, 105, 124));

        Color mouseMoveBackground = new Color(178, 186, 202);
        Color pressedBackground = new Color(69, 154, 182);

        Color defaultBackground = UIManager.getColor("Button.background");

        Dimension size = new Dimension(24, 24);

        boolean isFree = false;
        String text = null;
        String iconName = null;

        boolean isActionButton = false;

        public DesignerToolButton(String iconName, String toolTip) {
            super();
            ImageIcon icon = null;
            if (iconName != null) {
                
                final Matcher M = Pattern.compile("\\.JPG$|\\.JPEG$|\\.GIF$|\\.PNG$").matcher(iconName.toUpperCase(Constants.OK));
                icon =  (M.find()) ? getImageIconFull(iconName) : getImageIcon(iconName);
                this.iconName = iconName;
                setIcon(icon);
            }
            if (toolTip != null) {
                setToolTipText(toolTip);
            }
            setAllSize(this, size);
            setMargin(Constants.INSETS_2);
            // addMouseListener(this);
            setFont(Utils.getDefaultFont());
            // setForeground(Utils.getDarkShadowSysColor());
            // setBorderPainted(false);
            setOpaque(false);
        }

        public DesignerToolButton(String iconName, String ext, String toolTip) {
            this(iconName, ext, toolTip, null);
        }
        
        public DesignerToolButton(String iconName, String ext, String toolTip, String title) {
            super();
            ImageIcon icon = null;
            if (iconName != null) {
                icon = getImageIconFull(iconName + ext);
                setIcon(icon);
                if (toolTip != null) {
                    setToolTipText(toolTip);
                }
                if (title != null) {
                	this.text = title;
                	setText(title);
                }
            }
        	setAllSize(this, size);
            setMargin(Constants.INSETS_2);
            setFont(Utils.getDefaultFont());
            setOpaque(false);
        }

        public DesignerToolButton(Action action) {
            super(action);
            setAllSize(this, size);
            setMargin(Constants.INSETS_2);
            setFont(Utils.getDefaultFont());
            this.isActionButton = true;
            setOpaque(false);
        }

        public DesignerToolButton(String iconName, String toolTip, boolean isFree) {
            this(iconName, toolTip);
            this.isFree = isFree;
            setOpaque(false);
        }

        public DesignerToolButton(String iconName, String text, String toolTip, boolean isFree) {
            this(iconName, toolTip);
            this.text = text;
            if (text != null) {
                setText(this.text);
            }
            setOpaque(false);
        }

        public void setEnabled(boolean b) {
            super.setEnabled(b);
        }

        public void setSize(Dimension sz) {
            setPreferredSize(size = sz);
        }

        public void setTransparent(boolean isTransparent) {
            setOpaque(!isTransparent);
        }

        /**
         * @return the iconName
         */
        public String getIconName() {
            return iconName;
        }
    }

    // Кнопка для компонентов
    public static class DesignerCompButton extends JToggleButton implements Descriptionable {

        public String compClass;

        Border defaultBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);

        Border mouseMoveBorder = BorderFactory.createLineBorder(new Color(46, 105, 124), 2);
        Border pressedBorder = BorderFactory.createLineBorder(new Color(46, 105, 124));

        Color mouseMoveBackground = new Color(178, 186, 202);
        Color pressedBackground = new Color(69, 154, 182);

        Color defaultBackground = UIManager.getColor("Button.background");

        Dimension size = new Dimension(24, 24);
        private String desc;

        public DesignerCompButton(String compClass, ImageIcon icon) {
            super();
            if (icon == null) {
                setText(compClass);
            } else {
                setIcon(icon);
                setToolTipText(compClass);
            }
            this.compClass = compClass;
            setAllSize(this, size);
            setMargin(Constants.INSETS_2);
            setFont(Utils.getDefaultFont());
            setOpaque(false);
        }

        public DesignerCompButton(Action a, String compClass, ImageIcon icon) {
            super(a);
            if (icon == null) {
                setText("");
            } else {
                setIcon(icon);
                setToolTipText(compClass);
            }
            this.compClass = compClass;
            setAllSize(this, size);
            setMargin(Constants.INSETS_2);
            setFont(Utils.getDefaultFont());
            setOpaque(false);
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public void setTransparent(boolean isOpaque) {
            setOpaque(!isOpaque);
        }
    }

    protected static class DesignerRadioButton extends JRadioButton {

        public DesignerRadioButton() {
            super();
            init();
        }

        public DesignerRadioButton(String title) {
            super(title);
            init();
        }

        void init() {
        }
    }

    public static class FunctionToolButton extends JToggleButton {
        private int type;
        private ImageIcon icon;
        private OrRotationLabel titleL;
        private JLabel iconL = new JLabel(icon);
        private boolean isMarked = false;

        public FunctionToolButton(String text, boolean selected) {
            super(text,selected);
        }
        
        public FunctionToolButton(int type) {
            this(null, type, DONT_ROTATE);
        }

        public FunctionToolButton(String title, int type) {
            this(title, type, DONT_ROTATE);
        }
        
        public FunctionToolButton(String title, int type, int rotation) {
            super();
            this.type = type;
            titleL = new OrRotationLabel(title);
            setRotation(rotation);
            init();
        }

        public int getFnButtonType() {
            return type;
        }

        private void init() {
            switch (type) {
            case FN_TREE:
                icon = getImageIcon("FnComps");
                break;
            case FN_INSPECTOR:
                icon = getImageIcon("FnInspector");
                break;
            case FN_DEBUG:
                icon = getImageIcon("FnDebug");
                break;
            case FN_CLASSES:
                icon = getImageIconFull("FnClasses.png");
                break;
            case FN_AREA:
                icon = getImageIcon("TabbedPane");
                setAllSize(this, new Dimension(24, 24));
                break;
            case FN_SERVICES:
                icon = getImageIconFull("FnServices.png");
                break;
            case FN_INTERFACES:
                icon = getImageIconFull("FnIfc.png");
                break;
            case FN_FILTERS:
                icon = getImageIconFull("FnFilters.png");
                break;
            case FN_USERS:
                icon = getImageIconFull("FnUsers.png");
                break;
            case FN_REPORTS:
                icon = getImageIconFull("FnReports.png");
                break;
            case FN_HYPERS:
                icon = getImageIconFull("FnComps.png");
                break;
            case FN_BASE:
                icon = getImageIconFull("FnBase.png");
                break;
            case FN_SCHEDULER:
                icon = getImageIconFull("FnSched.png");
                break;
            case FN_BOXES:
                icon = getImageIconFull("FnBox.png");
                break;
            case FN_FUNC:
                icon = getImageIcon("FnFunc");
                break;
            case FN_ACTIVE_USERS:
                icon = getImageIconFull("FnActiveUsers.png");
                break;
            case FN_REPL:
                icon = getImageIconFull("FnRepl.png");
                break;
            case FN_SEARCH:
                icon = getImageIconFull("FnSearch.png");
                break;
            case FN_TERMINAL:
                icon = getImageIconFull("FnTerminal.png");
                break;
            case FN_CONFIG:
                icon = getImageIconFull("FnConfig.png");
                break;
            case FN_SERVICES_CONTROL:
                icon = getImageIconFull("FnServicesControl.png");
                break;
            case FN_RECYCLE:
                icon = getImageIconFull("FnRecycle.png");
                break;
            case FN_VCS_CHANGE:
                icon = getImageIconFull("FnVcsChange.png");
                break;
            case FN_CONFIGS:
                icon = getImageIconFull("FnConfigs.png");
                break;    
            case FN_PROC:
                icon = getImageIconFull("FnProcedure.png");
                break; 
            case FN_RIGHTS:
                icon = getImageIconFull("FnRights.png");
                break;
            }
            setIcon(icon);
            setLayout(new BorderLayout());
            if (isRotated()) {
                if (titleL.getRotation() == ROTATE_RIGHT) {
                    add(iconL, BorderLayout.NORTH);
                    add(titleL, BorderLayout.SOUTH);
                } else if (titleL.getRotation() == ROTATE_LEFT) {
                    add(iconL, BorderLayout.SOUTH);
                    add(titleL, BorderLayout.NORTH);
                }
            } else {
                add(iconL, BorderLayout.NORTH);
                add(titleL, BorderLayout.SOUTH);
            }

            Dimension size = new Dimension(24, 98);
            setAllSize(this, size);
            //setMargin(Constants.INSETS_0);
            titleL.setFont(new Font("SansSerif", Font.BOLD, 11));
            titleL.setForeground(Utils.getDarkShadowSysColor());
        }

        /**
         * @return the rotate
         */
        public int getRotation() {
            return titleL.getRotation();
        }

        /**
         * @param rotate
         *            the rotate to set
         */
        public void setRotation(int rotation) {
            titleL.setRotation(rotation);
        }

        public boolean isRotated() {
            return titleL.getRotation() != DONT_ROTATE;
        }



        public void setIcon(Icon icon) {
            this.icon = (ImageIcon) icon;
            iconL = new JLabel(icon);
        }

        /**
         * @return the isMarked
         */
        public boolean isMarked() {
            return isMarked;
        }

        /**
         * @param isMarked the isMarked to set
         */
        public void setMarked(boolean isMarked) {
            this.isMarked = isMarked;
        }
    }

    protected static class FunctionToolButton1 extends JButton {

        private int type;

        public FunctionToolButton1(int type) {
            super();
            this.type = type;
            init();
        }

        public int getFnButtonType() {
            return type;
        }

        private void init() {
            Dimension size = new Dimension(23, 100);
            setAllSize(this, size);
            setMargin(Constants.INSETS_1);
            ImageIcon icon = null;
            switch (type) {
            case FN_TREE:
                icon = getImageIcon("FnComps");
                break;
            case FN_INSPECTOR:
                icon = getImageIcon("FnInspector");
                break;
            case FN_DEBUG:
                icon = getImageIcon("FnDebug");
                break;
            case FN_CLASSES:
                icon = getImageIcon("FnClasses");
                break;
            }
            setIcon(icon);
        }
    }

    protected static class SpacerButton extends JButton {

        private int type;

        public SpacerButton(int type) {
            super();
            this.type = type;
            setContentAreaFilled(false);
            setOpaque(false);
            init();
        }

        private void init() {
            Dimension size = new Dimension(8, 8);
            setAllSize(this, size);
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setAlignmentY(Component.CENTER_ALIGNMENT);
            ImageIcon icon = null;
            switch (type) {
            case INS_ROW_BEFORE:
                icon = getImageIcon("InsertRowBefSmall");
                setToolTipText("Вставить строку до");
                break;
            case INS_ROW_AFTER:
                icon = getImageIcon("InsertRowAftSmall");
                setToolTipText("Вставить строку после");
                break;
            case INS_COL_BEFORE:
                icon = getImageIcon("InsertColBefSmall");
                setToolTipText("Вставить колонку до");
                break;
            case INS_COL_AFTER:
                icon = getImageIcon("InsertColAftSmall");
                setToolTipText("Вставить колонку после");
                break;
            case DELETE_RC:
                icon = getImageIcon("DelRCSmall");
                setToolTipText("Удалить");
                break;
            case ARROW_DOWN:
                icon = getImageIcon("ArrowUpSmall");
                break;
            case ARROW_LEFT:
                icon = getImageIcon("ArrowLeftSmall");
                break;
            }
            setIcon(icon);
        }
    }

    public static class EditorButton extends JButton {

        private int editorType;

        public EditorButton(int editorType) {
            super();
            this.editorType = editorType;
            init();
        }

        private void init() {
            setAllSize(this, new Dimension(18, 16));
            setMargin(Constants.INSETS_0);
            switch (editorType) {
            case DEFAULT_EDITOR:
                setIcon(getImageIcon("editor"));
                break;
            case IFC_EDITOR:
                setIcon(getImageIcon("editorIfc"));
                break;
            case CREATE_EDITOR:
                setIcon(getImageIcon("editorCreate"));
                break;
            default:
                setBorder(null);
            }
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    Object src = e.getSource();
                    ((JButton) src).setCursor(Constants.HAND_CURSOR);
                }
            });

        }

    }

    public static JButton createFrameButton(int type, Window owner) {
        return new FrameButton(type, owner);
    }

    public static class FrameButton extends JButton implements ActionListener {

        private int type;
        private Window frame;

        public FrameButton(int type, Window owner) {
            this.type = type;
            frame = owner;
            init();
        }

        private void setType(int type) {
            this.type = type;
            setContentAreaFilled(false);
            switch (type) {
            case MINIMIZE:
                setIcon(getImageIcon("minimize"));
                setToolTipText("Свернуть");
                break;
            case CLOSE:
                setIcon(getImageIcon("close"));
                setToolTipText("Закрыть");
                break;
            case MAXIMIZE:
                setIcon(getImageIcon("maximize"));
                setToolTipText("Развернуть");
                break;
            case RESTORE:
                setIcon(getImageIcon("restore"));
                setToolTipText("Восстановить");
                break;
            }
        }

        public int getType() {
            return type;
        }

        void init() {
            // setBorder(BorderFactory.createLineBorder(Utils.getMidSysColor()));
            setBorder(null);
            setAllSize(this, new Dimension(14, 14));
            setOpaque(false);
            switch (type) {
            case MINIMIZE:
                setIcon(getImageIcon("minimize"));
                setToolTipText("Свернуть");
                break;
            case CLOSE:
                setIcon(getImageIcon("close"));
                setToolTipText("Закрыть");
                break;
            case MAXIMIZE:
                setIcon(getImageIcon("maximize"));
                setToolTipText("Развернуть");
                break;
            case RESTORE:
                setIcon(getImageIcon("restore"));
                setToolTipText("Восстановить");
                break;
            }
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            if (type == CLOSE) {
                try {
                    if (frame instanceof Or3FrameTemplate) {
                        ((Or3FrameTemplate) frame).processClose();
                    } else if (frame instanceof DesignerDialog) {
                        ((DesignerDialog) frame).setDialogResult(BUTTON_NOACTION);
                        ((DesignerDialog) frame).dispose();
                    } else if (frame instanceof MessagesFactory.MessageDialog) {
                        ((MessagesFactory.MessageDialog) frame).setResult(BUTTON_NOACTION);
                        ((MessagesFactory.MessageDialog) frame).dispose();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (type == MINIMIZE) {
                if (frame instanceof Or3FrameTemplate) {
                    ((JFrame) frame).setExtendedState(JFrame.ICONIFIED);
                }
            }
            if (type == RESTORE) {
                if (frame instanceof Or3FrameTemplate) {
                    ((Or3FrameTemplate) frame).restoreFrame();
                    setType(MAXIMIZE);
                    return;
                }
            } else if (type == MAXIMIZE) {
                if (frame instanceof Or3FrameTemplate) {
                    ((Or3FrameTemplate) frame).maximizeFrame();
                    setType(RESTORE);
                    return;
                }
            }
        }

    }

    public static class ToggleButton extends JToggleButton implements ItemListener {
        Border m_raised = BorderFactory.createLineBorder(new Color(46, 105, 124), 2);
        Color raised_back = new Color(178, 186, 202);
        Border m_lowered = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        Dimension size = new Dimension(24, 24);
        Color default_back = UIManager.getColor("Button.background");

        public ToggleButton(boolean selected, String iconName, String toolTip) {
            setSelected(selected);
            if (iconName != null) {
                setIcon(getImageIcon(iconName));
                if (toolTip != null) {
                    setToolTipText(toolTip);
                }
            }
            setHorizontalAlignment(CENTER);
            setBorderPainted(true);
            setBorder(selected ? m_raised : m_lowered);
            setMargin(Constants.INSETS_1);
            setRequestFocusEnabled(false);
            setBackground(default_back);
            addItemListener(this);
            setAllSize(this, size);
            setMargin(Constants.INSETS_2);
        }

        public float getAlignmentY() {
            return 0.5f;
        }

        public void itemStateChanged(ItemEvent e) {
            setBorder(isSelected() ? m_raised : m_lowered);
            setBackground(isSelected() ? raised_back : default_back);
        }

    }

}
