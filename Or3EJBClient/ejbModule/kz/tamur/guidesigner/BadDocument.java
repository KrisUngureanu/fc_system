package kz.tamur.guidesigner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import kz.tamur.comps.Constants;
import kz.tamur.rt.TaskTable;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.ProcessException;
import com.cifs.or2.util.MultiMap;

/**
 * ТекстПоле от ComboBoxa.
 * @author g009c1233
 * @since 2011/06/07
 * @version 0.1
 */
public class BadDocument extends PlainDocument {
	public final Kernel krn = Kernel.instance();
	private HashMap map_srv = new HashMap();
	JComboBox box;
	ComboBoxModel model;
	JTextComponent editor;
	boolean selecting = false;
	boolean backspaced;
	boolean removed = true;
	HashMap res_map;
	private HashMap temp_map;
	private MultiMap map_;
	private String oldText = new String();
	int point = 0;
	boolean fromUp = false;
	boolean fromJustArrows = false;
	JPopupMenu jpop;
	JLabel label;
	boolean typed = false;
	boolean arrow = false;
	
	/**
	 * 
	 * @param box Цель, которой ТекстПоле
	 */
	public BadDocument(final JComboBox box){
		this.box = box;
		this.model = box.getModel();
		this.editor = (JTextComponent) box.getEditor().getEditorComponent();

		editor.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				backspaced = false;
				if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) { 
					backspaced = true;
					/*if(box.isPopupVisible())*/ box.setPopupVisible(false);
				}
				typed = true;
				if(KeyEvent.getKeyText(e.getKeyCode()) == "Up" || KeyEvent.getKeyText(e.getKeyCode())=="Down"){
					arrow = true;
				}
				if(KeyEvent.getKeyText(e.getKeyCode()) == "Enter"){
					typed = true;
					try {
						remove(0, editor.getText().length());
						insertString(0, box.getSelectedItem().toString(), null);
					} catch (BadLocationException ee) {
						ee.printStackTrace();
					}
					return;
				}
			}
		});
		box.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(!selecting) highlightText(0);
				if(e.getModifiers() == 4) {
					//arrows actions
				}
			}
		});
	}
	
	/**
	 * Удалять из поле
	 */
	public void remove(int offs, int len) throws BadLocationException {
        if (selecting) return;
		if(arrow) {
			return;
		}
        if (backspaced) {
            if (offs>0) {
            	highlightText(offs--);
            } else {
            	UIManager.getLookAndFeel().provideErrorFeedback(box);
            }
            highlightText(offs);
        } else {
            super.remove(offs, len);
        }
    }
	
	/**
	 * ввод текста
	 */
	public void insertStringBySuper(String s){
		try {
			//typed = true;
			super.insertString(0, s, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ввод текста
	 */
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException{
		if(!typed) return;
		typed = false;
		if(arrow) {
			arrow = false;
			return;
		}
		String text = editor.getText() + str;
		
        if (selecting) return;
        
        updateItems(offs, text, a);
        super.insertString(0, text, a);
        Object item = lookupItem(getText(0, getLength()));
        if (item != null) {
            setSelectedItem(item);
        } else {
            // keep old item selected if there is no match
            item = box.getSelectedItem();
            offs = offs-str.length();
            UIManager.getLookAndFeel().provideErrorFeedback(box);
			super.remove(0, getLength());
			super.insertString(0, text.substring(0, offs+1), null);
			highlightText(text.length());
			return;
        }
		//cut long part of text, not use it if you want
		if(offs + str.length() + 12 < item.toString().length())
			item = item.toString().substring(0, offs + str.length() + 12);
        try {
			super.remove(0, getLength());
			super.insertString(0, item.toString(), null);
		}catch(BadLocationException e){
			throw new RuntimeException(e.toString());
		}
        highlightText(offs+str.length());
	}
	
	/**
	 * Перекрашивание
	 * @param start откуда начиная
	 */
	private void highlightText(int start) {
		editor.setSelectionStart(start);
		editor.setSelectionEnd(getLength());
	}
	
	/**
	 * выбирает элемент.
	 * @param item цель
	 */
	private void setSelectedItem(Object item) {
		selecting = true;
		typed = true;
		model.setSelectedItem(item);
		selecting = false;
	}
	
	/**
	 * Проверка, начинается ли с
	 * @param str1 слово
	 * @param str2 начинается с
	 * @return да или нет
	 */
	private boolean startsWithIgnoreCase(String str1, String str2) {
        return str1.toUpperCase(Constants.OK).startsWith(str2.toUpperCase(Constants.OK));
    }
	
	//А смысл?! Все равно он первый
	public Object lookupItem(String pattern) {
		Object selectedItem = model.getSelectedItem();
        if (selectedItem != null && startsWithIgnoreCase(selectedItem.toString(), pattern)) {
            return selectedItem;
        } else {
            for (int i=0, n=model.getSize(); i < n; i++) {
                Object currentItem = model.getElementAt(i);
                if (startsWithIgnoreCase(currentItem.toString(), pattern)) return currentItem;
            }
        }
        return null;
	}
	
	/**
	 * Обнавляет содержимое
	 */
	private void updateItems(int offs, String str, AttributeSet a) {
		//insert search in new map of res_map, to make it faster!!!
		String txt = str;
		String txt2 = editor.getText();
		if(oldText.length()+1 != txt.length()) removed = true;
		if(!oldText.toLowerCase(Constants.OK).equals(txt.substring(0, txt.length()-1).toLowerCase(Constants.OK))) removed = true;
		oldText = txt;
		res_map = new HashMap();
		Set set;
		if(removed) {
			removed = false;
			set = map_srv.entrySet();
		} else {
			set = temp_map.entrySet();
		}
		Iterator itr = set.iterator();
		ArrayList items = new ArrayList();
		while(itr.hasNext()) {
			Map.Entry m = (Map.Entry)itr.next();
			if(m.getValue().toString().toLowerCase(Constants.OK).startsWith(txt.toLowerCase(Constants.OK))){
				//if(not in map_ <<< not folder! then
				res_map.put(m.getKey(), m.getValue());
				//box.addItem(m.getValue().toString());
				if(items.size()<=7)
				items.add(m.getValue());
			}
		}
		fromUp = true;
		this.model = new DefaultComboBoxModel(items.toArray());
		box.setModel(model);
		fromUp = false;
		this.editor = (JTextComponent) box.getEditor().getEditorComponent();
		temp_map = res_map;
		box.setPopupVisible(false);
		if(model.getSize() > 0)
		box.setPopupVisible(true);
	}
	
	public void setMap_Srv(Object ob, boolean fol){
		if(map_ == null) return;
		if(!fol) {
			HashMap mm = (HashMap)ob;
			Set keys = map_.keySet();
			Set set = mm.entrySet();
			Iterator itr = set.iterator();
			while(itr.hasNext()){
				Map.Entry m = (Map.Entry)itr.next();
				if(!keys.contains(m.getKey())) 
					map_srv.put(m.getKey(), m.getValue());
			}
			if(map_srv == null) {
				System.err.println("Error in BadDoc on setMap_Srv! map_srv==null!!!");
				return;
			}
		} else {
			map_srv = (HashMap)ob;
		}
	}
	
	/**
	 * задает карту
	 * @param mm задает его
	 */
	public void setMap_(MultiMap mm){
		map_ = mm;
	}
	
	/**
	 * Запуск процесса
	 */
	public void runSrv(){
		if(res_map != null && !res_map.isEmpty() && editor.getText().toLowerCase(Constants.OK).equals(model.getSelectedItem().toString().toLowerCase(Constants.OK))){
			String[] res_ = null;
			Set set = res_map.entrySet();
			Iterator itr = set.iterator();
			while(itr.hasNext()) {
				Map.Entry m = (Map.Entry)itr.next();
				if(m.getValue().toString().toLowerCase(Constants.OK).equals(editor.getText().toLowerCase(Constants.OK))){
					try {
						String text = TaskTable.instance(false).getResource().getString("startProcMessage");
						int result=MessagesFactory.showMessageDialog(
			                     (JFrame)TaskTable.instance(false).getTopLevelAncestor(),
			                    MessagesFactory.QUESTION_MESSAGE, text+":'"+m.getValue()+"'?", TaskTable.instance(false).li);
						if(result ==ButtonsFactory.BUTTON_YES) {
						res_ = krn.startProcess(Long.valueOf(m.getKey().toString()), null);
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (KrnException e) {
						e.printStackTrace();
					}
					 catch (ProcessException e) {
			                        e.printStackTrace();
			                }
					break;
				}
			}
		} else {
			//write something
		}
	}
}
