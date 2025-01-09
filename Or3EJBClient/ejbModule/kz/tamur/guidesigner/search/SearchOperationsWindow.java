package kz.tamur.guidesigner.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

import kz.tamur.comps.Constants;
import kz.tamur.util.Funcs;

public class SearchOperationsWindow extends JDialog implements ActionListener {
	
	private String title;
	private Dimension dimension;
	private int MODE;
	private String content;
	private String searchingPhrase;
	private List<String> searchingQueries;
	private List<String> searchingWords;
	private int[] searchingProperties;
	private boolean selection = false;
	private JButton cancelButton;
	private JButton replaceButton;
	private JButton okButton;
	private JButton continueButton;
	private JTextField whatTextField;
	private JTextField withTextField;
	private JPanel mainPanel = new JPanel();
	private JPanel wrapPanel = new JPanel();
	private Font font = new Font("Arial", Font.ITALIC, 12);
	private Dimension buttonDimension = new Dimension(80, 25);
	private ColorTextPane colorTextPane;
	private JScrollPane scrollPane;
	private int matchesCount = 0;
	private JLabel matchesCountLabel;
	private JCheckBox wrapCheckBox;
	private JCheckBox caseCheckBox;
	private String textPaneContent;
	private boolean isPressed = false;
	private Color fontColor = Color.RED;
	private Color backgroundColor = Color.YELLOW;
	private Color defaultFontColor = Color.BLACK;
	private Color defaultBackgroundColor = Color.WHITE;
	private List<Color[]> colors;
	private List<Object[]> combinations;
	private List<Pattern> patterns;
	private final String[] SPECIAL_CHARACTERS = new String[] {"+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?", ":", "\\"};
	private final String[][] SEARCH_SPECIAL_CHARACTERS = new String[][] {{"$", "\\$"}, {"'", "\\'"}, {"(", "\\("}, {")", "\\)"}};
	
	public SearchOperationsWindow(String title, int width, int height, int mode, List<Object> searchingInfo, String content) {
		this.title = title;
		dimension = new Dimension(width, height);
		this.MODE = mode;
		this.content = content;
		this.searchingQueries = (List<String>) searchingInfo.get(3);
		this.searchingProperties = (int[]) searchingInfo.get(1);
		init();
	}
	
	
	public SearchOperationsWindow(String title, int width, int height, int mode, String content, List<String> searchingWords) {
		this.title = title;
		dimension = new Dimension(width, height);
		this.MODE = mode;
		this.content = content;
		this.searchingWords = searchingWords;
		init();
	}
	
	public SearchOperationsWindow(String title, int width, int height, int mode, String searchingPhrase) {
		this.title = title;
		dimension = new Dimension(width, height);
		this.MODE = mode;
		this.searchingPhrase = searchingPhrase;
		init();
	}
	
