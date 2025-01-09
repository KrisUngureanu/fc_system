package kz.tamur.web.component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.table.TableCellRenderer;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.PropertyHelper;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.DocFieldPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.comps.interfaces.OrDocFieldComponent;
import kz.tamur.rt.Utils;
import kz.tamur.rt.adapters.DocFieldAdapter;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.web.common.JSONCellComponent;
import kz.tamur.web.common.JSONComponent;
import kz.tamur.web.common.Margin;
import kz.tamur.web.common.webgui.WebButton;
import kz.tamur.web.controller.WebController;

import org.apache.commons.fileupload.FileItem;
import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

public class OrWebDocField extends WebButton implements JSONComponent, OrDocFieldComponent, JSONCellComponent {

    public static PropertyNode PROPS = new DocFieldPropertyRoot();

    private OrGuiContainer parent;
    private int tabIndex;
    private boolean isHelpClick = false;
    private String title;
    private String titleUID;
    public String beforeAttachingTitle;
    private String beforeAttachingTitleUID;
    public byte[] beforeAttachingIconBytes;
    public String afterAttachingTitle;
    private String afterAttachingTitleUID;
    public byte[] afterAttachingIconBytes;
    private DocFieldAdapter adapter;
    private String openFileName;
    private File fileToUpload;
    private boolean openForEdit = false;
    /** Номер конфигурации, для нескольких БД. */
    private int configNumber;
    protected boolean showUploaded = false;
    private long maxFileSize;
	private String attentionExpr;
    
