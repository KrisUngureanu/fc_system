package kz.tamur.guidesigner.expr;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;

import kz.tamur.comps.Constants;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.ExpressionEditor;

public class DualTabPanel extends JPanel {

	private static final long serialVersionUID = 3370696693926091116L;

	public Map<Object, TabObj> TabMap = new HashMap<Object, TabObj>();

	private JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);

	JTabbedPane TabPanel1 = new JTabbedPane();
	JTabbedPane TabPanel2 = new JTabbedPane();

	// TODO: <EDIT>
	protected Dimension CloseButtonDimension = new Dimension(8, 8);

	Icon CloseIcon = kz.tamur.rt.Utils.getImageIconFull("DeleteValue.png");
	//Icon TabIcon = kz.tamur.comps.Utils.getImageIconExt("DeleteValue", ".png");
	private boolean isOpaque = !MainFrame.TRANSPARENT_DIALOG;

	// </EDIT>

	public DualTabPanel() {
		splitPanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPanel.setContinuousLayout(true);
		splitPanel.setLeftComponent(TabPanel1);
		splitPanel.setRightComponent(TabPanel2);

		splitPanel.setOpaque(isOpaque);
		TabPanel1.setOpaque(isOpaque);
		TabPanel2.setOpaque(isOpaque);
		
		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				this);
		this.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				splitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 0,
				Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				splitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 0,
				Short.MAX_VALUE));
		
		ChangeListener SelectTabListener = new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
            	visibleTabClose();
            }
        };
		
		TabPanel1.addChangeListener(SelectTabListener);
		TabPanel2.addChangeListener(SelectTabListener);
		
		TabPanel1.setTabLayoutPolicy(javax.swing.JTabbedPane.WRAP_TAB_LAYOUT);
		TabPanel1.setAutoscrolls(false);
		TabPanel2.setTabLayoutPolicy(javax.swing.JTabbedPane.WRAP_TAB_LAYOUT);
		TabPanel2.setAutoscrolls(false);
		
