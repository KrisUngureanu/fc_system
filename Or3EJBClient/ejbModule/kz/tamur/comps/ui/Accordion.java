package kz.tamur.comps.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import kz.tamur.comps.ui.accordion.AccordionStyle;
import kz.tamur.comps.ui.accordion.OrAccordionStyle;
import kz.tamur.comps.ui.collapsiblePanel.CollapsiblePanelAdapter;
import kz.tamur.comps.ui.collapsiblePanel.CollapsiblePanelListener;
import kz.tamur.comps.ui.ext.DataProvider;

/**
 * The Class Accordion.
 *
 * @author Lebedev Sergey
 */
public class Accordion extends GradientPanel implements SwingConstants {
    // Settings
    /** animate. */
    private boolean animate = OrAccordionStyle.animate;
    
    /** orientation. */
    private int orientation = OrAccordionStyle.orientation;
    
    /** expand icon. */
    private ImageIcon expandIcon = OrAccordionStyle.expandIcon;
    
    /** collapse icon. */
    private ImageIcon collapseIcon = OrAccordionStyle.collapseIcon;
    
    /** accordion style. */
    private AccordionStyle accordionStyle = OrAccordionStyle.accordionStyle;
    
    /** fill space. */
    private boolean fillSpace = OrAccordionStyle.fillSpace;
    
    /** multiply selection allowed. */
    private boolean multiplySelectionAllowed = OrAccordionStyle.multiplySelectionAllowed;
    
    /** gap. */
    private int gap = OrAccordionStyle.gap;

    // Accordion layout that positions all of the components
    /** accordion layout. */
    private AccordionLayout accordionLayout;

    // Accordion collapsible panes
    /** panes. */
    private List<CollapsiblePanel> panes = new ArrayList<CollapsiblePanel>();
    
    /** listeners. */
    private List<CollapsiblePanelListener> listeners = new ArrayList<CollapsiblePanelListener>();

    /**
     * Basic constructors.
     */

    public Accordion() {
        this(OrAccordionStyle.accordionStyle);
    }

    /**
     * Конструктор класса accordion.
     *
     * @param accordionStyle the accordion style
     */
    public Accordion(AccordionStyle accordionStyle) {
        super();
        // setDrawFocus(true); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
        // setWebColored(false); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
        setLayout(new AccordionLayout());
        updatePanesBorderStyling();
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
     * @param animate новое значение animate
     */
    public void setAnimate(boolean animate) {
        this.animate = animate;
        updatePanesBorderStyling();
    }

    /**
     * Update panes animation.
     */
    private void updatePanesAnimation() {
        for (CollapsiblePanel pane : panes) {
            pane.setAnimate(animate);
        }
    }

    /**
     * Accordion panes orientation.
     *
     * @return orientation
     */

    public int getOrientation() {
        return orientation;
    }

    
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        updatePanesBorderStyling();
    }

    /**
     * Accordion panes icons.
     *
     * @return expand icon
     */

    public ImageIcon getExpandIcon() {
        return expandIcon;
    }

    /**
     * Установить expand icon.
     *
     * @param expandIcon новое значение expand icon
     */
    public void setExpandIcon(ImageIcon expandIcon) {
        this.expandIcon = expandIcon;
        updatePaneIcons();
    }

    /**
     * Получить collapse icon.
     *
     * @return collapse icon
     */
    public ImageIcon getCollapseIcon() {
        return collapseIcon;
    }

    /**
     * Установить collapse icon.
     *
     * @param collapseIcon новое значение collapse icon
     */
    public void setCollapseIcon(ImageIcon collapseIcon) {
        this.collapseIcon = collapseIcon;
        updatePaneIcons();
    }

    /**
     * Update pane icons.
     */
    private void updatePaneIcons() {
        for (CollapsiblePanel pane : panes) {
            pane.setExpandIcon(expandIcon);
            pane.setCollapseIcon(collapseIcon);
        }
    }

    /**
     * Accordion style type.
     *
     * @return accordion style
     */

    public AccordionStyle getAccordionStyle() {
        return accordionStyle;
    }

    /**
     * Установить accordion style.
     *
     * @param accordionStyle новое значение accordion style
     */
    public void setAccordionStyle(AccordionStyle accordionStyle) {
        this.accordionStyle = accordionStyle;
        updatePanesBorderStyling();
    }

    /**
     * Fill the whole available for accordion space with expanded panes.
     *
     * @return <code>true</code>, если fill space
     */

