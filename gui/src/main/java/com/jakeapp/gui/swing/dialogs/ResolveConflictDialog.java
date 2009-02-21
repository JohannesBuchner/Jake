package com.jakeapp.gui.swing.dialogs;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.actions.abstracts.JakeAction;
import com.jakeapp.gui.swing.dialogs.generic.JakeDialog;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.*;
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
		setDialogTitle(getResourceMap().getString("resolveTitle") + " " + FileObjectHelper.getName(fo.getRelPath()));
		setMessage("resolveHeader");
		setPicture("/icons/file-conflict-large.png");
	}


	@Override
	protected JButton initComponents() {

		// create the custom content for resolve conflict. (with row gaps)
		JPanel customPanel = new JPanel(new MigLayout("wrap 3, ins 0, fill", "", "[]unrel[]rel[]"));

		JPanel hyp = new JPanel(new MigLayout("nogrid, ins 0, fill"));

		// create a hyperlink that opens the file in the explorer/nautilus/whatever
		JXHyperlink path = new JXHyperlink(new JakeAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GuiUtilities.selectFileInFileViewer(FileObjectHelper.getPath(JakeMainApp.getCore().getFile(fo)));
				} catch (FileOperationFailedException ex) {
					ExceptionUtilities.showError(ex);
				}
			}
		});
		// surround with html to wrap text
		path.setText(FileUtilities.getAbsPath(fo));

		//TODO: works on windows only?
		path.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		hyp.add(new JLabel(getResourceMap().getString("pathTitle")));
		hyp.add(path, "growy");
		this.add(hyp, "gapbottom 12");

		ICoreAccess core = JakeMainApp.getCore();

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

		JLabel remoteLabel = new JLabel("<html><font color=red>" + FileObjectHelper.getLastModifier(fo) + "</font>" + getResourceMap().getString("remoteLabelBegin") + " " +
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
		useRemoteRadioButton = new JRadioButton(getUseRemoteFileString());
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
			btnStr = getResourceMap().getString("resolveMyButton");
		} else if (isRemoteSelected()) {
			btnStr = getUseRemoteFileString();
		} else {
			// nothing selected
			btnStr = getResourceMap().getString("resolveSelectOption");
		}

		resolveBtn.setText(btnStr);
		resolveBtn.setEnabled(isLocalSelected() || isRemoteSelected());
	}

	private String getUseRemoteFileString() {
		try {
			return Translator.get(getResourceMap(), "resolveThemButton",
					  UserHelper.getNickOrFullName(JakeMainApp.getCore().getLastModifier(fo)));
		} catch (NoSuchLogEntryException e) {
			log.error(e);
			ExceptionUtilities.showError(e);
			return "";
		}
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

		// if local file is selected, we have to announce that.
		if (isLocalSelected()) {
			try {
				JakeMainApp.getCore().announceJakeObject(fo, null);
				//}// catch (SyncException e) {
				//log.error(e);
				//ExceptionUtilities.showError(e);
			} catch (FrontendNotLoggedInException e) {
				log.error(e);
				ExceptionUtilities.showError(e);
			} catch (FileOperationFailedException e) {
				ExceptionUtilities.showError(e);
			}
		} else {

			// remote file must have been selected.
			// so pull the file from remote (overwrites our file)
			try {
				JakeMainApp.getCore().pullJakeObject(fo);
			} catch (FrontendNotLoggedInException e) {
				log.error(e);
				ExceptionUtilities.showError(e);
			} catch (FileOperationFailedException e) {
				e.printStackTrace();
			}
		}

		// in any case - close the dialog
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
		dlg.showDialogSized(700, 280);
	}
}