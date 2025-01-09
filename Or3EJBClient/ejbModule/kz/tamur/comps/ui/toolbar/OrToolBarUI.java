package kz.tamur.comps.ui.toolbar;

import static kz.tamur.rt.MainFrame.GRADIENT_MAIN_FRAME;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;

import kz.tamur.comps.Constants;
import kz.tamur.comps.ui.GradientPanel;

public class OrToolBarUI extends BasicToolBarUI {

    protected Container dockingSource;
    protected RootPaneContainer floatingToolBar;
    protected boolean floating;
    private int dockingSensitivity = 0;
    protected DragWindow dragWindow;
    protected int floatingX;
    protected int floatingY;
    protected OrToolBar orToolBar = null;

    public static ComponentUI createUI(JComponent c) {
        return new OrToolBarUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        if (toolBar instanceof OrToolBar) {
            orToolBar = (OrToolBar) toolBar;
        }
        floating = false;
        floatingToolBar = null;
        dockingSource = null;
        dragWindow = null;
        dockingSensitivity = 0;
        setOrientation( toolBar.getOrientation() );
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        floatingToolBar = null;
        dockingSource = null;
    }

    protected WindowListener createFrameListener() {
        return new FrameListener();
    }

    protected class FrameListener extends WindowAdapter {
        public void windowClosing(WindowEvent w) {
            if (toolBar.isFloatable() == true) {
                if (dragWindow != null)
                    dragWindow.setVisible(false);
                floating = false;
                if (floatingToolBar == null)
                    floatingToolBar = createFloatingWindow(toolBar);
                if (floatingToolBar instanceof Window)
                    ((Window) floatingToolBar).setVisible(false);
                floatingToolBar.getContentPane().remove(toolBar);
                String constraint = constraintBeforeFloating;
                if (toolBar.getOrientation() == JToolBar.HORIZONTAL) {
                    if (constraint == "West" || constraint == "East") {
                        constraint = "North";
                    }
                } else {
                    if (constraint == "North" || constraint == "South") {
                        constraint = "West";
                    }
                }
                if (dockingSource == null)
                    dockingSource = toolBar.getParent();
                if (propertyListener != null)
                    UIManager.removePropertyChangeListener(propertyListener);
                if (orToolBar == null) {
                    dockingSource.add(toolBar, constraint);
                } else {
                    String pos = orToolBar.getPositionInSplitPane();
                    if (!pos.isEmpty()) {
                        if (pos.equals(JSplitPane.LEFT)) {
                            ((JSplitPane) dockingSource).setLeftComponent(orToolBar);
                        } else if (pos.equals(JSplitPane.RIGHT)) {
                            ((JSplitPane) dockingSource).setRightComponent(orToolBar);
                        } else if (pos.equals(JSplitPane.TOP)) {
                            ((JSplitPane) dockingSource).setTopComponent(orToolBar);
                        } else if (pos.equals(JSplitPane.BOTTOM)) {
                            ((JSplitPane) dockingSource).setBottomComponent(orToolBar);
                        }
                    } else {
                        dockingSource.add(toolBar, constraint);
                    }
                }

                dockingSource.invalidate();
                Container dockingSourceParent = dockingSource.getParent();
                if (dockingSourceParent != null)
                    dockingSourceParent.validate();
                dockingSource.repaint();
            }
        }

    }

    public boolean isFloating() {
        return floating;
    }

