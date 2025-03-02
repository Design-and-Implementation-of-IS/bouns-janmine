package util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * http://www.java2s.com/Tutorial/Java/0240__Swing/LimitJTextFieldinputtoamaximumlength.htm
 */
public class JTextFieldLimiter extends PlainDocument {
    /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final int limit;

    public JTextFieldLimiter(int limit) {
        super();
        this.limit = (limit > 0) ? limit : 0;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null)
            return;

        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        }
    }
}
