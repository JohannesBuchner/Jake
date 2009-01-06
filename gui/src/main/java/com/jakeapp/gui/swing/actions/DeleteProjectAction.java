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
public class DeleteProjectAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(DeleteProjectAction.class);

	public DeleteProjectAction() {
		super();

		putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().
				  getString("deleteProjectMenuItem.text"));
	}


	public void actionPerformed(ActionEvent actionEvent) {
		log.info("Delete Project: " + getProject());

		Object[] options = {JakeMainView.getMainView().getResourceMap().getString("confirmDeleteProjectDelete"), JakeMainView.getMainView().getResourceMap().getString("GenericCancel")};

		JSheet.showOptionSheet(JakeMainView.getMainView().getFrame(),
				  JakeMainView.getMainView().getResourceMap().getString("confirmDeleteProject"),
				  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0], new SheetListener() {

					  @Override
					  public void optionSelected(SheetEvent evt) {
						  if (evt.getOption() == 0) {
							  JakeMainApp.getApp().getCore().deleteProject(getProject());
						  }
					  }
				  });
	}


	@Override
	public void updateAction() {
	}
}