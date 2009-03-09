package com.jakeapp.gui.swing.helpers.styler;

import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.CapsulePainter;

import javax.swing.*;
import java.awt.*;

/**
 * Common styling code for the app
 */
public abstract class AbstractStyler implements Styler {
	private static final Font h1Font = new Font("Lucida Grande", Font.BOLD, 18);
	private static final Font h2Font = new Font("Lucida Grande", Font.BOLD, 15);

	private Painter loginBackgroundPainter;
	private Painter contentBackgroundPainter;
	private CapsulePainter userBackgroundPainter;

	public AbstractStyler() {
		
		loginBackgroundPainter = new SwingXGradientPainter(new Color(125, 125, 125),
						new Color(64, 64, 64));

		contentBackgroundPainter = new SwingXGradientPainter(new Color(137, 149, 171),
						new Color(157, 172, 201));

		userBackgroundPainter =  new CapsulePainter();
		Color c1 = new Color(100, 100, 100);
		Color c2 = new Color(130, 130, 130);
		userBackgroundPainter.setFillPaint(new GradientPaint(0f, 0f, c2, 0f, 50f, c1));
	}

	@Override
	public void makeWhiteRecessedButton(JButton btn) {
	}

	public void styleToolbarButton(JToggleButton jToggleButton) {
	}

	public Font getH1Font() {
		// TODO: search nicer font for windows? do they have lucida??
		return h1Font;
	}

	public Font getH2Font() {
		// TODO: search nicer font for windows? do they have lucida??
		return h2Font;
	}

	public Painter getLoginBackgroundPainter() {
		return loginBackgroundPainter;
		/*
		MattePainter mp = new MattePainter(Colors.LightBlue.alpha(0.6f));
		GlossPainter gp = new GlossPainter(Colors.White.alpha(0.5f),
					GlossPainter.GlossPosition.TOP);
		return new CompoundPainter(mp, gp);
		*/
	}

	public Painter getContentBackgroundPainter() {
		return contentBackgroundPainter;
	}

	public Painter getUserBackgroundPainter() {
		return userBackgroundPainter;
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