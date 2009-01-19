package com.jakeapp.gui.swing.dialogs;

import com.jakeapp.gui.swing.helpers.GuiUtilities;

import javax.swing.*;
import java.awt.*;

public class SplashWindow extends JFrame {
	private Image splashImage;
	private boolean paintCalled = false;
	private double transparency = 0.0F;

	public SplashWindow(Image splashImage) {
		super();
		this.splashImage = splashImage;
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(splashImage, 0);
		try {
			mt.waitForID(0);
		} catch (InterruptedException ie) {
		}

		int imgWidth = splashImage.getWidth(this);
		int imgHeight = splashImage.getHeight(this);
		setSize(imgWidth, imgHeight);
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenDim.width - imgWidth) / 2, (screenDim.height - imgHeight) / 2);
	}

	public void update(Graphics g) {
		g.setColor(getForeground());
		paint(g);
	}

	public void paint(Graphics g) {
		g.drawImage(splashImage, 0, 0, this);
		if (!paintCalled) {
			paintCalled = true;
			synchronized (this) {
				notifyAll();
			}
		}
	}

	/**
	 * Sets the Transparency, animates changes.
	 *
	 * @param transparency
	 */
	public synchronized void setTransparency(final double transparency) {
		final double oldTr = this.transparency;
		this.transparency = transparency;

		final JFrame frame = this;

		Runnable runn = new Runnable() {
			@Override
			public void run() {
				for (double i = oldTr; i < transparency; i += 0.02F) {
					GuiUtilities.setFrameAlpha(frame, i);
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(runn).start();
	}

	public static SplashWindow splash(Image splashImage) {
		SplashWindow w = new SplashWindow(splashImage);
		w.toFront();
		w.setUndecorated(true);
		w.setTransparency(0.3F);
		w.setVisible(true);

		if (!EventQueue.isDispatchThread()) {
			synchronized (w) {
				while (!w.paintCalled) {
					try {
						w.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
		return w;
	}
}