    public void setFloating(boolean b, Point p) {
        if (toolBar.isFloatable() == true) {
            boolean visible = false;
            Window ancestor = SwingUtilities.getWindowAncestor(toolBar);
            if (ancestor != null) {
                visible = ancestor.isVisible();
            }
            if (dragWindow != null)
                dragWindow.setVisible(false);
            this.floating = b|| orToolBar == null ? false : orToolBar.isForceFloat();
            if (floatingToolBar == null) {
                floatingToolBar = createFloatingWindow(toolBar);
            }
            if (b == true) {
                if (dockingSource == null) {
                    dockingSource = toolBar.getParent();
                    dockingSource.remove(toolBar);
                }
                constraintBeforeFloating = calculateConstraint();
                if (propertyListener != null)
                    UIManager.addPropertyChangeListener(propertyListener);
                floatingToolBar.getContentPane().add(toolBar, BorderLayout.CENTER);
                if (floatingToolBar instanceof Window) {
                    ((Window) floatingToolBar).pack();
                    ((Window) floatingToolBar).setLocation(floatingX, floatingY);
                    if (visible) {
                        ((Window) floatingToolBar).show();
                    } else {
                        ancestor.addWindowListener(new WindowAdapter() {
                            public void windowOpened(WindowEvent e) {
                                ((Window) floatingToolBar).show();
                            }
                        });
                    }
                }
            } else {
                if (floatingToolBar == null)
                    floatingToolBar = createFloatingWindow(toolBar);
                if (floatingToolBar instanceof Window)
                    ((Window) floatingToolBar).setVisible(false);
                floatingToolBar.getContentPane().remove(toolBar);
                String constraint = getDockingConstraint(dockingSource, p);
                if (constraint == null) {
                    constraint = BorderLayout.NORTH;
                }
                 int orientation = mapConstraintToOrientation(constraint);
                 setOrientation(orientation);
                if (dockingSource == null) {
                    dockingSource = toolBar.getParent();
                }
                if (propertyListener != null) {
                    UIManager.removePropertyChangeListener(propertyListener);
                }
                if (orToolBar == null) {
                    dockingSource.add(toolBar, constraint);
                } else {
                    String pos = orToolBar.getPositionInSplitPane();
                    if (!pos.isEmpty()) {
                        if (pos.equals(JSplitPane.LEFT)) {
                            ((JSplitPane) dockingSource).setLeftComponent(orToolBar);
                        } else if (pos.equals(JSplitPane.RIGHT)) {
                            ((JSplitPane) dockingSource).setRightComponent(orToolBar);
                        } else if (pos.equals(JSplitPane.TOP)) {
                            ((JSplitPane) dockingSource).setTopComponent(orToolBar);
                        } else if (pos.equals(JSplitPane.BOTTOM)) {
                            ((JSplitPane) dockingSource).setBottomComponent(orToolBar);
                        }
                    }
                }
            }
            dockingSource.invalidate();
            Container dockingSourceParent = dockingSource.getParent();
            if (dockingSourceParent != null)
                dockingSourceParent.validate();
            dockingSource.repaint();
        }
    }

    protected DragWindow createDragWindow_(JToolBar toolbar) {
        Window frame = null;
        if (toolBar != null) {
            Container p;
            for (p = toolBar.getParent(); p != null && !(p instanceof Window); p = p.getParent())
                ;
            if (p != null && p instanceof Window)
                frame = (Window) p;
        }
        if (floatingToolBar == null) {
            floatingToolBar = createFloatingWindow(toolBar);
        }
        if (floatingToolBar instanceof Window)
            frame = (Window) floatingToolBar;
        DragWindow dragWindow = new DragWindow(frame);
        return dragWindow;
    }

    protected String calculateConstraint() {
        String constraint = null;
        LayoutManager lm = dockingSource.getLayout();
        if (lm instanceof BorderLayout) {
            constraint = (String) ((BorderLayout) lm).getConstraints(toolBar);
        }
        return (constraint != null) ? constraint : constraintBeforeFloating;
    }

