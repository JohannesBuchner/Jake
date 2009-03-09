package com.jakeapp.gui.swing.helpers.styler;

import org.jdesktop.swingx.painter.Painter;
import com.explodingpixels.painter.GradientPainter;

import java.awt.*;

/**
 * @author studpete
*/
public class SwingXGradientPainter implements Painter {
	private GradientPainter gradient;

	public SwingXGradientPainter(Color a, Color b) {
		this.gradient = new GradientPainter(a, b);
	}

	@Override public void paint(Graphics2D g, Object object, int width, int height) {
		gradient.paint(g, (Component) object, width, height);
	}
}
