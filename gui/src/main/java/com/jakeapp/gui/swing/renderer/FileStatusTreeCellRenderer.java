package com.jakeapp.gui.swing.renderer;

import com.jakeapp.gui.swing.controls.JAsynchronousProgressIndicator;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


public class FileStatusTreeCellRenderer implements TableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JAsynchronousProgressIndicator progress = new JAsynchronousProgressIndicator();
		progress.startAnimation();
		progress.setOpaque(false);
		return progress;
	}
}
