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


		addCancelBtn();
		JButton resolveBtn = new JButton(getResourceMap().getString("resolveMyButton"));
		resolveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				resolveConflictAction();
			}
		});

		return resolveBtn;
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