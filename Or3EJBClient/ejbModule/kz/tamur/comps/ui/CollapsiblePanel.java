package kz.tamur.comps.ui;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import kz.tamur.comps.Constants;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.comps.ui.collapsiblePanel.CollapsiblePanelListener;
import kz.tamur.comps.ui.collapsiblePanel.OrCollapsiblePanelStyle;
import kz.tamur.comps.ui.ext.DataProvider;
import kz.tamur.comps.ui.ext.OrientedIcon;
import kz.tamur.comps.ui.ext.StyleConstants;
import kz.tamur.comps.ui.ext.Timer;
import kz.tamur.comps.ui.ext.utils.CollectionUtils;
import kz.tamur.comps.ui.ext.utils.ImageUtils;
import kz.tamur.comps.ui.ext.utils.SwingUtils;
import kz.tamur.comps.ui.label.OrLabel;
import kz.tamur.comps.ui.label.OrRotationLabel;

/**
 * The Class CollapsiblePanel.
 * 
 * @author Lebedev Sergey
 */
public class CollapsiblePanel extends GradientPanel implements SwingConstants {

    /** animate. */
    private boolean animate = OrCollapsiblePanelStyle.animate;

    /** expand icon. */
    private ImageIcon expandIcon = OrCollapsiblePanelStyle.expandIcon;

    /** collapse icon. */
    private ImageIcon collapseIcon = OrCollapsiblePanelStyle.collapseIcon;

    /** state icon margin. */
    private Insets stateIconMargin = OrCollapsiblePanelStyle.stateIconMargin;

    /** rotate state icon. */
    private boolean rotateStateIcon = OrCollapsiblePanelStyle.rotateStateIcon;

    /** show state icon. */
    private boolean showStateIcon = OrCollapsiblePanelStyle.showStateIcon;

    /** state icon postion. */
    private int stateIconPostion = OrCollapsiblePanelStyle.stateIconPostion;

    /** title pane postion. */
    private int titlePanePostion = OrCollapsiblePanelStyle.titlePanePostion;

    /** content margin. */
    private Insets contentMargin = OrCollapsiblePanelStyle.contentMargin;

    /** listeners. */
    private List<CollapsiblePanelListener> listeners = new ArrayList<CollapsiblePanelListener>();

    /** cached expand icon. */
    private ImageIcon cachedExpandIcon = null;

    /** cached disabled expand icon. */
    private ImageIcon cachedDisabledExpandIcon = null;

    /** cached collapse icon. */
    private ImageIcon cachedCollapseIcon = null;

    /** cached disabled collapse icon. */
    private ImageIcon cachedDisabledCollapseIcon = null;

    /** state change enabled. */
    private DataProvider<Boolean> stateChangeEnabled = null;

    /** expanded. */
    private boolean expanded = true;

    /** expand state. */
    private float expandState = 1f;

    /** expand speed. */
    private float expandSpeed = 0.1f;

    /** animator. */
    private Timer animator = null;

    /** custom title. */
    private boolean customTitle = false;

    /** title component. */
    private Component titleComponent;

    /** content. */
    private Component content = null;

    /** header panel. */
    private GradientPanel headerPanel;

    /** expand button. */
    private OrTransparentButton expandButton;

    /** content panel. */
    private GradientPanel contentPanel;

    /** alignment text. */
    private int alignmentText;

    /** font color. */
    private Color fontColor;

