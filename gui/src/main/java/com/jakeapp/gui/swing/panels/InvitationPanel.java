package com.jakeapp.gui.swing.panels;

import com.jakeapp.gui.swing.JakeContext;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.JoinProjectAction;
import com.jakeapp.gui.swing.actions.RejectProjectAction;
import com.jakeapp.gui.swing.callbacks.ContextChanged;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.Platform;
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

	public InvitationPanel() {
		EventCore.get().addContextChangedListener(this);

		initComponents();
	}


	private void initComponents() {
		// set the background painter
		this.setBackgroundPainter(Platform
						.getStyler().getContentPanelBackgroundPainter());

		MigLayout layout = new MigLayout("wrap 1, fillx");
		this.setLayout(layout);

		JLabel title = new JLabel("You have been invited to a new project.");
		title.setFont(Platform.getStyler().getH1Font());

		this.add(title, "span 1, al center, wrap");

		projectNameLabel = new JLabel();
		projectNameLabel.setFont(projectNameLabel.getFont().deriveFont(Font.BOLD));
		this.add(projectNameLabel, "span 1 ,al center, wrap");

		JLabel icon = new JLabel();
		icon.setIcon(new ImageIcon(Toolkit
						.getDefaultToolkit().getImage(getClass().getResource(
						"/icons/folder-new-large.png"))));

		this.add(icon, "span 1, al center, wrap");

		userNameLabel = new JLabel();
		userNameLabel.setFont(userNameLabel.getFont().deriveFont(Font.BOLD));
		this.add(userNameLabel, "span 1 ,al center, wrap");

		JPanel folderSelectPanel = new JPanel(new MigLayout("nogrid, fillx"));
		folderSelectPanel.setOpaque(false);

		folderTextField =	new JTextField();
		generateProjectDefaultLocation();

		folderTextField.setEditable(false);
		folderSelectPanel.add(folderTextField);

		JButton folderChooserButton = new JButton("...");
		folderChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				String folder = FileUtilities.openDirectoryChooser(null, null);
				if (folder != null) {
					folderTextField.setText(folder);
					joinProjectAction.setProjectLocation(folder);
				}
			}
		});
		folderSelectPanel.add(folderChooserButton, "");

		this.add(folderSelectPanel, "span 2, al center, wrap");

		JPanel btnPanel = new JPanel(new MigLayout("nogrid"));
		btnPanel.setOpaque(false);

		JButton joinButton = new JButton("Join");
		joinProjectAction = new JoinProjectAction();
		joinButton.setAction(joinProjectAction);

		JButton rejectButton = new JButton("Reject");
		rejectButton.setAction(new RejectProjectAction());

		btnPanel.add(joinButton, "tag ok");
		btnPanel.add(rejectButton, "tag cancel");

		this.add(btnPanel, "al center");
	}

	private void generateProjectDefaultLocation() {
		folderTextField.setText(FileUtilities.getDefaultProjectLocation(JakeContext.getInvitation()));
	}

	private void updatePanel() {
		if (JakeContext.getInvitation() != null) {
			projectNameLabel.setText(JakeContext.getInvitation().getProjectName());

			// TODO: is this user id the id from the inviter?
			// TODO: enable when this works without mock!
			String userId = "<needs real impl>"; //getProject().getUser().toString();
			userNameLabel.setText(JakeMainView.getMainView().getResourceMap()
							.getString("projectInvitedFrom") + " " + userId);
		}
	}

	@Override public void contextChanged(EnumSet<Reason> reason, Object context) {
		if (reason.contains(Reason.Invitation)) {
			updatePanel();
			generateProjectDefaultLocation();
		}
	}
}