package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.synchronization.JakeObjectSyncStatus;
import com.jakeapp.gui.swing.JakeMainApp;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class FileObjectStatusProvider {
	private static final Logger log = Logger.getLogger(FileObjectStatusProvider.class);

	private static Icon locked = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  FileObjectStatusProvider.class.getResource("/locked/locked.png")));

	private static Icon unlocked = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  FileObjectStatusProvider.class.getResource("/locked/unlocked.png")));

	private static Icon local_is_up_to_date = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  FileObjectStatusProvider.class.getResource("/status/local_is_up_to_date.png")));

	private static Icon local_is_modified = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  FileObjectStatusProvider.class.getResource("/status/local_is_modified.png")));

	private static Icon local_is_out_of_date = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  FileObjectStatusProvider.class.getResource("/status/local_is_out_of_date.png")));

	private static Icon local_has_conflict = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  FileObjectStatusProvider.class.getResource("/status/local_has_conflict.png")));

	private static JLabel getLabelComponent() {
		JLabel result = new JLabel();
		result.setOpaque(true);
		result.setBackground(new Color(0, 0, 0, 0));
		result.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		return result;
	}

	public static Component getStatusRendererComponent(FileObject obj) {
		JLabel label = getLabelComponent();

		JakeObjectSyncStatus status = JakeMainApp.getCore().getJakeObjectSyncStatus(JakeMainApp.getProject(), obj);

		if (status == null) {
			log.warn("Got NULL for sync status of: " + obj);
		} else {

			if (status.isOnlyLocal() || status.isOnlyRemote()) {
				label.setText("");
			} else if (status.isLocalLatest()) {
				label.setIcon(local_is_up_to_date);
			} else if (status.isInConflict()) {
				label.setIcon(local_has_conflict);
			} else if (status.isLocallyModified()) {
				label.setIcon(local_is_modified);
			} else if (status.isRemotelyModified()) {
				label.setIcon(local_is_out_of_date);
			}
		}

		return label;
	}

	public static Component getLockedRendererComponent(FileObject obj) {
		JLabel label = getLabelComponent();
		if (JakeMainApp.getCore().isJakeObjectLocked(obj)) {
			label.setIcon(locked);
		} else {
			label.setIcon(unlocked);
		}
		return label;
	}

	public static Component getEmptyComponent() {
		JLabel label = getLabelComponent();
		label.setText("");
		return label;
	}
}
