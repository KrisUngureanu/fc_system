package kz.tamur.or3.client.props.inspector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Utils;
import kz.tamur.comps.ui.GradientPanel;
import kz.tamur.comps.ui.button.OrTransparentButton;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.service.ServicesTree;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.AbstractDesignerTreeNode;
import kz.tamur.util.ExpressionCellEditor;
import kz.tamur.util.IconCellEditor;
import kz.tamur.util.OpenElementPanel;
import kz.tamur.util.Pair;

import com.cifs.or2.client.util.CnrBuilder;

import static java.awt.GridBagConstraints.*;
import static kz.tamur.rt.Utils.setAllSize;

public class ProcessesEditorDelegate extends JPanel implements EditorDelegate, RendererDelegate, ActionListener {

    private Object value;
    private PropertyEditor editor;

    private JLabel label;
    private JButton exprBtn;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
    public ProcessesEditorDelegate(JTable table) {
        setLayout(new GridBagLayout());
        label = new JLabel();
        label.setFont(table.getFont());
        exprBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        add(label, new CnrBuilder().x(1).wtx(1).fill(HORIZONTAL).build());
        add(exprBtn, new CnrBuilder().x(0).build());
    }

    public int getClickCountToStart() {
        return 1;
    }

    public Component getEditorComponent() {
        return this;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        if (value instanceof List) {
            List<ProcessRecord> prRecs = (List<ProcessRecord>) value;
            String txt = "";
            for (ProcessRecord mr : prRecs) {
                txt += mr.getName() + ";";
            }
            label.setText(txt);
        } else {
            this.value = null;
            label.setText("");
        }
    }

    public Component getRendererComponent() {
        return this;
    }

