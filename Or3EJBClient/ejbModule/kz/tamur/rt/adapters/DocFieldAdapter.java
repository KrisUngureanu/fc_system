package kz.tamur.rt.adapters;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellRenderer;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrCellEditor;
import kz.tamur.comps.OrDocField;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.Utils;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import kz.tamur.util.ZebraCellRenderer;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.util.expr.Editor;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.Variant;

public class DocFieldAdapter extends ComponentAdapter implements ActionListener {

	private OrDocField button;
    private int action = 0;
    private final OrDocFieldRenderer renderer = new OrDocFieldRenderer();
    private OrDocFieldCellEditor cellEditor = new OrDocFieldCellEditor();
    final Kernel krn = Kernel.instance();
    private ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));;
    private String fileNotFoundMsg = "Файл не прикреплён";
    private long ifcLangId = 0;
    private OrRef fileNameRef;
    private OrRef fileContentRef;
    private ASTStart beforeOpenAction;
    private File currDir;
    private String currFilterDesc;
    private boolean dontDependNull;
    private Object report;
    private String reportName;

    public DocFieldAdapter(UIFrame frame, OrDocField docField, boolean isEditor) throws KrnException {
        super(frame, docField, isEditor);
        button = docField;
        renderer.setNewIcon(docField.getIcon());
        docField.setAdapter(this);
        PropertyNode proot = button.getProperties();
        PropertyNode povNode = proot.getChild("pov");
        PropertyValue pv = button.getPropertyValue(povNode.getChild("action"));
        if (!pv.isNull()) {
            action = pv.intValue();
        }
        pv = button.getPropertyValue(povNode.getChild("activity").getChild("dontDependNull"));
        dontDependNull = pv.booleanValue();
        
        // Триггер "До модификации"
        PropertyNode beforeOpenNode = povNode.getChild("beforeOpenAction");
        if (beforeOpenNode != null) {
            pv = button.getPropertyValue(beforeOpenNode);
            String expr = pv.isNull() ? "" : pv.stringValue();
            if (expr.length() > 0) {
                beforeOpenAction = OrLang.createStaticTemplate(expr);
                Editor e = new Editor(expr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            }
        }
        this.button.addActionListener(this);
        button.setXml(null);
    }
    
    public int getAction() {
    	return action;
    }
    
	@Override
    protected void createDataRef(OrGuiComponent c) throws KrnException {
        super.createDataRef(c);
        if (dataRef != null) {
            String path = dataRef.toString();
			if (dataRef.getAttribute().typeClassId == Kernel.IC_BLOB) {
				fileContentRef = dataRef;
				fileNameRef = null;
			} else {
				fileNameRef = OrRef.createRef(path + ".filename", dataRef.isColumn, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
				fileContentRef = OrRef.createRef(path + ".file", dataRef.isColumn, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
				if (langId > 0) {
					fileNameRef.addLanguage(langId);
					fileContentRef.addLanguage(langId);
	            }
			}
        }
    }

	public void clear() {
		setEnabled(action == Constants.DOC_VIEW || action == Constants.DOC_UPDATE);
    }

    @Override
	public void valueChanged(OrRefEvent e) {
		super.valueChanged(e);
		if (e.getRef() == dataRef) {
			OrRef.Item item = dataRef.getItem(langId);
			if (action == Constants.DOC_VIEW || action == Constants.DOC_EDIT) {
				setEnabled(item != null && item.getCurrent() != null || dontDependNull);
			} else if (action == Constants.DOC_UPDATE_VIEW) {
                if (item == null || item.getCurrent() == null) {
                    button.setText(button.titleBeforeAttaching);
                } else {
                    button.setText(button.titleAfterAttaching);
                }
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
        if (button.isHelpClick()) {
            return;
        }
        try {
	        if (e.getSource() == button && action == Constants.DOC_VIEW) {
	        	if (doBeforeOpen()) {
	        		if (dataRef != null) {
			            OrRef.Item item = dataRef.getItem(langId);
			            if (item == null || item.getCurrent() == null) {
			                Container cnt = button.getTopLevelAncestor();
			                if (cnt instanceof Frame) {
			                    MessagesFactory.showMessageDialog((Frame)cnt, MessagesFactory.INFORMATION_MESSAGE, fileNotFoundMsg, LangItem.getById(ifcLangId));
			                } else {
			                    MessagesFactory.showMessageDialog((Dialog)cnt, MessagesFactory.INFORMATION_MESSAGE, fileNotFoundMsg, LangItem.getById(ifcLangId));
			                }
			            } else {
			                open();
			            }
	        		} else {
	        			open();
	        		}
	        	}
	        } else if (e.getSource() == button && action == Constants.DOC_UPDATE) {
	        	if (doBeforeOpen()) {
	                JFileChooser fChooser = Utils.createOpenChooser(Constants.MSDOC_FILTER, ifcLangId, button.isMultipleFile());
	                // Устанавливаем выбранный ранее фильтр
	                if (currFilterDesc != null) {
		                FileFilter[] ffilters = fChooser.getChoosableFileFilters();
		                for (FileFilter ffilter : ffilters) {
		                	if (currFilterDesc.equals(ffilter.getDescription())) {
		                		fChooser.setFileFilter(ffilter);
		                		break;
		                	}
		                }
	                }
	                // Устанавливаем выбранную ранее папку
	                if (currDir != null)
	                	fChooser.setCurrentDirectory(currDir);
	                if (fChooser.showOpenDialog(button) == JFileChooser.APPROVE_OPTION) {
	                	// Запоминаем выбранный фильтр
	                	currFilterDesc = fChooser.getFileFilter().getDescription();
	                    File[] sfs = fChooser.getSelectedFiles();
	                    if (sfs != null && sfs.length > 0) {
	                    	// Запоминаем выбранную папку
	                    	currDir = sfs[0].getParentFile();
	                    	kz.tamur.rt.Utils.setLastSelectDir(currDir.toString());
	                    	upload(sfs);
	                    }
	                }
	        	}
	        } else if (e.getSource() == button && action == Constants.DOC_EDIT) {
	        	if (doBeforeOpen()) {
	        		edit();
	        	}
	        } else if (e.getSource() == button && action == Constants.DOC_UPDATE_VIEW) {
            	OrRef.Item item = dataRef.getItem(langId);
                if (item == null || item.getCurrent() == null) {
                	if (doBeforeOpen()) {
    	                JFileChooser fChooser = Utils.createOpenChooser(Constants.MSDOC_FILTER, ifcLangId, button.isMultipleFile());
    	                // Устанавливаем выбранный ранее фильтр
    	                if (currFilterDesc != null) {
    		                FileFilter[] ffilters = fChooser.getChoosableFileFilters();
    		                for (FileFilter ffilter : ffilters) {
    		                	if (currFilterDesc.equals(ffilter.getDescription())) {
    		                		fChooser.setFileFilter(ffilter);
    		                		break;
    		                	}
    		                }
    	                }
    	                // Устанавливаем выбранную ранее папку
    	                if (currDir != null)
    	                	fChooser.setCurrentDirectory(currDir);
    	                if (fChooser.showOpenDialog(button) == JFileChooser.APPROVE_OPTION) {
    	                	// Запоминаем выбранный фильтр
    	                	currFilterDesc = fChooser.getFileFilter().getDescription();
    	                    File[] sfs = fChooser.getSelectedFiles();
    	                    if (sfs != null && sfs.length > 0) {
    	                    	// Запоминаем выбранную папку
    	                    	currDir = sfs[0].getParentFile();
    	                    	kz.tamur.rt.Utils.setLastSelectDir(currDir.toString());
    	                    	upload(sfs);
    	                    }
    	                }
    	        	}
                } else {
    	        	if (doBeforeOpen()) {
		                open();
    	        	}
                }
	        }
	        if (isEditor()) {
	        	cellEditor.stopCellEditing();
	        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setLangId(long langId) {
        this.ifcLangId = langId;
        LangItem li = LangItem.getById(langId);
        if (li != null) {
            res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("KZ".equals(li.code) ? "kk" : "ru"));
            changeTitles(res);
        }
    }

    private void changeTitles(ResourceBundle res) {
        fileNotFoundMsg = res.getString("fileNotAppend");
    }

    private class OrDocFieldRenderer extends ZebraCellRenderer {

        private final JLabel comp = new JLabel("");

        public OrDocFieldRenderer() {
            comp.setOpaque(true);
            comp.setSize(25, 23);
            comp.setHorizontalAlignment(SwingConstants.CENTER);
            ImageIcon icon = kz.tamur.rt.Utils.getImageIcon("DocField");
            comp.setIcon(icon);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return comp;
        }

        public void setNewIcon(Icon icon) {
            comp.setIcon(icon);
        }
    }

    public TableCellRenderer getCellRenderer() {
        return renderer;
    }

    public OrCellEditor getCellEditor() {
        if (cellEditor == null) {
            cellEditor = new OrDocFieldCellEditor();
        }
        return cellEditor;
    }

    class OrDocFieldCellEditor extends OrCellEditor {
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            valueChanged(new OrRefEvent(dataRef, -1, -1, null));
            button.setText("");
            return button;
        }

        public Object getCellEditorValue() {
            return null;
        }

        public Object getValueFor(Object obj) {
            return null;
        }
    }

    private File download() {
        try {
            String fileName = fileNameRef != null ? (String) fileNameRef.getValue(langId) : null;
            Object file = fileContentRef!=null ? fileContentRef.getValue(langId):null;
            if(file==null && report !=null && reportName!=null){
            	file=report;
            	fileName=reportName;
            }
            if (file != null) {
            	File tmpDir = new File("doc");
            	tmpDir.mkdirs();
                File tmpFile = (fileName != null) ? Funcs.getCanonicalFile(tmpDir, fileName) : Funcs.createTempFile("doc", ".tmp", tmpDir);
            	tmpFile.deleteOnExit();
            	if (file instanceof File)
            		Funcs.copy((File)file, tmpFile);
            	else if (file instanceof byte[])
            		Funcs.write((byte[])file, tmpFile);
            	return tmpFile;
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return null;
    }
    
    private void upload(File[] fs) throws Exception {
    	for (File f : fs) {
    		upload(f);
    	}
    }
    
    private void upload(File f) throws Exception {
    	f = (File)doBeforeModification(f);
    	if (f != null) {
	    	String fileName = f.getName();
	    	File tmpDir = Constants.TMP_DIRECTORY;
	    	File tmpFile = Funcs.getCanonicalFile(tmpDir, fileName);
	    	Funcs.copy(f, tmpFile);
	
        	boolean calcOwner = OrCalcRef.setCalculations();
        	try {
	    		if (fileNameRef != null) {
			        OrRef.Item item = dataRef.getItem(langId);
			        if (item == null) {
			        	item = dataRef.insertItem(-1, null, this, this, true);
			        } else if (item.getCurrent() == null) {
			        	dataRef.createObject(this, this);
			        }
			        
			        item = fileNameRef.getItem(langId);
			        if (item == null) {
			            fileNameRef.insertItem(-1, fileName, this, this, false);
			        } else {
			        	fileNameRef.changeItem(fileName, this, this);
			        }
	    		}
	    		
	    		OrRef.Item item = fileContentRef.getItem(langId);
		        if (item == null) {
		            fileContentRef.insertItem(-1, tmpFile, this, this, false);
		        } else {
		        	fileContentRef.changeItem(tmpFile, this, this);
		        }
		        if (action == Constants.DOC_UPDATE_VIEW) {
	                if (item == null || item.getCurrent() == null) {
	                    button.setText(button.titleBeforeAttaching);
	                } else {
	                    button.setText(button.titleAfterAttaching);
	                }
		        }
        	} catch (Exception e) {
                e.printStackTrace();
        	} finally {
	            if (calcOwner)
	            	OrCalcRef.makeCalculations();
        	}
	        doAfterModification();
    	}
    }

    private void open() {
        try {
        	File f = download();
        	if (f != null) {
                String str = f.getAbsolutePath();
                System.out.println(str);
                String[] cmd = new String[] {"explorer.exe", str};        
                Map<String, String> newEnv = new HashMap<String, String>();
                newEnv.putAll(System.getenv());
                String[] i18n = new String[cmd.length + 2];
                i18n[0] = "cmd";
                i18n[1] = "/C";
                i18n[2] = cmd[0];
                for (int counter = 1; counter < cmd.length; counter++) {
                    String envName = "JENV_" + counter;
                    i18n[counter + 2] = "%" + envName + "%";
                    newEnv.put(envName, cmd[counter]);
                }
                cmd = i18n;
                ProcessBuilder pb = new ProcessBuilder(cmd);
                Map<String, String> env = pb.environment();
                env.putAll(newEnv);
                pb.start();
        	}
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private void edit() throws Exception {
    	File f = download();
    	long time1 = f.lastModified();
    	String fileName = f.getName();
    	String ext = fileName.substring(fileName.lastIndexOf('.')).toLowerCase(Locale.ROOT);
    	if (ext.equals(".doc")||ext.equals(".docx")) {
    		editWordDocument(f);
    	} else if (ext.equals(".xls")||ext.equals(".xlsx")) {
    		editExcelDocument(f);
    	}
    	long time2 = f.lastModified();
    	if (time1 != time2) {
    		upload(f);
    		// обновление....
    		
    	}
    }

    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        button.setEnabled(isEnabled);
    }
    
    protected boolean doBeforeOpen() throws Exception {
        if (beforeOpenAction != null) {
            ClientOrLang orlang = new ClientOrLang(frame);
            Map<String, Object> vc = new HashMap<String, Object>();
            if (dataRef != null && dataRef.isColumn()) {
                OrRef p = dataRef;
                while (p != null && p.isColumn()) {
                    p = p.getParent();
                }
                if (p != null && p.getItem(0) != null) {
                    Object obj = p.getItem(0).getCurrent();
                    vc.put("SELOBJ", obj);
                }
            }
            
        	boolean calcOwner = OrCalcRef.setCalculations();
        	try {
        		orlang.evaluate(beforeOpenAction, vc, this, new Stack<String>());
        	} catch (Exception e) {
            	System.err.println("Ошибка при выполнении формулы 'Действие перед открытием' компонента 'DocField', uuid: " + getUUID());
        		e.printStackTrace();
        	} finally {
	            if (calcOwner)
	            	OrCalcRef.makeCalculations();
        	}

            Object res = vc.get("RETURN");
            report = vc.get("REPORT");
            reportName=(String)vc.get("REPORTNAME");
            return (Boolean.TRUE.equals(res) || Integer.valueOf(1).equals(res));
        }
        return true;
    }
    
    private void editWordDocument(File f) throws Exception {
        ActiveXComponent app = new ActiveXComponent("Word.Application");
        try {
    		Dispatch docs = app.getProperty("Documents").toDispatch();
			Dispatch doc = Dispatch.call(docs, "Open", f.getAbsolutePath()).toDispatch();
	        MsOfficeDispatchEvents appEvents = new MsOfficeDispatchEvents(app, doc);
            app.setProperty("Visible", new Variant(true));
            new DispatchEvents(doc, appEvents);
            while (!appEvents.finished) {
                Thread.sleep(2000);
            }
        } finally {
            //app.invoke("Quit");
            ComThread.Release();
        }
    }
    
    private void editExcelDocument(File f) throws Exception {
        ActiveXComponent app = new ActiveXComponent("Excel.Application");
        try {
            Dispatch wbs = app.getProperty("Workbooks").toDispatch();
			Dispatch wb = Dispatch.call(wbs, "Open", f.getAbsolutePath()).toDispatch();

//	        MsOfficeDispatchEvents appEvents = new MsOfficeDispatchEvents(app, wb);
            app.setProperty("Visible", new Variant(true));
            //new DispatchEvents(wb, appEvents);
            //Thread.sleep(Long.MAX_VALUE);
        } finally {
            //app.invoke("Quit");
            ComThread.Release();
        }
    }
    
    public static class MsOfficeDispatchEvents {
    	
    	private Dispatch app;
    	private Dispatch doc;
        
    	public transient boolean finished = false;
    	
    	public MsOfficeDispatchEvents(Dispatch app, Dispatch doc) {
    		this.app = app;
    		this.doc = doc;
    	}
    	
        public void Close(Variant[] args) {
	    	boolean b = Dispatch.get(doc, "Saved").getBoolean();
	    	if (!b) {
	    		Dispatch wb = Dispatch.get(app, "WordBasic").toDispatch();
				int option = Dispatch.call(wb, "MsgBox", new Variant("Сохранить файл?"), new Variant(4)).getInt();
				if (option == -1) {
					Dispatch.call(doc, "Save");
				} else {
		        	Dispatch.put(doc, "Saved", new Variant(true));
				}
	    	}
        	finished = true;
        }
    }

	
	@Override
	public boolean checkEnabled() {
		return super.checkEnabled();
	}
}