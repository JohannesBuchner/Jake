package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.dialogs.generic.SheetEvent;
import com.jakeapp.gui.swing.dialogs.generic.SheetListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 12:20:54 AM
 */
public class RenameProjectAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(RenameProjectAction.class);

	public RenameProjectAction() {
		super();

		putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().
				  getString("renameMenuItem.text"));
	}


	public void actionPerformed(ActionEvent actionEvent) {
		log.info("Rename Project: " + getProject());

		//TODO: beautiful rename would be within sourcelist.
		//currently there is no support for that.
		//so we stick with a dialog (or within newspanel?)

		JSheet.showInputSheet(JakeMainView.getMainView().getFrame(),
				  JakeMainView.getMainView().getResourceMap().getString("projectRenameInput"),
				  JOptionPane.PLAIN_MESSAGE, null, null, getProject().getName(), new SheetListener() {
					  @Override
					  public void optionSelected(SheetEvent evt) {
						  String prName = (String) evt.getInputValue();

						  if (evt.getOption() == 0) {
							  JakeMainApp.getApp().getCore().setProjectName(getProject(), prName);
						  }
					  }
				  });
	}

	@Override
	public void updateAction() {
		this.setEnabled(getProject() != null && !getProject().isInvitation());
	}
}