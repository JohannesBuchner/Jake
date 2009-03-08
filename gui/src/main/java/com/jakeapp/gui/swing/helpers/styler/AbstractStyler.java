package com.jakeapp.gui.swing.helpers.styler;

import com.jakeapp.gui.swing.helpers.Colors;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;

import javax.swing.*;
import java.awt.*;

/**
 * User: studpete
 * Date: Dec 25, 2008
 * Time: 2:37:18 PM
 */
public abstract class AbstractStyler implements Styler {

	@Override
	public void makeWhiteRecessedButton(JButton btn) {
	}

	public void styleToolbarButton(JToggleButton jToggleButton) {
	}

	public Font getH1Font() {
		// TODO: search nicer font for windows? do they have lucida??
		// TODO: cache font.
		return new Font("Lucida Grande", Font.BOLD, 18);
	}

	public Font getH2Font() {
		// TODO: search nicer font for windows? do they have lucida??
		// TODO: cache font.
		return new Font("Lucida Grande", Font.BOLD, 15);
	}

	public Painter getContentPanelBackgroundPainter() {

		MattePainter mp = new MattePainter(Colors.LightBlue.alpha(0.6f));
		GlossPainter gp = new GlossPainter(Colors.White.alpha(0.5f),
				  GlossPainter.GlossPosition.TOP);
		return new CompoundPainter(mp, gp);
	}


	public Font getSheetLargeFont() {
		return new JLabel().getFont().deriveFont(Font.BOLD, 14);
	}

	public Color getWindowBackground() {
		return new Color(232, 232, 232);
	}

	public Color getFilterPaneColor(boolean windowFocus) {
		if (windowFocus)
			return Color.DARK_GRAY;
		else
			return Color.GRAY;
	}
}