    public boolean isFillSpace() {
        return fillSpace;
    }

    /**
     * Установить fill space.
     *
     * @param fillSpace новое значение fill space
     */
    public void setFillSpace(boolean fillSpace) {
        this.fillSpace = fillSpace;
        revalidate();
    }

    /**
     * Allow multiply expanded panes.
     *
     * @return <code>true</code>, если multiply selection allowed
     */

    public boolean isMultiplySelectionAllowed() {
        return multiplySelectionAllowed;
    }

    /**
     * Установить multiply selection allowed.
     *
     * @param multiplySelectionAllowed новое значение multiply selection allowed
     */
    public void setMultiplySelectionAllowed(boolean multiplySelectionAllowed) {
        this.multiplySelectionAllowed = multiplySelectionAllowed;
        updateExpandState(-1);
    }

    /**
     * Update expand state.
     *
     * @param index the index
     */
    protected void updateExpandState(int index) {
        if (!multiplySelectionAllowed) {
            for (int i = 0; i < panes.size(); i++) {
                CollapsiblePanel pane = panes.get(i);
                if (index == -1 && pane.isExpanded()) {
                    index = i;
                }
                if (index != -1 && i != index && pane.isExpanded()) {
                    pane.setExpanded(false);
                }
            }
        }
    }

    /**
     * Gap between panes.
     *
     * @return gap
     */

    public int getGap() {
        return gap;
    }

    /**
     * Установить gap.
     *
     * @param gap новое значение gap
     */
    public void setGap(int gap) {
        this.gap = gap;
        revalidate();
    }

    /**
     * Panes add/remove methods.
     *
     * @param title the title
     * @param content the content
     */

    public void addPane(String title, Component content) {
        addPane(panes.size(), title, content);
    }

    /**
     * Adds the pane.
     *
     * @param index the index
     * @param title the title
     * @param content the content
     */
    public void addPane(int index, String title, Component content) {
        addPane(index, new CollapsiblePanel(title, content));
    }

    /**
     * Adds the pane.
     *
     * @param icon the icon
     * @param title the title
     * @param content the content
     */
    public void addPane(Icon icon, String title, Component content) {
        addPane(panes.size(), icon, title, content);
    }

    /**
     * Adds the pane.
     *
     * @param index the index
     * @param icon the icon
     * @param title the title
     * @param content the content
     */
    public void addPane(int index, Icon icon, String title, Component content) {
        addPane(index, new CollapsiblePanel(icon, title, content));
    }

    /**
     * Adds the pane.
     *
     * @param title the title
     * @param content the content
     */
    public void addPane(Component title, Component content) {
        addPane(panes.size(), title, content);
    }

    /**
     * Adds the pane.
     *
     * @param index the index
     * @param title the title
     * @param content the content
     */
    public void addPane(int index, Component title, Component content) {
        CollapsiblePanel pane = new CollapsiblePanel("", content);
        pane.setTitleComponent(title);
        addPane(index, pane);
    }

    /**
     * Adds the pane.
     *
     * @param index the index
     * @param pane the pane
     */
    private void addPane(int index, final CollapsiblePanel pane) {
        // Animation
        pane.setAnimate(animate);

        // Proper icons
        pane.setExpandIcon(expandIcon);
        pane.setCollapseIcon(collapseIcon);

        // Collapsing new pane if needed
        if (!multiplySelectionAllowed && isAnySelected()) {
            pane.setExpanded(false, false);
        }

        // State change enabler
        pane.setStateChangeEnabled(new DataProvider<Boolean>() {
            public Boolean provide() {
                return !fillSpace || !pane.isExpanded() || getSelectionCount() > 1;
            }
        });

        // Adding new listener
        CollapsiblePanelListener cpl = new CollapsiblePanelAdapter() {
            @Override
            public void expanding(CollapsiblePanel pane) {
                updateExpandState(panes.indexOf(pane));
            }

            @Override
            public void expanded(CollapsiblePanel pane) {
            }

            @Override
            public void collapsing(CollapsiblePanel pane) {
            }

            @Override
            public void collapsed(CollapsiblePanel pane) {
            }

         
        };
        pane.addCollapsiblePaneListener(cpl);
        listeners.add(cpl);

        // Adding new pane
        add(index, pane);
        panes.add(index, pane);

        // Updating accordion
        updatePanesBorderStyling();
    }

