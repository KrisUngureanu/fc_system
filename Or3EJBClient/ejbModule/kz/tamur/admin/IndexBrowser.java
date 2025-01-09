package kz.tamur.admin;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnIndex;
import com.cifs.or2.kernel.KrnIndexKey;

import kz.tamur.rt.Utils;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.rt.MainFrame;

public class IndexBrowser extends JPanel{
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode treeRoot,chosenNode;
	private IndexTree ndxTree;
	private JButton addIndexBtn = ButtonsFactory.createToolButton("index-add",
            "Добавить индекс", true);
    private JButton delIndexBtn = ButtonsFactory.createToolButton("index-del",
            "Удалить индекс", true);
    private KrnClass currentClass;
    private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;
    
	public IndexBrowser(KrnClass krnClass){
		this.currentClass = krnClass;
		initPanel();
	}
	private void initPanel(){
		JLabel label = Utils.createLabel(
				"<html><font color=#36393d>&nbsp;&nbsp;<i>" +
				"Список многоатрибутных индексов пуст" +
				"</i></font></html>");
		label.setFont(Utils.getDefaultComponentFont());
		treeRoot = new DefaultMutableTreeNode(label);
		ndxTree = new IndexTree(treeRoot);		
		treeModel = (DefaultTreeModel)ndxTree.getModel();
		setLayout(new GridBagLayout());
		addIndexBtn.setText("Создать");		
		delIndexBtn.setText("Удалить");
		delIndexBtn.setEnabled(false);
		setAllSize(addIndexBtn, new Dimension(100, 25));
		setAllSize(delIndexBtn, new Dimension(100, 25));
		
		add(new JScrollPane(ndxTree), new GridBagConstraints(0, 0, 1, 2, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                Constants.INSETS_0, 0, 0));
		add(addIndexBtn, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(30, 0, 0, 0), 0, 0));
		add(delIndexBtn, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 0, 0), 0, 0));		
		addIndexBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//Показать окно создания нового индекса
				createNewIndex();
			}
		});
		delIndexBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//Удаление индекса
				deleteIndex();				
			}
		});
		ndxTree.addTreeSelectionListener(new TreeSelectionListener(){
			public void valueChanged(TreeSelectionEvent e){
				chosenNode = (DefaultMutableTreeNode)ndxTree.getLastSelectedPathComponent();
				if(chosenNode != null)
					delIndexBtn.setEnabled(chosenNode.getLevel() == 1);
			}
		});		
		showIndexes();
		ndxTree.setOpaque(isOpaque);
		setOpaque(isOpaque);
	}
	//Показать окно создания нового индекса
	private void createNewIndex(){
    	DesignerDialog dlg = null;
    	Container cont = getTopLevelAncestor();
    	IndexPropPanel ip = new IndexPropPanel(this.currentClass);
    	JScrollPane sp = new JScrollPane(ip);
    	sp.setPreferredSize(new Dimension(500, 350));
    	String title = "Создание нового индекса *";
    	if (cont instanceof Dialog) {
            dlg = new DesignerDialog((Dialog)cont, title, sp);
        } else {
            dlg = new DesignerDialog((Frame)cont, title, sp);
        }
    	try{
	    	KrnAttribute[] attrs4ndx = Kernel.instance().getAttributesForIndexing(currentClass);
	    	if(attrs4ndx == null || attrs4ndx.length == 0){
	    		MessagesFactory.showMessageDialog(dlg, 
	    				MessagesFactory.INFORMATION_MESSAGE, 
	    				"У данного класса нет атрибутов для индексирования");
	    		return;
	    	}
    	}catch(KrnException e){
    		e.printStackTrace();
    	}
    	boolean exit = false;
    	do{
	    	dlg.show();
	    	if(dlg.getResult() == ButtonsFactory.BUTTON_OK){
	    		int keyCnt = ip.getListModel().getSize();
	    		if(keyCnt == 0){
	    			MessagesFactory.showMessageDialog(dlg, MessagesFactory.INFORMATION_MESSAGE, "Укажите составные части многоатрибутного индекса");
	    		}else if(keyCnt == 1){
	    			MessagesFactory.showMessageDialog(dlg, MessagesFactory.INFORMATION_MESSAGE, "В составе многоатрибутного индекса должно быть минимум два атрибута");
	    		}else{
	    			//создание индекса
	    			try{
	    				KrnAttribute[] attrs = new KrnAttribute[keyCnt];
	    				boolean[] descs = new boolean[keyCnt];
	    				for(int i=0;i<keyCnt;i++){
	    					attrs[i] = ((IndexKeyListNode)ip.getListModel().get(i)).getAttribute();
	    					descs[i] = ((IndexKeyListNode)ip.getListModel().get(i)).isDesc();
	    				}
	    				Kernel.instance().createIndex(currentClass, attrs,descs);
	    				//обновить дерево индексов
    					showIndexes();
	    			}catch(KrnException e){
	    				e.printStackTrace();
	    			}
	    			exit = true;
	    		}	    		
	    	}else{
	    		exit = true;
	    	}
    	}while(!exit);
    }
	
	private void setAllSize(JComponent comp, Dimension size) {
        comp.setPreferredSize(size);
        comp.setMaximumSize(size);
        comp.setMinimumSize(size);
    }
	
	private void showIndexes(){		
		IndexNode ndxNode;
		DefaultMutableTreeNode indexNode,keyNode;
		Kernel krn = Kernel.instance();		
		cleanTree();
		try{
			KrnIndex[] krnIndexes = krn.getIndexesByClassId(currentClass);
			//показать корень дерева, который является сообщением о том, что индексов нет
			ndxTree.setRootVisible(krnIndexes.length == 0);
			//заблокировать пустое дерево
			ndxTree.setEnabled(krnIndexes.length > 0);
			//Перебираем индексы
			for(int i=0;i<krnIndexes.length;i++){
				KrnIndex krnIndex = krnIndexes[i];
				ndxNode = new IndexNode("<html><b><i>Индекс</i> <font color=gray>[" + (i + 1) + "]</font></b></html>");
				ndxNode.setIcon(kz.tamur.rt.Utils.getImageIcon("index"));
				ndxNode.setKrnIndex(krnIndex);
				indexNode = new DefaultMutableTreeNode(ndxNode);
				treeRoot.add(indexNode);				
				//Перебираем ключи индекса
				KrnIndexKey[] krnIndexKeys = krn.getIndexKeysByIndexId(krnIndex);
				for(int j=0;j<krnIndexKeys.length;j++){
					KrnIndexKey ndxKey = krnIndexKeys[j];					
					KrnAttribute krnAttr = krn.getAttributeById(ndxKey.getAttributeId());
					
					String typeName = "";
					try{
						typeName = Kernel.instance().getClass(krnAttr.typeClassId).name;
					}catch(KrnException e){
						e.printStackTrace();
					}
					
					ndxNode = new IndexNode("<html>" + krnAttr.name + 
							" <font color=gray>" + typeName + "</font>" +
							(ndxKey.isDesc() ? " DESC" : "") +
							"</html>");
					ndxNode.setIcon(AttributeTreeIconLoader.getIcon(krnAttr));					
					keyNode = new DefaultMutableTreeNode(ndxNode);	
					indexNode.add(keyNode);					
				}				
			}			
			ndxTree.expandPath(new TreePath(treeRoot.getPath()));
			ndxTree.expandAll();			
		}catch(KrnException e){
			e.printStackTrace();
		}		
	}	
	//очистить дерево от индексов
	private void cleanTree(){
		treeRoot.removeAllChildren();
		treeModel.reload();
	}
	//Удаление индекса
	private void deleteIndex(){
		chosenNode = (DefaultMutableTreeNode)ndxTree.getLastSelectedPathComponent();
		
		KrnIndex krnIndex = ((IndexNode)chosenNode.getUserObject()).getKrnIndex();
		if(krnIndex != null){
			try{
				Kernel.instance().deleteIndex(krnIndex);
			}catch(KrnException e){
				e.printStackTrace();
			}
		}
		delIndexBtn.setEnabled(false);
		showIndexes();
	}
}

class IndexNode extends JLabel{
	KrnIndex ndx;
	IndexNode(String text){
		super(text);
		super.setFont(Utils.getDefaultComponentFont());
		super.setForeground(Color.RED);		
	}
	void setKrnIndex(KrnIndex ndx){
		this.ndx = ndx;
	}
	KrnIndex getKrnIndex(){
		return this.ndx;
	}
}