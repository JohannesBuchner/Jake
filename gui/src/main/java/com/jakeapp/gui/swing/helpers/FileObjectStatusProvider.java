package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeContext;
import com.jakeapp.gui.swing.controls.SpinningDial;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * This is the FileObject Status Provider
 * <p/>
 * Following Icons display the File State:
 * <p/>
 * LOCAL_LATEST
 * LOCAL_HAS_CONFLICT
 * LOCAL_MODIFIED
 * LOCAL_OUT_OF_DATE
 * <p/>
 * (LOCKED/UNLOCKED) :extra icons
 */
public class FileObjectStatusProvider {
	private static final Logger log = Logger.getLogger(FileObjectStatusProvider.class);

	private static Icon locked = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
					FileObjectStatusProvider.class.getResource("/locked/locked.png")));

	private static Icon unlocked = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
					FileObjectStatusProvider.class.getResource("/locked/unlocked.png")));

	private static Icon local_is_up_to_date = new ImageIcon(Toolkit
					.getDefaultToolkit().getImage(FileObjectStatusProvider.class.getResource(
					"/status/local_is_up_to_date.png")));

	private static Icon local_is_modified = new ImageIcon(Toolkit
					.getDefaultToolkit().getImage(FileObjectStatusProvider.class.getResource(
					"/status/local_is_modified.png")));

	private static Icon local_is_out_of_date = new ImageIcon(Toolkit
					.getDefaultToolkit().getImage(FileObjectStatusProvider.class.getResource(
					"/status/local_is_out_of_date.png")));

	private static Icon local_has_conflict = new ImageIcon(Toolkit
					.getDefaultToolkit().getImage(FileObjectStatusProvider.class.getResource(
					"/status/local_has_conflict.png")));

	private static Icon spinner = new SpinningDial(16, 16);

	private static JLabel getLabelComponent() {
		JLabel result = new JLabel();
		result.setOpaque(true);
		result.setBackground(new Color(0, 0, 0, 0));
		result.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		return result;
	}

	public static Component getStatusRendererComponent(FileObject obj) {
		JLabel label = getLabelComponent();
		Icon icon = getStatusIcon(obj);
		label.setIcon(icon);
		return label;
	}

	public static Icon getStatusIcon(FileObject obj) {
		Attributed<FileObject> status =
						JakeMainApp.getCore().getAttributed(JakeContext.getProject(), obj);

		// hack
		if(false)
			return spinner;

		if (status == null) {
			log.warn("Got NULL for sync status of: " + obj);
		} else {

			if (status.isOnlyLocal() || status.isOnlyRemote()) {
				return null;
			} else if (status.isLocalLatest()) {
				return local_is_up_to_date;
			} else if (status.isInConflict()) {
				return local_has_conflict;
			} else if (status.isModifiedLocally()) {
				return local_is_modified;
			} else if (status.isModifiedRemote()) {
				return local_is_out_of_date;
			}
		}
	return null;
	}

	public static Component getLockedRendererComponent(FileObject obj) {
		JLabel label = getLabelComponent();
		Attributed<FileObject> fo =
						JakeMainApp.getCore().getAttributed(obj.getProject(), obj);
		label.setIcon(fo.isLocked() ? locked : unlocked);
		return label;
	}

	public static Component getEmptyComponent() {
		JLabel label = getLabelComponent();
		label.setText("");
		return label;
	}
}