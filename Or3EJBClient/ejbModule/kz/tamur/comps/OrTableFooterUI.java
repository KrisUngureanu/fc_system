package kz.tamur.comps;


import kz.tamur.rt.adapters.*;

import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.MouseInputListener;
import kz.tamur.rt.Utils;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by IntelliJ IDEA.
 * User: Valera
 * Date: 29.01.2004
 * Time: 17:50:39
 * To change this template use Options | File Templates.
 */
public class OrTableFooterUI extends BasicTableHeaderUI  {
// Instance Variables
  /** The JTableHeader that is delegating the painting to this UI. */
    protected OrTableFooter header;
    protected CellRendererPane rendererPane;
 // Listeners that are attached to the JTable
    protected MouseInputListener mouseInputListener;
    private JTable table;
    private List columns;
    private JLabel label = new JLabel();
    private DecimalFormat floatF;
    private DecimalFormat intF;

//
//  The installation/uninstall procedures and support
//

    public static ComponentUI createUI(JComponent h, JTable table, List columns) {
        return new OrTableFooterUI(h, table, columns);
    }

    private OrTableFooterUI(JComponent h, JTable table, List columns) {
        super();
        this.table = table;
        this.columns = columns;
        label.setFont(Utils.getDefaultFont());

        intF = (DecimalFormat) NumberFormat.getIntegerInstance();
        intF.setGroupingUsed(true);
        intF.setGroupingSize(3);
        DecimalFormatSymbols dfs = intF.getDecimalFormatSymbols();
        dfs.setGroupingSeparator(' ');
        intF.setDecimalFormatSymbols(dfs);
        intF.setMaximumFractionDigits(0);

        floatF = new DecimalFormat("#.##");
        floatF.setGroupingUsed(true);
        floatF.setGroupingSize(3);
        dfs = floatF.getDecimalFormatSymbols();
        dfs.setGroupingSeparator(' ');
        dfs.setDecimalSeparator(',');
        floatF.setDecimalFormatSymbols(dfs);
    }

//  Installation
    public void installUI(JComponent c) {
        header = (OrTableFooter)c;
        rendererPane = new CellRendererPane();
        header.add(rendererPane);
        installDefaults();
        installListeners();
        installKeyboardActions();
    }

    /**
     * Initialize JTableHeader properties, e.g. font, foreground, and background.
     * The font, foreground, and background properties are only set if their
     * current value is either null or a UIResource, other properties are set
     * if the current value is null.
     *
     * @see #installUI
     */
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(header, "TableHeader.background",
                                         "TableHeader.foreground", "TableHeader.font");
    }

    /**
     * Attaches listeners to the JTableHeader.
     */
    protected void installListeners() {
        mouseInputListener = createMouseInputListener();
        header.addMouseListener(mouseInputListener);
        header.addMouseMotionListener(mouseInputListener);
    }

    /**
     * Register all keyboard actions on the JTableHeader.
     */
    protected void installKeyboardActions() { }

// Uninstall methods

    public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();
        header.remove(rendererPane);
        rendererPane = null;
        header = null;
    }

    protected void uninstallDefaults() {}

    protected void uninstallListeners() {
        header.removeMouseListener(mouseInputListener);
        header.removeMouseMotionListener(mouseInputListener);
        mouseInputListener = null;
    }

    protected void uninstallKeyboardActions() {}


//
//  Factory methods for the Listeners
//

  /**
   * Creates the mouse listener for the JTable.
   */
  protected MouseInputListener createMouseInputListener() {
      return new MouseInputHandler();
  }



