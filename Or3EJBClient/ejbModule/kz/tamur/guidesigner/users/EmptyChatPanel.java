package kz.tamur.guidesigner.users;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class EmptyChatPanel extends JPanel {
   
	private ColorTextPane infoText;
	
	public EmptyChatPanel() {
        super();
	    this.setLayout(new BorderLayout());
	    infoText = new ColorTextPane();
	    infoText.setBackground(kz.tamur.rt.Utils.getLightSysColor());
	    JScrollPane infoTextScroll = new JScrollPane(infoText);
	    infoTextScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(infoTextScroll, BorderLayout.CENTER);
        infoText.appendText("\tИнформация!\n", Color.RED, false, false, 14);
        infoText.appendText("\tДля функционирования модуля \"Чат\" необходимо создать системный класс \"ChatClass\" со следующими атрибутами:\n", Color.BLACK);
        infoText.appendText("     - from. ", Color.BLUE, false, true, 12);
        infoText.appendText("Данный атрибут имеет тип String и хранит в себе имя пользователя-отправителя.\n", Color.BLACK);
        infoText.appendText("     - to. ", Color.BLUE, false, true, 12);
        infoText.appendText("Данный атрибут имеет тип String и хранит в себе имя пользователя-получателя.\n", Color.BLACK);
        infoText.appendText("     - canDeleteFrom. ", Color.BLUE, false, true, 12);
        infoText.appendText("Данный атрибут имеет тип String, может принимать значения Yes/No и хранит в себе информацию о статусе удаления сообщения из архива пользователем-отправителем.\n", Color.BLACK);
        infoText.appendText("     - canDeleteTo. ", Color.BLUE, false, true, 12);
        infoText.appendText("Данный атрибут имеет тип String, может принимать значения Yes/No и хранит в себе информацию о статусе удаления сообщения из архива пользователем-получателем.\n", Color.BLACK);
        infoText.appendText("     - status. ", Color.BLUE, false, true, 12);
        infoText.appendText("Данный атрибут имеет тип String, может принимать значения New/Old и хранит в себе статус новизны сообщения.\n", Color.BLACK);
        infoText.appendText("     - datetime. ", Color.BLUE, false, true, 12);
        infoText.appendText("Данный атрибут имеет тип Time и хранит в себе дату и время отправления сообщения.\n", Color.BLACK);
	}
	
	class ColorTextPane extends JTextPane { 
    	
    	public ColorTextPane() {
    		super();
    	}
    	
		private void appendText(String line, Color myColor, boolean isBold, boolean isItalic, int fontSize) {
			try {
				StyledDocument myDoc = this.getStyledDocument();	
				SimpleAttributeSet keyWord = new SimpleAttributeSet();
				StyleConstants.setForeground(keyWord, myColor);
				StyleConstants.setBold(keyWord, isBold);
				StyleConstants.setItalic(keyWord, isItalic);
				StyleConstants.setFontSize(keyWord, fontSize);
				myDoc.insertString(myDoc.getLength(), line, keyWord);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
    	}
		
		private void appendText(String line, Color myColor) {
			try {
				StyledDocument myDoc = this.getStyledDocument();	
				SimpleAttributeSet keyWord = new SimpleAttributeSet();
				StyleConstants.setForeground(keyWord, myColor);
				StyleConstants.setFontSize(keyWord, 12);
				myDoc.insertString(myDoc.getLength(), line, keyWord);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
    	}
    }
	
}
