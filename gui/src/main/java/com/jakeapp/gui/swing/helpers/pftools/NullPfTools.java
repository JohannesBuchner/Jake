package com.jakeapp.gui.swing.helpers.pftools;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;

/**
 * Mac implementation if PfTools
 *
 * @author: studpete
 */
public class NullPfTools extends AbstractPfTools {
	private static final Logger log = Logger.getLogger(NullPfTools.class);

	@Override
	public Icon getFileIcon(File file, int size) {
		log.debug("Null impl called for getFileIcon: " + file);
		return null;
	}
}