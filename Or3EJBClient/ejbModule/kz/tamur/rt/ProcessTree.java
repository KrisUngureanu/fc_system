package kz.tamur.rt;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.util.CursorToolkit;

import kz.tamur.rt.TaskTable.TaskTableModel;
import kz.tamur.rt.Utils;
import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ButtonsFactory;
//Added!!!
import kz.tamur.guidesigner.DesignerDialog;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.QuickSrvListPanel;
//Added!!!
import kz.tamur.guidesigner.QuickSrvPanel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
//Added!!!
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 18.02.2005
 * Time: 10:51:09
 * To change this template use File | Settings | File Templates.
 */
public class ProcessTree extends JTree implements MouseListener,KeyListener{
    ResourceBundle res = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    protected DefaultTreeModel model;
    JPopupMenu menu;
    public ProcessTree(final ProcessNode root,boolean isRunTime) {
        super(root);
        model = new DefaultTreeModel(root);
        setModel(model);
        setCellRenderer(new CellRenderer());
        setBackground(Utils.getLightGraySysColor());
        if(isRunTime){
            addKeyListener(this);
            addMouseListener(this);
        }
    }


    private class CellRenderer  extends JLabel implements TreeCellRenderer  {

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected,
                                                      boolean expanded,
                                                      boolean leaf,
                                                      int row, boolean hasFocus) {
            setOpaque(true);
            //if (hasFocus && selected) {
            if(hasFocus || selected) {
                setBackground(Utils.getDarkShadowSysColor());
                setForeground(Color.white);
            } else {
                    setBackground(Utils.getLightGraySysColor());
                    setForeground(Color.black);
            }
            if (!leaf) {
                if (expanded) {
                    setIcon(kz.tamur.rt.Utils.getImageIcon("Open"));
                } else {
                    setIcon(kz.tamur.rt.Utils.getImageIcon("CloseFolder"));
                }
            } else {
                setIcon(kz.tamur.rt.Utils.getImageIcon("ServiceTab"));
            }
            setFont(Utils.getDefaultFont());
            setText(value.toString());
            return this;
        }

    }
    
    /**
     * Попап при клике правой кнопкой мыши в дереве процессов
     */
    private void popupShow(MouseEvent e){
    	//make it easier & shorter!!!
		menu = new JPopupMenu();
		JMenuItem setQKeyMenuItem = new JMenuItem(res.getString("setHotKey"));
		setQKeyMenuItem.addActionListener(new ActionListener(){
		      public void actionPerformed(ActionEvent e){
		    	  Window cnt = (Window)getTopLevelAncestor();
		    	  QuickSrvPanel qpanel = new QuickSrvPanel(getLastSelectedPathComponent().toString(), getSelectionPath().toString(), ((ProcessNode)getLastSelectedPathComponent()).getKrnObject().id);
		    	  DesignerDialog dlg = new DesignerDialog(true, cnt, res.getString("setHotKey"), qpanel);
		    	  dlg.setOnlyOkButton();
		    	  dlg.setOkVisible(false);
		    	  dlg.show();
		      }
	      });
        menu.add(setQKeyMenuItem);
        /*JMenuItem testMenuItem = new JMenuItem("testMenuItem");
		testMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println("model : " + model.toString());
				System.out.println(model);
			}
		});
		menu.add(testMenuItem);*/
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

	private void createProcess() {
		try {
			String text = TaskTable.instance(false).getResource().getString("startProcMessage");
			int result = MessagesFactory.showMessageDialog((JFrame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, text + ":'" + getLastSelectedPathComponent().toString() + "'?", TaskTable.instance(false).li);
			if (result == ButtonsFactory.BUTTON_YES) {
				CursorToolkit.startWaitCursor(this);
				String[] res_ = Kernel.instance().startProcess(((ProcessNode) getLastSelectedPathComponent()).getKrnObject().id, null);
				if (res_.length > 0 && !res_[0].equals("")) {
					CursorToolkit.stopWaitCursor(this);
					String msg = res_[0];
					String flowIdText = "ID потока: ";
					int index = msg.indexOf(flowIdText);
					if (index > 0) {
							String flowId = msg.substring(index + flowIdText.length(), msg.length() - 1);
							msg += " Открыть раннее запущенный процесс?";
							result = MessagesFactory.showMessageDialog((JFrame) getTopLevelAncestor(), MessagesFactory.QUESTION_MESSAGE, msg);
							if (result == ButtonsFactory.BUTTON_YES) {
								try {
									System.out.println("Запуск процесса!");
									TaskTable taskTable = TaskTable.instance(true);
									TaskTableModel model = (TaskTableModel) taskTable.getTable().getModel();
									Activity activity = model.getActivity(Long.parseLong(flowId));
									taskTable.openUI(activity);
								} catch (Exception e) {
									msg = "Ошибка открытия интерфейса!";
									MessagesFactory.showMessageDialog((JFrame) getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, msg);
									e.printStackTrace();
								}
							}
					} else {
						MessagesFactory.showMessageDialog((JFrame) getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE, msg);
					}
				} else {
					List<String> param = new ArrayList<String>();
					// Если монитор событий скрыт - отобразить интерфейс
					if (!Application.instance().isMonitorTask()) {
						param.add("autoIfc");
					}
					if (res_.length > 3) {
						param.add(res_[3]);
					}
					TaskTable.instance(false).startProcess(res_[1], param);
				}
				CursorToolkit.stopWaitCursor(this);
				QuickSrvListPanel qpanel = new QuickSrvListPanel(((ProcessNode) getLastSelectedPathComponent()).toString(), getSelectionPath().toString(), String.valueOf(((ProcessNode) getLastSelectedPathComponent()).getKrnObject().id));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
    public void mouseClicked(MouseEvent e) {
        ProcessNode node= (ProcessNode)getLastSelectedPathComponent();
        if (node != null && node.isLeaf() && e.getClickCount() == 2 &&
            getSelectionPath() ==getPathForLocation(e.getX(), e.getY()) ){
                createProcess();
        }
        //right click!!!
        if(e.getModifiers() == 4 && node != null && node.isLeaf() && getSelectionPath() == getPathForLocation(e.getX(), e.getY())){
        	popupShow(e);
        }
    }

    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) {}

    public void keyPressed(KeyEvent e) {
        ProcessNode node= (ProcessNode)getLastSelectedPathComponent();
        if (node!=null && node.isLeaf() &&
            (e.getKeyCode() == KeyEvent.VK_INSERT ||e.getKeyCode() == KeyEvent.VK_ENTER))
            createProcess();
    }

    public void keyReleased(KeyEvent e) { }
    public void keyTyped(KeyEvent e) { }
    public ProcessNode getSelectedProcess(){
        ProcessNode node= (ProcessNode)getLastSelectedPathComponent();
        if (node != null && node.isLeaf()){
            return node;
        }else return null;
    }
}
