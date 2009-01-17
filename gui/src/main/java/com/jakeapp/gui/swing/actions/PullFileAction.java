package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;

import java.awt.event.ActionEvent;
import java.util.List;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;

public class PullFileAction extends FileAction {
	public PullFileAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
			 getString("pullMenuItem.text");

		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: Implement me!
	}
}