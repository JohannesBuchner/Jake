package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
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

	private static Icon locked =
					ImageLoader.get(FileObjectStatusProvider.class, "/locked/locked.png");

	private static Icon unlocked =
					ImageLoader.get(FileObjectStatusProvider.class, "/locked/unlocked.png");

	private static Icon local_is_up_to_date = ImageLoader
					.get(FileObjectStatusProvider.class, "/status/local_is_up_to_date.png");

	private static Icon local_is_modified = ImageLoader
					.get(FileObjectStatusProvider.class, "/status/local_is_modified.png");

	private static Icon local_is_out_of_date = ImageLoader
					.get(FileObjectStatusProvider.class, "/status/local_is_out_of_date.png");

	private static Icon local_has_conflict = ImageLoader
					.get(FileObjectStatusProvider.class, "/status/local_has_conflict.png");

	private static Icon remote_only = ImageLoader
					.get(FileObjectStatusProvider.class, "/status/remote_only.png");

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

		Attributed<FileObject> aFo =
						JakeMainApp.getCore().getAttributed(obj);

		Icon icon = getStatusIcon(aFo);
		label.setIcon(icon);

		// set tooltip!
		label.setToolTipText(aFo.getSyncStatus().toString());

		return label;
	}

	public static Icon getStatusIcon(Attributed<FileObject> aFo) {
		// hack
		if (false)
			return spinner;

		if (aFo == null) {
			log.warn("Got NULL for sync aFo of: " + aFo);
		} else {

			if (aFo.isOnlyLocal()) {
				return null;
			} else if (aFo.isOnlyRemote()) {
				return remote_only;
			} else if (aFo.isLocalLatest()) {
				return local_is_up_to_date;
			} else if (aFo.isInConflict()) {
				return local_has_conflict;
			} else if (aFo.isModifiedLocally()) {
				return local_is_modified;
			} else if (aFo.isModifiedRemote()) {
				return local_is_out_of_date;
			}
		}
		return null;
	}

	public static Component getLockedRendererComponent(FileObject obj) {
		JLabel label = getLabelComponent();
		Attributed<FileObject> fo =
						JakeMainApp.getCore().getAttributed(obj);
		label.setIcon(fo.isLocked() ? locked : unlocked);
		return label;
	}

	public static Component getEmptyComponent() {
		JLabel label = getLabelComponent();
		label.setText("");
		return label;
	}
}