    /** font g. */
    private Font fontG;
    private final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, BOTH,
            Constants.INSETS_0, 0, 0);

    /**
     * Конструктор класса collapsible panel.
     */
    public CollapsiblePanel() {
        this("");
    }

    /**
     * Конструктор класса collapsible panel.
     * 
     * @param title
     *            the title
     */
    public CollapsiblePanel(String title) {
        this(null, title);
    }

    /**
     * Конструктор класса collapsible panel.
     * 
     * @param icon
     *            the icon
     * @param title
     *            the title
     */
    public CollapsiblePanel(ImageIcon icon, String title) {
        this(icon, title, null);
    }

    /**
     * Конструктор класса collapsible panel.
     * 
     * @param title
     *            the title
     * @param content
     *            the content
     */
    public CollapsiblePanel(String title, Component content) {
        this(null, title, content);
    }

    /**
     * Конструктор класса collapsible panel.
     * 
     * @param icon
     *            the icon
     * @param title
     *            the title
     * @param content
     *            the content
     */
    public CollapsiblePanel(Icon icon, String title, Component content) {
        super();
        // putClientProperty ( SwingUtils.HANDLES_ENABLE_STATE, true );

        this.content = content;

        // setDrawFocus(true); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
        // setUndecorated(false); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
        // setWebColored(false); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
        // setRound(StyleConstants.smallRound); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
        setLayout(new BorderLayout(0, 0));

        // Header

        headerPanel = new GradientPanel();
        headerPanel.setOpaque(true);
        headerPanel.setGradient(new GradientColor("-1, -3355393, 1, 1, 0, 50, 1"));
        // headerPanel.setUndecorated(false); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
        // headerPanel.setShadeWidth(0); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
        headerPanel.setLayout(new BorderLayout());
        headerPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (isAllowAction(e)) {
                    takeFocus();
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (isAllowAction(e)) {
                    invertExpandState();
                }
            }

            private boolean isAllowAction(MouseEvent e) {
                return SwingUtilities.isLeftMouseButton(e) && SwingUtils.size(CollapsiblePanel.this).contains(e.getPoint());
            }
        });
        updateTitlePosition();

        updateDefaultTitleComponent(title, icon);
        updateDefaultTitleBorder();

        expandButton = new OrTransparentButton();
        expandButton.setIcon(collapseIcon);
        expandButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                invertExpandState();
                takeFocus();
            }
        });
        setStateIcons();
        updateStateIconMargin();
        updateStateIconPosition();

        // Content

        contentPanel = new GradientPanel() {
            public Dimension getPreferredSize() {
                Dimension ps = super.getPreferredSize();
                if (titlePanePostion == TOP || titlePanePostion == BOTTOM) {
                    if (CollapsiblePanel.this.content != null) {
                        Insets insets = getInsets();
                        ps.width = insets.left + CollapsiblePanel.this.content.getPreferredSize().width + insets.right;
                    }
                    if (expandState < 1f) {
                        ps.height = Math.round(ps.height * expandState);
                    }
                } else {
                    if (CollapsiblePanel.this.content != null) {
                        Insets insets = getInsets();
                        ps.height = insets.top + CollapsiblePanel.this.content.getPreferredSize().height + insets.bottom;
                    }
                    if (expandState < 1f) {
                        ps.width = Math.round(ps.width * expandState);
                    }
                }
                return ps;
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        // contentPanel.setMargin(contentMargin); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
        add(contentPanel, BorderLayout.CENTER);

        if (this.content != null) {
            contentPanel.add(this.content, gbc);
        }

        addPropertyChangeListener(OrLookAndFeel.COMPONENT_ORIENTATION_PROPERTY, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateStateIcons();
            }
        });
    }

    /**
     * Take focus.
     */
    private void takeFocus() {
        if (isShowing() && isEnabled()) {
            expandButton.requestFocusInWindow();
        }
    }

    /**
     * Update default title component.
     */
    public void updateDefaultTitleComponent() {
        updateDefaultTitleComponent(getTitle(), getIcon());
    }

    /**
     * Update default title component.
     * 
     * @param title
     *            the title
     * @param icon
     *            the icon
     */
    protected void updateDefaultTitleComponent(String title, Icon icon) {
        if (!customTitle) {
            if (titleComponent != null) {
                headerPanel.remove(titleComponent);
            }
            titleComponent = createDefaultTitleComponent(title, icon);
            headerPanel.add(titleComponent, BorderLayout.CENTER);
        }
    }

    /**
     * Update default title border.
     */
    private void updateDefaultTitleBorder() {
        if (titleComponent != null && !customTitle) {
            // Updating title margin according to title pane position
            boolean ltr = getComponentOrientation().isLeftToRight();
            Insets margin = getIcon() != null || titlePanePostion != LEFT || titlePanePostion == RIGHT ? new Insets(2, 2, 2, 2)
                    : new Insets(2, 4, 2, 2);
            if (titlePanePostion == LEFT) {
                margin = new Insets(margin.right, margin.top, margin.left, margin.bottom);
            } else if (titlePanePostion == RIGHT) {
                margin = new Insets(margin.left, margin.bottom, margin.right, margin.top);
            }
            ((OrLabel) titleComponent).setMargin(margin);
        }
    }

    /**
     * Update title position.
     */
    public void updateTitlePosition() {
        updateTitleSides();
        if (titlePanePostion == TOP) {
            add(headerPanel, BorderLayout.NORTH);
        } else if (titlePanePostion == BOTTOM) {
            add(headerPanel, BorderLayout.SOUTH);
        } else if (titlePanePostion == LEFT) {
            add(headerPanel, BorderLayout.LINE_START);
        } else if (titlePanePostion == RIGHT) {
            add(headerPanel, BorderLayout.LINE_END);
        }
        revalidate();
    }

    /**
     * Update title sides.
     */
    private void updateTitleSides() {
        // headerPanel.setDrawSides(expanded && titlePanePostion == BOTTOM, expanded && titlePanePostion == RIGHT, expanded && titlePanePostion == TOP, expanded && titlePanePostion == LEFT); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
    }

    /**
     * Update state icon position.
     */
    private void updateStateIconPosition() {
        if (showStateIcon) {
            if (titlePanePostion == TOP || titlePanePostion == BOTTOM) {
                headerPanel.add(expandButton, stateIconPostion == RIGHT ? BorderLayout.LINE_END : BorderLayout.LINE_START);
            } else if (titlePanePostion == LEFT) {
                headerPanel.add(expandButton, stateIconPostion == RIGHT ? BorderLayout.PAGE_START : BorderLayout.PAGE_END);
            } else if (titlePanePostion == RIGHT) {
                headerPanel.add(expandButton, stateIconPostion == RIGHT ? BorderLayout.PAGE_END : BorderLayout.PAGE_START);
            }
        } else {
            headerPanel.remove(expandButton);
        }
        headerPanel.revalidate();
    }

    /**
     * Update state icon margin.
     */
    private void updateStateIconMargin() {
        expandButton.setMargin(stateIconMargin);
    }

    /**
     * Creates the default title component.
     * 
     * @param title
     *            the title
     * @param icon
     *            the icon
     * @return j component
     */
    private JComponent createDefaultTitleComponent(String title, Icon icon) {
        // todo ltr!
        OrLabel defaultTitle;
        if (titlePanePostion == LEFT) {
            defaultTitle = new OrRotationLabel(title, icon, Constants.ROTATE_LEFT);
        } else if (titlePanePostion == RIGHT) {
            defaultTitle = new OrRotationLabel(title, icon, Constants.ROTATE_RIGHT);
        } else {
            defaultTitle = new OrLabel(title, icon);
        }
        defaultTitle.setDrawShade(true);
        defaultTitle.setFont(fontG);
        defaultTitle.setForeground(fontColor);
        defaultTitle.setHorizontalAlignment(alignmentText);

        return defaultTitle;
    }

    /**
     * State change enabler.
     * 
     * @return state change enabled
     */

    public DataProvider<Boolean> getStateChangeEnabled() {
        return stateChangeEnabled;
    }

    /**
     * Установить state change enabled.
     * 
     * @param stateChangeEnabled
     *            новое значение state change enabled
     */
    public void setStateChangeEnabled(DataProvider<Boolean> stateChangeEnabled) {
        this.stateChangeEnabled = stateChangeEnabled;
    }

    /**
     * Проверяет, является ли state change enabled.
     * 
     * @return <code>true</code>, если state change enabled
     */
    public boolean isStateChangeEnabled() {
        return stateChangeEnabled == null || stateChangeEnabled.provide();
    }

    /**
     * Collapse and expand methods.
     * 
     * @return <code>true</code>, если animating
     */

    public boolean isAnimating() {
        return animator != null && animator.isRunning();
    }

    /**
     * Invert expand state.
     * 
     * @return true, в случае успеха
     */
    public boolean invertExpandState() {
        return invertExpandState(animate);
    }

    /**
     * Invert expand state.
     * 
     * @param animate
     *            the animate
     * @return true, в случае успеха
     */
    public boolean invertExpandState(boolean animate) {
        return setExpanded(!isExpanded(), animate);
    }

    /**
     * Проверяет, является ли expanded.
     * 
     * @return <code>true</code>, если expanded
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * Sets the expanded.
     * 
     * @param expanded
     *            the expanded
     * @return true, в случае успеха
     */
    public boolean setExpanded(boolean expanded) {
        return setExpanded(expanded, isShowing() && animate);
    }

    /**
     * Sets the expanded.
     * 
     * @param expanded
     *            the expanded
     * @param animate
     *            the animate
     * @return true, в случае успеха
     */
    public boolean setExpanded(boolean expanded, boolean animate) {
        if (isEnabled()) {
            return expanded ? expand(animate) : collapse(animate);
        }
        return false;
    }

    /**
     * Collapse.
     * 
     * @return true, в случае успеха
     */
    public boolean collapse() {
        return collapse(animate);
    }

    /**
     * Collapse.
     * 
     * @param animate
     *            the animate
     * @return true, в случае успеха
     */
    public boolean collapse(boolean animate) {
        if (!expanded || !isStateChangeEnabled()) {
            return false;
        }

        stopAnimation();

        expanded = false;
        setStateIcons();
        fireCollapsing();

        if (animate && isShowing()) {
            animator = new Timer("CollapsiblePanel.collapseTimer", StyleConstants.fastAnimationDelay, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (expandState > 0f) {
                        expandState = Math.max(0f, expandState - expandSpeed);
                        CollapsiblePanel.this.revalidate();
                    } else {
                        expandState = 0f;
                        hideContent();
                        animator.stop();
                    }
                }
            });
            animator.start();
        } else {
            expandState = 0f;
            hideContent();
        }
        return true;
    }

    /**
     * Hide content.
     */
    private void hideContent() {
        // Hide title border
        updateTitleSides();

        // Hide content
        if (content != null) {
            content.setVisible(false);
        }

        // Update collapsible pane
        revalidate();
        repaint();

        // Inform about event
        fireCollapsed();
    }

    /**
     * Expand.
     * 
     * @return true, в случае успеха
     */
    public boolean expand() {
        return expand(animate);
    }

    /**
     * Expand.
     * 
     * @param animate
     *            the animate
     * @return true, в случае успеха
     */
    public boolean expand(boolean animate) {
        if (expanded || !isStateChangeEnabled()) {
            return false;
        }

        stopAnimation();

        expanded = true;
        setStateIcons();

        if (content != null) {
            content.setVisible(true);
        }

        // Show title border
        updateTitleSides();

        fireExpanding();

        if (animate && isShowing()) {
            animator = new Timer("CollapsiblePanel.expandTimer", StyleConstants.fastAnimationDelay, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (expandState < 1f) {
                        expandState = Math.min(1f, expandState + expandSpeed);
                        CollapsiblePanel.this.revalidate();
                    } else {
                        expandState = 1f;
                        showContent();
                        animator.stop();
                    }
                }
            });
            animator.start();
        } else {
            expandState = 1f;
            showContent();
        }
        return true;
    }

    /**
     * Show content.
     */
    private void showContent() {
        CollapsiblePanel.this.revalidate();
        CollapsiblePanel.this.repaint();
        fireExpanded();
    }

    /**
     * Stop animation.
     */
    private void stopAnimation() {
        if (animator != null && animator.isRunning()) {
            animator.stop();
        }
    }

    /**
     * Title pane position.
     * 
     * @return title pane postion
     */

    public int getTitlePanePostion() {
        return titlePanePostion;
    }

    /**
     * Установить title pane postion.
     * 
     * @param titlePanePostion
     *            новое значение title pane postion
     */
    public void setTitlePanePostion(int titlePanePostion) {
        this.titlePanePostion = titlePanePostion;
        updateDefaultTitleComponent();
        updateDefaultTitleBorder();
        updateTitlePosition();
        updateStateIcons();
        updateStateIconPosition();
    }

    /**
     * Content component margin.
     * 
     * @return content margin
     */

    public Insets getContentMargin() {
        return contentMargin;
    }

    /**
     * Установить content margin.
     * 
     * @param contentMargin
     *            новое значение content margin
     */
    public void setContentMargin(Insets contentMargin) {
        this.contentMargin = contentMargin;
        // contentPanel.setMargin(contentMargin); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
        revalidate();
    }

    /**
     * Sets the content margin.
     * 
     * @param top
     *            the top
     * @param left
     *            the left
     * @param bottom
     *            the bottom
     * @param right
     *            the right
     */
    public void setContentMargin(int top, int left, int bottom, int right) {
        setContentMargin(new Insets(top, left, bottom, right));
    }

    /**
     * Установить content margin.
     * 
     * @param margin
     *            новое значение content margin
     */
    public void setContentMargin(int margin) {
        setContentMargin(margin, margin, margin, margin);
    }

    /**
     * Should animate expand and collapse transitions.
     * 
     * @return <code>true</code>, если animate
     */

    public boolean isAnimate() {
        return animate;
    }

    /**
     * Установить animate.
     * 
     * @param animate
     *            новое значение animate
     */
    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    /**
     * Title text.
     * 
     * @param title
     *            новое значение title
     */

    public void setTitle(String title) {
        if (!customTitle) {
            ((OrLabel) titleComponent).setText(title);
        }
    }

    /**
     * Получить title.
     * 
     * @return title
     */
    public String getTitle() {
        return customTitle ? null : ((OrLabel) titleComponent).getText();
    }

    /**
     * Title icon.
     * 
     * @param icon
     *            новое значение icon
     */

    public void setIcon(Icon icon) {
        if (!customTitle) {
            if (titleComponent instanceof OrRotationLabel) {
                ((OrRotationLabel) titleComponent).setIconSuperClass(icon);
            } else {
                ((OrLabel) titleComponent).setIcon(icon);
            }
            updateDefaultTitleBorder();
        }
    }

    /**
     * Получить icon.
     * 
     * @return icon
     */
    public Icon getIcon() {
        return customTitle ? null : ((OrLabel) titleComponent).getIcon();
    }

    /**
     * Collapse button icon.
     * 
     * @return collapse icon
     */

    public ImageIcon getCollapseIcon() {
        return collapseIcon;
    }

    /**
     * Установить collapse icon.
     * 
     * @param collapseIcon
     *            новое значение collapse icon
     */
    public void setCollapseIcon(ImageIcon collapseIcon) {
        this.collapseIcon = collapseIcon;
        clearCachedCollapseIcons();
        setStateIcons();
    }

    /**
     * Expand button icon.
     * 
     * @return expand icon
     */

    public ImageIcon getExpandIcon() {
        return expandIcon;
    }

    /**
     * Установить expand icon.
     * 
     * @param expandIcon
     *            новое значение expand icon
     */
    public void setExpandIcon(ImageIcon expandIcon) {
        this.expandIcon = expandIcon;
        clearCachedExpandIcons();
        setStateIcons();
    }

    /**
     * State icon margin.
     * 
     * @return state icon margin
     */

    public Insets getStateIconMargin() {
        return stateIconMargin;
    }

    /**
     * Установить state icon margin.
     * 
     * @param stateIconMargin
     *            новое значение state icon margin
     */
    public void setStateIconMargin(Insets stateIconMargin) {
        this.stateIconMargin = stateIconMargin;
        updateStateIconMargin();
    }

    /**
     * Should rotate state icon according to title position.
     * 
     * @return <code>true</code>, если rotate state icon
     */

    public boolean isRotateStateIcon() {
        return rotateStateIcon;
    }

    /**
     * Установить rotate state icon.
     * 
     * @param rotateStateIcon
     *            новое значение rotate state icon
     */
    public void setRotateStateIcon(boolean rotateStateIcon) {
        this.rotateStateIcon = rotateStateIcon;
        updateStateIcons();
    }

    /**
     * Should display state icon.
     * 
     * @return <code>true</code>, если show state icon
     */

    public boolean isShowStateIcon() {
        return showStateIcon;
    }

    /**
     * Установить show state icon.
     * 
     * @param showStateIcon
     *            новое значение show state icon
     */
    public void setShowStateIcon(boolean showStateIcon) {
        this.showStateIcon = showStateIcon;
        updateStateIconPosition();
    }

    /**
     * State icon position in title pane.
     * 
     * @return state icon postion
     */

    public int getStateIconPostion() {
        return stateIconPostion;
    }

    /**
     * Установить state icon postion.
     * 
     * @param stateIconPostion
     *            новое значение state icon postion
     */
    public void setStateIconPostion(int stateIconPostion) {
        this.stateIconPostion = stateIconPostion;
        updateStateIconPosition();
    }

    /**
     * Collapse and expand icons update methods.
     */

    private void updateStateIcons() {
        clearCachedCollapseIcons();
        clearCachedExpandIcons();
        setStateIcons();
    }

    /**
     * Sets the state icons.
     */
    private void setStateIcons() {
        expandButton.setIcon(expanded ? getCachedCollapseIcon() : getCachedExpandIcon());
        expandButton.setDisabledIcon(expanded ? getCachedDisabledCollapseIcon() : getCachedDisabledExpandIcon());
    }

    /**
     * Clear cached collapse icons.
     */
    private void clearCachedCollapseIcons() {
        cachedCollapseIcon = null;
        cachedDisabledCollapseIcon = null;
    }

    /**
     * Получить cached collapse icon.
     * 
     * @return cached collapse icon
     */
    private ImageIcon getCachedCollapseIcon() {
        if (cachedCollapseIcon == null) {
            boolean ltr = getComponentOrientation().isLeftToRight();
            if (!rotateStateIcon || titlePanePostion == TOP || titlePanePostion == BOTTOM) {
                cachedCollapseIcon = new OrientedIcon(collapseIcon);
            } else if (titlePanePostion == LEFT) {
                cachedCollapseIcon = ImageUtils.rotateImage90CCW(collapseIcon);
            } else if (titlePanePostion == RIGHT) {
                cachedCollapseIcon = ImageUtils.rotateImage90CW(collapseIcon);
            }
        }
        return cachedCollapseIcon;
    }

    /**
     * Получить cached disabled collapse icon.
     * 
     * @return cached disabled collapse icon
     */
    private ImageIcon getCachedDisabledCollapseIcon() {
        if (cachedDisabledCollapseIcon == null) {
            cachedDisabledCollapseIcon = ImageUtils.createDisabledCopy(getCachedCollapseIcon());
        }
        return cachedDisabledCollapseIcon;
    }

    /**
     * Clear cached expand icons.
     */
    private void clearCachedExpandIcons() {
        cachedExpandIcon = null;
        cachedDisabledExpandIcon = null;
    }

    /**
     * Получить cached expand icon.
     * 
     * @return cached expand icon
     */
    private ImageIcon getCachedExpandIcon() {
        if (cachedExpandIcon == null) {
            boolean ltr = getComponentOrientation().isLeftToRight();
            if (!rotateStateIcon || titlePanePostion == TOP || titlePanePostion == BOTTOM) {
                cachedExpandIcon = expandIcon;
            } else if (ltr ? titlePanePostion == LEFT : titlePanePostion == RIGHT) {
                cachedExpandIcon = ImageUtils.rotateImage90CCW(expandIcon);
            } else if (ltr ? titlePanePostion == RIGHT : titlePanePostion == LEFT) {
                cachedExpandIcon = ImageUtils.rotateImage90CW(expandIcon);
            }
        }
        return cachedExpandIcon;
    }

    /**
     * Получить cached disabled expand icon.
     * 
     * @return cached disabled expand icon
     */
    private ImageIcon getCachedDisabledExpandIcon() {
        if (cachedDisabledExpandIcon == null) {
            cachedDisabledExpandIcon = ImageUtils.createDisabledCopy(getCachedExpandIcon());
        }
        return cachedDisabledExpandIcon;
    }

    /**
     * Header panel.
     * 
     * @return header panel
     */

    public GradientPanel getHeaderPanel() {
        return headerPanel;
    }

    /**
     * Expand button.
     * 
     * @return expand button
     */

    public OrTransparentButton getExpandButton() {
        return expandButton;
    }

    /**
     * Collapsible pane title component.
     * 
     * @return title component
     */

    public Component getTitleComponent() {
        return titleComponent;
    }

    /**
     * Установить title component.
     * 
     * @param component
     *            новое значение title component
     */
    public void setTitleComponent(Component component) {
        if (titleComponent != null) {
            headerPanel.remove(titleComponent);
        }
        if (component != null) {
            headerPanel.add(component, BorderLayout.CENTER);
        }
        titleComponent = component;
        customTitle = true;
    }

    /**
     * Collapsible pane content.
     * 
     * @return content
     */

    public Component getContent() {
        return content;
    }

    /**
     * Установить content.
     * 
     * @param content
     *            новое значение content
     */
    public void setContent(Component newContent) {
        if (content != null) {
            contentPanel.remove(content);
        }
        content = newContent;
        content.setVisible(expandState > 0f);
        contentPanel.add(content, gbc);
        revalidate();
    }

    /**
     * Collapsible pane listeners.
     * 
     * @return listeners
     */

    public List<CollapsiblePanelListener> getListeners() {
        return listeners;
    }

    /**
     * Установить listeners.
     * 
     * @param listeners
     *            новое значение listeners
     */
    public void setListeners(List<CollapsiblePanelListener> listeners) {
        this.listeners = listeners;
    }

    /**
     * Adds the collapsible pane listener.
     * 
     * @param listener
     *            the listener
     */
    public void addCollapsiblePaneListener(CollapsiblePanelListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the collapsible pane listener.
     * 
     * @param listener
     *            the listener
     */
    public void removeCollapsiblePaneListener(CollapsiblePanelListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fire collapsing.
     */
    private void fireCollapsing() {
        for (CollapsiblePanelListener listener : CollectionUtils.clone(listeners)) {
            listener.collapsing(this);
        }
    }

    /**
     * Fire collapsed.
     */
    private void fireCollapsed() {
        for (CollapsiblePanelListener listener : CollectionUtils.clone(listeners)) {
            listener.collapsed(this);
        }
    }

    /**
     * Fire expanding.
     */
    private void fireExpanding() {
        for (CollapsiblePanelListener listener : CollectionUtils.clone(listeners)) {
            listener.expanding(this);
        }
    }

    /**
     * Fire expanded.
     */
    private void fireExpanded() {
        for (CollapsiblePanelListener listener : CollectionUtils.clone(listeners)) {
            listener.expanded(this);
        }
    }

    /**
     * Visible content part.
     * 
     * @return expand state
     */

    public float getExpandState() {
        return expandState;
    }

    /**
     * Base preferred size (without content size).
     * 
     * @return base preferred size
     */

    public Dimension getBasePreferredSize() {
        Dimension ps = getPreferredSize();
        if (content == null || expandState <= 0) {
            return ps;
        } else {
            Dimension cps = content.getPreferredSize();
            if (titlePanePostion == TOP || titlePanePostion == BOTTOM) {
                return new Dimension(ps.width, ps.height - Math.round(cps.height * expandState));
            } else {
                return new Dimension(ps.width - Math.round(cps.width * expandState), ps.height);
            }
        }
    }

    /**
     * Установить title alignment text.
     * 
     * @param alignmentText
     *            the alignmentText to set
     */
    public void setTitleAlignmentText(int alignmentText) {
        this.alignmentText = alignmentText;
        ((OrLabel) titleComponent).setHorizontalAlignment(alignmentText);
    }

    /**
     * Установить title font color.
     * 
     * @param color
     *            новое значение title font color
     */
    public void setTitleFontColor(Color color) {
        fontColor = color;
        ((OrLabel) titleComponent).setForeground(color);
    }

    /**
     * Установить title font.
     * 
     * @param font
     *            новое значение title font
     */
    public void setTitleFont(Font font) {
        fontG = font;
        ((OrLabel) titleComponent).setFont(font);

    }

    /**
     * @return the alignmentText
     */
    public int getTitleAlignmentText() {
        return alignmentText;
    }

    /**
     * @return the fontColor
     */
    public Color getTitleFontColor() {
        return fontColor;
    }

    /**
     * @return the fontG
     */
    public Font getTitleFontG() {
        return fontG;
    }
}
