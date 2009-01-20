package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.*;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Flat representation of FolderObjectTreeTableModel
 */
public class FileObjectsTableModel extends AbstractTableModel {
	private static final Logger log = Logger.getLogger(FolderObjectsTreeTableModel.class);
	private List<FileObject> files;

	enum Columns {
		FLock, FState, Name, Path, Size, LastMod, Tags
	}

	;

	public FileObjectsTableModel(List<FileObject> files) {
		this.files = files;
	}

	@Override
	public String getColumnName(int column) {
		switch (Columns.values()[column]) {
			case FLock:
				return "";
			case FState:
				return "";
			case Name:
				return "Name";
			case Path:
				return "in";
			case Size:
				return "Size";
			case LastMod:
				return "Last Modified";
			case Tags:
				return "Tags";
			default:
				return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (Columns.values()[columnIndex]) {
			case FLock:
				return FileObjectLockedCell.class;
			case FState:
				return FileObjectStatusCell.class;
			case Name:
				return ProjectFilesTreeNode.class;
			case Path:
				return String.class;
			case Size:
				return String.class;
			case LastMod:
				return String.class;
			case Tags:
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
		switch (Columns.values()[columnIndex]) {
			case FLock:
				return new FileObjectLockedCell(ournode.getFileObject());
			case FState:
				return new FileObjectStatusCell(ournode.getFileObject());
			case Name:
				return ournode;
			case Path:
				String s = ournode.getFileObject().getRelPath();
				if (s.contains(System.getProperty("file.separator"))) {
					return System.getProperty("file.separator") + s.substring(0, s.lastIndexOf(System.getProperty("file.separator")));
				} else {
					return System.getProperty("file.separator");
				}
			case Size:
				return FileUtilities.getSize(JakeMainApp.getApp().getCore().getFileSize(ournode.getFileObject()));
			case LastMod:
				return TimeUtilities.getRelativeTime(JakeMainApp.getApp().getCore().getFileLastModified(ournode.getFileObject()));
			case Tags:
				return "";
			default:
				log.warn("Accessed invalid column:" + columnIndex);
				return "INVALIDCOLUMN";
		}
	}
}
