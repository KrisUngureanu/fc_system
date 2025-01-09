package kz.tamur.guidesigner.expr;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TabObj {
	public JButton btnClose;
	public String Title;
	public Component comp;
	public ActionListener CloseListener;
	
	public JPanel TitlePanel = new JPanel();
	
	public TabObj(JButton _btnClose, String _Title, Component _comp, ActionListener _CloseListener) {
		btnClose = _btnClose;
		Title = _Title;
		comp = _comp;
		CloseListener = _CloseListener;
		
		btnClose.setOpaque(false);
		TitlePanel.setOpaque(false);
		
		btnClose.addActionListener(CloseListener);
		JLabel LabelTitle = new JLabel(Title);
		LabelTitle.setFont(LabelTitle.getFont().deriveFont(10F)); // size font
		LabelTitle.setFont(LabelTitle.getFont().deriveFont(0)); //style font
		LabelTitle.setOpaque(false);
		
        javax.swing.GroupLayout tabLayout = new javax.swing.GroupLayout(TitlePanel);
        TitlePanel.setLayout(tabLayout);
        tabLayout.setHorizontalGroup(
        		tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(tabLayout.createSequentialGroup()
                    .addComponent(LabelTitle)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnClose))
            );
        tabLayout.setVerticalGroup(
        		tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(tabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose)
                    .addComponent(LabelTitle))
            );
	}
	
	public Component getTitle(){
		return TitlePanel;
	}
	
	public Component getTabComponent(){
		return comp;
	}
}