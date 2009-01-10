package com.jakeapp.gui.swing.dialogs;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.actions.abstracts.JakeAction;
import com.jakeapp.gui.swing.dialogs.generic.JakeDialog;
import com.jakeapp.gui.swing.helpers.FileObjectHelper;
import com.jakeapp.gui.swing.helpers.GuiUtilities;
import com.jakeapp.gui.swing.helpers.StringUtilities;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXHyperlink;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * People Invitation Dialog. Opens modal dialog to add ProjectMembers
 *
 * @author: studpete
 */
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
		log.info("Opening ResolveConflictDialog on " + project + " with file: " + fo);

		this.fo = fo;

		// load the resource map
		setResourceMap(org.jdesktop.application.Application.getInstance(
				  JakeMainApp.class).getContext()
				  .getResourceMap(ResolveConflictDialog.class));

		initDialog();

		// set custom properties
		setDialogTitle(getResourceMap().getString("resolveTitle") + " " + FileObjectHelper.getName(fo.getAbsolutePath()));
		setMessage("resolveHeader");
		setPicture("/icons/file-conflict-large.png");
	}


	@Override
	protected JButton initComponents() {

		// create the custom content for resolve conflict.
		JPanel customPanel = new JPanel(new MigLayout("wrap 3, ins 0, fill", "", "[]unrel[]rel[]"));

		JPanel hyp = new JPanel(new MigLayout("nogrid, ins 0, fill"));

		// create a hyperlink that opens the file in the explorer/nautilus/whatever
		JXHyperlink path = new JXHyperlink(new JakeAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiUtilities.selectFileInFileViewer(FileObjectHelper.getPath(fo.getAbsolutePath()));
			}
		});
		// surround with html to wrap text
		path.setText(fo.getAbsolutePath().toString());

		//TODO: this doesn't work?
		path.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		hyp.add(new JLabel(getResourceMap().getString("pathTitle")));
		hyp.add(path, "growy");
		this.add(hyp, "gapbottom 12");

		ICoreAccess core = JakeMainApp.getApp().getCore();

		// what are the differences?
		// editor, size, time


		// demermine the differences
		boolean localLarger = core.getLocalFileSize(fo) < core.getFileSize(fo);
		boolean localNewer = core.getLocalFileLastModified(fo).after(core.getFileLastModified(fo));

		JLabel localLabel = new JLabel("<html>" + getResourceMap().getString("localLabelBegin") + " " +
				  StringUtilities.boldIf(FileObjectHelper.getLocalSizeHR(fo), localLarger) + ", " +
				  StringUtilities.boldIf(FileObjectHelper.getLocalTime(fo) + " ("
							 + FileObjectHelper.getLocalTimeRel(fo) + ")", localNewer) + "</html>");
		JButton viewLocal = new JButton(getResourceMap().getString("openFileButton"));

		JLabel remoteLabel = new JLabel("<html>" + getResourceMap().getString("remoteLabelBegin") + " " + FileObjectHelper.getLastModifier(fo) + ": " +
				  StringUtilities.boldIf(FileObjectHelper.getSizeHR(fo), !localLarger) + ", " +
				  StringUtilities.boldIf(FileObjectHelper.getTime(fo) + " ("
							 + FileObjectHelper.getTimeRel(fo) + ")", !localNewer) + "</html>");
		JButton viewRemote = new JButton(getResourceMap().getString("openFileButton"));

		ActionListener updateResolveAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateResolveButton();
			}
		};

		useLocalRadioButton = new JRadioButton(getResourceMap().getString("resolveMyButton"));
		useLocalRadioButton.addActionListener(updateResolveAction);
		useRemoteRadioButton = new JRadioButton(getResourceMap().getString("resolveThemButton"));
		useRemoteRadioButton.addActionListener(updateResolveAction);

		ButtonGroup grp = new ButtonGroup();
		grp.add(useLocalRadioButton);
		grp.add(useRemoteRadioButton);

		// add local info
		customPanel.add(localLabel, "growy");
		customPanel.add(viewLocal, "");
		customPanel.add(useLocalRadioButton, "wrap");

		// add remote info
		customPanel.add(remoteLabel, "growy");
		customPanel.add(viewRemote, "");
		customPanel.add(useRemoteRadioButton, "wrap");

		this.add(customPanel, "");

		// add the buttons on bottom
		addCancelBtn();
		resolveBtn = new JButton();
		resolveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				resolveConflictAction();
			}
		});
		updateResolveButton();

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
		resolveBtn.setEnabled(isLocalSelected() || isRemoteSelected());
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
		dlg.showDialogSized(750, 280);
	}
}