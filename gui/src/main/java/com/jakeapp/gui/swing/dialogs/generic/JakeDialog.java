package com.jakeapp.gui.swing.dialogs.generic;

import org.jdesktop.application.ResourceMap;

import java.awt.*;

/**
 * @author: studpete
 */
public class JakeDialog extends EscapeDialog {
	private ResourceMap resourceMap;

	public JakeDialog(Frame owner) {
		super(owner);
	}

	public ResourceMap getResourceMap() {
		return resourceMap;
	}

	public void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}
}