//		TabPanel1.setPreferredSize(new Dimension(300, 300));
//		TabPanel1.setMinimumSize(new Dimension(300, 300));
//		TabPanel1.setMaximumSize(new Dimension(9999, 9999));
//		TabPanel2.setPreferredSize(new Dimension(300, 300));
//		TabPanel2.setMinimumSize(new Dimension(300, 300));
//		TabPanel2.setMaximumSize(new Dimension(9999, 9999));
		
		resize(false);
	}
	
	private void visibleTabClose(){
		for (TabObj t : TabMap.values()){
			if (TabPanel1.getSelectedComponent() == t.comp || TabPanel2.getSelectedComponent() == t.comp){
				t.btnClose.setVisible(true);
			} else {
				t.btnClose.setVisible(false);
			}
		}
	}
	
	public void TabMove(JTabbedPane T1, JTabbedPane T2){
		int index = T1.getSelectedIndex();
		Component c = T1.getComponentAt(index);
		Component tc = T1.getTabComponentAt(index);
		
		T2.addTab(null, null, c);
		int selfIndexTab = T2.getTabCount() - 1;
		T2.setTabComponentAt(selfIndexTab, tc);
		T2.setSelectedComponent(c);
		
		if (T2.getTabCount() == 1) {
			resize(true);
		} else {
			resize(false);
		}
	}
	
	private void OnVisibleDual(JTabbedPane T1){
		T1.setPreferredSize(new Dimension(300, 300));
		T1.setMinimumSize(new Dimension(300, 300));
		T1.setMaximumSize(new Dimension(9999, 9999));
		//splitPanel.setEnabled(true);
		splitPanel.setDividerLocation(0.5);
		splitPanel.setResizeWeight(0.5);
		splitPanel.setDividerSize(2);
	}
	
	private void OffVisibleDual(JTabbedPane T1, double proportionalLocation){
		T1.setPreferredSize(new Dimension(0, 0));
		T1.setMinimumSize(new Dimension(0, 0));
		T1.setMaximumSize(new Dimension(0, 0));
		splitPanel.setDividerLocation(proportionalLocation);
		splitPanel.setResizeWeight(proportionalLocation);
		splitPanel.setDividerSize(0);
		//splitPanel.setEnabled(false);
	}
	
	public void resize(boolean isadd){
		if (TabPanel1.getTabCount() == 1 && TabPanel2.getTabCount() == 1 && isadd){
			OnVisibleDual(TabPanel1);
			OnVisibleDual(TabPanel2);
		} else if (TabPanel1.getTabCount() == 1 && isadd){
			OnVisibleDual(TabPanel1);
		} else if (TabPanel2.getTabCount() == 1 && isadd){
			OnVisibleDual(TabPanel2);
		}
		if (TabPanel2.getTabCount() == 0) {
			OffVisibleDual(TabPanel2, 1D);
		}
		if (TabPanel1.getTabCount() == 0) {
			OffVisibleDual(TabPanel1, 0D);
		}
	}
	
	private void pushTab(JTabbedPane T1, Component comp, TabObj tab){
		T1.addTab(null, null, comp);
		int selfIndexTab = T1.getTabCount() - 1;
		T1.setTabComponentAt(selfIndexTab, tab.getTitle());
		T1.setSelectedComponent(comp);
	}

	public void addTab(final Object id, final String title, final Component comp, final ActionListener CloseListener) {
		if (TabMap.containsKey(id)) {
			try {
				TabPanel1.setSelectedComponent(TabMap.get(id).comp);
			} catch (Exception e) {
			}
			try {
				TabPanel2.setSelectedComponent(TabMap.get(id).comp);
			} catch (Exception e) {
			}
		} else {
			JButton btnClose = new JButton();
			
			btnClose.setIcon(CloseIcon);
			btnClose.setMargin(Constants.INSETS_2);
			btnClose.setOpaque(false);
			btnClose.setMaximumSize(CloseButtonDimension);
			btnClose.setPreferredSize(CloseButtonDimension);
			
			final TabObj tab = new TabObj(btnClose, title, comp, CloseListener);

			final JPopupMenu RTabPopup = new JPopupMenu();
			JMenuItem RToNewTab = new JMenuItem("переместить в право >>");
			final JPopupMenu LTabPopup = new JPopupMenu();
			JMenuItem LToNewTab = new JMenuItem("переместить в лево <<");
			RTabPopup.add(RToNewTab);
			LTabPopup.add(LToNewTab);
			
			RToNewTab.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TabMove(TabPanel1, TabPanel2);
				}
			});
			
			LToNewTab.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TabMove(TabPanel2, TabPanel1);
				}
			});
			
			TabPanel1.setComponentPopupMenu(RTabPopup);
			TabPanel2.setComponentPopupMenu(LTabPopup);

			if (TabPanel1.getTabCount() == 0){
				if (TabPanel2.getTabCount() == 0){
					pushTab(TabPanel1, comp, tab);
				} else {
					pushTab(TabPanel2, comp, tab);
				}
			} else {
				pushTab(TabPanel1, comp, tab);
			}
			
			resize(true);
			
			if(comp instanceof ExpressionEditor)
				((ExpressionEditor) comp).editor.requestFocus();

			TabMap.put(id, tab);
		}
	}
	
	public void selectTab(Component comp){
		try {
			TabPanel1.setSelectedComponent(comp);
		} catch (Exception e) {
		}
		try {
			TabPanel2.setSelectedComponent(comp);
		} catch (Exception e) {
		}
	}
	
	public void removeTab(Object id){
		try {
			TabPanel1.remove(TabMap.get(id).comp);
		} catch (Exception e) {
		}
		try {
			TabPanel2.remove(TabMap.get(id).comp);
		} catch (Exception e) {
		}
	}

	public boolean delTabs(String id) {
		Set<Object> keys = TabMap.keySet();
		List<String> keyList = new ArrayList<String>();
		for(Object key: keys) {
			if(key instanceof String) {
				keyList.add(key.toString());
			}
		}
		for(String nextId: keyList) {
			if(nextId.startsWith(id)){
				TabObj obj = TabMap.get(nextId); 
				if(obj.comp instanceof ExpressionEditor) {
					ExpressionEditor ex = (ExpressionEditor) obj.comp;
						if (ex.onChanged && !ex.isReadOnly() && !ex.text.equals(ex.getExpression())) {
							try {
								TabPanel1.setSelectedComponent(ex);
							} catch (Exception e) {
							}
							try {
								TabPanel2.setSelectedComponent(ex);
							} catch (Exception e) {
							}
							int n = JOptionPane.showConfirmDialog(null,
									"Сохранить изменения в документе: \n\"" + obj.Title
									+ "\"?", "Сохранить изменения?",
									JOptionPane.YES_NO_CANCEL_OPTION);
							if (n == JOptionPane.YES_OPTION) {
								// //YES
								if(ex.saveBut())
									return true;
								removeTab(nextId);
								TabMap.remove(nextId);
							} else if (n == JOptionPane.NO_OPTION) {
								// //NO
								removeTab(nextId);
								TabMap.remove(nextId);
							}
							  else { return true; }
							 
						} else {
							removeTab(nextId);
							TabMap.remove(nextId);
						}
				}

			}
		}		
		
		return false;
	}		
	
	public boolean delTab(Object id){
		if (TabMap.containsKey(id)){
			removeTab(id);
			TabMap.remove(id);
			resize(false);
			return true;
		}
		return false;
	}
}
