package com.jakeapp.gui.swing.dialogs;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.actions.abstracts.JakeAction;
import com.jakeapp.gui.swing.dialogs.generic.JakeDialog;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.FileObjectHelper;
import com.jakeapp.gui.swing.helpers.StringUtilities;
import com.jakeapp.gui.swing.helpers.Translator;
import com.jakeapp.gui.swing.helpers.UserHelper;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.worker.AnnounceJakeObjectTask;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.worker.PullJakeObjectsTask;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXHyperlink;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Date;

/**
 * Resolve Conflict Dialog.
 */
public class ResolveConflictDialog extends JakeDialog {
	private static final Logger log = Logger.getLogger(ResolveConflictDialog.class);
	private Attributed<FileObject> fo;
	private JButton resolveBtn;
	private JRadioButton useLocalRadioButton;
	private JRadioButton useRemoteRadioButton;

	/**
	 * Private Constructor for ResolveConflictDialog.
	 * Use showDialog.
	 *
	 * @param fileObject
	 */
	private ResolveConflictDialog(FileObject fileObject) {
		super(fileObject.getProject());
		log.info("Opening ResolveConflictDialog on " + fileObject.getProject() + " with file: " + fileObject);

		// get attributed object
		this.fo = JakeMainApp.getCore().getAttributed(fileObject);

		// load the resource map
		setResourceMap(
						org.jdesktop.application.Application.getInstance(JakeMainApp.class)
										.getContext().getResourceMap(ResolveConflictDialog.class));

		initDialog();

		// set custom properties
		setDialogTitle(
						getResourceMap().getString("resolveTitle") + " " + FileObjectHelper
										.getName(fo.getJakeObject().getRelPath()));
		setMessage(Translator.get(getResourceMap(), "resolveHeader", fo.getLastVersionEditor().getUserId()));
		setPicture("/icons/file-conflict-large.png");
	}


	@Override
	protected JButton initComponents() {

		// create the custom content for resolve conflict. (with row gaps)
		JPanel customPanel = new JPanel(
						new MigLayout("wrap 2, ins 0, fill", "", "[]unrel[]rel[]"));

		JPanel hyp = new JPanel(new MigLayout("nogrid, ins 0, fill"));

		// create a hyperlink that opens the file in the explorer/nautilus/whatever
		JXHyperlink path = new JXHyperlink(new JakeAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileUtilities.selectFileInFileViewer(FileObjectHelper.getPath(
									JakeMainApp.getCore().getFile(fo.getJakeObject())));
				} catch (FileOperationFailedException ex) {
					ExceptionUtilities.showError(ex);
				}
			}
		});
		// surround with html to wrap text
		path.setText(fo.getJakeObject().getRelPath());

		//TODO: works on windows only?
		path.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		hyp.add(new JLabel(getResourceMap().getString("pathTitle")));
		hyp.add(path, "growy");
		this.add(hyp, "gapbottom 12");

		// what are the differences?
		// editor, size, time


		// demermine the differences
		long localSize = JakeMainApp.getCore().getLocalFileSize(fo.getJakeObject());
		long remoteSize = fo.getSize();
		Boolean localLarger = localSize < remoteSize;
		if(localSize == remoteSize) {
			localLarger = null;
		}

		Date localDate = JakeMainApp.getCore().getLocalFileLastModified(fo.getJakeObject());
		Date remoteDate = new Date(fo.getLastModificationDate());
		Boolean localNewer = localDate.after(remoteDate);
		if(localDate == remoteDate) {
			localNewer = null;
		}

		useLocalRadioButton = new JRadioButton("<html>" + getResourceMap()
						.getString("localLabelBegin") + " " + StringUtilities
						.boldIf(FileObjectHelper.getLocalSizeHR(fo.getJakeObject()),
										localLarger != null && localLarger) + ", " + StringUtilities
						.boldIf(FileObjectHelper.getLocalTimeRel(fo) + " (" + FileObjectHelper
										.getLocalTime(fo.getJakeObject()) + ")", localNewer != null && localNewer) + "</html>");
		JButton viewLocal = new JButton(getResourceMap().getString("openFileButton"));

		useRemoteRadioButton = new JRadioButton("<html><font color=red>" + FileObjectHelper
						.getLastModifier(fo) + "</font>" + getResourceMap()
						.getString("remoteLabelBegin") + " " + StringUtilities
						.boldIf(FileObjectHelper.getSizeHR(fo),
										 localLarger != null && !localLarger) + ", " + StringUtilities
						.boldIf(FileObjectHelper.getTimeRel(fo) + " (" + FileObjectHelper
										.getTime(fo) + ")", localNewer != null && !localNewer) + "</html>");
		JButton viewRemote = new JButton(getResourceMap().getString("openFileButton"));

		ActionListener updateResolveAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateResolveButton();
			}
		};

		useLocalRadioButton.addActionListener(updateResolveAction);
		useRemoteRadioButton.addActionListener(updateResolveAction);

		ButtonGroup grp = new ButtonGroup();
		grp.add(useLocalRadioButton);
		grp.add(useRemoteRadioButton);

		// add local info
		customPanel.add(useLocalRadioButton, "growy");
		customPanel.add(viewLocal, "right, wrap");

		// add remote info
		customPanel.add(useRemoteRadioButton, "growy");
		customPanel.add(viewRemote, "right, wrap");

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
		return Translator.get(getResourceMap(), "resolveThemButton",
						UserHelper.getNickOrFullName(fo.getLastVersionLogEntry().getMember()));
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
	log.debug("Resolving conflict action.");
		
		// if local file is selected, we have to announce that.
		if (isLocalSelected()) {
			try {

				JakeExecutor.exec(new AnnounceJakeObjectTask(Arrays.asList((JakeObject)fo.getJakeObject()),
								"Resolved Conflict with " + fo.getLastVersionEditor().getUserId()));
				//}// catch (SyncException e) {
				//log.error(e);
				//ExceptionUtilities.showError(e);
			} catch (FrontendNotLoggedInException e) {
				log.error(e);
				ExceptionUtilities.showError(e);
			}
		} else {

			// remote file must have been selected.
			// so pull the file from remote (overwrites our file)
			try {
				JakeExecutor.exec(new PullJakeObjectsTask(Arrays.asList((JakeObject)fo.getJakeObject())));
			} catch (FrontendNotLoggedInException e) {
				log.error(e);
				ExceptionUtilities.showError(e);
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
	public static void showDialog(FileObject fo) {
		ResolveConflictDialog dlg = new ResolveConflictDialog(fo);
		dlg.showDialogSized(500, 280);
	}
}