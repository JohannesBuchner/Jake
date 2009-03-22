package com.jakeapp.gui.swing.renderer.files;

import com.jakeapp.gui.swing.helpers.FileObjectStatusCell;
import com.jakeapp.gui.swing.helpers.FileObjectStatusProvider;
import com.jakeapp.gui.swing.renderer.DefaultJakeTableCellRenderer;

import javax.swing.*;
import java.awt.*;


public class FileStatusTreeCellRenderer extends DefaultJakeTableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
		table.getColumnModel().getColumn(column).setMaxWidth(125);
		table.getColumnModel().getColumn(column).setMinWidth(25);

		Component comp;

		//if(value instanceof FileObjectStatusCell) {
		comp = (value == null) ? FileObjectStatusProvider.getEmptyComponent() :
						FileObjectStatusProvider
										.getStatusRendererComponent(((FileObjectStatusCell) value).getFileObject());
		//}else {
	//		comp = FileObjectStatusProvider.getEmptyComponent();
		//}

		return comp;
	}
}