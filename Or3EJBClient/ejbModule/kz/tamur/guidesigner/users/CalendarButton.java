package kz.tamur.guidesigner.users;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.cifs.or2.client.util.CnrBuilder;
import com.toedter.calendar.JCalendar;

import kz.tamur.rt.Utils;

public class CalendarButton extends JButton {
	private ActionListener copyAdapter;

	private DateTimeField dateField;

	public CalendarButton(String title) {
		super(Utils.getImageIcon("JCalendar"));
		setPreferredSize(new Dimension(20, 20));
		setMargin(new Insets(0, 0, 0, 0));
		setCursor(Cursor.getDefaultCursor());
		setToolTipText(title);
	}

	public void setDataField(DateTimeField dataField) {
		this.dateField=dataField;
		this.copyAdapter = new CalendarAdapter();
		addActionListener(copyAdapter);
	}

	public void setCopyTitle(String title) {
		setToolTipText(title);
	}
	private class CalendarAdapter implements ActionListener, PropertyChangeListener {

		JPopupMenu popup;
		boolean dateSelected = false;
		JCalendar c;
		JLabel hLbl = new JLabel("Hours: ");
		JLabel mLbl = new JLabel("Minutes: ");
		TimeSpinner hSpn = new TimeSpinner(24);
		TimeSpinner mSpn = new TimeSpinner(60);
		JButton setBtn = new JButton("  Set  ");
		private boolean initialized = false;

		public CalendarAdapter() {
			c = new JCalendar(false);
			c.getDayChooser().addPropertyChangeListener(this);
			c.getDayChooser().setAlwaysFireDayProperty(true);

			popup = new JPopupMenu() {
				public void setVisible(boolean b) {
					Boolean isCanceled = (Boolean) getClientProperty(
							"JPopupMenu.firePopupMenuCanceled");

					if (b || (!b && dateSelected) ||
							((isCanceled != null) && !b && isCanceled.booleanValue())) {
						super.setVisible(b);
					}
				}
			};
			JPanel calPanel = new JPanel();
			calPanel.setLayout(new GridBagLayout());
			
			setBtn.addActionListener(this);
			
			hSpn.setSize(new Dimension(50, 25));
			hSpn.setPreferredSize(new Dimension(50, 25));
			hSpn.setMinimumSize(new Dimension(50, 25));
			mSpn.setSize(new Dimension(50, 25));
			mSpn.setPreferredSize(new Dimension(50, 25));
			mSpn.setMinimumSize(new Dimension(50, 25));
			calPanel.add(c, new CnrBuilder().h(3).anchor(java.awt.GridBagConstraints.CENTER).build());
			calPanel.add(hLbl, new CnrBuilder().x(1).y(0).anchor(java.awt.GridBagConstraints.SOUTHWEST).ins(5,10,5,5).build());
			calPanel.add(hSpn, new CnrBuilder().x(1).y(1).anchor(java.awt.GridBagConstraints.NORTHWEST).ins(5,10,5,5).build());
			calPanel.add(mLbl, new CnrBuilder().x(2).y(0).anchor(java.awt.GridBagConstraints.SOUTHWEST).ins(5,5,5,5).build());
			calPanel.add(mSpn, new CnrBuilder().x(2).y(1).anchor(java.awt.GridBagConstraints.NORTHWEST).ins(5,5,5,5).build());
			calPanel.add(setBtn, new CnrBuilder().x(2).y(2).anchor(java.awt.GridBagConstraints.SOUTHEAST).ins(5,5,20,10).build());

			popup.setLightWeightPopupEnabled(true);
			
			popup.add(calPanel);
		}

		public void actionPerformed(ActionEvent e) {
			if(e.getSource() instanceof CalendarButton) {
				initialized = false;

				Component calBtn = (Component) e.getSource();
				int x = calBtn.getWidth() - (int) popup.getPreferredSize().getWidth();
				int y = calBtn.getY() + calBtn.getHeight();

				Calendar calendar = Calendar.getInstance();
				Object val = dateField.getValue();
				if(val instanceof Date) {
					Date d = (Date) val;
					calendar.setTime(d);
					hSpn.setValue(d.getHours());
					mSpn.setValue(d.getMinutes());
				} else {
					calendar.setTime(new Date());
					hSpn.setValue(0);
					mSpn.setValue(0);
				}
				c.setCalendar(calendar);
				popup.show(calBtn, x, y);
				initialized = true;
				dateSelected = false;
			} else if(e.getSource().equals(setBtn)) {
				Date value = new Date(c.getCalendar().getTimeInMillis());
				try {
					dateSelected = true;
					popup.setVisible(false);
					value.setHours((int) hSpn.getValue());
					value.setMinutes((int) mSpn.getValue());
					dateField.setValue(value);
					dateField.postActionEvent();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			// защита от ненужного срабатывания перед выводом календарика
			if (!initialized){
				return;
			}
			if (evt.getPropertyName().equals("day")) {
				dateSelected = true;
				popup.setVisible(false);
				// получение даты из календаря
				Date value = new Date(c.getCalendar().getTimeInMillis());
				try {
					value.setHours((int) hSpn.getValue());
					value.setMinutes((int) mSpn.getValue());
					dateField.setValue(value);
					dateField.postActionEvent();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (evt.getPropertyName().equals("date")) {
				Object value = evt.getNewValue();
				try {
					dateField.setValue((Date)value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static class TimeSpinner extends JSpinner {
		
		public TimeSpinner(final int maxValue) {
//			super(new SpinnerNumberModel(0, -1, maxValue, 1));
			DefaultEditor editor = (DefaultEditor)getEditor();
			JTextField tf = (JTextField) editor.getTextField();			
			((DefaultEditor)getEditor()).getTextField().addFocusListener(new TimeFocusListener());
			
			((DefaultEditor)getEditor()).getTextField().getDocument().addDocumentListener(new TimeDocListener(tf));
			
			super.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {

					Object obj = e.getSource();
					TimeSpinner ts = (TimeSpinner)e.getSource();
					int val = (int)ts.getValue();
					if(val == maxValue) {
						ts.setValue(0);
					}else if(val > maxValue) {
						ts.setValue(maxValue - 1);
					} else if(val < 0) {
						ts.setValue(maxValue - 1);
					}
				}
			});
		}
		
		static class TimeFocusListener extends FocusAdapter {
			@Override
			public void focusGained(FocusEvent e) {
								
				final JTextField tf = (JTextField) e.getSource();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Thread.sleep(50);
							tf.selectAll();
						} catch(InterruptedException e) {
							
						}
						
					}
				}).start();				
			}
		}
		
		static class TimeDocListener implements DocumentListener {
			
			private final JTextField textField;
			
			TimeDocListener(JTextField textField){
				this.textField = textField;
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
					final String txt = textField.getText();
					if(txt.length() > 2) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								try {
									Thread.sleep(5);
									textField.setText(txt.substring(0, 2));
								} catch(InterruptedException e) {
									
								}
							}
						}).start();	
					}
					
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				String s = textField.getText();
				int i = 0;
				
			}
			
		}
	}
}

