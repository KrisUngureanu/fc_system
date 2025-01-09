package kz.tamur.guidesigner.users;

import static kz.tamur.comps.Constants.DD_MM_YYYY_HH_MM;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.Date;

import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import kz.tamur.util.Funcs;

public class DateTimeField extends JTextField {
	private static final String MASK_ = "дд.мм.гггг ЧЧ:ММ";
	private static final CaretListener CARET_LISTENER_ = new DateCaretListener();
	private static final FocusListener FOCUS_LISTENER_ = new DateFocusListener();

	private int oldCaretPos_ = 0;

	public DateTimeField() {
		super(10);
		setText(MASK_);
		addCaretListener(CARET_LISTENER_);
		addFocusListener(FOCUS_LISTENER_);
	}

	public Date getValue() {
		Date res = null;
		String str = getText();
		if(str.matches(".*[дмгЧМ]")) {
			str = MASK_;
		}
		if (!str.equals(MASK_)) {
			try {
				res = Funcs.getDateFormat(DD_MM_YYYY_HH_MM).parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	public void setValue(Date value) {
		String str = (value == null) ?
				MASK_ : Funcs.getDateFormat(DD_MM_YYYY_HH_MM).format(value);
		setText(str);
	}

	protected Document createDefaultModel() {
		return new DateDocument();
	}

	static class DateCaretListener implements CaretListener {
		private boolean selfChange_ = false;

		public void caretUpdate(CaretEvent e) {
			if (!selfChange_) {
				try {
					selfChange_ = true;
					DateTimeField comp = (DateTimeField) e.getSource();
					int oldPos = comp.oldCaretPos_;
					int pos_ = e.getDot();
					int pos_m = e.getMark();
					if (pos_ == 2 || pos_ == 5 || pos_ == 10 || pos_ == 13) {
						pos_ = oldPos < pos_ ? pos_ + 1 : pos_ - 1;
						comp.setCaretPosition(pos_);
					} else if (pos_m == 2 || pos_m == 5) {
						pos_m = oldPos < pos_m ? pos_m + 1 : pos_m - 1;
						comp.setCaretPosition(pos_m);
					}
					if (pos_ > 0) {
						String str_ = comp.getText();
						char c = str_.charAt(pos_ - 1);
						if (c == '.' || c == ' ' || c == ':')
							c = str_.charAt(pos_ - 2);
						if (!Character.isDigit(c)) {
							pos_ = oldPos;
							comp.setCaretPosition(pos_);
						}
						str_ = str_.substring(0, pos_);
						int m = pos_;
						int i = str_.indexOf("д");
						if (i >= 0)
							m = i;
						else {
							int j = str_.indexOf("м");
							if (j >= 0)
								m = Math.min(m, j);
							else {
								int k = str_.indexOf("г");
								if (k >= 0) m = Math.min(m, k);
								else {
									int l = str_.indexOf("Ч");
									if(l >= 0) m = Math.min(m, l);
									else {
										int l2 = str_.indexOf("М");
										if(l2 >= 0) m = Math.min(m, l2);
									}
								}
							}
						}
						if (m < pos_)
							comp.setCaretPosition(m);
					}
					comp.oldCaretPos_ = pos_;
				} finally {
					selfChange_ = false;
				}
			}
		}
	}

	static class DateFocusListener extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			DateTimeField comp = (DateTimeField) e.getSource();
			CaretEvent ce = new CaretEvent (comp) {
				public int getDot() {
					return 16;
				}

				public int getMark() {
					return 16;
				}
			};
			comp.fireCaretUpdate(ce);
		}
	}

	static class DateDocument extends PlainDocument {
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			if (str == null) {
				return;
			}

			if ((offs + str.length()) > 16)
				return;

			int currLength = getLength();
			int length = str.length();

			if (length != 16 && offs != 0) {
				char[] chs = str.toCharArray();
				for (int i = 0; i < chs.length; i++) {
					if (!Character.isDigit(chs[i]))
						return;
				}
			}

			if (currLength > offs) {
				int l = currLength > offs + length ? length : currLength - offs;
				super.remove(offs, l);
			}

			super.insertString(offs, str, a);
		}

		public void remove(int offs, int len) throws BadLocationException {
			if (len == 1 && (offs == 2 || offs == 5) || offs == 10 || offs == 13)
				--offs;
			super.insertString(offs + len, MASK_.substring(offs, offs + len), null);
			super.remove(offs, len);
		}
	}
}