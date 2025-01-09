package kz.tamur.guidesigner.expr;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;

import kz.tamur.util.ExpressionEditor;

public class EditorWindow {
	public static DualTabWindow TabWnd = new DualTabWindow();
	public static Object Parked = null;
	public static ActionListener ParkedListener = null;
	public static boolean isMoved = false;
	static {
		TabWnd.setTitle("Editor");

		ImageIcon TabIcon = kz.tamur.rt.Utils.getImageIconFull("FnFunc.gif");
		TabWnd.setIconImage(TabIcon.getImage());
	}
	
	public static void addTab(Object id, String title, final ExpressionEditor ex) {
		if (id == null) {
			SecureRandom rnd = new SecureRandom();
			do {
				id = new Integer(rnd.nextInt());
			} while (TabWnd.DTP.TabMap.containsKey(id));

		}
		final Object fID = id;
		
		int iTitle = 0;
		if (title == null) {
			title = "New expression";
			Iterator<Entry<Object, TabObj>> iterator;
			for (iterator = TabWnd.DTP.TabMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<Object, TabObj> entry = iterator.next();
				if (entry.getValue().Title.equals(((iTitle == 0) ? title : title + " (" + iTitle + ")"))){
					iterator = TabWnd.DTP.TabMap.entrySet().iterator();
					iTitle++;
				}
			}
		}
		final String tabTitleString = (iTitle == 0) ? title : title + " (" + iTitle + ")";
		
//		if (Parked != parked) {
//			moveToComponent(null, parked, null);
//		}

		ActionListener btnaction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ex.onChanged && !ex.isReadOnly() && !ex.text.equals(ex.getExpression())) {
					int n = JOptionPane.showConfirmDialog(null,
							"Сохранить изменения в документе: \n\"" + tabTitleString
									+ "\"?", "Сохранить изменения?",
							JOptionPane.YES_NO_CANCEL_OPTION);
					if (n == JOptionPane.YES_OPTION) {	
						// //YES					
						ex.saveBut();
						TabWnd.delTab(fID);
					} else if (n == JOptionPane.NO_OPTION) {
						// //NO
						TabWnd.delTab(fID);
					}/*
					 * else { ////CENSEL }
					 */
				} else {
					TabWnd.delTab(fID);
				}				
			}
		};

		TabWnd.appendTab(fID, tabTitleString, ex, btnaction);
	}

	public static void addTab(Object id, String title, final ExpressionEditor ex, final ActionListener CloseListener, Object parked) {
		if (id == null) {
			SecureRandom rnd = new SecureRandom();
			do {
				id = new Integer(rnd.nextInt());
			} while (TabWnd.DTP.TabMap.containsKey(id));

		}
		final Object fID = id;
		
		int iTitle = 0;
		if (title == null) {
			title = "New expression";
			Iterator<Entry<Object, TabObj>> iterator;
			for (iterator = TabWnd.DTP.TabMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<Object, TabObj> entry = iterator.next();
				if (entry.getValue().Title.equals(((iTitle == 0) ? title : title + " (" + iTitle + ")"))){
					iterator = TabWnd.DTP.TabMap.entrySet().iterator();
					iTitle++;
				}
			}
		}
		final String tabTitleString = (iTitle == 0) ? title : title + " (" + iTitle + ")";
		
		if (Parked != parked) {
			moveToComponent(null, parked, null);
		}

		ActionListener btnaction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ex.onChanged && !ex.isReadOnly() && !ex.text.equals(ex.getExpression())) {
					int n = JOptionPane.showConfirmDialog(null,
							"Сохранить изменения в документе: \n\"" + tabTitleString
									+ "\"?", "Сохранить изменения?",
							JOptionPane.YES_NO_CANCEL_OPTION);
					if (n == JOptionPane.YES_OPTION) {
						// //YES
						CloseListener.actionPerformed(null);
						EditorWindow.TabWnd.delTab(fID);
					} else if (n == JOptionPane.NO_OPTION) {
						// //NO
						TabWnd.delTab(fID);
					}/*
					 * else { ////CENSEL }
					 */
				} else {
					TabWnd.delTab(fID);
				}
				if(!ex.isReadOnly() && ex.isMethodExpr()){
					try {
						Kernel.instance().unlockMethod((String)fID);
					} catch (KrnException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		};

		TabWnd.appendTab(fID, tabTitleString, ex, btnaction);
	}
	
	public static boolean delTabs(String id) {
		return TabWnd.delTabs(id);
	}

	public static void moveToComponent(JPanel panel, Object parked, ActionListener _ParkedListener) {
		Parked = parked;
		if (panel != null) {
			isMoved = true;
			ParkedListener = _ParkedListener;
			javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(panel);
			panel.setLayout(jPanel2Layout);
			jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
					javax.swing.GroupLayout.Alignment.LEADING).addComponent(
					TabWnd.DTP, javax.swing.GroupLayout.DEFAULT_SIZE, 512,
					Short.MAX_VALUE));
			jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
					javax.swing.GroupLayout.Alignment.LEADING).addComponent(
					TabWnd.DTP, javax.swing.GroupLayout.DEFAULT_SIZE, 273,
					Short.MAX_VALUE));

			TabWnd.isWindow = false;
			TabWnd.setVisible(false);

			panel.repaint();
			TabWnd.repaint();
		} else {
			if (isMoved) {
				if (ParkedListener != null) {
					ParkedListener.actionPerformed(null);
				}
				TabWnd.toWindow();
				if (TabWnd.DTP.TabMap.size() != 0) {
					TabWnd.setState(Frame.NORMAL);
					TabWnd.setVisible(true);
					TabWnd.toFront();
				}
				TabWnd.repaint();
				isMoved = false;
			}
		}
	}
}