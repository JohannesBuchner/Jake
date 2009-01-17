package com.jakeapp.gui.swing.dialogs;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.dialogs.generic.JakeDialog;
import com.jakeapp.gui.swing.models.InvitePeopleComboBoxModel;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * People Invitation Dialog. Opens modal dialog to add ProjectMembers
 *
 * @author: studpete
 */
// TODO: enable add multiple
// TODO: enable add by name (for already known)
public class InvitePeopleDialog extends JakeDialog {
	private static final Logger log = Logger.getLogger(InvitePeopleDialog.class);
	private JComboBox peopleComboBox;

	public InvitePeopleDialog(Project project) {
		super(project);

		setResourceMap(org.jdesktop.application.Application.getInstance(
				  com.jakeapp.gui.swing.JakeMainApp.class).getContext()
				  .getResourceMap(InvitePeopleDialog.class));

		initDialog();

		// set custom properties
		setDialogTitle(getResourceMap().getString("inviteTitle"));
		setMessage("inviteHeader");
		setPicture("/icons/user-large.png");
	}

	@Override
	protected JButton initComponents() {

		// generate a auto-completion combobox
		peopleComboBox = new JComboBox() {
			public void processKeyEvent(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					invitePeopleAction();
					//super.processKeyEvent(e);
				} else {
					e.consume();
				}
			}
		};
		peopleComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				// fired when selecting or press enter
			}
		});
		peopleComboBox.setEditable(true);
		peopleComboBox.setModel(new InvitePeopleComboBoxModel(getProject()));
		AutoCompleteDecorator.decorate(peopleComboBox);
		this.add(peopleComboBox, "growx");

		// create buttons
		this.addCancelBtn();
		JButton inviteBtn = new JButton(getResourceMap().getString("inviteButton"));
		inviteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				invitePeopleAction();
			}
		});
		return inviteBtn;
	}

	/**
	 * Reads the comboBox and sends the invites to the core.
	 */
	private void invitePeopleAction() {
		JakeMainApp.getApp().getCore().invitePeople(getProject(), peopleComboBox.getSelectedItem().toString());
		closeDialog();
	}

	/**
	 * Shows the Dialog. Static, configures modality and size, shows dialog.
	 *
	 * @param project: project where people will be added.
	 */
	public static void showDialog(Project project) {
		InvitePeopleDialog dlg = new InvitePeopleDialog(project);
		dlg.showDialogSized(400, 220);
	}
}
