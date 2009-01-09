package com.jakeapp.gui.swing.dialogs;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.dialogs.generic.JakeDialog;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * People Invitation Dialog. Opens modal dialog to add ProjectMembers
 *
 * @author: studpete
 */
// TODO: enable add multiple
// TODO: enable add by name (for already known)
// TODO: enable guess list; filter already added
public class ResolveConflictDialog extends JakeDialog {
	private static final Logger log = Logger.getLogger(ResolveConflictDialog.class);
	private FileObject fo;
	private JButton resolveBtn;
	private JRadioButton useLocalRadioButton;
	private JRadioButton useRemoteRadioButton;

	/**
	 * Private Constructor for ResolveConflictDialog.
	 * Use showDialog.
	 *
	 * @param project
	 * @param fo
	 */
	private ResolveConflictDialog(Project project, FileObject fo) {
		super(project);

		this.fo = fo;

		// load the resource map
		setResourceMap(org.jdesktop.application.Application.getInstance(
				  JakeMainApp.class).getContext()
				  .getResourceMap(ResolveConflictDialog.class));

		initDialog();

		// set custom properties
		setDialogTitle("resolveConflictTitle");
		setMessage("resolveHeader");
		setPicture("/icons/file-conflict-large.png");
	}


	@Override
	protected JButton initComponents() {

		// create the custom content for resolve conflict.
		JPanel customPanel = new JPanel(new MigLayout("wrap 2, fill, ins 0"));

		JLabel localLabel = new JLabel("<html>From: " + fo);
		JButton viewLocal = new JButton("Open Local");
		JButton viewRemote = new JButton("Open Remote");

		ActionListener updateResolveAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateResolveButton();
			}
		};

		useLocalRadioButton = new JRadioButton("Use local");
		useLocalRadioButton.addActionListener(updateResolveAction);
		useRemoteRadioButton = new JRadioButton("Use remote");
		useRemoteRadioButton.addActionListener(updateResolveAction);

		ButtonGroup grp = new ButtonGroup();
		grp.add(useLocalRadioButton);
		grp.add(useRemoteRadioButton);


		addCancelBtn();
		resolveBtn = new JButton();
		resolveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				resolveConflictAction();
			}
		});

		return resolveBtn;
	}

	/**
	 * Updates the resolve conflict button with the action
	 * that will be done upon click.
	 * Updates as RadioButtons changes.
	 */
	private void updateResolveButton() {
		String btnStr;
		if (isLocalSelected()) {
			btnStr = "resolveMyButton";
		} else if (isRemoteSelected()) {
			btnStr = "resolveThemButton";
		} else {
			// nothing selected
			btnStr = "resolveSelectOption";
		}

		resolveBtn.setText(getResourceMap().getString(btnStr));
	}

	/**
	 * Returs true if local radio button is seleted
	 *
	 * @return
	 */
	private boolean isLocalSelected() {
		return useLocalRadioButton.isSelected();
	}

	/**
	 * Returs true if remote radio button is seleted
	 *
	 * @return
	 */
	private boolean isRemoteSelected() {
		return useRemoteRadioButton.isSelected();
	}

	/**
	 * Reads the comboBox and sends the invites to the core.
	 */
	private void resolveConflictAction() {
		//JakeMainApp.getApp().getCore().invitePeople(project, peopleComboBox.getSelectedItem().toString());
		// TODO
		closeDialog();
	}

	/**
	 * Shows the Dialog. Static, configures modality and size, shows dialog.
	 *
	 * @param project: project where people will be added.
	 * @param fo:      file object
	 */
	public static void showDialog(Project project, FileObject fo) {
		ResolveConflictDialog dlg = new ResolveConflictDialog(project, fo);
		dlg.showDialogSized(500, 190);
	}
}