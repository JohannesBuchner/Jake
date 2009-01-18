package com.jakeapp.gui.swing.models;

import com.jakeapp.gui.swing.helpers.*;
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
				return "";
			case 1:
				return "";
			case 2:
				return "Name";
			case 3:
				return "in";
			case 4:
				return "Size";
			case 5:
				return "Last Modified";
			case 6:
				return "Tags";
			default:
				return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0:
				return FileObjectLockedCell.class;
			case 1:
				return FileObjectStatusCell.class;
			case 2:
				return ProjectFilesTreeNode.class;
			case 3:
				return String.class;
			case 4:
				return String.class;
			case 5:
				return String.class;
			case 6:
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
		return 7;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ProjectFilesTreeNode ournode = new ProjectFilesTreeNode(files.get(rowIndex));
		switch (columnIndex) {
			case 0:
				return new FileObjectLockedCell(ournode.getFileObject());
			case 1:
				return new FileObjectStatusCell(ournode.getFileObject());
			case 2:
				return ournode;
			case 3:
				String s = ournode.getFileObject().getRelPath();
				if (s.contains(System.getProperty("file.separator"))) {
					return System.getProperty("file.separator") + s.substring(0, s.lastIndexOf(System.getProperty("file.separator")));
				} else {
					return System.getProperty("file.separator");
				}
			case 4:
				return FileUtilities.getSize(JakeMainApp.getApp().getCore().getFileSize(ournode.getFileObject()));
			case 5:
				return TimeUtilities.getRelativeTime(JakeMainApp.getApp().getCore().getFileLastModified(ournode.getFileObject()));
			default:
				return "INVALIDCOLUMN";
		}
	}
}
