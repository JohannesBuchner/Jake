package com.doublesignal.sepm.jake.gui;

import com.doublesignal.sepm.jake.core.domain.JakeMessage;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.IJakeMessageReceiveListener;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the StatusPanel (root of main gui window) Shows status messages +
 * event buttons.
 * 
 * @author studpete
 * 
 */
@SuppressWarnings("serial")
public class StatusPanel extends JPanel implements IJakeMessageReceiveListener {
	
	private static final Logger log = Logger.getLogger(StatusPanel.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();

	private final JakeGui gui;
	private final IJakeGuiAccess jakeGuiAccess;
	private List<JakeMessage> unreadMessages = new ArrayList<JakeMessage>();

	public StatusPanel(JakeGui gui) {
		log.info("Initializing StatusPanel.");
		this.gui = gui;
		this.gui.addLoginStatusListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateData();
			}
		});
		this.jakeGuiAccess = gui.getJakeGuiAccess();
		this.jakeGuiAccess.registerReceiveMessageListener(this);

		initComponents();
		updateData();
	}

	/**
	 * Set a status message for 5 seconds.
	 * 
	 * @param msg
	 */
	public void setStatusMsg(String msg) {
		this.statusLabel.setText(msg);
		cleanStatusTimer.start();
	}

	/**
	 * Clears the current status message.
	 */
	public void clearStatusMsg() {
		this.statusLabel.setText("");
		cleanStatusTimer.stop();
	}

	/**
	 * Updates the whole status bar
	 */
	private void updateData() {
		int messagesReceived = unreadMessages.size();
		int filesConflict = 0;
		boolean connectionOnline = jakeGuiAccess.isLoggedIn();

		if (messagesReceived > 0) {
			if (messagesReceived == 1)
				messageReceivedStatusButton.setText("1 Message received");
			else
				messageReceivedStatusButton.setText(messagesReceived
						+ " Messages received");

			messageReceivedStatusButton.setVisible(true);
		} else {
			messageReceivedStatusButton.setVisible(false);
		}

		if (filesConflict > 0) {
			if (filesConflict == 1)
				fileConflictStatusButton.setText("File Conflict");
			else
				fileConflictStatusButton.setText(filesConflict
						+ " File Conflicts");

			fileConflictStatusButton.setVisible(true);
		} else {
			fileConflictStatusButton.setVisible(false);
		}

		if (connectionOnline) {
			connectionStatusButton.setText("Connected");
			connectionStatusButton.setToolTipText("Connected as "
				+ jakeGuiAccess.getLoginUserid());
		} else {
			connectionStatusButton.setText("Not Connected");
			connectionStatusButton.setToolTipText("Press to connect");
		}
	}

	javax.swing.Timer cleanStatusTimer = new javax.swing.Timer(5000,
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					clearStatusMsg();
				}
			});

	/**
	 * ** Status Bar Buttons ****
	 */
	private void messageReceivedStatusButtonActionPerformed(ActionEvent e) {
		for (JakeMessage jm: unreadMessages) {
			new ReceiveMessageDialog(gui.getMainFrame(), jm).setVisible(true);
		}
		unreadMessages.clear();
		updateData();
	}

	private void fileConflictStatusButtonActionPerformed(ActionEvent e) {
		//new ResolveConflictDialog(gui.getMainFrame()).setVisible(true);
	}

	private void connectionStatusButtonActionPerformed(ActionEvent e) {
		if (jakeGuiAccess.isLoggedIn()) {
			gui.signOutNetwork();
		} else {
			gui.signInNetwork(null, null, true);
		}
	}

	void initComponents() {
		
		statusLabel = new JLabel();
		statusButtonsPanel = new JPanel();
		messageReceivedStatusButton = new JButton();
		fileConflictStatusButton = new JButton();
		connectionStatusButton = new JButton();

		this.setLayout(new BorderLayout(2, 2));

		// ---- statusLabel ----
		statusLabel.setText("");
		this.add(statusLabel, BorderLayout.WEST);

		statusButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// ---- messageReceivedStatusButton ----
		messageReceivedStatusButton.setText("");
		messageReceivedStatusButton.setIcon(new ImageIcon(getClass()
				.getResource("/icons/message.png")));
		messageReceivedStatusButton.setBorder(new SoftBevelBorder(
				SoftBevelBorder.RAISED));
		messageReceivedStatusButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				messageReceivedStatusButtonActionPerformed(e);
			}
		});
		messageReceivedStatusButton.setVisible(false);
		statusButtonsPanel.add(messageReceivedStatusButton);

		// ---- fileConflictStatusButton ----
		fileConflictStatusButton.setText("");
		fileConflictStatusButton.setIcon(new ImageIcon(getClass().getResource(
				"/icons/warning.png")));
		fileConflictStatusButton.setBorder(new SoftBevelBorder(
				SoftBevelBorder.RAISED));
		fileConflictStatusButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileConflictStatusButtonActionPerformed(e);
			}
		});
		fileConflictStatusButton.setVisible(false);
		statusButtonsPanel.add(fileConflictStatusButton);

		// ---- connectionStatusButton ----
		connectionStatusButton.setText("");
		connectionStatusButton.setIcon(new ImageIcon(getClass().getResource(
				"/icons/network-idle.png")));
		connectionStatusButton.setBorder(new SoftBevelBorder(
				SoftBevelBorder.RAISED));
		connectionStatusButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectionStatusButtonActionPerformed(e);
			}
		});
		statusButtonsPanel.add(connectionStatusButton);

		this.add(statusButtonsPanel, BorderLayout.EAST);
	}

	private JLabel statusLabel;
	private JPanel statusButtonsPanel;
	private JButton messageReceivedStatusButton;
	private JButton fileConflictStatusButton;
	private JButton connectionStatusButton;

	public void receivedJakeMessage(JakeMessage message) {
		unreadMessages.add(message);
		updateData();
	}
}
