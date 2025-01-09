package kz.tamur.rt.adapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.client.comps.interfaces.OrDocFieldComponent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.rt.data.Record;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.LangHelper;
import kz.tamur.web.common.webgui.WebComponent;
import kz.tamur.web.component.OrWebDocField;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.controller.WebController;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.util.expr.Editor;
import com.eclipsesource.json.JsonObject;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.Variant;

public class DocFieldAdapter extends ComponentAdapter {

    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + DocFieldAdapter.class.getName());

    private OrDocFieldComponent button;
    String path;
    private int action = 0;
    private Kernel krn;
    private ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    private String fileNotFoundMsg = "Файл не прикреплён";
    private long ifcLangId = 0;
    
    private OrRef fileNameRef;
    private OrRef fileContentRef;
    private KrnAttribute fileContentAttr;
    
    private ASTStart beforeOpenAction;
    private boolean dontDependNull;
    private Object report;
    private String reportName;
    private boolean file = true;
	private long maxFileSize;
	
	private OrCalcRef attentionRef;

    public DocFieldAdapter(OrFrame frame, OrDocFieldComponent docField, boolean isEditor) throws KrnException {
        super(frame, docField, isEditor);
        krn = frame.getKernel();
        button = docField;
        PropertyNode proot = button.getProperties();
        PropertyNode povNode = proot.getChild("pov");
        PropertyValue pv = button.getPropertyValue(povNode.getChild("action"));
        if (!pv.isNull()) {
            action = pv.intValue();
        }
        pv = button.getPropertyValue(povNode.getChild("activity").getChild("dontDependNull"));
        dontDependNull = pv.booleanValue();
        // Триггеры
        // До модификации
        PropertyNode beforeOpenNode = povNode.getChild("beforeOpenAction");
        if (beforeOpenNode != null) {
            pv = docField.getPropertyValue(beforeOpenNode);
            String expr = pv.isNull() ? "" : pv.stringValue(frame.getKernel());
            if (expr.length() > 0) {
            	long ifcId = ((WebFrame)frame).getObj().id;
            	String key = ((WebComponent)docField).getId() + "_" + OrLang.BEFORE_OPEN_TYPE;
            	beforeOpenAction = ClientOrLang.getStaticTemplate(ifcId, key, expr, getLog());
                Editor e = new Editor(expr);
                ArrayList<String> paths = e.getRefPaths();
                for (int j = 0; j < paths.size(); ++j) {
                    String path = paths.get(j);
                    OrRef.createRef(path, false, Mode.RUNTIME, frame.getRefs(),
                            OrRef.TR_CLEAR, frame);
                }
            }
        }
        button.setXml(null);
        setАttentionRef(button);
    }
    
    public void setАttentionRef(OrGuiComponent c) {
    	String attentionExpr = ((OrWebDocField) c).getaAttentionExpr();
    	if (attentionExpr != null && attentionExpr.length() > 0) {
        	try {
                propertyName = "Свойство: Поведение.Активность.Внимание";
	            attentionRef = new OrCalcRef(attentionExpr, false, Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame, c, propertyName, this);
	            attentionRef.addOrRefListener(this);
            } catch (Exception e) {
                showErrorNessage(e.getMessage() + attentionExpr);
                log.error(e, e);
            }
    	}
    }
    
	@Override
	protected void createDataRef(OrGuiComponent c) throws KrnException {
		super.createDataRef(c);
		if (dataRef != null) {
			String path = dataRef.toString();
			if (dataRef.getAttr().typeClassId == Kernel.IC_BLOB) {
				fileContentRef = dataRef;
				fileNameRef = null;
			} else {
				fileNameRef = OrRef.createRef(path + ".filename", dataRef.isColumn || dataRef.isArray(), Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
				
				// Создаем реф для содержимого MsDoc только если это не множественный
				if (!dataRef.isColumn && !dataRef.isArray())
					fileContentRef = OrRef.createRef(path + ".file", dataRef.isColumn || dataRef.isArray(), Mode.RUNTIME, frame.getRefs(), frame.getTransactionIsolation(), frame);
				else
					fileContentAttr = frame.getKernel().getAttributeByName(dataRef.getAttr().typeClassId, "file");
				
				if (langId > 0) {
					fileNameRef.addLanguage(langId);
					if (fileContentRef != null)
						fileContentRef.addLanguage(langId);
	            }
			}
		}
	}

	public void clear() {
		setEnabled(action == Constants.DOC_VIEW || action == Constants.DOC_UPDATE || action == Constants.DOC_PRINT);
    }

    public JsonObject deleteValue(int index) {
        try {
            dataRef.deleteItem(this, index, this);
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return new JsonObject().set("result", "success");
    }

    public JsonObject buttonPressed() {
         try {
             if (action == Constants.DOC_VIEW || action == Constants.DOC_PRINT) {
                 if (doBeforeOpen()) {
                     OrRef.Item item = dataRef.getItem(langId);
                     if (item == null || item.getCurrent() == null) {
                         Util.showMessage(button, fileNotFoundMsg, "Выражение", MessagesFactory.INFORMATION_MESSAGE);
                     } else {
                         return open(-1);
                     }
                 }
             } else if (action == Constants.DOC_UPDATE || action == Constants.DOC_UPDATE_VIEW) {
                 if (doBeforeOpen()) {
                     File sf = button.getFileToUpload();
                     if (sf != null) {
                         upload(sf);
                     }
                 }
             } else if (action == Constants.DOC_EDIT) {
                 if (doBeforeOpen()) {
                     return edit();
                 }
             }
         } catch (Exception ex) {
             log.error(ex, ex);
         }
         return new JsonObject().set("result", "error");
     }

    public JsonObject openFile(int index) {
        try {
            if (doBeforeOpen()) {
                OrRef.Item item = dataRef!=null ? (index > -1 ? dataRef.getItem(langId, index) : dataRef.getItem(langId)):null;
                if (dataRef==null && (report==null || reportName==null) && (item == null || item.getCurrent() == null)) {
                    Util.showMessage(button, fileNotFoundMsg, "Выражение", MessagesFactory.INFORMATION_MESSAGE);
                } else {
                    return open(index);
                }
            }
        } catch (Exception ex) {
            log.error(ex, ex);
        }
        return new JsonObject().set("result", "error");
    }

    @Override
	public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        if (e.getRef() == dataRef) {
            OrRef.Item item = dataRef.getItem(langId);
        	if (action == Constants.DOC_VIEW || action == Constants.DOC_EDIT || action == Constants.DOC_PRINT) {
        		setEnabled(item != null && item.getCurrent() != null || dontDependNull);
        	} else if (action == Constants.DOC_UPDATE_VIEW) {
	            if (item == null || item.getCurrent() == null) {
                    ((OrWebDocField) button).changeMode(0);	// 0 - Загрузка, 1 - Просмотр
	            } else {
                    ((OrWebDocField) button).changeMode(1);	// 0 - Загрузка, 1 - Просмотр
	            }
        	}
        } else if (e.getRef() == attentionRef) {
			((OrWebDocField) button).sendChangeProperty("buttonAttention", attentionRef.getValue(langId).toString());
        }
        if (((OrWebDocField) button).isShowUploaded()) {
        	((OrWebDocField) button).sendChangeProperty("reloadUpload", 1);
        }
    }

    public void setLangId(long langId) {
        this.ifcLangId = langId;
        LangHelper.WebLangItem li = LangHelper.getLangById(langId, ((WebFrame)frame).getSession().getConfigNumber());
        if (li != null) {
            if ("KZ".equals(li.code)) {
                res = ResourceBundle.getBundle(
                        Constants.NAME_RESOURCES, new Locale("kk"));
            } else {
                res = ResourceBundle.getBundle(
                        Constants.NAME_RESOURCES, new Locale("ru"));
            }
            changeTitles(res);
        }
    }

    private void changeTitles(ResourceBundle res) {
        fileNotFoundMsg = res.getString("fileNotAppend");
    }

    private File download(int index) {
        try {
            String fileName = fileNameRef != null 
            		? (String) (index > -1 ? fileNameRef.getValue(langId, index) : fileNameRef.getValue(langId)) : null;
            Object file = fileContentRef != null 
            		? (index > -1 ? fileContentRef.getValue(langId, index) : fileContentRef.getValue(langId)) : null;

            if (fileContentAttr != null) {
                KrnObject doc = (KrnObject) (index > -1 ? dataRef.getValue(langId, index) : dataRef.getValue(langId));

                try {
                	Record fileRecord = frame.getCash().getRecord(doc.id, fileContentAttr, 0, 0);
                	if (fileRecord != null)
                		file = fileRecord.getValue();
                } catch (KrnException e) {
                	log.error(e, e);
                }
            }
            		
            if (file==null && report !=null && reportName!=null){
              	file=report;
               	fileName=reportName;
            }
            if (file != null) {
                File tmpDir = WebController.WEB_DOCS_DIRECTORY;
                
                String fn = (fileName != null) ? fileName : "doc.tmp";
                String fs = "";
                int beg = fn.lastIndexOf('.');
                if (beg > -1) {
                	fs = fn.substring(beg);
                	fn = fn.substring(0, beg);
                }
                
                File tmpFile = null;
                int i = 0;
                
                beg = fn.lastIndexOf('-');
                if (beg > -1) {
                	String ind = fn.substring(beg);
                	if (Funcs.isDigits(ind)) {
                		i = Integer.parseInt(ind);
                    	fn = fn.substring(0, beg);
                	}
                }
                
                do {
                	tmpFile = new File(tmpDir, fn + (i++ > 0 ? ("-" + i) : "") + fs);
                } while (!tmpFile.createNewFile());
                ((WebFrame)frame).getSession().deleteOnExit(tmpFile);
                
                if (file instanceof File) {
                    Funcs.copy((File) file, tmpFile);
                } else if (file instanceof byte[]) {
                    Funcs.write((byte[]) file, tmpFile);
                }
                return tmpFile;
            }
        } catch (IOException e) {
            log.error(e, e);
        }
        return null;
    }
    
    private void upload(File f) throws Exception {
    	maxFileSize = ((OrWebDocField)button).isMaxFileSize();
    	if (maxFileSize>0){
    		long fileSize = f.length();
    		if (fileSize >= maxFileSize){
    			file = false;
    		} else {
    			file = true;
    		}
    	}
    	if (file){
    		f = (File)doBeforeModification(f);
        	if (f != null) {
    	    	String fileName = f.getName();
    	    	File tmpFile = Funcs.createTempFile("UPL", null, WebController.WEB_DOCS_DIRECTORY);
                ((WebFrame)frame).getSession().deleteOnExit(tmpFile);
    	    	Funcs.copy(f, tmpFile);
            	boolean calcOwner = OrCalcRef.setCalculations();
            	try {
            		if (fileNameRef != null) {
                		int index = fileNameRef.getIndex();
    			        OrRef.Item item = dataRef.getItem(langId);
    			        if (item == null || dataRef.isArray()) {
    			        	dataRef.insertItem(-1, null, this, this, true);
    			        	index = dataRef.getItems(langId).size() - 1;
    			        } else if (item.getCurrent() == null) {
    			        	dataRef.createObject(this, this);
    			        }
    			        item = fileNameRef.getItem(langId, index);
    			        if (item == null) {
    			            fileNameRef.insertItem(-1, fileName, this, this, false);
    			        } else {
    			        	fileNameRef.changeItem(fileName, this, this);
    			        }
    			        if (fileContentRef != null) {
	    	        		item = fileContentRef.getItem(langId, index);
	    			        if (item == null) {
	    			            fileContentRef.insertItem(-1, tmpFile, this, this, false);
	    			        } else {
	    			        	fileContentRef.changeItem(tmpFile, this, this);
	    			        }
    			        } else if (fileContentAttr != null) {
			                KrnObject doc = (KrnObject) (index > -1 ? dataRef.getValue(langId, index) : dataRef.getValue(langId));

			                try {
			                	Record fileRecord = frame.getCash().getRecord(doc.id, fileContentAttr, 0, 0);
			                	if (fileRecord == null)
			                		frame.getCash().insertObjectAttribute(doc, fileContentAttr, 0, langId, tmpFile, this);
			                	else
			                		frame.getCash().changeObjectAttribute(fileRecord, tmpFile, this);
			                	
			                } catch (KrnException e) {
			                	log.error(e, e);
			                }
    			        }
            		} else {
            			if (fileContentRef != null) {
	            			OrRef.Item item = fileContentRef.getItem(langId);
	            			int index = fileContentRef.getIndex();
	            			if (item == null && fileContentRef.isArray()) {
	            				fileContentRef.insertItem(-1, tmpFile, this, this, false);
	            			} else if (item == null) {
	        		            fileContentRef.insertItem(index, tmpFile, this, this, false);
	            			} else {
	        		        	fileContentRef.changeItem(index, tmpFile, this, this);
	            			}
            			}
            		}
    		        if (action == Constants.DOC_UPDATE_VIEW) {
    		        	if (fileContentRef != null) {
	    		    		OrRef.Item item = fileContentRef.getItem(langId);
	    	                if (item != null || item.getCurrent() != null) {
	    	                    ((OrWebDocField) button).changeMode(1);	// 0 - Загрузка, 1 - Просмотр
	    	                }
    		        	}
    		        }
            	} catch (Exception e) {
            		log.error(e, e);
            	} finally {
    	            if (calcOwner)
    	            	OrCalcRef.makeCalculations();
            	}

    	        doAfterModification();
    	        if (((OrWebDocField)button).isShowUploaded())
    	        	((OrWebDocField)button).sendChangeProperty("reloadUpload", 1);
        	}
		} else {
    		Util.showInformMessage(button, "Максимальный размер загружаемого файла не должен превышать "+maxFileSize/(1024*1024)+" МБ");
    	}
		if (f != null) {
			try { f.delete(); } catch (Exception e) {}
		}
    }

    private JsonObject open(int index) {
        File f = download(index);
        return button.open(f);
    }
    
    private JsonObject edit() {
        File f = download(-1);
        return button.edit(f);
    }

    private void _edit() throws Exception {
    	File f = download(-1);
    	long time1 = f.lastModified();
    	String fileName = f.getName();
    	String ext = fileName.substring(fileName.lastIndexOf('.')).toLowerCase(Constants.OK);
    	if (ext.equals(".doc")||ext.equals(".docx")) {
    		editWordDocument(f);
    	} else if (ext.equals(".xls")||ext.equals(".xlsx")) {
    		editExcelDocument(f);
    	}
    	long time2 = f.lastModified();
    	if (time1 != time2) {
    		upload(f);
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
            	log.error("Ошибка при выполнении формулы 'Действие перед открытием' компонента '" + (comp != null ? comp.getClass().getName() : "") + "', uuid: " + getUUID());
        		log.error(e, e);
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
    		Dispatch docs = app.getProperty("Documents").toDispatch();
			Dispatch doc = Dispatch.call(docs, "Open", f.getAbsolutePath()).toDispatch();
	        MsOfficeDispatchEvents appEvents = new MsOfficeDispatchEvents(app, doc);
            app.setProperty("Visible", new Variant(true));
            new DispatchEvents(app.getObject(), appEvents, "Excel.Application");
            Thread.sleep(Long.MAX_VALUE);
        } finally {
            app.invoke("Quit");
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
    
    public int getAction() {
        return action;
    }

	public String getUploadedData() {
		OrWebDocField df = (OrWebDocField)button;
		StringBuilder sb = new StringBuilder();
		List<Item> items = dataRef.getItems(langId);
		if (items != null) {
			for (int i=0; i<items.size(); i++) {
				if (fileNameRef != null) {
					Item item = fileNameRef.getItem(langId, i);
					String fileName = (item != null) ? (String)item.getCurrent() : "#" + i;
					sb.append("<p class=\"file-panel\" id=\"").append(df.getUUID()).append("\" index=\"").append(i)
						.append("\"><a class=\"docField view-file\">").append(fileName)
						.append("</a><i class=\"delete-file fam-bin\"></i></p>");
				}
			}
		}
		
		return sb.toString();
	}
}
