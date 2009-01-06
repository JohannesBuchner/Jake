package com.jakeapp.gui.swing.dialogs;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.dialogs.generic.JakeDialog;
import com.jakeapp.gui.swing.models.InvitePeopleComboBoxModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;
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
public class InvitePeopleDialog extends JakeDialog {
	private Project project;
	private JComboBox peopleComboBox;

	public InvitePeopleDialog(Project project, Frame frame) {
		super(frame);
		this.project = project;

		setResourceMap(org.jdesktop.application.Application.getInstance(
				  com.jakeapp.gui.swing.JakeMainApp.class).getContext()
				  .getResourceMap(InvitePeopleDialog.class));

		setTitle(getResourceMap().getString("inviteTitle"));
		initComponents();
	}

	private void initComponents() {
		this.setLayout(new MigLayout("wrap 2, insets dialog, fill"));

		JLabel picture = new JLabel();

		picture.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				  getClass().getResource("/icons/user-large.png"))));
		this.add(picture, "dock west, gap 10 10");

		JLabel explanation = new JLabel(getResourceMap().getString("inviteHeader"));
		this.add(explanation, "span 2, gapbottom 15");

		//JLabel addUser = new JLabel("XMPP Id:");
		//this.add(addUser, "");

		// generate a auto-completion combobox
		peopleComboBox = new JComboBox();
		peopleComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				invitePeopleAction();
			}
		});
		peopleComboBox.setEditable(true);
		peopleComboBox.setModel(new InvitePeopleComboBoxModel());
		AutoCompleteDecorator.decorate(peopleComboBox);
		this.add(peopleComboBox, "span 2, growx");

		JPanel buttons = new JPanel(new MigLayout("nogrid, fill, ins 0"));

		JButton closeBtn = new JButton(getResourceMap().getString("closeButton"));
		closeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				setVisible(false);
			}
		});
		buttons.add(closeBtn, "tag cancel, aligny bottom");

		JButton inviteBtn = new JButton(getResourceMap().getString("inviteButton"));
		inviteBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				invitePeopleAction();
			}
		});
		buttons.add(inviteBtn, "tag ok, aligny bottom");

		this.add(buttons, "span 2, grow");

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.getRootPane().setDefaultButton(inviteBtn);
	}

	/**
	 * Reads the comboBox and sends the invites to the core.
	 */
	private void invitePeopleAction() {
		JakeMainApp.getApp().getCore().invitePeople(project, peopleComboBox.getSelectedItem().toString());
		this.setVisible(false);
	}

	/**
	 * Shows the Dialog. Static, configures modality and size, shows dialog.
	 *
	 * @param project: project where people will be added.
	 * @param frame:   parent frame
	 */
	public static void showDialog(Project project, JFrame frame) {
		InvitePeopleDialog ipd = new InvitePeopleDialog(project, frame);
		ipd.setSize(500, 190);
		/*
		ipd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		ipd.setResizable(false);
		ipd.setLocationRelativeTo(JakeMainView.getMainView().getFrame());
		*/
		ipd.setVisible(true);
	}
}
