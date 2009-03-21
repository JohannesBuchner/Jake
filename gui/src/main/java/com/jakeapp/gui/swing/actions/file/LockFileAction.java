package com.jakeapp.gui.swing.actions.file;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.dialogs.generic.SheetEvent;
import com.jakeapp.gui.swing.dialogs.generic.SheetListener;
import com.jakeapp.gui.swing.globals.JakeContext;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action for locking files with a message. No batch processing.
 *
 * @author Simon
 */
public class LockFileAction extends FileAction {
	private static final Logger log =
					Logger.getLogger(LockFileAction.class);
	private static final long serialVersionUID = -7898650898881238796L;
	private final ResourceMap resourceMap;

	public LockFileAction() {
		super();
		this.resourceMap = JakeMainView.getResouceMap();

		updateAction();
	}

	@Override
	public void updateAction() {
		// only enable if exact one element is selected AND that element is NOT a folder.
		setEnabled(this.isSingleFileSelected());

		// detect if there is a soft lock set
		Attributed<FileObject> aFo = null;

		if (isSingleFileSelected()) {
			aFo = JakeMainApp.getCore()
							.getAttributed(getSelectedFile());
		}

		String actionStr;
		if (aFo != null && aFo.isLocked()) {
			actionStr = this.resourceMap.getString("unlockMenuItem.text");
		} else {
			actionStr = this.resourceMap.getString("lockMenuItem.text");
		}
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!isSingleFileSelected()) {
			log.warn("Action Lock failed: no single file selected.");
		}

		// detect if there is a soft lock set
		final Attributed<FileObject> aFo = JakeMainApp.getCore()
						.getAttributed(getSelectedFile());

		String promptStr = this.resourceMap.getString("promptLockWithComment");

		JSheet.showInputSheet(JakeContext.getFrame(), promptStr, null,
						new SheetListener() {
							@Override public void optionSelected(SheetEvent evt) {
								JakeMainApp.getCore()
												.setSoftLock(aFo.getJakeObject(), aFo.isLocked(),
																(String) evt.getInputValue());
							}
						});
	}
}