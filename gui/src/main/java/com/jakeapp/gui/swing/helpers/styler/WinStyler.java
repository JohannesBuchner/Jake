/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.helpers.styler;

import javax.swing.*;
import java.awt.*;

/**
 * @author studpete
 */
public class WinStyler extends AbstractStyler {

	@Override
	public void styleToolbarButton(JToggleButton btn) {
		if (btn.isSelected()) {
			btn.setFont(btn.getFont().deriveFont(Font.BOLD));
		} else {
			btn.setFont(btn.getFont().deriveFont(Font.PLAIN));
		}
	}
}
