package kz.tamur.or3.client.util;
import static java.awt.event.KeyEvent.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListModel;


public class ClientUtils {
	//Чтение данных из буфера обмена (naik)
	public static String getClipboard(){
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try{
			if(t!=null && t.isDataFlavorSupported(DataFlavor.stringFlavor)){
				return (String)t.getTransferData(DataFlavor.stringFlavor);
			}
		}catch(IOException e){
			//NOP
		}catch(UnsupportedFlavorException e){
			//NOP
		}
		return null;
	}
	//Запись данных в буфер обмена (naik)
	public static void setClipboard(String str){
		StringSelection ss = new StringSelection(str);
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}
	
	//Получить максимально доступную область экрана (naik)
	public static Rectangle getMaxScreenArea(){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		Insets ins = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		Rectangle maxRect = gc.getBounds();
		maxRect.setBounds(maxRect.x - ins.left, maxRect.y - ins.top, maxRect.width - ins.right, maxRect.height - ins.bottom);				
		return maxRect;
	}
	
	//Вычислить новую позицию для навигации (naik)
	private static int navigationComputeIndex(int keyCode,int size,int ndx,int step){
		if(size > 0){
			switch(keyCode){
				case VK_DOWN: return (ndx + 1) % size;
				case VK_UP: return (ndx == -1) ? 0 : (ndx - 1 + size) % size;
				case VK_PAGE_DOWN: return (ndx += step) < size ? ndx : size - 1;
				case VK_PAGE_UP: return (ndx -= step) >= 0 ? ndx : 0;
				case VK_END: return size - 1;
				case VK_HOME: return 0;
			}
		}
		return -1;
	}
	
	//Проверка: навигационная клавиша (naik)
	private static boolean isNavigationKey(int keyCode){
		switch(keyCode){
			case VK_DOWN:
			case VK_UP:
			case VK_PAGE_DOWN:
			case VK_PAGE_UP:
			case VK_HOME:
			case VK_END: return true;
		}
		return false;
	}
	
	//Навигация по JList с помощью клавиш: Down,Up,PageDown,PageUp,Home,End (naik)
	//step - величина прокрутки при PageDown и PageUp
	public static boolean navigationList(JList list,ListModel listModel,int keyCode,int step){
		int size = listModel.getSize();		
		int ndx = navigationComputeIndex(keyCode, size, list.getSelectedIndex(), step);
		if(ndx != -1){
			list.setSelectedIndex(ndx);
			list.ensureIndexIsVisible(ndx);
		}		
		return isNavigationKey(keyCode);
	}
	
	//Навигация по JTable с помощью клавиш: Down,Up,PageDown,PageUp,Home,End (naik)
	//step - величина прокрутки при PageDown и PageUp
	public static boolean navigationTable(JTable table,int keyCode,int step){
		int size = table.getRowCount();
    	int ndx = navigationComputeIndex(keyCode, size, table.getSelectedRow(), step);
    	if(ndx != -1){
    		table.setRowSelectionInterval(ndx, ndx);
    		table.scrollRectToVisible(table.getCellRect(ndx, 0, true));
    	}
    	return isNavigationKey(keyCode);
	}
	
}
