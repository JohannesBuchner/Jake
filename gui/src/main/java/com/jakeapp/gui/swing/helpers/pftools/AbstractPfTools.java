package com.jakeapp.gui.swing.helpers.pftools;

import javax.swing.*;
import java.io.File;

/**
 * Abstract base class for platform specific toolkit.
 *
 * @author: studpete
 */
public abstract class AbstractPfTools {

	/**
	 * Get a FileIcon from the OS.
	 * The java variant can only get 16x16 pixel icons.
	 * This variant is size independent.
	 * Does scaling if implementation limits size.
	 *
	 * @param file
	 * @param size
	 * @return
	 */
	// TODO: get icon on unix? found some code... but needs jna lib!
	public abstract Icon getFileIcon(File file, int size);
}
