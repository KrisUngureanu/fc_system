package kz.tamur.admin.clsbrow;

import static java.awt.GridBagConstraints.LINE_START;
import static java.awt.GridBagConstraints.NONE;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.owasp.esapi.ESAPI;

import kz.tamur.Or3Frame;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.search.SearchOperationsWindow;
import kz.tamur.guidesigner.search.SearchPanel;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.CnrBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class BlobAttrEditorDelegate extends JPanel implements ObjectEditorDelegate,ObjectRendererDelegate, ActionListener {

    private Object value;
    private ObjectPropertyEditor editor;

    private JButton blobBtn;
    private KrnAttribute attr;
    private JPopupMenu pm;
    private String encoding = "UTF-8";
    private JTable table;
    byte[] val;
    JTextArea editorText;
    private static long KrnObjectID;
    
    public BlobAttrEditorDelegate(JTable table, KrnAttribute attr) {
        this.table=table;
        this.attr=attr;
        setLayout(new GridBagLayout());

        JTextField label = kz.tamur.comps.Utils.createEditor(table.getFont());
        label.setEditable(false);
        blobBtn = kz.tamur.comps.Utils.createBtnEditor(this);
        pm = new JPopupMenu();
        pm.setFont(Utils.getDefaultFont());
        pm.setBackground(Utils.getLightSysColor());
        java.util.List langItems = LangItem.getAll();
        for (Object langItem : langItems) {
            LangMenuItem mi = new LangMenuItem((LangItem) langItem);
            mi.addActionListener(this);
            if ("RU".equals(mi.getLangItem().code) ||
                    "KZ".equals(mi.getLangItem().code) ||
                    "EN".equals(mi.getLangItem().code)) {
                pm.add(mi);
            }
        }


        add(label, new CnrBuilder().x(1).wtx(1).fill(GridBagConstraints.HORIZONTAL).build());
        add(blobBtn, new CnrBuilder().x(0).build());
    }

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
        this.value = value;
	}

    public Component getObjectRendererComponent() {
        return this;
    }

    public Component getObjectEditorComponent() {
		return this;
	}

	public int getClickCountToStart() {
		return 1;
	}

	public void setObjectPropertyEditor(ObjectPropertyEditor editor) {
        this.editor=editor;
	}
	
	public static void setObjectID(long objectID) {
		KrnObjectID = objectID;
	}

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == blobBtn) {
            if (attr.isMultilingual) {
                Point p = this.getMousePosition();
                pm.show(this, (int)p.getX(), (int)p.getY());
            }else {
                if(attr.collectionType>0){
                    try{
                    int row=table.getSelectedRow();
                    String title=table.getValueAt(row,1).toString()+":"+table.getValueAt(row,2).toString();
                    ArrayPropertyField apf=new ArrayPropertyField(editor.getObject().getKrnObject(),attr,value==null?new byte[0][0]:(byte[][])value);
                    Container cont = getTopLevelAncestor();
                    DesignerDialog dlg;
                    if (cont instanceof Dialog) {
                        dlg = new DesignerDialog((Dialog)cont, title, apf);
                    } else {
                        dlg = new DesignerDialog((Frame)cont, title, apf);
                    }
                    dlg.show();
                    if (dlg.isOK()) {
                        ObjectInspectable ins= editor.getObject();
                        Vector data= apf.getList();
                        Vector<PropertyField> tpfs=new Vector<PropertyField>();
                        boolean isModified=false;
                        byte[][] value_=new byte[data.size()][];
                        for(int i=0;i<data.size();i++){
                            PropertyField tpf=(PropertyField)((Vector)data.get(i)).get(0);
                            tpfs.add(tpf);
                            if(!isModified && tpf.isModified())
                                isModified=true;
                                value_[i]=((BlobPropertyField)((Vector)data.get(i)).get(0)).getData();
                        }
                        Vector delList=apf.getDelList();
                        if(delList.size()>0){
                            isModified=true;
                            tpfs.addAll(delList);
                        }
                        if(isModified){
                            ins.setObjectArray(attr,tpfs);
                            value=value_;
                        }
                        editor.stopCellEditing();
                    }
                    } catch (KrnException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    byte[] data = (byte[])value;
                    value=editBlob(data);
                    if(!Arrays.equals((byte[])value, data))
                        editor.stopCellEditing();
                }
            }
        }else if(e.getSource() instanceof LangMenuItem){
            byte[] data = (byte[])value;
            ObjectInspectable ins= editor.getObject();
            Map<Long,byte[]> dataMap=ins.getLangData(attr.id);
            if(dataMap==null)
                dataMap=new HashMap<Long,byte[]>();
            try{
                long langId= ((LangMenuItem)e.getSource()).getLangItem().obj.id;
                byte[][] datas;
                if(langId != ins.getLangId()){
                    if(!dataMap.containsKey(langId)){
                    	datas = Kernel.instance().getBlobs(ins.getKrnObject().id, attr, langId, ObjectBrowser.transId);
                    }else{
                        datas= new byte[][]{dataMap.get(langId)};
                    }
                    byte[] value_=editBlob(datas.length>0?datas[0]:new byte[0]);
                    if((datas.length==0 && value_.length>0)|| ((value_!=null && datas.length!=0) &&!Arrays.equals(value_, datas[0])))
                        dataMap.put(langId,value_);
                }else{
                    value=editBlob(data);
                    if(!Arrays.equals((byte[])value, data))
                        dataMap.put(langId,(byte[])value);
                }
            }catch(KrnException ex){
                ex.printStackTrace();
            }
            if(dataMap.size() > 0)
                ins.setLangData(attr.id,dataMap);
            if(!Arrays.equals((byte[])value, data)) {
				try {
					Kernel krn = Kernel.instance();
					KrnObject obj = krn.getObjectById(KrnObjectID, 0);
					final long uiID = krn.getClassByName("UI").id;
					if(obj.classId == uiID || krn.getAttributeByName(krn.getClassByName("UI"), "webConfig").equals(attr)){
						krn.setLong(obj.id, obj.classId, "webConfigChanged", 0, 1, 0);
						JOptionPane.showMessageDialog(null, "Аттрибут webConfig был изменен вручную", "Внимание!", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (KrnException e1) {
					e1.printStackTrace();
				}
                editor.stopCellEditing();
            }
        }
    }
    
    private boolean isSearchigAttribute(int selectedRow, long selectedObjectClassID, long attributeID, long selectedObjectID) {
        return selectedRow == -1 ? false : attr.classId == selectedObjectClassID && attr.id == attributeID
                && KrnObjectID == selectedObjectID;
    }

    private byte[] editBlob(final byte[] data) {
    	Kernel krn = Kernel.instance();
		int selectedRow = -1;
		long attributeID = 0;
		long selectedObjectClassID = 0;
		long selectedObjectID = 0;
		if (Or3Frame.instance().getSearchPanel() != null) {
			selectedRow = SearchPanel.getSearchResult().getTable().getSelectedRow();
		}
		if (selectedRow != -1) {
			String selectedObjectUID = SearchPanel.getSearchResult().getTable().getModel().getValueAt(selectedRow, 0).toString();
			attributeID = Long.parseLong(SearchPanel.getSearchResult().getTable().getModel().getValueAt(selectedRow, 1).toString());
			try {
				selectedObjectClassID = (krn.getObjectByUid(selectedObjectUID, 0)).classId;
				selectedObjectID = (krn.getObjectByUid(selectedObjectUID, 0)).id;
	
			} catch (KrnException exception) {
				exception.printStackTrace();
			}
		}		
    	if (isSearchigAttribute(selectedRow, selectedObjectClassID, attributeID, selectedObjectID)) {
			String blobString = new String(data);
			List<String> searchingWords = Or3Frame.instance().getSearchPanel().getSearchingWords();
			SearchOperationsWindow dialog = new SearchOperationsWindow("Редактирование свойства", 700, 700, 3, blobString, searchingWords);
			if (dialog.getTextPaneContent() != null) {
				try {
					return dialog.getTextPaneContent().getBytes(encoding);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				JPanel content = new JPanel(new GridBagLayout());
				JButton upload = ButtonsFactory.createToolButtonTransp("fileOpen", ".png", "Загрузить файл");
				Utils.setAllSize(upload, new Dimension(32, 32));
				upload.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fChooser = kz.tamur.comps.Utils.createOpenChooser(-1);
						if (fChooser.showOpenDialog(Or3Frame.instance()) == JFileChooser.APPROVE_OPTION) {
							File sf = fChooser.getSelectedFile();
							Utils.setLastSelectDir(sf.getParentFile().toString());
							if (sf != null) {
								val = null;
								try {
									val = Funcs.read(sf);
									if (val.length < 1000000)
										editorText.setText(new String(val, encoding));
									else
										JOptionPane.showMessageDialog(null, "Текст слишком большой и не может быть отображен. Вы можете выгрузить его в файл", "Внимание!", JOptionPane.INFORMATION_MESSAGE);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					}
				});
				JButton download = ButtonsFactory.createToolButtonTransp("Apply32", ".png", "Сохранить в файл");
				Utils.setAllSize(download, new Dimension(32, 32));
				download.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fChooser = kz.tamur.comps.Utils.createOpenChooser(-1);
						if (fChooser.showOpenDialog(Or3Frame.instance()) == JFileChooser.APPROVE_OPTION) {
							File sf = fChooser.getSelectedFile();
							Utils.setLastSelectDir(sf.getParentFile().toString());
							if (sf != null) {
								try {
									String canonicalPath = Funcs.normalizeInput(sf.getCanonicalPath());
									if (canonicalPath.matches(".+")) {
										Path f = Paths.get(canonicalPath);
										OutputStream out = Files.newOutputStream(f);
										String text_ = editorText.getText();
										if (val != null) {
											out.write(val);
										} else if (text_.length() > 0 && text_.length() < Constants.MAX_BLOB_SIZE) {
											text_ = Funcs.validate(Funcs.normalizeInput(text_));
//											text_ = ESAPI.encoder().encodeForHTML(text_);
											out.write(text_.getBytes(encoding));
										} else if (data != null) {
											out.write(data);
										}
										out.close();
									}
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					}
				});
				val = null;
				content.setOpaque(false);
				Container cont = getTopLevelAncestor();
				String text = (data != null && data.length <= 4 * 1024 * 1024) ? new String(data, encoding) : "";
				editorText = new JTextArea();
				
				if (data != null) {
					if (data.length > 4 * 1024 * 1024) {
						JOptionPane.showMessageDialog(null, "Текст слишком большой и не может быть отображен. Вы можете выгрузить его в файл", "Внимание!", JOptionPane.INFORMATION_MESSAGE);
					} else {
						if (text.matches(".+"))
							editorText.setText(Funcs.validate(Funcs.normalizeInput(text)));
					}
				}
				
				JScrollPane scroller = new JScrollPane(editorText);
				scroller.setPreferredSize(new Dimension(600, 400));
				content.add(upload, new GridBagConstraints(0, 0, 1, 1, 0, 0, LINE_START, NONE, Constants.INSETS_1, 0, 0));
				content.add(download, new GridBagConstraints(1, 0, 1, 1, 0, 0, LINE_START, NONE, Constants.INSETS_1, 0, 0));
				content.add(scroller, new GridBagConstraints(0, 1, 2, 1, 1, 1, LINE_START, GridBagConstraints.BOTH, Constants.INSETS_1, 0, 0));
				DesignerDialog dlg = new DesignerDialog(cont instanceof Dialog ? (Dialog) cont : (Frame) cont, "Редактирование свойства", content);
				dlg.show();
				String text_ = Funcs.validate(Funcs.normalizeInput(editorText.getText()));
				if (dlg.getResult() == ButtonsFactory.BUTTON_OK) {
					if (val != null) {
						return val;
					} else if (!text.equals(text_)) {
						return text_.getBytes(encoding);
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        return data;
    }
}