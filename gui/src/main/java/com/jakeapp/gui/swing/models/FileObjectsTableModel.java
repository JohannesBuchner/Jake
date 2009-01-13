package com.jakeapp.gui.swing.models;

import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.core.domain.FileObject;

import javax.swing.table.AbstractTableModel;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Flat representation of FolderObjectTreeTableModel
 */
public class FileObjectsTableModel extends AbstractTableModel {
	private static final Logger log = Logger.getLogger(FolderObjectsTreeTableModel.class);
	private List<FileObject> files;

	public FileObjectsTableModel(List<FileObject> files) {
		this.files = files;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return "Name";
			case 1:
				return "Size";
			case 2:
				return "Last Modified";
			default:
				return null;
		}
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
		ProjectFilesTreeNode ournode = new ProjectFilesTreeNode(files.get(rowIndex));
		switch (columnIndex) {
			case 0:
				return ournode;
			case 1:
				return FileUtilities.getSize(JakeMainApp.getApp().getCore().getFileSize(ournode.getFileObject()));
			case 2:
				return TimeUtilities.getRelativeTime(JakeMainApp.getApp().getCore().getFileLastModified(ournode.getFileObject()));
			default:
				return "INVALIDCOLUMN";
		}
	}
}
