package kz.tamur.admin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;

import kz.tamur.rt.Utils;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;

public class IndexPropPanel extends JPanel{
	private DesignerDialog dlg = null;
	private DefaultListModel listModel;
	private JList selectedKeysList;
	private JButton addKeyBtn = ButtonsFactory.createToolButton("plus",
            "Добавить ключ", true);
    private JButton delKeyBtn = ButtonsFactory.createToolButton("minus",
            "Удалить ключ", true);
    private JButton upBtn = ButtonsFactory.createToolButton("ArrowUpSmall", 
    		"Переместить выделенный элемент вверх", false);
    private JButton downBtn = ButtonsFactory.createToolButton("ArrowDownSmall", 
    		"Переместить выделенный элемент вверх", false);
    private KrnClass currentClass;    
    IndexKeyListNode noDataNode = new IndexKeyListNode(
    		"<html><br><font color=#36393d>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>" +
    		"Выберите атрибуты для индексирования" +
    		"</i></font></html>");
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
	public IndexPropPanel(KrnClass krnClass){
		this.currentClass = krnClass;
		initPanel();
	}
	private void initPanel(){	
	        setOpaque(isOpaque);
		setLayout(new GridBagLayout());
		listModel = new DefaultListModel();
		selectedKeysList = Utils.createListBox(listModel);
		selectedKeysList.setCellRenderer(new CellRenderList());		
		setAllSize(addKeyBtn, new Dimension(25, 25));
		setAllSize(delKeyBtn, new Dimension(25, 25));
		delKeyBtn.setEnabled(false);
			
		
		showNoData(true);//пока не данных для отображения
		add(new JScrollPane(selectedKeysList), new GridBagConstraints(0, 0, 1, 4, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_0, 0, 0));
		add(addKeyBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(30, 0, 0, 0), 0, 0));
		add(delKeyBtn, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 0), 0, 0));
		add(upBtn, new GridBagConstraints(1, 2, 1, 1, 0, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(60, 0, 0, 0), 0, 0));
		add(downBtn, new GridBagConstraints(1, 3, 1, 1, 0, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 0), 0, 0));
		addKeyBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showIndexKeys();
			}
		});
		delKeyBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				deleteIndexKey();
			}
		});
		upBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				moveUp();
			}
		});
		downBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				moveDown();
			}
		});
		selectedKeysList.addListSelectionListener(new ListSelectionListener() {			
			public void valueChanged(ListSelectionEvent e) {				
				delKeyBtn.setEnabled(selectedKeysList.getSelectedIndex() != -1);
			}
		});
	}	
	
	private void setAllSize(JComponent comp, Dimension size) {
        comp.setPreferredSize(size);
        comp.setMaximumSize(size);
        comp.setMinimumSize(size);
    }
	
	//Показать окно выбора нового ключа
	private void showIndexKeys(){    	
    	Container cont = getTopLevelAncestor();
    	Set<Long> usedAttrs = new HashSet<Long>();
    	if(selectedKeysList.isEnabled()){
	    	for(int i=0;i<listModel.size();i++){	    		
	    		usedAttrs.add(((IndexKeyListNode)listModel.get(i)).getAttribute().id);
	    	}
    	}
    	IndexKeyPropPanel ik = new IndexKeyPropPanel(this.currentClass,usedAttrs);    	
    	ik.setPreferredSize(new Dimension(450, 300));
    	
    	String title = "Атрибуты для индексирования";
    	if (cont instanceof Dialog) {
    		dlg = new DesignerDialog((Dialog)cont, title, ik);
        } else {
        	dlg = new DesignerDialog((Frame)cont, title, ik);
        }
    	boolean exit = false;
    	do{
    		dlg.show();
    		if(dlg.getResult() == ButtonsFactory.BUTTON_OK){
    			int ndx = ik.getKeyList().getSelectedIndex();
    			if(ndx == -1){
    				MessagesFactory.showMessageDialog(dlg, MessagesFactory.INFORMATION_MESSAGE, "Выберите элемент списка");
    			}else{
    				if(ik.getKeyList().getSelectedValues().length != 1){
    					MessagesFactory.showMessageDialog(dlg, MessagesFactory.INFORMATION_MESSAGE, "Выбрать можно только один элемент из списка");
    				}else{    					
    					KrnAttribute krnAttr = ((IndexKeyAttrListNode)ik.getListModel().get(ndx)).getAttribute();    					
    					boolean isDesc = ik.isDescending();
    					IndexKeyListNode node = new IndexKeyListNode(krnAttr,isDesc);    					
    					listModel.addElement(node);    					
    					showNoData(false);
    					exit = true;
    				}
    			}    		
    		}else{
    			exit = true;
    		}
    	}while(!exit);
    }
	
	//Удаление ключа
	private void deleteIndexKey(){
		int ndx = selectedKeysList.getSelectedIndex();
		if(ndx == -1){
			MessagesFactory.showMessageDialog(dlg, MessagesFactory.INFORMATION_MESSAGE, "Выберите удаляемый элемент");
		}else{
			listModel.remove(ndx);
			showNoData(listModel.getSize() == 0);
		}
	}
	
	//Показать сообщение о пустоте списка с ключами
	private void showNoData(boolean value){		
		if(value){			
			listModel.addElement(noDataNode);
		}else{
			listModel.removeElement(noDataNode);
		}
		selectedKeysList.setEnabled(!value);
		upBtn.setVisible(listModel.size() > 1 && !value);
		downBtn.setVisible(listModel.size() > 1 && !value);
	}
	
	public JList getSelectedKeysList(){
		return this.selectedKeysList;
	}
	
	public DefaultListModel getListModel(){
		return this.listModel;
	}
	
	private void swapNodes(int index1, int index2){
		Object swap = listModel.get(index1);
		listModel.set(index1, listModel.get(index2));
		listModel.set(index2, swap);
	}

	//Перемещение ключа индекса вверх
	private void moveUp(){
		if(selectedKeysList.isEnabled()){
			int size = listModel.size();
			if(size > 1){
				int sel = selectedKeysList.getSelectedIndex(); 
				if(sel != -1){
					int dest = (sel!=0 ) ? (sel-1) : (size - 1);
					swapNodes(dest, sel);
					selectedKeysList.setSelectedIndex(dest);					
				}else{
					MessagesFactory.showMessageDialog(dlg, 
							MessagesFactory.INFORMATION_MESSAGE, "Выберите перемещаемый элемент");
				}
			}
		}
	}
	//Перемещение ключа индекса вниз
	private void moveDown(){
		if(selectedKeysList.isEnabled()){
			int size = listModel.size();
			if(size > 1){
				int sel = selectedKeysList.getSelectedIndex();
				if(sel != -1){
					int dest = (sel!=size-1) ? (sel+1) : 0;
					swapNodes(dest, sel);
					selectedKeysList.setSelectedIndex(dest);
				}else{
					MessagesFactory.showMessageDialog(dlg, 
							MessagesFactory.INFORMATION_MESSAGE, "Выберите перемещаемый элемент");
				}
			}
		}
	}
	
	private static class CellRenderList extends JLabel implements ListCellRenderer{		
		public Component getListCellRendererComponent(JList list,Object obj,int index,boolean selected,boolean b){
			IndexKeyListNode node = (IndexKeyListNode)obj;
			String text = "" + 
				"<html>" + node.getText() + 
				" <font color=gray>" + node.getSubText() + "</font>" + 
				node.getDescText() +				
				"</html>";
			JLabel lbl = Utils.createLabel(text);
			lbl.setIcon(node.getIcon());
			lbl.setOpaque(true);
			lbl.setFont(Utils.getDefaultComponentFont());
			if(selected){//выделенный элемент
				lbl.setForeground(Color.WHITE);
				lbl.setBackground(Utils.getDarkShadowSysColor());
			}else{//не выделенный элемент
				lbl.setForeground(Color.BLACK);
				lbl.setBackground(Utils.getSilverColor());
			}
			return lbl;
		}
	}
}