    protected void dragTo(Point position, Point origin) {
        if (toolBar.isFloatable()) {
            try {
                if (dragWindow == null)
                    dragWindow = createDragWindow_(toolBar);
                Point offset = dragWindow.getOffset();
                if (offset == null) {
                   offset = new Point(position);
                    // точка за которую будет переноситься окно
                    dragWindow.setOffset(offset);
                }
                Point global = new Point(origin.x + position.x, origin.y + position.y);
                Point dragPoint = new Point(global.x - offset.x, global.y - offset.y);
                if (dockingSource == null)
                    dockingSource = toolBar.getParent();
                constraintBeforeFloating = calculateConstraint();
                Point dockingPosition = dockingSource.getLocationOnScreen();
                Point comparisonPoint = new Point(global.x - dockingPosition.x, global.y - dockingPosition.y);
                if (canDock(dockingSource, comparisonPoint)) {
                    dragWindow.setBackground(getDockingColor());
                    String constraint = getDockingConstraint(dockingSource, comparisonPoint);
                    int orientation = mapConstraintToOrientation(constraint);
                    dragWindow.setOrientation(orientation);
                    dragWindow.setBorderColor(dockingBorderColor);
                } else {
                    dragWindow.setBackground(getFloatingColor());
                    dragWindow.setBorderColor(floatingBorderColor);
                }

                dragWindow.setLocation(dragPoint.x, dragPoint.y);
                if (dragWindow.isVisible() == false) {
                    dragWindow.setSize(toolBar.getSize());
                    dragWindow.show();
                }
            } catch (IllegalComponentStateException e) {
            }
        }
    }

    public void floatAt2(Point position, Point origin) {
        floatAt( position,  origin);
    }
    protected void floatAt(Point position, Point origin) {
        if (toolBar.isFloatable()) {
            try {
                Point offset = dragWindow.getOffset();
                if (offset == null) {
                    offset = position;
                    dragWindow.setOffset(offset);
                }
                Point global = new Point(origin.x + position.x, origin.y + position.y);
                setFloatingLocation(global.x - offset.x, global.y - offset.y);
                if (dockingSource != null) {
                    Point dockingPosition = dockingSource.getLocationOnScreen();
                    Point comparisonPoint = new Point(global.x - dockingPosition.x, global.y - dockingPosition.y);
                    if (( orToolBar == null ? true : !orToolBar.isForceFloat())&&canDock(dockingSource, comparisonPoint)) {
                        setFloating(false, comparisonPoint);
                    } else {
                        setFloating(true, null);
                    }
                } else {
                    setFloating(true, null);
                }
                dragWindow.setOffset(null);
            } catch (IllegalComponentStateException e) {
            }
        }
    }

    public void setFloatingLocation(int x, int y) {
        floatingX = x;
        floatingY = y;
    }

    private String getDockingConstraint(Component c, Point p) {
        if (p == null)
            return constraintBeforeFloating;
        if (c.contains(p)) {
            dockingSensitivity = (toolBar.getOrientation() == JToolBar.HORIZONTAL) ? toolBar.getSize().height
                    : toolBar.getSize().width;
            // North (Base distance on height for now!)
            if (p.y < dockingSensitivity && !isBlocked(c, BorderLayout.NORTH)) {
                return BorderLayout.NORTH;
            }
            // East (Base distance on height for now!)
            if (p.x >= c.getWidth() - dockingSensitivity && !isBlocked(c, BorderLayout.EAST)) {
                return BorderLayout.EAST;
            }
            // West (Base distance on height for now!)
            if (p.x < dockingSensitivity && !isBlocked(c, BorderLayout.WEST)) {
                return BorderLayout.WEST;
            }
            if (p.y >= c.getHeight() - dockingSensitivity && !isBlocked(c, BorderLayout.SOUTH)) {
                return BorderLayout.SOUTH;
            }
        }
        return null;
    }

    private int mapConstraintToOrientation(String constraint) {
        int orientation = toolBar.getOrientation();

        if (constraint != null) {
            if (constraint.equals(BorderLayout.EAST) || constraint.equals(BorderLayout.WEST))
                orientation = JToolBar.VERTICAL;
            else if (constraint.equals(BorderLayout.NORTH) || constraint.equals(BorderLayout.SOUTH))
                orientation = JToolBar.HORIZONTAL;
        }

        return orientation;
    }