	private void init() {
		initCloseListener();
		setTitle(title);
		setResizable(false);
		setSize(dimension);
		setLocation(kz.tamur.comps.Utils.getCenterLocationPoint(getSize()));
		if (searchingWords != null) {
			colors = getRandomColor(searchingWords.size());
		}
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		mainPanel.setMinimumSize(dimension);
		mainPanel.setMaximumSize(dimension);
		mainPanel.setPreferredSize(dimension);
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setLayout(new GridBagLayout());
		add(mainPanel);
		switch (MODE) {
			case 1:		
				JLabel whatLabel = kz.tamur.rt.Utils.createLabel("Что заменить:");
				mainPanel.add(whatLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				
				whatTextField = new JTextField();
				whatTextField.setEditable(true);
				whatTextField.setMinimumSize(new Dimension(100, 20));
				whatTextField.setMaximumSize(new Dimension(100, 20));
				whatTextField.setPreferredSize(new Dimension(100, 20));
				if (content != null && content.matches(".+"))
					whatTextField.setText(Funcs.sanitizeHtml(content));
				whatTextField.setFont(font);
				whatTextField.setText("");
				mainPanel.add(whatTextField, new GridBagConstraints(1, 0, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
				
				caseCheckBox = kz.tamur.rt.Utils.createCheckBox("Регистр", false);
				caseCheckBox.setBackground(Color.WHITE);
				caseCheckBox.setFocusPainted(false);
				caseCheckBox.addActionListener(this);
				mainPanel.add(caseCheckBox, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
								
				JLabel withLabel = kz.tamur.rt.Utils.createLabel("Чем заменить:");
				mainPanel.add(withLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				
				withTextField = new JTextField();
				withTextField.setMinimumSize(new Dimension(100, 20));
				withTextField.setMaximumSize(new Dimension(100, 20));
				withTextField.setPreferredSize(new Dimension(100, 20));
				withTextField.getDocument().addDocumentListener(new DocumentListener() {
					public void insertUpdate(DocumentEvent e) {
						testTextField();
					}
					
					public void removeUpdate(DocumentEvent e) {
						testTextField();
					}
		
					public void changedUpdate(DocumentEvent e) {
						testTextField();
					}
					
					private void testTextField() {
						if (withTextField.getText().equals("")) {
							replaceButton.setEnabled(false);					
						} else {
							replaceButton.setEnabled(true);
						}
					}
				});
				withTextField.setFont(font);
				mainPanel.add(withTextField, new GridBagConstraints(1, 1, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		
				replaceButton = new JButton("Заменить");
				replaceButton.setMinimumSize(buttonDimension);
				replaceButton.setMaximumSize(buttonDimension);
				replaceButton.setPreferredSize(buttonDimension);
				replaceButton.addActionListener(this);
				replaceButton.setEnabled(false);
				mainPanel.add(replaceButton, new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				
				cancelButton = new JButton("Отмена");
				cancelButton.setMinimumSize(buttonDimension);
				cancelButton.setMaximumSize(buttonDimension);
				cancelButton.setPreferredSize(buttonDimension);		
				cancelButton.addActionListener(this);
				cancelButton.setFocusable(true);
				cancelButton.requestFocus(true);
				mainPanel.add(cancelButton, new GridBagConstraints(2, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				break;
			case 2: 
				colorTextPane = new ColorTextPane();
				colorTextPane.setEditable(false);
				colorTextPane.setBackground(Color.WHITE);
				wrapPanel.setLayout(new BorderLayout());
				wrapPanel.add(colorTextPane, BorderLayout.CENTER);
				scrollPane = new JScrollPane(wrapPanel);
				scrollPane.setWheelScrollingEnabled(true);
				scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setBorder(BorderFactory.createLineBorder(kz.tamur.rt.Utils.getDarkShadowSysColor(), 1));
				mainPanel.add(scrollPane, new GridBagConstraints(0, 0, 3, 1, 1, 100, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(1, 1, 0, 1), 0, 0));		
				
				wrapCheckBox = kz.tamur.rt.Utils.createCheckBox("Перенос слов", false);
				wrapCheckBox.setBackground(Color.WHITE);
				wrapCheckBox.setFocusPainted(false);
				wrapCheckBox.addActionListener(this);
				mainPanel.add(wrapCheckBox, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));		
				
				okButton = new JButton("OK");
				okButton.setMinimumSize(buttonDimension);
				okButton.setMaximumSize(buttonDimension);
				okButton.setPreferredSize(buttonDimension);		
				okButton.addActionListener(this);
				okButton.setFocusable(true);
				okButton.requestFocus(true);
				mainPanel.add(okButton, new GridBagConstraints(2, 1, 1, 1, 1, 1, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 7, 7, 7), 0, 0));
	
				colorTextPane.appendText(content, -1, Color.BLACK, Color.WHITE, false, false);
				matchesCountLabel = kz.tamur.rt.Utils.createLabel("Количество совпадений: " + matchesCount);
				mainPanel.add(matchesCountLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				highlighText();
				break;
			case 3:
				colorTextPane = new ColorTextPane();
				colorTextPane.setBackground(Color.WHITE);
				colorTextPane.setCaretColor(Color.GREEN);
				colorTextPane.addKeyListener(new KeyListener() {
					public void keyPressed(KeyEvent e) {}
					public void keyTyped(KeyEvent e) {
						isPressed = true;
					}				
					
					public void keyReleased(KeyEvent e) {
						if (isPressed) {
							textSelection();
							isPressed = false;
						}
					}				
				});			
				wrapPanel.setLayout(new BorderLayout());
				wrapPanel.add(colorTextPane, BorderLayout.CENTER);
				scrollPane = new JScrollPane(wrapPanel);
				scrollPane.setWheelScrollingEnabled(true);
				scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setBorder(BorderFactory.createLineBorder(kz.tamur.rt.Utils.getDarkShadowSysColor(), 1));
				mainPanel.add(scrollPane, new GridBagConstraints(0, 0, 3, 1, 1, 100, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(1, 1, 0, 1), 0, 0));		
				
				wrapCheckBox = kz.tamur.rt.Utils.createCheckBox("Перенос слов", false);
				wrapCheckBox.setBackground(Color.WHITE);
				wrapCheckBox.setFocusPainted(false);
				wrapCheckBox.addActionListener(this);
				mainPanel.add(wrapCheckBox, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));		
				
				JPanel buttonsPanel = new JPanel();
				buttonsPanel.setLayout(new GridBagLayout());
				buttonsPanel.setBackground(Color.WHITE);
				
				okButton = new JButton("OK");
				okButton.setMinimumSize(buttonDimension);
				okButton.setMaximumSize(buttonDimension);
				okButton.setPreferredSize(buttonDimension);		
				okButton.addActionListener(this);
				okButton.setFocusable(true);
				okButton.requestFocus(true);
				buttonsPanel.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 7, 0, 7), 0, 0));
				
				cancelButton = new JButton("Отмена");
				cancelButton.setMinimumSize(buttonDimension);
				cancelButton.setMaximumSize(buttonDimension);
				cancelButton.setPreferredSize(buttonDimension);		
				cancelButton.addActionListener(this);
				cancelButton.setFocusable(true);
				cancelButton.requestFocus(true);
				buttonsPanel.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 7), 0, 0));
				
				mainPanel.add(buttonsPanel, new GridBagConstraints(2, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(7, 0, 7, 0), 0, 0));			
				
				colorTextPane.appendText(content, -1, Color.BLACK, Color.WHITE, false, false);
				matchesCountLabel = kz.tamur.rt.Utils.createLabel("Количество совпадений: " + matchesCount);
				mainPanel.add(matchesCountLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				textSelection();
				break;
			case 4:
				ColorTextPane messagePane = new ColorTextPane();
				messagePane.setEditable(false);
				Dimension mesAreaDimension = new Dimension(340, 85);
				messagePane.setMinimumSize(mesAreaDimension);
				messagePane.setMaximumSize(mesAreaDimension);
				messagePane.setPreferredSize(mesAreaDimension);
				StyledDocument doc = messagePane.getStyledDocument();
				SimpleAttributeSet justify = new SimpleAttributeSet();
				StyleConstants.setAlignment(justify, StyleConstants.ALIGN_JUSTIFIED);
				doc.setParagraphAttributes(0, doc.getLength(), justify, false);
				messagePane.appendText("          В выражении ", -1, Color.BLACK, Color.WHITE, false, false);
				messagePane.appendText("\"" + searchingPhrase + "\"", -1, Color.RED, Color.WHITE, false, false);
				messagePane.appendText(" содержатся специальные символы ", -1, Color.BLACK, Color.WHITE, false, false);
				messagePane.appendText(escSymbolsToSting(), -1, Color.RED, Color.WHITE, false, false);
				messagePane.appendText(". При использовании выбранного режима поиска все специальные символы будут заменены символом нижнего подчеркивания.", -1, Color.BLACK, Color.WHITE, false, false);
				mainPanel.add(messagePane, new GridBagConstraints(0, 0, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

				continueButton = new JButton("Продолжить");
				Dimension contButtonDimension = new Dimension(100, 25);
				continueButton.setMinimumSize(contButtonDimension);
				continueButton.setMaximumSize(contButtonDimension);
				continueButton.setPreferredSize(contButtonDimension);
				continueButton.addActionListener(this);
				mainPanel.add(continueButton, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				
				cancelButton = new JButton("Отмена");
				cancelButton.setMinimumSize(buttonDimension);
				cancelButton.setMaximumSize(buttonDimension);
				cancelButton.setPreferredSize(buttonDimension);		
				cancelButton.addActionListener(this);
				cancelButton.setFocusable(true);
				cancelButton.requestFocus(true);
				mainPanel.add(cancelButton, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				break;
		}
		setVisible(true);
	}

	private void initCloseListener() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "close");
		getRootPane().getActionMap().put("close", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	private String escSymbolsToSting() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < SPECIAL_CHARACTERS.length; i++) {
			buffer.append(SPECIAL_CHARACTERS[i]);
			if (i < SPECIAL_CHARACTERS.length - 1) {
				buffer.append(", ");
			}
		}
		return buffer.toString();
	}
	
	private void highlighText(Color highlighColor) {
		DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(highlighColor);
		for (int i = 0; i < searchingQueries.size(); i++) {
			Pattern pattern =  Pattern.compile(searchingQueries.get(i), Pattern.UNICODE_CASE);
			Matcher matcher = pattern.matcher(content);
			 while (matcher.find()) {
				matchesCount++;
	            try {
					colorTextPane.getHighlighter().addHighlight(matcher.start(), matcher.end(), highlightPainter);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			 }
		}
		matchesCountLabel.setText("Количество совпадений: " + matchesCount);
	}
	
	private void highlighText() {
		DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		if (searchingProperties[0] == 0) {
			for (int i = 0; i < searchingQueries.size(); i++) {
				searchingQueries.set(i, searchingQueries.get(i).toLowerCase(Constants.OK));
			}
			content = content.toLowerCase(Constants.OK);
		}
 		Pattern pattern;
		if (searchingProperties[1] == 0 || searchingProperties[1] == 2) {
			for (int i = 0; i < searchingQueries.size(); i++) {
				pattern =  Pattern.compile(searchingQueries.get(i), Pattern.UNICODE_CASE);
				Matcher matcher = pattern.matcher(content);
				 while (matcher.find()) {
					matchesCount++;
		            try {
						colorTextPane.getHighlighter().addHighlight(matcher.start(), matcher.end(), highlightPainter);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				 }
			}
		} else if (searchingProperties[1] == 1) {
			String searchStr=searchingQueries.get(0);
			for (int i=0;i<SEARCH_SPECIAL_CHARACTERS.length;i++) {
				searchStr= searchStr.replace(SEARCH_SPECIAL_CHARACTERS[i][0],SEARCH_SPECIAL_CHARACTERS[i][1]);
			}
			String regex = "[^A-Za-z0-9](" + searchStr + ")[^A-Za-z0-9]";
			pattern =  Pattern.compile(regex, Pattern.UNICODE_CASE);
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				matchesCount++;
	            try {
					colorTextPane.getHighlighter().addHighlight(matcher.start(1), matcher.end(1), highlightPainter);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
		matchesCountLabel.setText("Количество совпадений: " + matchesCount);
	}
	
	public void getCombinations(String[] words, Stack<String> stack, int index) {
        if (index == 0) {
        	combinations.add(stack.toArray());
            return;
        }
        for (int i = 0; i < words.length; i++) if (words[i] != "") {
        	stack.push(words[i]);
            words[i] = "";
            getCombinations(words, stack, index - 1);
            words[i] = stack.pop();
        }
    }
	
	private void textSelection() {
		List<String> currentSearchingWords = new ArrayList<String>(); 
		currentSearchingWords.addAll(searchingWords);
		int caret = colorTextPane.getCaretPosition();
		caret = caret + getLineNumber(caret, colorTextPane) - 1;
		content = colorTextPane.getText();
		colorTextPane.setText("");
		matchesCount = 0;
		while (true) {
			List<String> deletingWords = new ArrayList<String>();
			Map<Integer, String> positions = new HashMap<Integer, String>();
			for (int j = 0; j < currentSearchingWords.size(); j++) {
				String word = currentSearchingWords.get(j);
				int position = content.indexOf(word, 0);
				if (position > -1) {
					if (positions.size() > 0) {
						if (positions.containsKey(position)) {
							String existingWord = (String) positions.get(position);
							if (word.contains(existingWord)) {
								positions.put(position, word);
							}
						} else {
							if (position < (Integer) positions.keySet().toArray()[0]) {
								positions.remove((Integer) positions.keySet().toArray()[0]);
								positions.put(position, word);
							}
						}
					} else {
						positions.put(position, word);
					}						
				} else {
					deletingWords.add(word);
				}
			}
			currentSearchingWords.removeAll(deletingWords);
			if (positions.size() > 0) {					
				int position = (Integer) positions.keySet().toArray()[0];
				String searchingText = positions.get(position);
				String substring = content.substring(0, position);
				colorTextPane.appendText(substring, -1, defaultFontColor, defaultBackgroundColor, false, false);
//				colorTextPane.appendText(searchingText, -1, colors.get(searchingWords.indexOf(searchingText))[0], colors.get(searchingWords.indexOf(searchingText))[1], true, false);
				colorTextPane.appendText(searchingText, -1, fontColor, backgroundColor, true, false);
				content = content.substring(substring.length() + searchingText.length());
				matchesCount++;					
			} else {
				colorTextPane.appendText(content, -1, defaultFontColor, defaultBackgroundColor, false, false);
				break;
			}				
		}
		matchesCountLabel.setText("Количество совпадений: " + matchesCount);
		colorTextPane.setCaretPosition(caret);
	}
	
	private int getLineNumber(int caretPosition, JTextPane colorTextPane) {
		int lineNumber;
		if (wrapCheckBox.isSelected()) {
			lineNumber = (caretPosition == 0) ? 0 : -1;
			if (lineNumber == 0) {
				return lineNumber;
			} else {
				String substring = colorTextPane.getText().substring(0, caretPosition);
				lineNumber = substring.split("\\n").length;
				return lineNumber;
			}
		} else {		
			lineNumber = (caretPosition == 0) ? 1 : 0;
			try {
				int i = 0;
				while (i++ < 100000) {
					if (caretPosition > 0) {
						caretPosition = Utilities.getRowStart(colorTextPane, caretPosition) - 1;
						lineNumber++;
					} else
						return lineNumber;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			return lineNumber;
		}
	}
	
	public String getTextPaneContent() {
		return textPaneContent;
	}
	
	public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();      
        if (source == cancelButton) {
        	dispose();
        } else if (source == replaceButton) {
        	selection = true;
        	dispose();
        } else if (source == okButton) {
        	if (MODE == 3) {
        		if (!(colorTextPane.getText().equals(textPaneContent))) {
        			textPaneContent = colorTextPane.getText();
        		} else {
        			textPaneContent = null;
        		}
        	}
        	dispose();
        } else if (source == wrapCheckBox) {
        	if (wrapCheckBox.isSelected()) {
                scrollPane.setViewportView(colorTextPane);
			} else {
				wrapPanel.add(colorTextPane, BorderLayout.CENTER);
                scrollPane.setViewportView(wrapPanel);
			}
        } else if (source == continueButton) {
        	selection = true;
        	dispose();
        }
	}
	
	public boolean getSelection() {
		return selection;
	}
	
	public String getText() {
		return Funcs.validate(Funcs.normalizeInput(withTextField.getText()));
	}
	
	public List<Color[]> getRandomColor(int length) {
		List<Color[]> colors = new ArrayList<Color[]>();
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < length; i++) {
			Color[] colorScheme = new Color[2];
			while (true) {
				for (int j = 0; j < 2; j++) {
					while (true) {
						int red = random.nextInt(256);
						int green = random.nextInt(256);
						int blue = random.nextInt(256);				
						Color color = new Color(red, green, blue);
						if (j == 0) {
							colorScheme[0] = color;
							break;
						} else if (color != colorScheme[0]) {
							colorScheme[1] = color;
							break;
						}
					}
				}
				if (!colors.contains(colorScheme) && colorScheme != new Color[] {Color.BLACK, Color.WHITE} && colorScheme != new Color[] {Color.WHITE, Color.BLACK}) {
					colors.add(colorScheme);
					break;
				}
			}
		}			
		return colors;
	}
	
	class ColorTextPane extends JTextPane { 
		private StyledDocument myDoc;
		private SimpleAttributeSet keyWord;
		
		public ColorTextPane() {
			super();
			myDoc = this.getStyledDocument();	
			keyWord = new SimpleAttributeSet();
		}
		
		private void appendText(String line, int startPosition,  Color fontColor, Color backgroundColor, boolean isBold, boolean isItalic) {
			try {
				StyleConstants.setForeground(keyWord, fontColor);
				StyleConstants.setBackground(keyWord, backgroundColor);
				StyleConstants.setFontSize(keyWord, 12);
				StyleConstants.setItalic(keyWord, isItalic);
				StyleConstants.setBold(keyWord, isBold);
				if (startPosition < 0) {
					myDoc.insertString(myDoc.getLength(), line, keyWord);
				} else {
					myDoc.insertString(startPosition, line, keyWord);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		private boolean removeText(int startPosition, int length) {
			try {
				myDoc.remove(startPosition, length);
				return true;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			return false;
		}		
	}
	 /**
	 * @return the caseCheckBox
	 */
	protected boolean isCaseSelected(){
		 return caseCheckBox.isSelected();
	 }


	/**
	 * @return the whatTextField
	 */
	protected JTextField getWhatTextField() {
		return whatTextField;
	}
	 
}

