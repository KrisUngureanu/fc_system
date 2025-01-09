package kz.tamur.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.Utilities;

public class TextLineNumber extends JPanel implements CaretListener, DocumentListener, PropertyChangeListener {
	
	public final static float LEFT = 0.0f;
	public final static float CENTER = 0.5f;
	public final static float RIGHT = 1.0f;
	
	private final static int HEIGHT = 1000000000;
    private static final int MAX_ELEMENTS_COUNT = 1000000;

    private JTextComponent component;

	private boolean updateFont;
	private int borderGap;
	private Color currentLineForeground;
	private float digitAlignment;
	private int minimumDisplayDigits;

    private int lastDigits;
    private int lastHeight;
    private int lastLine;

	private HashMap<String, FontMetrics> fonts;

	/**
	 * Инициализация конструктора значениями по умолчанию
	 */
	public TextLineNumber(JTextComponent component) {
		this.component = component;
		setFont(component.getFont());
		setBorderGap(10, false);
		setCurrentLineForeground(Color.RED);
		setBackground(component.getBackground());
		setDigitAlignment(RIGHT);
		setMinimumDisplayDigits(3);
		component.getDocument().addDocumentListener(this);
		component.addCaretListener(this);
		component.addPropertyChangeListener("font", this);
	}
	
	/**
	 * Инициализация конструктора
	 * @param component - Текстовый компонент
	 * @param font - Шрифт символов нумерации
	 * @param borderGap - ширина рамки
	 * @param lineForeground - Цвет шрифта символов нумерации
	 * @param panelBackground - Цвет фона панели нумерации
	 * @param digitAlignment - Ровнение строк нумерации
	 * @param minimumDisplayDigits - Минимальная разрядность цифр нумерации
	 * @param isDoubleBorder - Установка двойной рамки (разделитель между редактором и панелью нумерации)
	 */
	public TextLineNumber(JTextComponent component, Font font, int borderGap, Color lineForeground, Color panelBackground, float digitAlignment, int minimumDisplayDigits, boolean isDoubleBorder)	{
		this.component = component;
		setFont(font);
		setBorderGap(borderGap, isDoubleBorder);
		setCurrentLineForeground(lineForeground);
		setBackground(panelBackground);
		setDigitAlignment(digitAlignment);
		setMinimumDisplayDigits(minimumDisplayDigits);
		component.getDocument().addDocumentListener(this);
		component.addCaretListener(this);
		component.addPropertyChangeListener("font", this);
	}

	public boolean getUpdateFont() {
		return updateFont;
	}

	public void setUpdateFont(boolean updateFont) {
		this.updateFont = updateFont;
	}
	
	public int getBorderGap() {
		return borderGap;
	}

