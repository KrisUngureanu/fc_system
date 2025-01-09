package kz.tamur.guidesigner.expr;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class DualTabWindow extends JFrame {
	
	private static final long serialVersionUID = -9151392480495538163L;
	
	public DualTabPanel DTP = new DualTabPanel();
	public boolean isWindow = true;

	public DualTabWindow() {
		setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		toWindow();
	}
	
	public Collection<TabObj> getTabs() {
		return DTP.TabMap.values();
	}
	
	public void toWindow(){
		isWindow = true;
		this.setMinimumSize(new Dimension(400, 320));
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DTP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(DTP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        
	}
	
	public void appendTab(Object id, String title, Component comp, ActionListener CloseListener){
		DTP.addTab(id, title, comp, CloseListener);
		if (isWindow) {
			this.setState(Frame.NORMAL);
			this.setVisible(true);
			this.toFront();
		}
	}
	

	
	public boolean delTabs(String id) {
		boolean res = DTP.delTabs(id);
		if (DTP.TabMap.size() == 0 && isWindow){
			setVisible(false);
		}
		return res;
	}
	
	public void delTab(Object id){
		DTP.delTab(id);
		if (DTP.TabMap.size() == 0 && isWindow){
			setVisible(false);
		}
	}
	
	
	@Override
	public void setVisible(boolean b) {
		if (!isWindow) {
			super.setVisible(b);
		} else {
			if (!b && DTP.TabMap.size() != 0 && DTP.TabMap.size() != 0) {
				int n = JOptionPane.showConfirmDialog(null, "Закрыть вкладки?",
						"Close...", JOptionPane.YES_NO_CANCEL_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					// //YES
				    for (int i = 0; i < DTP.TabMap.size(); i++){
				    	TabObj to = (TabObj)DTP.TabMap.values().toArray()[i];
				    	to.CloseListener.actionPerformed(null);
				        if (!DTP.TabMap.containsValue(to)) {
				        	i--;
				        }
				    }
					if (DTP.TabMap.size() == 0) {
						DTP.TabMap.clear();
						super.setVisible(b);
					}
				} else if (n == JOptionPane.NO_OPTION) {
					// //NO
					this.setState(Frame.ICONIFIED);
				} else {
					// //CENSEL
					if (DTP.TabMap.size() > 1) {
						return;
					}
				}
			} else {
				super.setVisible(b);
			}
		}
	}
}