    /**
     * Removes the pane.
     *
     * @param index the index
     */
    public void removePane(int index) {
        removePane(panes.get(index));
    }

    /**
     * Removes the pane.
     *
     * @param pane the pane
     */
    private void removePane(CollapsiblePanel pane) {
        int index = panes.indexOf(pane);

        // State change enabler
        pane.setStateChangeEnabled(null);

        // Removing pane listener
        pane.removeCollapsiblePaneListener(listeners.get(index));
        listeners.remove(index);

        // Removing pane
        remove(pane);
        panes.remove(index);

        // Updating accordion
        updatePanesBorderStyling();
    }

    /**
     * Updates panes styling according to accordion settings.
     */

    private void updatePanesBorderStyling() {
        boolean united = accordionStyle.equals(AccordionStyle.united);
        boolean separated = accordionStyle.equals(AccordionStyle.separated);
        boolean hor = orientation == HORIZONTAL;

        // Accordion decoration
   //     setUndecorated(!united); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI

        // Panes decoration
        for (int i = 0; i < panes.size(); i++) {
            CollapsiblePanel pane = panes.get(i);
            pane.setTitlePanePostion(hor ? LEFT : TOP);
            if (separated) {
       //         pane.setShadeWidth(OrPanelStyle.shadeWidth); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
        //        pane.setDrawSides(separated, separated, separated, separated); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
            } else {
        //        pane.setShadeWidth(0); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
         //       pane.setDrawSides(!hor && i > 0, hor && i > 0, false, false); TODO НЕ УБИРАТЬ! код необходим при включении OrPanelUI
            }
        }

        // Updating accordion
        revalidate();
        repaint();
    }

    /**
     * Selection methods.
     *
     * @return <code>true</code>, если any selected
     */