    private boolean isBlocked(Component comp, Object constraint) {
        if (comp instanceof Container) {
            Container cont = (Container) comp;
            LayoutManager lm = cont.getLayout();
            if (lm instanceof BorderLayout) {
                BorderLayout blm = (BorderLayout) lm;
                Component c = blm.getLayoutComponent(cont, constraint);
                return (c != null && c != toolBar);
            }
        }
        return false;
    }

    protected class DragWindow extends Window {
        Color borderColor = Color.gray;
        int orientation = toolBar.getOrientation();
        Point offset; // offset of the mouse cursor inside the DragWindow

        DragWindow(Window w) {
            super(w);
        }

        /**
         * Returns the orientation of the toolbar window when the toolbar is
         * floating. The orientation is either one of <code>JToolBar.HORIZONTAL</code> or <code>JToolBar.VERTICAL</code>.
         * 
         * @return the orientation of the toolbar window
         * @since 1.6
         */
        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int o) {
            if (isShowing()) {
                if (o == this.orientation)
                    return;
                this.orientation = o;
                Dimension size = toolBar.getSize();
                setSize(size);
                if (offset != null) {
                    if (toolBar.getComponentOrientation().isLeftToRight()) {
                        setOffset(new Point(offset.y, offset.x));
                    } else if (o == JToolBar.HORIZONTAL) {
                        setOffset(new Point(size.height - offset.y, offset.x));
                    } else {
                        setOffset(new Point(offset.y, size.width - offset.x));
                    }
                }
                repaint();
            }
        }

        public Point getOffset() {
            return offset;
        }

        public void setOffset(Point p) {
            this.offset = p;
        }

        public void setBorderColor(Color c) {
            if (this.borderColor == c)
                return;
            this.borderColor = c;
            repaint();
        }

        public Color getBorderColor() {
            return this.borderColor;
        }

        public void paint(Graphics g) {
            paintDragWindow(g);
            // Paint the children
            super.paint(g);
        }

        public Insets getInsets() {
            return new Insets(1, 1, 1, 1);
        }
    }

    public void setOrientation(int orientation) {
        toolBar.setOrientation(orientation);

        if (dragWindow != null)
            dragWindow.setOrientation(orientation);
    }

    protected void paintDragWindow(Graphics g) {
        g.setColor(dragWindow.getBackground());
        int w = dragWindow.getWidth();
        int h = dragWindow.getHeight();
        g.fillRect(0, 0, w, h);
        g.setColor(dragWindow.getBorderColor());
        g.drawRect(0, 0, w - 1, h - 1);
    }

    /**
     * Creates a window which contains the toolbar after it has been
     * dragged out from its container
     * 
     * @return a <code>RootPaneContainer</code> object, containing the toolbar.
     * @since 1.4
     */
    protected RootPaneContainer createFloatingWindow(JToolBar toolbar) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(toolbar), toolbar.getName());
        dialog.getRootPane().setName("ToolBar.FloatingWindow");
        dialog.setContentPane(new GradientPanel(dialog.getContentPane().getLayout()));
        ((GradientPanel) dialog.getContentPane()).setGradient(GRADIENT_MAIN_FRAME.isEmpty() ? Constants.GLOBAL_DEF_GRADIENT : GRADIENT_MAIN_FRAME);
        dialog.setTitle(toolbar.getName());
        dialog.setResizable(orToolBar == null ? false : orToolBar.isWindowResize());
        dialog.setSize(toolbar.getSize());
        toolBar.setPreferredSize(toolBar.getSize());
        WindowListener wl = createFrameListener();
        dialog.addWindowListener(wl);
        return dialog;
    }

    /**
     * @return the floatingToolBar
     */
    public RootPaneContainer getFloatingToolBar() {
        return floatingToolBar;
    }
}
