package com.jakeapp.gui.swing.models;

import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.core.domain.FileObject;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Flat representation of FolderObjectTreeTableModel
 */
public class FileObjectsTableModel extends AbstractTableModel {
	private List<FileObject> files;

	public FileObjectsTableModel(List<FileObject> files) {
		this.files = files;
	}

	@Override
	public String getColumnName(int column) {
		return "FOO";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0:
				return ProjectFilesTreeNode.class;
			case 1:
				return String.class;
			case 2:
				return String.class;
			default:
				return null;
		}
	}

	@Override
	public int getRowCount() {
		return files.size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				return new ProjectFilesTreeNode(files.get(rowIndex));
			default:
				return "Blubb";
		}
	}
}
