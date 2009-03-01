package com.jakeapp.gui.swing.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Renders a ColboBox with a Icon and Text
 *
 * @author studpete
 */
public class IconComboBoxRenderer extends JLabel
		  implements ListCellRenderer {
	private Font uhOhFont;

	private ImageIcon[] images;
	private String[] strings;

	public IconComboBoxRenderer(ImageIcon[] images, String[] strings) {
		this.images = images;
		this.strings = strings;

		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
	}

	/*
				* This method finds the image and text corresponding
				* to the selected value and returns the label, set up
				* to display the text and image.
				*/
	public Component getListCellRendererComponent(
			  JList list,
			  Object value,
			  int index,
			  boolean isSelected,
			  boolean cellHasFocus) {
		//Get the selected index. (The index param isn't
		//always valid, so just use the value.)
		int selectedIndex = (Integer) value;

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		//Set the icon and text.  If icon was null, say so.
		ImageIcon icon = images[selectedIndex];
		String pet = strings[selectedIndex];
		setIcon(icon);
		if (icon != null) {
			setText(pet);
			setFont(list.getFont());
			setHorizontalAlignment(JLabel.LEFT);
		} else {
			setUhOhText(pet + " (no image available)",
					  list.getFont());
		}

		return this;
	}

	//Set the font and text when no image was found.
	protected void setUhOhText(String uhOhText, Font normalFont) {
		if (uhOhFont == null) { //lazily create this font
			uhOhFont = normalFont.deriveFont(Font.ITALIC);
		}
		setFont(uhOhFont);
		setText(uhOhText);
	}
}