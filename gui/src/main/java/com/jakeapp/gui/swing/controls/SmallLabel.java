package com.jakeapp.gui.swing.controls;

import javax.swing.*;
import java.awt.*;

/**
 * Just a small class with slightly smaller font.
 * @author Simon
 *
 */
public class SmallLabel extends JLabel {
	private static final long serialVersionUID = 4110706626176389735L;
	private final Font smallFont = UIManager.getFont("Label.font").deriveFont(12);
	
	public SmallLabel() {
		super();
		this.setFont(this.smallFont);
	}
	
	public SmallLabel(String text) {
		super(text);
		this.setFont(this.smallFont);
	}
}
