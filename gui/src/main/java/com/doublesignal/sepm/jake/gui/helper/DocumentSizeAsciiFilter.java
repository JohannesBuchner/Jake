package com.doublesignal.sepm.jake.gui.helper;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * Extends the DocumentSizeFilter with an Ascii Filter.
 * @author peter
 *
 */
public class DocumentSizeAsciiFilter extends DocumentSizeFilter {

	public DocumentSizeAsciiFilter(int maxChars) {
		super(maxChars);
	}

	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a)
			throws BadLocationException {

		// filter the string
		super.insertString(fb, offs, filterStrToAscii(str), a);
	}

	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a)
			throws BadLocationException {
		
		// filter the string
		super.replace(fb, offs, length, filterStrToAscii(str), a);
	}
	

	private String filterStrToAscii(String str) {
		int stringSize = str.length();
		StringBuilder filteredStr = new StringBuilder(stringSize);

		for (int i = 0; i < stringSize; i++) {
			char c = str.charAt(i);
			if (Character.isDigit(c) || Character.isLetter(c) || Character.isWhitespace(c))
				filteredStr.append(c);
		}
		return filteredStr.toString();
	}
}
