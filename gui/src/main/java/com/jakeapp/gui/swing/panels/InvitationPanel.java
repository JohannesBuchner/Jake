package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.Invitation;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.project.JoinProjectAction;
import com.jakeapp.gui.swing.actions.project.RejectProjectAction;
import com.jakeapp.gui.swing.callbacks.ContextChanged;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import com.jakeapp.gui.swing.helpers.UserHelper;
import com.jakeapp.gui.swing.xcore.EventCore;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;

/**
 * The Project Invitation Panel.
 * A Unjoined project is displayed here, the user can join or reject.
 */
public class InvitationPanel extends JXPanel implements ContextChanged {
	private JTextField folderTextField;
	private JLabel projectNameLabel;
	private JLabel userNameLabel;
	private JoinProjectAction joinProjectAction;
	private JLabel generateNewFolderLabel;

	public InvitationPanel() {
		EventCore.get().addContextChangedListener(this);

		initComponents();
	}


	private void initComponents() {
		// set the background painter
		this.setBackgroundPainter(
						Platform.getStyler().getContentPanelBackgroundPainter());

		MigLayout layout = new MigLayout("wrap 1, fillx");
		this.setLayout(layout);

		JLabel title = new JLabel("You have been invited to a new project!");
		title.setFont(Platform.getStyler().getH1Font());

		this.add(title, "span 1, al center, wrap");

		projectNameLabel = new JLabel();
		projectNameLabel.setFont(Platform.getStyler().getH2Font());
		this.add(projectNameLabel, "span 1 ,al center, wrap");

		JLabel icon = new JLabel();
		icon.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/icons/folder-new-large.png"))));

		this.add(icon, "span 1, al center, wrap");

		userNameLabel = new JLabel();
		userNameLabel.setFont(userNameLabel.getFont().deriveFont(Font.BOLD));
		this.add(userNameLabel, "span 1, al center, wrap");

		JPanel folderSelectPanel = new JPanel(new MigLayout("nogrid, fillx"));
		folderSelectPanel.setOpaque(false);

		folderTextField = new JTextField();
		generateProjectDefaultLocation();

		folderTextField.setEditable(false);
		folderSelectPanel.add(folderTextField, "wmin 400");

		JButton folderChooserButton = new JButton("...");
		folderChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				String folder = FileUtilities.openDirectoryChooser(null, null);
				if (folder != null) {
					folderTextField.setText(folder);
					joinProjectAction.setProjectLocation(folder);
					updatePanel();
				}
			}
		});
		folderSelectPanel.add(folderChooserButton, "");

		// fixme: make larger!?
		this.add(folderSelectPanel, "span 2, al center, wrap");

		generateNewFolderLabel = new JLabel("We'll create a new folder for you.");
		this.add(generateNewFolderLabel, "al center");

		JPanel btnPanel = new JPanel(new MigLayout("nogrid"));
		btnPanel.setOpaque(false);

		JButton joinButton = new JButton("Join");
		joinButton.putClientProperty("JButton.buttonType", "textured");
		joinProjectAction = new JoinProjectAction();
		joinButton.setAction(joinProjectAction);

		JButton rejectButton = new JButton("Reject");
		rejectButton.putClientProperty("JButton.buttonType", "textured");
		rejectButton.setAction(new RejectProjectAction());

		btnPanel.add(joinButton, "tag ok");
		btnPanel.add(rejectButton, "tag cancel");

		this.add(btnPanel, "al center");
	}

	private void generateProjectDefaultLocation() {
		folderTextField.setText(
						FileUtilities.getDefaultProjectLocation(JakeContext.getInvitation()));
	}

	private void updatePanel() {
		Invitation invite = JakeContext.getInvitation();
		if (invite != null) {
			projectNameLabel.setText("> " + invite.getProjectName() + " <");

			String userId = UserHelper.cleanUserId(invite.getInviter().getUserId());
			userNameLabel.setText(String.format("%s %s, %s",
							JakeMainView.getMainView().getResourceMap().getString(
											"projectInvitedBy"), userId,
							TimeUtilities.getRelativeTime(invite.getCreation())));

			boolean createNewFolder =
							!FileUtilities.checkDirectoryExistence(folderTextField.getText());
			generateNewFolderLabel.setVisible(createNewFolder);
		}
	}

	@Override public void contextChanged(EnumSet<Reason> reason, Object context) {
		if (reason.contains(Reason.Invitation)) {
			updatePanel();
			generateProjectDefaultLocation();
		}
	}
}