    OrWebDocField(Element xml, int mode, OrFrame frame, boolean isEditor, String id) throws KrnException {
        super("OrDocField", xml, mode, frame, id);
        uuid = PropertyHelper.getUUID(this, frame);
        try {
	        configNumber = ((WebFrame) frame).getSession().getConfigNumber();
	        constraints = PropertyHelper.getConstraints(PROPS, xml, id, frame);
	        prefSize = PropertyHelper.getPreferredSize(this, id, frame);
	        maxSize = PropertyHelper.getMaximumSize(this, id, frame);
	        minSize = PropertyHelper.getMinimumSize(this, id, frame);
	        updateProperties();
	        setIconFullPath(WebController.APP_PATH + "/images/DocField.gif");
	        setPadding(new Dimension(1, 1));
	        if (mode == Mode.RUNTIME) {
	            adapter = new DocFieldAdapter(frame, this, isEditor);
	        }
        } catch (KrnException e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	throw e;
        } catch (Exception e) {
        	log.error("Ошибка при инициализации компонента " + this.getClass().getName() + "; uuid = " + uuid);
        	log.error(e, e);
        	throw new KrnException(0, "Ошибка при инициализации компонента");
        }
        this.xml = null;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public GridBagConstraints getConstraints() {
        return mode == Mode.RUNTIME ? constraints : PropertyHelper.getConstraints(PROPS, xml, id, frame);
    }

    public void setLangId(long langId) {
        if (mode == Mode.RUNTIME) {
            title = frame.getString(titleUID);
            setText(title);
            updateDescription();
            if (adapter != null) {
                adapter.setLangId(langId);
            }
        }
    }

    private void updateProperties() {
        PropertyValue pv = getPropertyValue(getProperties().getChild("title"));
        if (!pv.isNull()) {
        	Pair<String, Object> p = pv.resourceStringValue();
            titleUID = p.first;
            title = frame.getString(titleUID);
            setText(title);
        }
    	pv = getPropertyValue(getProperties().getChild("titleBeforeAttaching"));
    	if (!pv.isNull()) {
        	Pair<String, Object> p = pv.resourceStringValue();
        	beforeAttachingTitleUID = p.first;
    		beforeAttachingTitle = frame.getString(beforeAttachingTitleUID);
    	}
    	if (beforeAttachingTitle == null || beforeAttachingTitle.isEmpty()) {
    		beforeAttachingTitle = "Прикрепить файл";
    	}
    	pv = getPropertyValue(getProperties().getChild("titleAfterAttaching"));
    	if (!pv.isNull()) {
        	Pair<String, Object> p = pv.resourceStringValue();
        	afterAttachingTitleUID = p.first;
        	afterAttachingTitle = frame.getString(afterAttachingTitleUID);;
    	}
    	if (afterAttachingTitle == null || afterAttachingTitle.isEmpty()) {
    		afterAttachingTitle = "Просмотреть файл";
    	}
    	pv = getPropertyValue(getProperties().getChild("iconBeforeAttaching"));
    	if (!pv.isNull()) {
    		beforeAttachingIconBytes = pv.getImageValue();    		
    	}
    	pv = getPropertyValue(getProperties().getChild("iconAfterAttaching"));
    	if (!pv.isNull()) {
    		afterAttachingIconBytes = pv.getImageValue();    		
    	}
        pv = getPropertyValue(PROPS.getChild("varName"));
        if (!pv.isNull()) {
            varName = pv.stringValue(frame.getKernel());
        }
        PropertyNode pn = getProperties().getChild("view");
        pv = getPropertyValue(pn.getChild("font").getChild("fontG"));
        if (!pv.isNull()) {
            setFont(pv.fontValue());
        }
        pv = getPropertyValue(pn.getChild("font").getChild("fontColor"));
        if (!pv.isNull()) {
            setForeground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("background").getChild("backgroundColor"));
        if (!pv.isNull()) {
            setBackground(pv.colorValue());
        }
        pv = getPropertyValue(pn.getChild("alignmentText"));
        if (!pv.isNull()) {
            setHorizontalAlignment(pv.intValue());
        } else {
            setHorizontalAlignment((Integer) pn.getChild("alignmentText").getDefaultValue());
        }
        pv = getPropertyValue(getProperties().getChild("pov").getChild("activity").getChild("enabled"));
        if (!pv.isNull()) {
            setEnabled(pv.booleanValue());
        }
        pv = getPropertyValue(PROPS.getChild("view").getChild("showUploaded"));
        showUploaded = pv.isNull() ? (Boolean) PROPS.getChild("view").getChild("showUploaded").getDefaultValue() : pv.booleanValue();

        pn = getProperties().getChild("pov");
        pv = getPropertyValue(pn.getChild("tabIndex"));
        tabIndex = pv.intValue();
        
        pv = getPropertyValue(pn.getChild("activity").getChild("attention"));
        if (!pv.isNull()) {
        	attentionExpr = pv.stringValue(frame.getKernel());
        }
        
        pn = getProperties().getChild("constraints");
        pv = getPropertyValue(pn.getChild("maxSize2"));
        if (!pv.isNull()){
        	maxFileSize = pv.intValue()*1024*1024;
        }

        updateProperties(PROPS);
    }
    
    public String getaAttentionExpr() {
    	return attentionExpr;
    }

    public OrGuiContainer getGuiParent() {
        return parent;
    }

    public void setGuiParent(OrGuiContainer parent) {
        this.parent = parent;
    }

    public Dimension getPrefSize() {
        return mode == Mode.RUNTIME ? prefSize : PropertyHelper.getPreferredSize(this, id, frame);
    }

    public Dimension getMaxSize() {
        return mode == Mode.RUNTIME ? maxSize : PropertyHelper.getMaximumSize(this, id, frame);
    }

    public Dimension getMinSize() {
        return mode == Mode.RUNTIME ? minSize : PropertyHelper.getMinimumSize(this, id, frame);
    }

    public String getBorderTitleUID() {
        return null;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public boolean isHelpClick() {
        return isHelpClick;
    }

    public void setHelpClick(boolean helpClick) {
        isHelpClick = helpClick;
    }

    public void setAdapter(DocFieldAdapter adapter) {
        this.adapter = adapter;
    }

    public DocFieldAdapter getAdapter() {
        return adapter;
    }

    public File getFileToUpload() {
        return fileToUpload;
    }

    public TableCellRenderer getCellRenderer() {
        return null;
    }

    public void setValue(String value) {
		buttonPressed();
    }

    public JsonObject buttonPressed() {
        if (isHelpClick()) {
            setHelpClick(false);
        } else {
            return adapter.buttonPressed();
        }
        return new JsonObject().add("result", "error");
    }

    public JsonObject openFile(int index) {
        if (isHelpClick()) {
            setHelpClick(false);
        } else {
            return adapter.openFile(index);
        }
        return new JsonObject().add("result", "error");
    }

    public void deleteValue(int index) {
		adapter.deleteValue(index);
    }

    public JsonObject open(File f) {
        if (f != null) {
            openFileName = kz.tamur.web.common.Base64.encodeBytes(f.getName().getBytes());
            openFileNameChanged();
            JsonObject res = new JsonObject().add("result", "success").add("file", openFileName);
            if (adapter.getAction() == Constants.DOC_PRINT) {
            	res.add("action", "print");
            } else if (adapter.getAction() == Constants.DOC_VIEW) {
            	res.add("action", "view");
            	if (f.getName().endsWith(".html")) {
                	res.add("ext", "html");
            	}
            }
            return res;
        }
        return new JsonObject().add("result", "error");
    }

    public JsonObject edit(File f) {
        openForEdit = true;
        return open(f);
    }

    protected void valueChanged() {
    	if (adapter.getAction() != Constants.DOC_UPDATE_VIEW) {
    		sendChangeProperty("text", getText());
    	}
    }

    public void changeMode(int mode) {
    	JsonObject props = new JsonObject();
    	String text;
    	// 0 - Загрузка, 1 - Просмотр
    	if (mode == 0) {
    		text = frame.getString(beforeAttachingTitleUID);
    		if (text == null || text.isEmpty()) {
    			text = beforeAttachingTitle;
    		}
    		if (beforeAttachingIconBytes != null) {
    	    	props.add("iconBytes", new String(Base64.encode(beforeAttachingIconBytes)));
    		} else {
    			props.add("iconBytes", new String());
    		}
    	} else {
    		text = frame.getString(afterAttachingTitleUID);
    		if (text == null || text.isEmpty()) {
    			text = afterAttachingTitle;
    		}
    		if (afterAttachingIconBytes != null) {
    	    	props.add("iconBytes", new String(Base64.encode(afterAttachingIconBytes)));
    		} else {
    			props.add("iconBytes", new String());
    		}
    	}
    	this.text = text;
    	props.add("text", text);
    	props.add("mode", mode);
    	props.add("id", getUUID());
		sendChangeProperty("OrWebDocFieldProps", props);
    }
    
    protected void openFileNameChanged() {
        if (openForEdit) {
            openForEdit = false;
        }
        sendChangeProperty("fileName", openFileName);
    }

    public void setValue(FileItem fileItem) {
    	File dir = WebController.WEB_DOCS_DIRECTORY;
    	OutputStream os = null;
        try {
            valueChanged = true;
            String fn = fileItem.getName();
            String fs = "";
            int beg = fn.lastIndexOf("\\");
            if (beg == -1) {
                beg = fn.lastIndexOf("/");
            }
            if (beg > -1) {
                fn = fn.substring(beg + 1);
            }
            
            beg = fn.lastIndexOf('.');
            if (beg > -1) {
            	fs = fn.substring(beg);
            	fn = fn.substring(0, beg);
            }
            
            int i = 0;
            do {
            	fileToUpload = new File(dir, fn + (i++ > 0 ? ("-" + i) : "") + fs);
            } while (!fileToUpload.createNewFile());
            
            ((WebFrame)frame).getSession().deleteOnExit(fileToUpload);

            os = new FileOutputStream(fileToUpload);
            Funcs.writeStream(fileItem.getInputStream(), os, Constants.MAX_DOC_SIZE);

            adapter.buttonPressed();
            valueChanged();
        } catch (Exception e) {
            getLog().error(e, e);
        } finally {
			Utils.closeQuietly(os);
        }
    }

    public void setValue(InputStream is, String name) {
    	File dir = WebController.WEB_DOCS_DIRECTORY;
    	OutputStream os = null;
        try {
            valueChanged = true;
            
            String fs = "";
            int beg = name.lastIndexOf('.');
            if (beg > -1) {
            	fs = name.substring(beg);
            	name = name.substring(0, beg);
            }
            
            int i = 0;
            do {
            	fileToUpload = Funcs.getCanonicalFile(dir, name + (i++ > 0 ? ("-" + i) : "") + fs);
            } while (!fileToUpload.createNewFile());
            ((WebFrame)frame).getSession().deleteOnExit(fileToUpload);

            os = new FileOutputStream(fileToUpload);
            Funcs.writeStream(is, os, Constants.MAX_DOC_SIZE);
            
            adapter.buttonPressed();
            valueChanged();
        } catch (Exception e) {
            getLog().error(e, e);
        } finally {
			Utils.closeQuietly(os);
        }
    }

    @Override
    public JsonObject putJSON(boolean isSend) {
        JsonObject json = super.putJSON(isSend);
        JsonObject property = (JsonObject) json.get("pr");
        property.set("action", adapter.getAction());
        marginImage = new Margin(1, 1, 1, 1);
        if (tooltipText != null) {
            property.set("tt", tooltipText);
        }
        sendChange(json, isSend);
        return json;
    }

    @Override
    public JsonObject getJSON(Object value, int row, int column, String tid, boolean cellEditable, boolean isSelected, int state) {
        JsonObject obj = addJSON(tid);
        JsonObject action = new JsonObject();
        JsonObject property = new JsonObject();
        property.add("row", row);
        property.add("column", column);
        property.add("cellEditable", cellEditable);
        property.add("isSelected", isSelected);
        property.add("state", state);

        if (isEnabled() && adapter.getAction() == Constants.DOC_VIEW) {
            action.add("click", "docFieldPressed(this); return false;");
        } else if (adapter.isActive() && adapter.getAction() == Constants.DOC_UPDATE) {
            action.add("click", "loadImage2(this); return false;");
        } else if (isEnabled() && adapter.getAction() == Constants.DOC_EDIT) {
            action.add("click", "docFieldPressed(this); return false;");
        }

        property.add("value", Funcs.xmlQuote(getText()));

        JsonObject img = new JsonObject();
        img.add("src", getIconFullPath());
        property.add("img", img);
        if (action.size() > 0) {
            obj.add("on", action);
        }
        if (property.size() > 0) {
            obj.add("pr", property);
        }
        return obj;
    }

    @Override
    public String getPath() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().toString();
    }

    @Override
    public KrnAttribute getAttribute() {
        return adapter == null || adapter.getDataRef() == null ? null : adapter.getDataRef().getAttr();
    }

	public String getUploadedData() {
		return adapter.getUploadedData();
	}

	public boolean isShowUploaded() {
		return showUploaded;
	}
	
	public long isMaxFileSize(){
		return maxFileSize;
	}
}