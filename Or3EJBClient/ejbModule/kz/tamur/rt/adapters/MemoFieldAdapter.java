package kz.tamur.rt.adapters;

import com.cifs.or2.kernel.KrnException;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrCellEditor;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrMemoField;
import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.rt.adapters.TableAdapter.RtTableModel;
import kz.tamur.util.Funcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.EventObject;

public class MemoFieldAdapter extends ComponentAdapter {
    private static final Log log = LogFactory.getLog(MemoFieldAdapter.class);

    private OrMemoField memoField;
    private boolean selfChange = false;
    private RadioGroupManager groupManager = new RadioGroupManager();
    OrCellEditor editor_;
    private OrRef copyRef;
    private CopyAdapter adapter = new CopyAdapter();

    public MemoFieldAdapter(OrFrame frame, OrMemoField memoField, boolean isEditor)
            throws KrnException {
        super(frame, memoField, isEditor);
        this.memoField = memoField;
        //this.memoField.getDocument().addDocumentListener(this);
        //Копируемый атрибут
        String copyRefPath = memoField.getCopyRefPath();
        if (copyRefPath != null && !"".equals(copyRefPath)) {
            try {
                propertyName = "Свойство: Копируемый атрибут";
                copyRef = OrRef.createRef(copyRefPath, false, Mode.RUNTIME, frame.getRefs(),
                        OrRef.TR_CLEAR, frame);
                memoField.getCopyBtn().setCopyAdapter(adapter);
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    showErrorNessage(e.getMessage());
                }
                e.printStackTrace();
            }
        }
        if (!isEditor) {
            kz.tamur.rt.Utils.setMemoComponentFocusCircle(this.memoField);
            this.memoField.addFocusListener(new DefaultFocusAdapter(this));
        }
        this.memoField.setXml(null);
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
    }
    
    public void clear() {
    }

    // DocumentListener
/*
    public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
    }

    public void changedUpdate(DocumentEvent e) {
        if (!selfChange) {
            try {
                if (dataRef != null) {
                    selfChange = true;
                    OrRef.Item item = dataRef.getItem(langId);
                    if (item != null)
                        dataRef.changeItem(memoField.getText(), this, this);
                    else
                        dataRef.insertItem(0, memoField.getText(), this, this, false);
                }
                updateParamFilters(memoField.getText());
            } catch (KrnException e1) {
                e1.printStackTrace();
            } finally {
                selfChange = false;
            }
        }
    }
*/

    public void focusGained(FocusEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    class OrMemoCellEditor extends OrCellEditor {
    	MemoColumnAdapter ca = null;
    	
    	public OrMemoCellEditor(MemoColumnAdapter ca) {
    		this.ca = ca;
    	}
    	
        public Object getCellEditorValue() {
            return memoField.getText();
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {
            if (isSelected) {
            	
            	boolean isEditable = ((RtTableModel)ca.tableAdapter.getModel()).isColumnCellEditable(row, column);
            	
            	final String title = table.getColumnName(column);
                valueChanged(new OrRefEvent(dataRef, 0, -1, null));

                JTextArea ta = new JTextArea(memoField.getText());
                
                ta.setLineWrap(true);
                ta.setWrapStyleWord(true);
                ta.setFocusable(true);

                if (isEditable) {
	                ta.addKeyListener(new KeyAdapter() {
	                    public void keyReleased(KeyEvent e) {
	                        JTextArea t = (JTextArea)e.getSource();
	
	                        if (Character.isLetterOrDigit(e.getKeyChar()) &&
	                                t.getText().length() == 0) {
	                            t.setText(new String(new char[] {e.getKeyChar()}));
	                        }
	                        super.keyReleased(e);
	                    }
	                });
	                ta.setCaretPosition(memoField.getText().length());
                } else {
                	ta.setEditable(false);
                }
	            
                JScrollPane sp = new JScrollPane(ta);
                sp.setPreferredSize(new Dimension(450, 300));
                sp.setFont(new Font("Tahoma", 0, 12));
                Container cnt = memoField.getTopLevelAncestor();
                DesignerDialog dlg = null;
                //if (cnt instanceof Frame) {
                    dlg = new DesignerDialog(
                            (Frame)cnt , "", sp, copyRef != null);
/*
                } else {
                    dlg = new DesignerDialog(
                            (Dialog)cnt , "", sp, copyRef != null);
                }
*/
                if (isEditable && copyRef != null) {
                    dlg.setClearBtnText("Копировать");
                    dlg.setClearBtnActionListener(adapter);
                }
                if (title.indexOf("<html>") >= 0) {
                    String s = title.toString();
                    s = s.substring(s.lastIndexOf("\">") + 2);
                    //  s = s.substring(s.lastIndexOf("\">") + 2).replace('<',' ');
                    //  s = s.replace('b',' ');
                    //  s = s.replace('r',' ');
                    //   s = s.replace('>',' ');
                    s = s.replace('@', ' ');
                    dlg.setTitle(s);
                } else {
                    String s = title.toString();
                    s = s.replace('@', ' ');
                    dlg.setTitle(s);
                }
                ta.grabFocus();
                dlg.show();
                if (isEditable && dlg.getResult() == ButtonsFactory.BUTTON_OK) {
                    memoField.setText(ta.getText());
                    try {
                        changeValue(ta.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                editor_.stopCellEditing();
            }
            return null;
        }

        public Object getValueFor(Object obj) {
            Object val = ((OrRef.Item) obj).getCurrent();
            String res = null;
            if (val instanceof File) {
                try {
                	byte[] buff = Funcs.read((File)val);
                    res = Funcs.normalizeInput(new String(buff));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (val instanceof String) {
                res = Funcs.normalizeInput((String) val);
            }
            return getFirstString(res);
        }


        public boolean isCellEditable(EventObject e) {
            boolean res = super.isCellEditable(e);
            if (!res && e instanceof KeyEvent) {
                res = true;
            }
            return res;
        }
    }

    private String getFirstString(String str) {
        if (str != null) {
            int last = str.indexOf('\n');
            if (last != -1)
                return str.substring(0, last);
        }
        return str;
    }

    public OrCellEditor getCellEditor(MemoColumnAdapter ca) {
        if (editor_ == null) {
            editor_ = new OrMemoCellEditor(ca);
            memoField.setBorder(BorderFactory.createEmptyBorder());
        }
        return editor_;
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        memoField.setEditable(isEnabled);
/*
        if (dataRef != null) {
            dataRef.setActive(isEnabled);
        }
*/
    }

    public void clearFilterParam() {
        super.clearFilterParam();
        if (dataRef == null) {
            memoField.setText("");
        }
    }

    private class CopyAdapter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (copyRef != null) {
                try {
                    OrRef ref = dataRef;
                    OrRef.Item item = copyRef.getItem(langId);
                    Object value = (item != null) ? item.getCurrent() : null;
                    if (ref.getItem(langId) == null)
                        ref.insertItem(0, value, null, MemoFieldAdapter.this, false);
                    else
                        ref.changeItem(value, MemoFieldAdapter.this, null);
                    if (isEditor()) {
                        //OrCellEditor editor = dateField.getCellEditor();
                        memoField.setText(String.valueOf(value));
                        editor_.stopCellEditing();
                        //editor_.cancelCellEditing();
                    }
                } catch (KrnException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }



}
