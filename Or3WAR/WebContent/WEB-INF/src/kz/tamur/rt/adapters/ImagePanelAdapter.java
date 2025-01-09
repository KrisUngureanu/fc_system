package kz.tamur.rt.adapters;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;

import kz.tamur.comps.Constants;
import kz.tamur.comps.Mode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.comps.interfaces.OrPanelComponent;
import kz.tamur.rt.adapters.OrRef.Item;
import kz.tamur.util.Funcs;
import kz.tamur.util.ImageUtil;
import kz.tamur.web.component.OrWebImagePanel;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.controller.WebController;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

public class ImagePanelAdapter extends ContainerAdapter {

    private OrWebImagePanel imgPanel;
    private boolean isEn = true;

    private OrRef tableRef;
    private OrRef titleRef;
    private OrRef imageRef;
    
    public ImagePanelAdapter(OrFrame frame, OrPanelComponent panel, boolean isEditor) throws KrnException {
        super(frame, panel, isEditor);
        this.imgPanel = (OrWebImagePanel)panel;

        PropertyNode pov = panel.getProperties().getChild("pov"); 
        PropertyNode pn = pov.getChild("activity").getChild("enabled");
        PropertyValue pv = panel.getPropertyValue(pn);
        if (!pv.isNull()) {
            isEn = pv.booleanValue();
        } else {
            isEn = ((Boolean)pn.getDefaultValue()).booleanValue();
        }
        setEnabled(isEn);
        
        createTableRef(panel);
        createImageRef(panel);
        createTitleRef(panel);

        if (tableRef != null) {
	        // Переставляем адаптер в список листененров позже refов других
	        tableRef.removeOrRefListener(this);
	        tableRef.addOrRefListener(this);
        }
    }

    public boolean isEnabled() {
        return isEn;
    }

    public OrPanelComponent getPanel() {
        return imgPanel;
    }

    public void clear() {}

