package kz.tamur.rt.adapters;

import kz.tamur.comps.OrImage;
import kz.tamur.comps.Utils;
import kz.tamur.comps.Constants;
import kz.tamur.comps.gui.DefaultFocusAdapter;
import kz.tamur.rt.RadioGroupManager;
import kz.tamur.util.Funcs;
import kz.tamur.util.LangItem;
import com.cifs.or2.kernel.KrnException;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ResourceBundle;
import java.util.Locale;
import static kz.tamur.rt.Utils.createMenuItem;
public class ImageAdapter extends ComponentAdapter implements ActionListener {
    private OrImage image;
    private RadioGroupManager groupManager = new RadioGroupManager();
    private JPopupMenu pm = new JPopupMenu();
    private JMenuItem miLoad = createMenuItem("Загрузить");
    private JMenuItem miDelete = createMenuItem("Удалить");
    private long ifcLangId = 0;
    private ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    
    public ImageAdapter(UIFrame frame, OrImage img, boolean isEditor) throws KrnException {
        super(frame, img, isEditor);
        image = img;
        image.setAdapter(this);
        if (dataRef != null && !dataRef.isColumn()) {
            kz.tamur.rt.Utils.setComponentTabFocusCircle(image);
            image.addFocusListener(new DefaultFocusAdapter(image));
        }

        image.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                popupShow(e);
            }

            public void mousePressed(MouseEvent e) {
                popupShow(e);
            }

            public void mouseReleased(MouseEvent e) {
                popupShow(e);
            }

            void popupShow(MouseEvent e) {
                if (e.isPopupTrigger() && image.isShowPopup()) {
                    pm.show(image, e.getX(), e.getY());
                }
            }
        });
        image.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                    popupShow(e);
                }
            }

            void popupShow(KeyEvent e) {
                if (image.isShowPopup()) {
                    pm.show(image, image.getWidth() / 2, image.getHeight() / 2);
                }
            }
        });

        pm.add(miLoad);
        miLoad.addActionListener(this);

        pm.add(miDelete);
        miDelete.addActionListener(this);

        image.setXml(null);
    }

    // RefListener
    public void valueChanged(OrRefEvent e) {
        super.valueChanged(e);
        OrRef.Item item = dataRef.getItem(langId);
        Object value = (item == null) ? null : item.getCurrent();

        if (value instanceof File) {
            File val = (File) value;
            ImageIcon img = new ImageIcon(Funcs.getCanonicalName(val));

            if (image.isAutoResize()) {
            	int width = image.getWidth();
            	if (width < 1) width = image.getPrefSize().width;
            	int height = image.getWidth();
            	if (height < 1) height = image.getPrefSize().height;
                img = kz.tamur.rt.Utils.setSize(img, width, height);
            }

            image.setHorizontalAlignment(SwingConstants.CENTER);
            image.setVerticalAlignment(SwingConstants.CENTER);
            image.setIcon(img);
        } else if (value instanceof byte[]) {
            byte[] val = (byte[]) value;
            ImageIcon img = new ImageIcon(val);

            if (image.isAutoResize()) {
                img = kz.tamur.rt.Utils.setSize(img, image.getWidth(), image.getHeight());
            }

            image.setHorizontalAlignment(SwingConstants.CENTER);
            image.setVerticalAlignment(SwingConstants.CENTER);
            image.setIcon(img);
        }else if (value == null){
            image.setIcon(null);
        }

        if (radioGroup != null) {
            groupManager.evaluate(frame, radioGroup);
        }
    }
    
    public void doAfterModification() {
    	try {
			super.doAfterModification();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public Object doBeforeModification(File file) {
    	Object res = null;
    	try {    		
    		if(file != null) {    			
    			res = super.doBeforeModification(file);
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return res;
    }

    public void clear() {
    }

    public void actionPerformed(ActionEvent e) {
        try {
            Object src = e.getSource();
            if (miLoad == src) {
                boolean select = true;
                JFileChooser fch = Utils.createOpenChooser(Constants.IMAGE_FILTER, ifcLangId);
                while (select) {
                    if (fch.showOpenDialog(image) == JFileChooser.APPROVE_OPTION) {
                        File sf = fch.getSelectedFile();
                        if (image.getMaxDataSize() == 0 || image.getMaxDataSize() > sf.length()) {
                        	sf = (File) doBeforeModification(sf);
                            final String ext = "." + sf.getName().replaceFirst("^.*\\.", "");
                            kz.tamur.rt.Utils.setLastSelectDir(sf.getParentFile().toString());
                            if (sf != null) {
                                File df = null;
                                df = Funcs.createTempFile("img", ext);
                                df.deleteOnExit();
                                copy(sf, df);
                                setItem(df);
                                select = false;
                                doAfterModification();
                            }
                        }else {
                            String mess = res.getString("maxSize").replaceFirst("XXX",image.getMaxDataSize()/1024+"");
                            JOptionPane.showMessageDialog(image, mess , "Предупреждение", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        select = false;
                    }
                }
            } else if (miDelete == src) {
                setItem(null);
                image.setIcon(null);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

	private void setItem(File file) throws KrnException {
		OrRef ref = dataRef;
		if (ref.getItem(langId) == null) {
			ref.insertItem(0, file, this, this, false);
		} else {
			if (file == null) {
				dataRef.deleteItem(this, dataRef.getValue(0, 0));
			} else {
				ref.changeItem(file, this, this);
			}
		}
	}

    public void copy(File src, File dst) throws IOException, FileNotFoundException {
        FileInputStream is = new FileInputStream(src);
        byte[] buf = new byte[(int) src.length()];
        is.read(buf);
        is.close();
        FileOutputStream os = new FileOutputStream(dst);
        os.write(buf);
        os.close();
    }

    public void setEnabled(boolean isEnabled) {
        image.setEnabled(isEnabled);
    }

    public void setLangId(long langId) {
        ifcLangId = langId;
        LangItem li = LangItem.getById(langId);
        if (li != null) {
            res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("KZ".equals(li.code) ? "kk" : "ru"));
            miLoad.setText(res.getString("upload"));
            miDelete.setText(res.getString("delete2"));
        }
    }
}