	public void setBorderGap(int borderGap, boolean isDoubleBorder) {
		this.borderGap = borderGap;
		if (isDoubleBorder) {
			setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, Color.BLACK), new EmptyBorder(0, 0, 0, borderGap)));
		} else {
			setBorder(new EmptyBorder(0, 0, 0, borderGap));
		}
		lastDigits = 0;
		setPreferredWidth();
	}

	public Color getCurrentLineForeground() {
		return currentLineForeground == null ? getForeground() : currentLineForeground;
	}

	public void setCurrentLineForeground(Color currentLineForeground) {
		this.currentLineForeground = currentLineForeground;
	}

	public float getDigitAlignment() {
		return digitAlignment;
	}

	public void setDigitAlignment(float digitAlignment) {
		this.digitAlignment =
			digitAlignment > 1.0f ? 1.0f : digitAlignment < 0.0f ? -1.0f : digitAlignment;
	}

	public int getMinimumDisplayDigits() {
		return minimumDisplayDigits;
	}

	public void setMinimumDisplayDigits(int minimumDisplayDigits) {
		this.minimumDisplayDigits = minimumDisplayDigits;
		setPreferredWidth();
	}

	private void setPreferredWidth() {
		Element root = component.getDocument().getDefaultRootElement();
		int lines = root.getElementCount();
		int digits = Math.max(String.valueOf(lines).length(), minimumDisplayDigits);
		if (lastDigits != digits)
		{
			lastDigits = digits;
			FontMetrics fontMetrics = getFontMetrics(getFont());
			int width = fontMetrics.charWidth('0') * digits;
			Insets insets = getInsets();
			int preferredWidth = insets.left + insets.right + width;
			Dimension dimension = getPreferredSize();
			dimension.setSize(preferredWidth, HEIGHT);
			setPreferredSize(dimension);
			setSize(dimension);
		}
	}

	public void paintComponent(Graphics g)	{
		super.paintComponent(g);
		FontMetrics fontMetrics = component.getFontMetrics(component.getFont());
		Insets insets = getInsets();
		int availableWidth = getSize().width - insets.left - insets.right;
		Rectangle clip = g.getClipBounds();
		int rowStartOffset = component.viewToModel(new Point(0, clip.y));
		int endOffset = component.viewToModel(new Point(0, clip.y + clip.height));
		while (rowStartOffset <= endOffset) {
			try {
    			if (isCurrentLine(rowStartOffset))
    				g.setColor(getCurrentLineForeground());
    			else
    				g.setColor(getForeground());
    			String lineNumber = getTextLineNumber(rowStartOffset);
    			int stringWidth = fontMetrics.stringWidth(lineNumber);
    			int x = getOffsetX(availableWidth, stringWidth) + insets.left;
				int y = getOffsetY(rowStartOffset, fontMetrics);
    			g.drawString(lineNumber, x, y);
    			rowStartOffset = Utilities.getRowEnd(component, rowStartOffset) + 1;
			}
			catch(Exception e) {
				break;
			}
		}
	}

	private boolean isCurrentLine(int rowStartOffset) {
		int caretPosition = component.getCaretPosition();
		Element root = component.getDocument().getDefaultRootElement();
		if (root.getElementIndex(rowStartOffset) == root.getElementIndex(caretPosition))
			return true;
		else
			return false;
	}

	protected String getTextLineNumber(int rowStartOffset) {
		Element root = component.getDocument().getDefaultRootElement();
		int index = root.getElementIndex(rowStartOffset);
		Element line = root.getElement(index);
		if (line.getStartOffset() == rowStartOffset)
			return String.valueOf(index + 1);
		else
			return "";
	}

	private int getOffsetX(int availableWidth, int stringWidth)	{
		return (int)((availableWidth - stringWidth) * digitAlignment);
	}

	private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics) throws BadLocationException	{
		Rectangle r = component.modelToView(rowStartOffset);
		int lineHeight = fontMetrics.getHeight();
		int y = r.y + r.height;
		int descent = 0;
		if (r.height == lineHeight) {
			descent = fontMetrics.getDescent();
		}
		else {
			if (fonts == null)
				fonts = new HashMap<String, FontMetrics>();
			Element root = component.getDocument().getDefaultRootElement();
			int index = root.getElementIndex(rowStartOffset);
			
			if (index < MAX_ELEMENTS_COUNT) {
				Element line = root.getElement(index);
	
				if (line.getName().matches(".+")) {
					int count = line.getElementCount();
					if (count < MAX_ELEMENTS_COUNT) {
						for (int i = 0; i < count && i < MAX_ELEMENTS_COUNT; i++) {
							Element child = line.getElement(i);
							AttributeSet as = child.getAttributes();
							String fontFamily = Funcs.sanitizeElementName((String)as.getAttribute(StyleConstants.FontFamily));
							Integer fontSize = (Integer)as.getAttribute(StyleConstants.FontSize);
							String key = fontFamily + fontSize;
							FontMetrics fm = fonts.get(key);
							if (fm == null) {
								Font font = new Font(fontFamily, Font.PLAIN, fontSize);
								fm = component.getFontMetrics(font);
								fonts.put(key, fm);
							}
							descent = Math.max(descent, fm.getDescent());
						}
					}
				}
			}
		}
		return y - descent;
	}

	public void caretUpdate(CaretEvent e) {
		int caretPosition = component.getCaretPosition();
		Element root = component.getDocument().getDefaultRootElement();
		int currentLine = root.getElementIndex(caretPosition);
		if (lastLine != currentLine) {
			repaint();
			lastLine = currentLine;
		}
	}

	public void changedUpdate(DocumentEvent e) {
		documentChanged();
	}

	public void insertUpdate(DocumentEvent e) {
		documentChanged();
	}

	public void removeUpdate(DocumentEvent e) {
		documentChanged();
	}

	private void documentChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					int endPos = component.getDocument().getLength();
					Rectangle rect = component.modelToView(endPos);
					if (rect != null && rect.y != lastHeight) {
						setPreferredWidth();
						repaint();
						lastHeight = rect.y;
					}
				}
				catch (BadLocationException ex) {}
			}
		});
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() instanceof Font) {
			if (updateFont) {
				Font newFont = (Font) evt.getNewValue();
				setFont(newFont);
				lastDigits = 0;
				setPreferredWidth();
			} else {
				repaint();
			}
		}
	}
}