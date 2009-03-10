package com.jakeapp.gui.swing.helpers;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;


public class ImageLoader {

	private static final Logger log = Logger.getLogger(ImageLoader.class);

	public static ImageIcon get(Class clazz, String name) {
		URL res = clazz.getResource(name);
		log.debug("loading " + res);
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(res));
	}

	public static ImageIcon getScaled(Class clazz, String name, int size) {
		return getScaled(clazz, name, size, size, Image.SCALE_SMOOTH);
	}

	public static ImageIcon getScaled(Class clazz, String name, int width, int height,
			int hints) {
		URL res = clazz.getResource(name);
		log.debug("loading " + res);

		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(res).getScaledInstance(
				width, height, hints));
	}
}
