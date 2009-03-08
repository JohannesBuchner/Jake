package com.jakeapp.gui.swing.dialogs;

import com.jakeapp.core.domain.Account;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.dialogs.generic.JakeDialog;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
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
	private static Account creds;
	private JTextField serverText;
	private JTextField portText;

	public AdvancedAccountSettingsDialog(Account creds) {
		super(JakeContext.getProject());
		setCreds(creds);

		setResourceMap(org.jdesktop.application.Application.getInstance(
				  com.jakeapp.gui.swing.JakeMainApp.class).getContext()
				  .getResourceMap(AdvancedAccountSettingsDialog.class));

		initDialog();

		// set custom properties
		setDialogTitle(getResourceMap().getString("advTitle"));
		setMessage("advHeader");
		// use default picture

		// load settings from credientals
		loadSettings();
	}

	@Override
	protected JButton initComponents() {

		JPanel settingsPanel = new JPanel(new MigLayout("wrap 2, fill"));
		settingsPanel.setOpaque(true);

		JLabel serverLabel = new JLabel(getResourceMap().getString("connectServer"));
		settingsPanel.add(serverLabel, "right");

		serverText = new JTextField();
		settingsPanel.add(serverText, "w 200!");

		JLabel portLabel = new JLabel(getResourceMap().getString("port"));
		settingsPanel.add(portLabel, "right");

		portText = new JTextField();
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

	private void loadSettings() {
		serverText.setText(getCreds().getServerAddress());
		portText.setText(getCreds().getServerPort() + "");
	}

	private void saveSettingsAction() {
		log.info("saving advanded settings");
		boolean success = false;

		try {
			getCreds().setServerAddress(serverText.getText());
			getCreds().setServerPort(Long.parseLong(portText.getText()));
			success = true;
		}
		// catch all (conversion errors for port, for example)
		catch (Exception e) {
			ExceptionUtilities.showError(e);
		} finally {
			if (success) {
				this.setVisible(false);
			}
		}
	}

	/**
	 * Shows the Dialog. Static, configures modality and size, shows dialog.
	 *
	 * @param creds: ServiceCredentials
	 */
	public static void showDialog(Account creds) {
		AdvancedAccountSettingsDialog dlg = new AdvancedAccountSettingsDialog(creds);
		dlg.showDialogSized(400, 255);
	}

	public static Account getCreds() {
		return creds;
	}

	public static void setCreds(Account creds) {
		AdvancedAccountSettingsDialog.creds = creds;
	}
}