    public boolean isAnySelected() {
        for (CollapsiblePanel pane : panes) {
            if (pane.isExpanded()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Получить first selected index.
     *
     * @return first selected index
     */
    public int getFirstSelectedIndex() {
        for (CollapsiblePanel pane : panes) {
            if (pane.isExpanded()) {
                return panes.indexOf(pane);
            }
        }
        return -1;
    }

    /**
     * Получить selection count.
     *
     * @return selection count
     */
    public int getSelectionCount() {
        int count = 0;
        for (CollapsiblePanel pane : panes) {
            if (pane.isExpanded()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Collapsible panes settings methods.
     *
     * @param index the index
     * @return icon at
     */

    public Icon getIconAt(int index) {
        return panes.get(index).getIcon();
    }

    /**
     * Sets the icon at.
     *
     * @param index the index
     * @param icon the icon
     */
    public void setIconAt(int index, Icon icon) {
        panes.get(index).setIcon(icon);
    }

    /**
     * Получить title at.
     *
     * @param index the index
     * @return title at
     */
    public String getTitleAt(int index) {
        return panes.get(index).getTitle();
    }

    /**
     * Sets the title at.
     *
     * @param index the index
     * @param title the title
     */
    public void setTitleAt(int index, String title) {
        panes.get(index).setTitle(title);
    }

    /**
     * Получить title component at.
     *
     * @param index the index
     * @return title component at
     */
    public Component getTitleComponentAt(int index) {
        return panes.get(index).getTitleComponent();
    }

    /**
     * Sets the title component at.
     *
     * @param index the index
     * @param titleComponent the title component
     */
    public void setTitleComponentAt(int index, Component titleComponent) {
        panes.get(index).setTitleComponent(titleComponent);
    }

    /**
     * Получить content at.
     *
     * @param index the index
     * @return content at
     */
    public Component getContentAt(int index) {
        return panes.get(index).getContent();
    }

    /**
     * Sets the content at.
     *
     * @param index the index
     * @param content the content
     */
    public void setContentAt(int index, Component content) {
        panes.get(index).setContent(content);
    }

    /**
     * Получить content margin at.
     *
     * @param index the index
     * @return content margin at
     */
    public Insets getContentMarginAt(int index) {
        return panes.get(index).getContentMargin();
    }

    /**
     * Sets the content margin at.
     *
     * @param index the index
     * @param margin the margin
     */
    public void setContentMarginAt(int index, Insets margin) {
        panes.get(index).setContentMargin(margin);
    }

    /**
     * Sets the content margin at.
     *
     * @param index the index
     * @param top the top
     * @param left the left
     * @param bottom the bottom
     * @param right the right
     */
    public void setContentMarginAt(int index, int top, int left, int bottom, int right) {
        setContentMarginAt(index, new Insets(top, left, bottom, right));
    }

    /**
     * Sets the content margin at.
     *
     * @param index the index
     * @param margin the margin
     */
    public void setContentMarginAt(int index, int margin) {
        setContentMarginAt(index, margin, margin, margin, margin);
    }

    /**
     * Special accordion layout.
     *
     * @author Lebedev Sergey
     */

    private class AccordionLayout implements LayoutManager {
        
        
        public void addLayoutComponent(String name, Component comp) {
            //
        }

        
        public void removeLayoutComponent(Component comp) {
            //
        }

        
        public void layoutContainer(Container parent) {
            Insets insets = parent.getInsets();
            Dimension size = parent.getSize();
            int x = insets.left;
            int y = insets.top;
            int w = size.width - insets.left - insets.right;
            int h = size.height - insets.top - insets.bottom;
            boolean hor = orientation == HORIZONTAL;
            if (fillSpace) {
                // Computing the part available to fill in with panes content
                float totalStates = 0;
                int totalFillLength = hor ? size.width - insets.left - insets.right : size.height - insets.top - insets.bottom
                        + gap;
                int visuallyExpanded = 0;
                int lastFillIndex = -1;
                List<Integer> base = new ArrayList<Integer>();
                for (CollapsiblePanel pane : panes) {
                    Dimension bps = pane.getBasePreferredSize();
                    base.add(hor ? bps.width : bps.height);

                    float expandState = pane.getExpandState();

                    totalStates += expandState;
                    totalFillLength -= hor ? bps.width : bps.height + gap;

                    if (expandState > 0f) {
                        lastFillIndex = panes.indexOf(pane);
                        visuallyExpanded++;
                    }
                }
                totalStates = visuallyExpanded == 1 ? 1f : totalStates;
                totalFillLength = Math.max(totalFillLength, 0);

                // Layouting panes
                float end = 0f;
                for (int i = 0; i < panes.size(); i++) {
                    float expandState = panes.get(i).getExpandState();
                    int length = base.get(i);
                    if (expandState > 0f) {
                        end += (totalFillLength * expandState / totalStates) % 1;
                        length += Math.round((float) Math.floor(totalFillLength * expandState / totalStates))
                                + (i == lastFillIndex ? Math.round(end) : 0);
                    }
                    panes.get(i).setBounds(x, y, hor ? length : w, hor ? h : length);
                    if (hor) {
                        x += length + gap;
                    } else {
                        y += length + gap;
                    }
                }
            } else {
                // Simply layouting panes by preferred size
                for (CollapsiblePanel pane : panes) {
                    Dimension cps = pane.getPreferredSize();
                    pane.setBounds(x, y, hor ? cps.width : w, hor ? h : cps.height);
                    if (hor) {
                        x += cps.width + gap;
                    } else {
                        y += cps.height + gap;
                    }
                }
            }
        }

        
        public Dimension preferredLayoutSize(Container parent) {
            return getSize(parent, true);
        }

        
        public Dimension minimumLayoutSize(Container parent) {
            return getSize(parent, false);
        }

        /**
         * Получить size.
         *
         * @param parent the parent
         * @param preferred the preferred
         * @return size
         */
        private Dimension getSize(Container parent, boolean preferred) {
            Dimension ps = new Dimension();
            boolean hor = orientation == HORIZONTAL;
            for (CollapsiblePanel pane : panes) {
                Dimension cps = preferred || !fillSpace ? pane.getPreferredSize() : pane.getBasePreferredSize();
                if (hor) {
                    ps.width += cps.width;
                    ps.height += Math.max(ps.height, cps.height);
                } else {
                    ps.width = Math.max(ps.width, cps.width);
                    ps.height += cps.height;
                }
            }
            if (panes.size() > 0) {
                if (hor) {
                    ps.width += gap * (panes.size() - 1);
                } else {
                    ps.height += gap * (panes.size() - 1);
                }
            }

            Insets insets = parent.getInsets();
            ps.width += insets.left + insets.right;
            ps.height += insets.top + insets.bottom;
            return ps;
        }
    }

    /**
     * Получить panes.
     *
     * @return the panes
     */
    public List<CollapsiblePanel> getPanes() {
        return panes;
    }
    
    /**
     * Получить panel at.
     *
     * @param index the index
     * @return panel at
     */
    public CollapsiblePanel getPanelAt(int index) {
        return panes.get(index);
    }
}