    public void setPropertyEditor(PropertyEditor editor) {
        this.editor = editor;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exprBtn) {
            EditorPanel me = new EditorPanel();
            DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Бизнес-процессы", me);
            dlg.show();
            int res = dlg.getResult();
            if (res != ButtonsFactory.BUTTON_NOACTION && res == ButtonsFactory.BUTTON_OK) {
                value = me.prRecs;
                editor.stopCellEditing();
            } else {
                editor.cancelCellEditing();
            }
        }
    }

    private class EditorPanel extends GradientPanel implements ActionListener {

        private JTable table;
        private AbstractTableModel model;
        private OrTransparentButton addProcessBtn;
        private OrTransparentButton addActionBtn;
        private OrTransparentButton delBtn;
        private OrTransparentButton upBtn;
        private OrTransparentButton downBtn;

        private List<ProcessRecord> prRecs;

        public EditorPanel() {
            setOpaque(isOpaque);
            if (value instanceof List) {
                List<ProcessRecord> curPrRecs = (List<ProcessRecord>) value;
                prRecs = new ArrayList<ProcessRecord>();
                for (ProcessRecord curPrRec : curPrRecs)
                    prRecs.add(new ProcessRecord(curPrRec));
            }

            setLayout(new GridBagLayout());
            model = new AbstractTableModel() {

                @Override
                public String getColumnName(int column) {
                    switch (column) {
                    case 0:
                        return "UID";
                    case 1:
                        return "Наименование";
                    case 2:
                        return "Краткое наименование";
                    case 3:
                        return "Дост";
                    case 4:
                        return "Вид";
                    case 5:
                        return "Формула";
                    case 6:
                        return "Иконка";
                    }
                    return null;
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return columnIndex >= 2;
                }

                public Object getValueAt(int rowIndex, int columnIndex) {
                    ProcessRecord pr = prRecs != null && prRecs.size() > rowIndex ? prRecs.get(rowIndex) : null;
                    if (pr != null) {
                        switch (columnIndex) {
                        case 0:
                            return pr.getUid();
                        case 1:
                            return pr.getName();
                        case 2:
                            Pair<String, Object> p = pr.getShortName();
                            return p != null ? p.second : null;
                        case 3:
                            return pr.getEnabledExpr();
                        case 4:
                            return pr.getVisibleExpr();
                        case 5:
                            return pr.getActionExpr();
                        case 6:
                            return pr.getImage();
                        }
                    }
                    return null;
                }

                @Override
                public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                    ProcessRecord pr = prRecs != null && prRecs.size() > rowIndex ? prRecs.get(rowIndex) : null;
                    if (pr != null) {
                        switch (columnIndex) {
                        case 2:
                            pr.setShortName((String) aValue);
                            break;
                        case 3:
                            pr.setEnabledExpr((Expression) aValue);
                            break;
                        case 4:
                            pr.setVisibleExpr((Expression) aValue);
                            break;
                        case 5:
                            pr.setActionExpr((Expression) aValue);
                            break;
                        case 6:
                            pr.setImage((BufferedImage) aValue);
                            break;
                        }
                    }
                }

                public int getRowCount() {
                    return prRecs != null ? prRecs.size() : 0;
                }

                public int getColumnCount() {
                    return 7;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    switch (columnIndex) {
                    case 3:
                    case 4:
                    case 5:
                        return Expression.class;
                    case 6:
                        return ImageIcon.class;
                    }
                    return super.getColumnClass(columnIndex);
                }
            };
            table = new JTable(model);

            addProcessBtn = new OrTransparentButton();
            initButton(addProcessBtn, "addProcess.png", "Добавить процесс");

            addActionBtn = new OrTransparentButton();
            initButton(addActionBtn, "addTask.png", "Добавить действие");

            delBtn = new OrTransparentButton();
            initButton(delBtn, "delete2.png", "Удалить");

            upBtn = new OrTransparentButton();
            initButton(upBtn, "up.png", "Переместить вверх");

            downBtn = new OrTransparentButton();
            initButton(downBtn, "down.png", "Переместить вниз");
            Dimension sz = new Dimension(24, 24);
            setAllSize(addProcessBtn, sz);
            setAllSize(addActionBtn, sz);
            setAllSize(delBtn, sz);
            setAllSize(upBtn, sz);
            setAllSize(downBtn, sz);
            
            
            final JScrollPane scrollTable = new JScrollPane(table);

            add(scrollTable, new GridBagConstraints(0, 0, 1, 5, 1, 0, CENTER, BOTH, new Insets(2, 1, 1, 1), 0, 0));
            add(addProcessBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0, CENTER, NONE, new Insets(2, 5, 1, 5), 0, 0));
            add(addActionBtn, new GridBagConstraints(1, 1, 1, 1, 0, 0, CENTER, NONE, new Insets(1, 5, 1,5), 0, 0));
            add(delBtn, new GridBagConstraints(1, 2, 1, 1, 0, 0, CENTER, NONE, new Insets(1, 5, 1, 5), 0, 0));
            add(upBtn, new GridBagConstraints(1, 3, 1, 1, 0, 0, CENTER, NONE, new Insets(1, 5, 1, 5), 0, 0));
            add(downBtn, new GridBagConstraints(1, 4, 1, 1, 0, 1, NORTH, NONE, new Insets(1, 5, 1, 5), 0, 0));

            table.setDefaultEditor(Expression.class, new ExpressionCellEditor());
            table.setDefaultRenderer(Expression.class, new ExpressionCellEditor());

            table.setDefaultEditor(ImageIcon.class, new IconCellEditor());
            table.setDefaultRenderer(ImageIcon.class, new IconCellEditor());
            // градиентныя заливка основной панели
            setGradient(Constants.GLOBAL_DEF_GRADIENT);
            // прозрачность таблицы, для красоты
            table.setOpaque(false);
            scrollTable.setOpaque(false);
            scrollTable.getViewport().setOpaque(false);
        }

        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (src == addProcessBtn) {
                ServicesTree procTree = Utils.getServicesTree();
                OpenElementPanel op = new OpenElementPanel(procTree);
                DesignerDialog dlg = new DesignerDialog(Or3Frame.instance(), "Выберите бизнес-процесс", op, true);
                dlg.show();
                if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    AbstractDesignerTreeNode node = op.getTree().getSelectedNode();
                    if (node != null) {
                        if (prRecs == null)
                            prRecs = new ArrayList<ProcessRecord>();
                        prRecs.add(new ProcessRecord(op.getNodeObj(node), node.toString()));
                        model.fireTableRowsInserted(prRecs.size() - 1, prRecs.size() - 1);
                    }
                }
            } else if (src == addActionBtn) {
                if (prRecs == null)
                    prRecs = new ArrayList<ProcessRecord>();
                prRecs.add(new ProcessRecord(null, ""));
                model.fireTableRowsInserted(prRecs.size() - 1, prRecs.size() - 1);
            } else if (src == delBtn) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    prRecs.remove(row);
                    model.fireTableRowsDeleted(row, row);
                }
            } else if (src == upBtn) {
                int row = table.getSelectedRow();
                if (row >0) {
                    int rowUp = row-1;
                    ProcessRecord elemUp = prRecs.get(rowUp);
                    ProcessRecord elemSelect = prRecs.get(row);
                    prRecs.set(rowUp, elemSelect);
                    prRecs.set(row, elemUp);
                    model.fireTableRowsUpdated(rowUp, row);
                    table.setRowSelectionInterval(rowUp, rowUp);
                }
            } else if (src == downBtn) {
                int row = table.getSelectedRow();
                if (row <table.getRowCount()-1) {
                    int rowDown = row+1;
                    ProcessRecord elemDown = prRecs.get(rowDown);
                    ProcessRecord elemSelect = prRecs.get(row);
                    prRecs.set(rowDown, elemSelect);
                    prRecs.set(row, elemDown);
                    model.fireTableRowsUpdated(row, rowDown);
                    table.setRowSelectionInterval(rowDown, rowDown);
                }
            }
        }

        private void initButton(OrTransparentButton button, String icon, String toolTip) {
            button.addActionListener(this);
            button.setToolTipText(toolTip);
            button.setIcon(kz.tamur.rt.Utils.getImageIconFull(icon));
            kz.tamur.rt.Utils.setAllSize(button, new Dimension(32,32));
            button.setBackground(Color.BLACK);
        }
    }
}
