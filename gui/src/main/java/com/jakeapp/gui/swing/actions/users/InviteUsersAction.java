package com.jakeapp.gui.swing.actions.users;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.actions.project.StartStopProjectAction;
import com.jakeapp.gui.swing.dialogs.InvitePeopleDialog;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.dialogs.generic.SheetEvent;
import com.jakeapp.gui.swing.dialogs.generic.SheetListener;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.panels.FilePanel;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The Invite people action.
 * Opens a Dialog that let you add people to the project.
 * They get an invitation and can join/refuse the project.
 */
public class InviteUsersAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(InviteUsersAction.class);

	/**
	 * Create a new <code>InvitePeopleAction</code>.
	 *
	 * @param ellipsis if <code>true</code> the <code>Action.NAME</code> ends with an ellipsis (...), if
	 *                 <code>false</code> the dots are omitted.
	 */
	public InviteUsersAction(boolean ellipsis) {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().
						getString("invitePeopleMenuItem.text");

		if (ellipsis) {
			actionStr += "...";
		}

		putValue(Action.NAME, actionStr);
		putValue(Action.ACCELERATOR_KEY,
						javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I,
										java.awt.event.InputEvent.META_MASK));


		// add large icon (for toolbar only)
		Icon invitePeopleIcon = new ImageIcon(Toolkit.getDefaultToolkit()
						.getImage(getClass().getResource("/icons/people.png")).getScaledInstance(
						32, 32, Image.SCALE_SMOOTH));

		this.putValue(Action.LARGE_ICON_KEY, invitePeopleIcon);
	}


	public void actionPerformed(ActionEvent actionEvent) {
		log.debug("Invite People to: " + getProject());

		if (!getProject().isStarted()) {
			ResourceMap map = FilePanel.getInstance().getResourceMap();
			String[] options = {"Start Project", map.getString("genericCancel")};
			
			//ask user and do the real work with a Worker!
			JSheet.showOptionSheet(JakeContext.getFrame(),
							"You need to start the project first.", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options, options[0],
							new SheetListener() {
								@Override
								public void optionSelected(SheetEvent evt) {
									if (evt.getOption() == 0) {
										StartStopProjectAction.perform(getProject());
										showInviteDialog();
									}
								}
							});
		} else {
			showInviteDialog();
		}
	}

	private void showInviteDialog() {InvitePeopleDialog.showDialog(getProject());}
}