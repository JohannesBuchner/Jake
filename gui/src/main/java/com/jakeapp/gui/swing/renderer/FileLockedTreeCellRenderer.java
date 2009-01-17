package com.jakeapp.gui.swing.renderer;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.awt.*;

public class FileLockedTreeCellRenderer extends DefaultTableCellRenderer {
	private static Icon locked = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
		 FileLockedTreeCellRenderer.class.getResource("/locked/locked.png")));

	private static Icon unlocked = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
		 FileLockedTreeCellRenderer.class.getResource("/locked/unlocked.png")));

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		table.getColumnModel().getColumn(column).setMinWidth(30);
		table.getColumnModel().getColumn(column).setMaxWidth(30);

		this.setBackground(new Color(0, 0, 0, 0));
		this.setOpaque(true);

		this.setIcon(unlocked);
		this.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		if (isSelected) {
			this.setBackground(table.getSelectionBackground());
		}

		return this;
	}
}
