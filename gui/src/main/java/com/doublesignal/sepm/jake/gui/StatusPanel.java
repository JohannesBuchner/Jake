package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXLoginDialog;

import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;

/**
 * This is the StatusPanel (root of main gui window) Shows status messages +
 * event buttons.
 * 
 * @author studpete
 * 
 */
@SuppressWarnings("serial")
public class StatusPanel extends JPanel {
	private static Logger log = Logger.getLogger(StatusPanel.class);
	private final JakeGui gui;
	private final IJakeGuiAccess jakeGuiAccess;

	public StatusPanel(JakeGui gui) {
		log.info("Initializing StatusPanel.");
		this.gui = gui;
		this.jakeGuiAccess = gui.getJakeGuiAccess();

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
		int messagesReceived = 0;
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
		new ReceiveMessageDialog(gui.getMainFrame()).setVisible(true);
	}

	private void fileConflictStatusButtonActionPerformed(ActionEvent e) {
		//new ResolveConflictDialog(gui.getMainFrame()).setVisible(true);
	}

	private void connectionStatusButtonActionPerformed(ActionEvent e) {
		new JXLoginDialog().setVisible(true);
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
}