//
// Paint Methods and support
//

    public void paint(Graphics g, JComponent c) {
         header.setPreferredSize(table.getTableHeader().getPreferredSize());
        if (header.getColumnModel().getColumnCount() <= 0) {
            return;
        }
        Rectangle clip = g.getClipBounds();
        TableColumnModel cm = header.getColumnModel();
        int cMin = cm.getColumnIndexAtX(clip.x);
        int cMax = cm.getColumnIndexAtX(clip.x + clip.width - 1);
          // This should never happen.
        if (cMin == -1) {
            cMin = 0;
        }
          // If the table does not have enough columns to fill the view we'll get -1.
          // Replace this with the index of the last column.
        if (cMax == -1) {
            cMax = cm.getColumnCount()-1;
        }
        TableColumn draggedColumn = header.getDraggedColumn();
        Rectangle cellRect = header.getHeaderRect(cMin);
        for (int column = cMin; column <= cMax ; column++) {
            TableColumn aColumn = cm.getColumn(column);
            int columnWidth = aColumn.getWidth();
            cellRect.width = columnWidth;
            if (aColumn != draggedColumn) {
                paintCell(g, cellRect, column);
            }
            cellRect.x += columnWidth;
        }
          // Paint the dragged column if we are dragging.
        if (draggedColumn != null) {
            int draggedColumnIndex = viewIndexForColumn(draggedColumn);
            Rectangle draggedCellRect = header.getHeaderRect(draggedColumnIndex);
              // Draw a gray well in place of the moving column.
            g.setColor(header.getParent().getBackground());
            g.fillRect(draggedCellRect.x, draggedCellRect.y,
                draggedCellRect.width, draggedCellRect.height);
            draggedCellRect.x += header.getDraggedDistance();
            paintCell(g, draggedCellRect, draggedColumnIndex);
        }
      // Remove all components in the rendererPane.
        rendererPane.removeAll();
    }

    private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
        if (columnIndex < columns.size()) {
            int column = table.convertColumnIndexToModel(columnIndex);
            ColumnAdapter tc = (ColumnAdapter) columns.get(column);
            Component component = getFooterRenderer(columnIndex, tc);
            rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
                                        cellRect.width, cellRect.height , true);
        } else {
            rendererPane.paintComponent(g, null, header, cellRect.x, cellRect.y,
                                        cellRect.width, cellRect.height, true);
        }
    }

    public Component getFooterRenderer(int columnIndex, ColumnAdapter tc) {
        TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        TableCellRenderer  renderer = header.getDefaultRenderer();
        label =
            (JLabel)renderer.getTableCellRendererComponent(header.getTable(),
                    aColumn.getHeaderValue(), false, false, -1, columnIndex);
/*
        if ((!(tc instanceof IntColumnAdapter) && !(tc instanceof FloatColumnAdapter) &&
                !(tc instanceof CheckBoxColumnAdapter) && !(tc instanceof TextColumnAdapter))
                || tc.getSummaryType() == Constants.SUMMARY_NO) {
            label.setText("");
            label.setIcon(null);
        } else {
*/
        label.setText("");
        label.setIcon(null);
        if (tc != null) {
            label.setFont(Utils.getDefaultFont());
            label.setForeground(Utils.getDarkShadowSysColor());
            label.setBackground(Utils.getLightSysColor());
            label.setIconTextGap(2);
            if (tc.getSummaryType() == Constants.SUMMARY_SUMM) {
                label.setIcon(kz.tamur.rt.Utils.getImageIcon("summ"));
                if (tc instanceof IntColumnAdapter) {
                    String txt = intF.format(tc.sumInt());
                    label.setText(txt);
                } else if (tc instanceof FloatColumnAdapter) {
                    FloatFieldAdapter f = ((FloatColumnAdapter)tc).getEditor();
                    OrFloatField.FloatFormatter ft = (OrFloatField.FloatFormatter)f.getFloatField().getFormatter();
                    DecimalFormat df = ft.getSummFmt();
                    String txt = df.format(tc.sumFloat());
                    label.setText(txt);
                }
            }
            if (tc.getSummaryType() == Constants.SUMMARY_AVERAGE) {
                label.setIcon(kz.tamur.rt.Utils.getImageIcon("aver"));
                if (tc instanceof IntColumnAdapter) {
                    String txt = floatF.format(tc.average());
                    label.setText(txt);
                } else if (tc instanceof FloatColumnAdapter) {
                    FloatFieldAdapter f = ((FloatColumnAdapter)tc).getEditor();
                    OrFloatField.FloatFormatter ft = (OrFloatField.FloatFormatter)f.getFloatField().getFormatter();
                    DecimalFormat df = ft.getFmt();
                    String txt = df.format(tc.average());
                    label.setText(txt);
                }
            }
            if (tc.getSummaryType() == Constants.SUMMARY_MAX) {
                label.setIcon(kz.tamur.rt.Utils.getImageIcon("max"));
                if (tc instanceof IntColumnAdapter) {
                    String txt = intF.format(tc.maxMinInt(false));
                    label.setText(txt);
                } else if (tc instanceof FloatColumnAdapter) {
                    FloatFieldAdapter f = ((FloatColumnAdapter)tc).getEditor();
                    OrFloatField.FloatFormatter ft = (OrFloatField.FloatFormatter)f.getFloatField().getFormatter();
                    DecimalFormat df = ft.getFmt();
                    String txt = df.format(tc.maxMinFloat(false));
                    label.setText(txt);
                }
            }
            if (tc.getSummaryType() == Constants.SUMMARY_MIN) {
                label.setIcon(kz.tamur.rt.Utils.getImageIcon("min"));
                if (tc instanceof IntColumnAdapter) {
                    String txt = intF.format(tc.maxMinInt(true));
                    label.setText(txt);
                } else if (tc instanceof FloatColumnAdapter) {
                    FloatFieldAdapter f = ((FloatColumnAdapter)tc).getEditor();
                    OrFloatField.FloatFormatter ft = (OrFloatField.FloatFormatter)f.getFloatField().getFormatter();
                    DecimalFormat df = ft.getFmt();
                    String txt = df.format(tc.maxMinFloat(true));
                    label.setText(txt);
                }
            }
            if (tc.getSummaryType() == Constants.SUMMARY_COUNT) {
                String txt = intF.format(tc.count());
                label.setText(txt);
            }
            if (tc.getSummaryType() == Constants.SUMMARY_TRUE_COUNT) {
/*
            if (tc.getColumn() instanceof OrCheckColumn &&
                    tc.isDefSummary()) {
*/
                label.setIcon(kz.tamur.rt.Utils.getImageIcon("trueCount"));
                if (tc instanceof CheckBoxColumnAdapter) {
                    String txt = intF.format(tc.trueCount());
                    label.setText(txt);
                }
            }
        }
        label.setVerticalAlignment(JLabel.NORTH);
        return label;
    }


    private int viewIndexForColumn(TableColumn aColumn) {
        TableColumnModel cm = header.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }

    /**
         * This inner class is marked &quot;public&quot; due to a compiler bug.
         * This class should be treated as a &quot;protected&quot; inner class.
         * Instantiate it only within subclasses of BasicTableUI.
         */
    public class MouseInputHandler implements MouseInputListener {
        private int lastEffectiveMouseX;

        public void mouseClicked(MouseEvent e) {}

        private boolean canResize(TableColumn column) {
            return (column != null) && header.getResizingAllowed() && column.getResizable();
        }

        private TableColumn getResizingColumn(Point p) {
            return getResizingColumn(p, header.getColumnModel().getColumnIndexAtX(p.x));
        }

        private TableColumn getResizingColumn(Point p, int column) {
            if (column == -1) {
                return null;
            }
            Rectangle r = header.getHeaderRect(column);
            r.grow(-3, 0);
            if (r.contains(p)) {
                return null;
            }
            int midPoint = r.x + r.width/2;
            int columnIndex = (p.x < midPoint) ? column - 1 : column;
            if (columnIndex == -1) {
                return null;
            }
            return header.getColumnModel().getColumn(columnIndex);
        }

        public void mousePressed(MouseEvent e) {
            header.setDraggedColumn(null);
            header.setResizingColumn(null);
            header.setDraggedDistance(0);
            Point p = e.getPoint();
            lastEffectiveMouseX = p.x;
                // First find which header cell was hit
            TableColumnModel columnModel = header.getColumnModel();
            int index = columnModel.getColumnIndexAtX(p.x);
            if (index != -1) {
               // The last 3 pixels + 3 pixels of next column are for resizing
               TableColumn  resizingColumn = getResizingColumn(p, index);
                resizingColumn = getResizingColumn(p, index);
                if (canResize(resizingColumn)) {
                    header.setResizingColumn(resizingColumn);
                } else if (header.getReorderingAllowed()) {
                    TableColumn hitColumn = columnModel.getColumn(index);
                    header.setDraggedColumn(hitColumn);
                } else {  // Not allowed to reorder or resize.
                }
            }
        }

        private void setCursor(Cursor c) {
            if (header.getCursor() != c) {
                header.setCursor(c);
            }
        }

        public void mouseMoved(MouseEvent e) {
            if (canResize(getResizingColumn(e.getPoint()))) {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        public void mouseDragged(MouseEvent e) {
            int mouseX = e.getX();
            int deltaX = mouseX - lastEffectiveMouseX;
            if (deltaX == 0) {
                return;
            }
            TableColumn resizingColumn  = header.getResizingColumn();
            TableColumn draggedColumn  = header.getDraggedColumn();
          //  if (resizingColumn != null) {
          //      System.out.println("----------> " + resizingColumn.getModelIndex());
          //      System.out.println("----------> resizingColumn.getWidth() "
          //              + resizingColumn.getWidth());
          //  }

            if (resizingColumn != null) {
                int oldWidth = resizingColumn.getWidth();
                int newWidth = oldWidth + deltaX;
                resizingColumn.setWidth(newWidth);
                //resizingColumn.setWidth(newWidth);
                int acheivedDeltaX = resizingColumn.getWidth() - oldWidth;
                lastEffectiveMouseX = lastEffectiveMouseX + acheivedDeltaX;
            } else if (draggedColumn != null) {
                move(e, deltaX);
                lastEffectiveMouseX = mouseX;
            } else {
                lastEffectiveMouseX = mouseX;  // Neither dragging nor resizing ...
            }
        }

        public void mouseReleased(MouseEvent e) {
            setDraggedDistance(0, viewIndexForColumn(header.getDraggedColumn()));
            header.setResizingColumn(null);
            header.setDraggedColumn(null);
        }

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}
//
// Protected & Private Methods
//
        private void setDraggedDistance(int draggedDistance, int column) {
            header.setDraggedDistance(draggedDistance);
            if (column != -1) {
                header.getColumnModel().moveColumn(column, column);
            }
        }

        private void move(MouseEvent e, int delta) {
            TableColumnModel columnModel = header.getColumnModel();
            int lastColumn = columnModel.getColumnCount() - 1;
            TableColumn draggedColumn = header.getDraggedColumn();
            int draggedDistance = header.getDraggedDistance() + delta;
            int hitColumnIndex = viewIndexForColumn(draggedColumn);
                // Now check if we have moved enough to do a swap
            if ((draggedDistance < 0) && (hitColumnIndex != 0)) {
                    // Moving left; check prevColumn
                int width = columnModel.getColumn(hitColumnIndex-1).getWidth();
                if (-draggedDistance > (width / 2)) {
                        // Swap me
                    columnModel.moveColumn(hitColumnIndex, hitColumnIndex-1);
                    draggedDistance = width + draggedDistance;
                    hitColumnIndex--;
                }
            } else if ((draggedDistance > 0) && (hitColumnIndex != lastColumn)) {
                    // Moving right; check nextColumn
                int width = columnModel.getColumn(hitColumnIndex+1).getWidth();
                if (draggedDistance > (width / 2)) {
                        // Swap me
                    columnModel.moveColumn(hitColumnIndex, hitColumnIndex+1);
                    draggedDistance = -(width - draggedDistance);
                    hitColumnIndex++;
                }
            }
            header.setDraggedColumn(columnModel.getColumn(hitColumnIndex));
            setDraggedDistance(draggedDistance, hitColumnIndex);
        }
    }
}

