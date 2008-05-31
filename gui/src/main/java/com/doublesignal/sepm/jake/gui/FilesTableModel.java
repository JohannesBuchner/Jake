package com.doublesignal.sepm.jake.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchJakeObjectException;

/**
 * @author peter
 * 
 */
@SuppressWarnings("serial")
public class FilesTableModel extends AbstractTableModel {
	private static Logger log = Logger.getLogger(FilesTableModel.class);
	private final IJakeGuiAccess jakeGuiAccess;
	private List<FileObject> files;

	String[] colNames = new String[] { "Name", "Size", "Tags", "Sync Status",
			"Last Changed", "User" };

	boolean[] columnEditable = new boolean[] { false, false, true, false,
			false, false };

	enum FilesColumns {
		Name, Size, Tags, SyncStatus, LastChanged, User
	}

	FilesTableModel(IJakeGuiAccess jakeGuiAccess) {
		log.info("Initializing FilesTableModel.");
		this.jakeGuiAccess = jakeGuiAccess;

		updateData();
	}

	/**
	 * Update whole table data.
	 */
	private void updateData() {
		try {
			this.files = jakeGuiAccess.getFileObjectsByPath("/");
		} catch (NoSuchJakeObjectException e) {
			log.warn("Got NoSuchJakeObjectException: " + e.toString());
			e.printStackTrace();
		}
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public int getRowCount() {
		return files.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		FileObject file = files.get(rowIndex);

		FilesColumns col = FilesColumns.values()[columnIndex];
		switch (col) {
		case Name:
			return file.getName();

		case Size:
			return FilesLib.getHumanReadableFileSize(jakeGuiAccess
					.getFileSize(file));

		case Tags:
			return JakeObjLib.getTagString(file.getTags());

		case SyncStatus:
			return "???";

		case LastChanged:
			ProjectMember pmLastModifier = jakeGuiAccess.getLastModifier(file);
			return (pmLastModifier.getNickname().isEmpty()) ? pmLastModifier
					.getUserId() : pmLastModifier.getNickname();

		case User:
			return "???";

		default:
			throw new IllegalArgumentException(
					"Cannot get Information for column " + columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnEditable[columnIndex];
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colNames[columnIndex];
	}
}
