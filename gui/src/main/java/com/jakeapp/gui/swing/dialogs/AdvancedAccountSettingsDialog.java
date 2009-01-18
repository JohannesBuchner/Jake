package com.jakeapp.gui.swing.dialogs;

import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.dialogs.generic.JakeDialog;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Advanced Jabber Settings
 *
 * @author: studpete
 */
public class AdvancedAccountSettingsDialog extends JakeDialog {
	private static final Logger log = Logger.getLogger(AdvancedAccountSettingsDialog.class);
	private static ServiceCredentials creds;

	public AdvancedAccountSettingsDialog(ServiceCredentials creds) {
		super(JakeMainApp.getProject());
		this.creds = creds;

		setResourceMap(org.jdesktop.application.Application.getInstance(
				  com.jakeapp.gui.swing.JakeMainApp.class).getContext()
				  .getResourceMap(AdvancedAccountSettingsDialog.class));

		initDialog();

		// set custom properties
		setDialogTitle(getResourceMap().getString("advTitle"));
		setMessage("advHeader");
		// use default picture
	}

	@Override
	protected JButton initComponents() {

		JPanel settingsPanel = new JPanel(new MigLayout("wrap 2, fill"));
		settingsPanel.setOpaque(true);

		JLabel serverLabel = new JLabel("Connect Server:");
		settingsPanel.add(serverLabel);

		JTextField serverText = new JTextField();
		settingsPanel.add(serverText, "w 200!");

		JLabel portLabel = new JLabel("Port:");
		settingsPanel.add(portLabel);

		JTextField portText = new JTextField();
		settingsPanel.add(portText, "w 200!");

		this.add(settingsPanel, "grow");

		// create buttons
		this.addCancelBtn();
		JButton deleteBtn = new JButton(getResourceMap().getString("advButton"));
		deleteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				saveSettingsAction();
			}
		});
		return deleteBtn;
	}

	private void saveSettingsAction() {
		log.info("save advanded settings");
	}

	/**
	 * Shows the Dialog. Static, configures modality and size, shows dialog.
	 *
	 * @param creds: ServiceCredentials
	 */
	public static void showDialog(ServiceCredentials creds) {
		AdvancedAccountSettingsDialog dlg = new AdvancedAccountSettingsDialog(creds);
		dlg.showDialogSized(400, 270);
	}
}
