package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;

import javax.swing.*;
import java.awt.*;

public class FileObjectStatusProvider {
	private static Icon locked = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
		 FileObjectStatusProvider.class.getResource("/locked/locked.png")));

	private static Icon unlocked = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
		 FileObjectStatusProvider.class.getResource("/locked/unlocked.png")));

	private static Icon local_is_up_to_date = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
		 FileObjectStatusProvider.class.getResource("/status/local_is_up_to_date.png")));

	private static JLabel getLabelComponent() {
		JLabel result = new JLabel();
		result.setOpaque(true);
		result.setBackground(new Color(0, 0, 0, 0));
		result.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		return result;
	}

	public static Component getStatusRendererComponent(FileObject obj) {
		JLabel label = getLabelComponent();
		label.setIcon(local_is_up_to_date);
		return label;
	}

	public static Component getLockedRendererComponent(FileObject obj) {
		JLabel label = getLabelComponent();
		label.setIcon(unlocked);
		return label;
	}

	public static Component getEmptyComponent() {
		JLabel label = getLabelComponent();
		label.setText("");
		return label;
	}
}