    protected void createTableRef(OrGuiComponent c) throws KrnException {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("ref").getChild("tableRef");
        
        PropertyValue pv = c.getPropertyValue(rprop);
        if (!pv.isNull() && pv.stringValue().length() > 0) {
            try {
                propertyName = "Свойство: Данные";

                boolean hasParentRef = false;
                Map<String, OrRef> refs = frame.getRefs();
                Iterator<String> it = refs.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    if (key != null && pv.stringValue().startsWith(key.toString())) {
                        hasParentRef = true;
                        break;
                    }
                }
                if (!hasParentRef)
                    tableRef = OrRef.createContentRef(pv.stringValue(),
                            Constants.RM_ALWAYS, Mode.RUNTIME,
                            frame.getTransactionIsolation(), frame);

                if (tableRef == null)
                    tableRef = OrRef.createRef(pv.stringValue(), false, Mode.RUNTIME,
                            frame.getRefs(), frame.getTransactionIsolation(), frame);

                tableRef.addCheckContext(this);
            } catch (Exception e) {
                showErrorNessage(e.getMessage() + pv.stringValue());
                e.printStackTrace();
            }
        }
    }

    protected void createImageRef(OrGuiComponent c) throws KrnException {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("ref").getChild("imageRef");
        
        PropertyValue pv = c.getPropertyValue(rprop);
        if (!pv.isNull() && pv.stringValue().length() > 0) {
            try {
                propertyName = "Свойство: Данные для изображения";

                if (imageRef == null)
                    imageRef = OrRef.createRef(pv.stringValue(), true, Mode.RUNTIME,
                            frame.getRefs(), frame.getTransactionIsolation(), frame);

                imageRef.addCheckContext(this);
                imageRef.addOrRefListener(this);
            } catch (Exception e) {
                showErrorNessage(e.getMessage() + pv.stringValue());
                e.printStackTrace();
            }
        }
    }

    protected void createTitleRef(OrGuiComponent c) throws KrnException {
        PropertyNode prop = c.getProperties();
        PropertyNode rprop = prop.getChild("ref").getChild("titleRef");
        
        PropertyValue pv = c.getPropertyValue(rprop);
        if (!pv.isNull() && pv.stringValue().length() > 0) {
            try {
                propertyName = "Свойство: Титулы";

                if (titleRef == null)
                	titleRef = OrRef.createRef(pv.stringValue(), true, Mode.RUNTIME,
                            frame.getRefs(), frame.getTransactionIsolation(), frame);

                titleRef.addCheckContext(this);
                titleRef.addOrRefListener(this);
            } catch (Exception e) {
                showErrorNessage(e.getMessage() + pv.stringValue());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void valueChanged(OrRefEvent e) {
     	OrRef ref = e.getRef();
        if (ref == null) 
            return;

        if (e.getOriginator() != this && !selfChange) {
        	// Если изменения данных
        	if (ref == tableRef) {
        		// если изменился только выделенный индекс
                try {
                    selfChange = true;
		            if (e.getReason() == OrRefEvent.ITERATING) {
		            	// то просто выделяем необходимый элемент
		                int i = tableRef.getIndex();
		                setSelectedIndex(i);
		            } else if (e.getReason() == OrRefEvent.INSERTED) {
		            	imageInserted(e.getStartIndex());
		            	setSelectedIndex(tableRef.getIndex());
		            } else if (e.getReason() == OrRefEvent.DELETED) {
                        for (int i = e.getEndIndex() - 1; i >= e.getStartIndex(); i--) {
                        	imageDeleted(e.getStartIndex());
                        }
                        imgPanel.imageDeleted(e.getStartIndex(), e.getEndIndex());
		            	setSelectedIndex(tableRef.getIndex());
		            } else {
		            	// иначе заново формируем модель данных
	                    initModel();
		                int i = tableRef.getIndex();
		                setSelectedIndex(i);
		            }
                } finally {
                    selfChange = false;
                }
        	} else if (ref == imageRef) {
	            if (e.getReason() == OrRefEvent.UPDATED) {
	                int i = titleRef.getIndex();
	                
	                Object o = imageRef.getItem(0, i).getCurrent();
	                
	                File fileValue = null;
	                if (o instanceof File) {
	                	fileValue = (File)o;
	                	
	                	if (!fileValue.getAbsolutePath().startsWith(WebController.WEB_IMAGES_DIRECTORY.getAbsolutePath())) {
	                		fileValue = Funcs.createTempFile("blob", null, WebController.WEB_IMAGES_DIRECTORY);
		                    ((WebFrame)frame).getSession().deleteOnExit(fileValue);
		                    try {
		                    	Funcs.copy((File)o, fileValue);
		                    	imageRef.getItem(0, i).getRec().setValue(fileValue);
		                    } catch (IOException ioe) {
		                    	log.error(ioe, ioe);
		                    }
	                	}
	                } else if (o instanceof byte[]) {
	                	fileValue = Funcs.createTempFile("blob", null, WebController.WEB_IMAGES_DIRECTORY);
	                    ((WebFrame)frame).getSession().deleteOnExit(fileValue);
	                    try {
	                    	Funcs.write((byte[])o, fileValue);
	                    	imageRef.getItem(0, i).getRec().setValue(fileValue);
	                    } catch (IOException ioe) {
	                    	log.error(ioe, ioe);
	                    }
	                }
	                imgPanel.imageChanged(i, fileValue);
	            }        		
        	} else if (ref == titleRef) {
	            if (e.getReason() == OrRefEvent.UPDATED) {
	                int i = titleRef.getIndex();
	                imgPanel.titleChanged(i, (String)titleRef.getItem(0, i).getCurrent());
	            }        		
        	}
        }

        super.valueChanged(e);
    }
    
    public void setSelectedIndex(int i) {
    	imgPanel.setSelectedIndexDirectly(i);
    	if (!selfChange) {
            try {
                selfChange = true;
        		tableRef.absolute(i, this);
            } finally {
                selfChange = false;
            }
    	}
    }

    public void initModel() {
		DefaultListModel<OrImageItem> model = new DefaultListModel<OrImageItem>();

		List<Item> items = tableRef.getItems(0);

		for (int i = 0; i < items.size(); ++i) {
			OrRef.Item item = (OrRef.Item) items.get(i);
			KrnObject obj = (KrnObject) item.getCurrent();
			
			if (obj != null) {
				OrImageItem citem = new OrImageItem(obj, imageRef.getItem(0, i).getCurrent(), (String)titleRef.getItem(0, i).getCurrent());
				model.addElement(citem);
			}
		}
		
		imgPanel.setModel(model);
    }
    
    public void imageInserted(int index) {
		DefaultListModel<OrImageItem> model = imgPanel.getModel();

		Item item = tableRef.getItem(0, index);
		KrnObject obj = (KrnObject) item.getCurrent();
			
		if (obj != null) {
			OrImageItem citem = new OrImageItem(obj, imageRef.getItem(0, index).getCurrent(), (String)titleRef.getItem(0, index).getCurrent());
			model.add(index, citem);
		}
		
		imgPanel.imageInserted(index);
    }

    public void imageDeleted(int index) {
		DefaultListModel<OrImageItem> model = imgPanel.getModel();
		model.remove(index);
    }

    public int getItemCount() {
    	return imgPanel.getModel().getSize();
    }
    
    public class OrImageItem implements Comparable<OrImageItem> {
        
    	private KrnObject object_;
        private String title_;
        private File file_;
        private boolean isImage = false;

        public OrImageItem(KrnObject object, Object fileOrBytes, String title) {
            object_ = object;
            title_ = title;
            
            File file = null;
            if (fileOrBytes instanceof byte[]) {
            	file = Funcs.createTempFile("blob", null, WebController.WEB_IMAGES_DIRECTORY);
                ((WebFrame)frame).getSession().deleteOnExit(file);
                try {
                	Funcs.write((byte[])fileOrBytes, file);
                } catch (IOException ioe) {
                	log.error(ioe, ioe);
                }
            } else if (fileOrBytes instanceof File) {
            	file = (File)fileOrBytes;
            }
            	
            setFile(file);
        }

        public KrnObject getObject() {
            return object_;
        }
        
        public String toString() {
            return (title_ != null) ? title_.toString() : "";
        }
        
        public String getDownloadPath() {
            return (file_ != null) ? file_.getName() : "";
        }
        
        public boolean isImage() {
        	return this.isImage;
        }
        
        public void setTitle(String title) {
        	this.title_ = title;
        }
        
        public void setFile(File file) {
        	this.file_ = file;
        	this.isImage = false;

    		try {
    			if (file_ != null) {
    				BufferedImage img = ImageIO.read(file_);
    				this.isImage = (img != null);
    			}
    		} catch (Exception e) {
    			log.warn("Not image file");
    		}
    		
    		if (!this.isImage) {
    			File pdfImage = ImageUtil.savePDFasImage(file_, WebController.WEB_IMAGES_DIRECTORY);
    			if (pdfImage != null) {
    				((WebFrame)frame).getSession().deleteOnExit(pdfImage);
    				this.file_ = pdfImage;
    				this.isImage = true;
    			}
    		}
        }

        public int compareTo(OrImageItem o) {
            if (o != null) {
            	
                if ((title_ == null || "".equals(title_)) && !(o.title_ == null || "".equals(o.title_))) {
            		return -1;
            	} else if (!(title_ == null || "".equals(title_)) && (o.title_ == null || "".equals(o.title_))) {
            		return 1;
            	}
            	
                return title_.compareTo(o.title_);
            }
            return 1;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof OrImageItem) {
            	OrImageItem item = (OrImageItem) obj;
                return object_ != null && item.object_ != null && object_.id == item.object_.id;
            }
            return false;
        }
    }

}