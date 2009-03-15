package com.jakeapp.gui.swing.controls;

import javax.swing.*;

import com.jakeapp.gui.swing.helpers.StringUtilities;

import java.awt.*;

/**
 * A label with smaller font and the text is shortened (with "..." at the end) to a specific width. 
 * It the text would be longer, it is added as a tooltip.
 * @author Simon
 *
 */
public class SmallShortenedLabel extends SmallLabel {
	private static final long serialVersionUID = 4110706626176389735L;
	private final Font smallFont = UIManager.getFont("Label.font").deriveFont(12);
	
	private static int maxLength = 16; //FIXME: magic number preset.
	
	/**
	 * set the maximum length for the label text. the text will be cut after <code>len</code> characters
	 * and "..." will be appended. If the string is cut, the full string is added as a tooltip.
	 * @param len
	 */
	public static void setMaxLEngth(int len) {
		maxLength = len;
	}
	
	public SmallShortenedLabel() {
		super();
		
	}
	
	public SmallShortenedLabel(String text) {
		super();
		this.setText(text);
	}

	@Override
	public void setText(String text) {
		if (text.length() > maxLength) {
			this.setToolTipText(text);
			super.setText(StringUtilities.shorten(text, maxLength));
		} else 
			super.setText(text);
	}	
}
