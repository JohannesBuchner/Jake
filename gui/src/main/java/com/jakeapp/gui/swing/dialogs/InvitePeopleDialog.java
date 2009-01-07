package com.jakeapp.gui.swing.dialogs;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.dialogs.generic.JakeDialog;
import com.jakeapp.gui.swing.helpers.JakeObjectHelpers;
import com.jakeapp.gui.swing.models.InvitePeopleComboBoxModel;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

import javax.swing.*;
import java.awt.*;
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
// TODO: enable guess list; filter already added
public class InvitePeopleDialog extends JakeDialog {
	private static final Logger log = Logger.getLogger(InvitePeopleDialog.class);
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

		//if (Platform.showAsSheet()) {
		JLabel title = new JLabel(getTitle());
		title.setFont(title.getFont().deriveFont(Font.BOLD, 14));
		this.add(title, "span 2, gapbottom 10");
		//}


		JLabel picture = new JLabel();

		picture.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				  getClass().getResource("/icons/user-large.png"))));
		this.add(picture, "dock west, gap 10 10");

		JLabel explanation = new JLabel(getResourceMap().getString("inviteHeader"));
		this.add(explanation, "span 2, gapbottom 15");

		//JLabel addUser = new JLabel("XMPP Id:");
		//this.add(addUser, "");

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
		final ObjectToStringConverter conv = new ObjectToStringConverter() {
			@Override
			public String getPreferredStringForItem(Object o) {
				if (ProjectMember.class.isInstance(o)) {
					ProjectMember member = (ProjectMember) o;

					return member.getUserId().getUserId() + " (" + JakeObjectHelpers.getNickOrFullName(member, 30) + ")";
				} else {
					return o.toString();
				}
			}
		};

		peopleComboBox.setModel(new InvitePeopleComboBoxModel(project));

		AutoCompleteDecorator.decorate(peopleComboBox);

		//peopleComboBox.setEditor(new AutoCompleteComboBoxEditor(peopleComboBox.getEditor(), conv));
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
		ipd.setResizable(true);
		ipd.setVisible(true);
	}
}
