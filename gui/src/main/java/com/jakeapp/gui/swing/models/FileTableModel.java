package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ContextChangedCallback;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.helpers.FileObjectLockedCell;
import com.jakeapp.gui.swing.helpers.FileObjectStatusCell;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import com.jakeapp.gui.swing.helpers.UserHelper;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.gui.swing.xcore.ObjectCache;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Flat representation of FolderObjectTreeTableModel
 */
public class FileTableModel extends AbstractTableModel
				implements ContextChangedCallback, DataChangedCallback {
	private static final Logger log = Logger.getLogger(FolderTreeTableModel.class);

	private List<FileObject> files;

	private void updateData() {
		this.files = new ArrayList<FileObject>(
						ObjectCache.get().getFiles(JakeContext.getProject()));
	}

	@Override public void dataChanged(EnumSet<DataReason> dataReason, Project p) {
		// fixme react on specific project only
		if (dataReason.contains(DataReason.Files)) {
			fireUpdate();
		}
	}

	@Override public void contextChanged(EnumSet<Reason> reason, Object context) {
		if (reason.contains(Reason.Project)) {
			updateData();
		}
	}

	public enum Columns {
		FState, Name, Path, Size, LastMod, LastModBy, FLock
	}

	public FileTableModel() {
		log.trace("created FileTableModel");

		// register for selection changes
		EventCore.get().addDataChangedCallbackListener(this);
		EventCore.get().addContextChangedListener(this);

		updateData();
	}

	public void fireUpdate() {
		updateData();
		this.fireTableDataChanged();
	}

	@Override
	public String getColumnName(int column) {
		switch (Columns.values()[column]) {
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
			case LastModBy:
				return "Modifier";
			case FLock:
				return "";
			default:
				return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (Columns.values()[columnIndex]) {
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
			case LastModBy:
				return String.class;
			case FLock:
				return FileObjectLockedCell.class;
			default:
				return null;
		}
	}

	@Override
	public int getRowCount() {
		return files != null ? files.size() : 0;
	}

	@Override
	public int getColumnCount() {
		return Columns.values().length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (files == null || rowIndex >= files.size() || rowIndex < 0) {
			log.warn("Attemt to get value for invalid row " + rowIndex);
			return null;
		}

		ProjectFilesTreeNode ournode = new ProjectFilesTreeNode(files.get(rowIndex));

		// FIXME cache!! get async?
		FileObject fo = ournode.getFileObject();
		Attributed<FileObject> fileInfo = null;

		try {
			fileInfo = JakeMainApp.getCore().getAttributed(fo);
		} catch (Exception ex) {
			log.warn("Error trying get attributed informations for FileObject", ex);
		}

		switch (Columns.values()[columnIndex]) {
			case FLock:
				return new FileObjectLockedCell(fo);
			case FState:
				return new FileObjectStatusCell(fo);
			case Name:
				return ournode;
			case Path:
				String s = fo.getRelPath();
				String pathSep = FileUtilities.getPathSeparator();
				if (s.contains(pathSep)) {
					return pathSep + s.substring(0, s.lastIndexOf(pathSep));
				} else {
					return pathSep;
				}
			case Size: {
				if (fileInfo == null || fileInfo.isOnlyRemote()) {
					return "";
				} else {
					return FileUtilities.getSize(fileInfo.getSize());
				}
			}
			case LastMod:
				if (fileInfo == null)
					return "";
				else
					return TimeUtilities.getRelativeTime(fileInfo.getLastModificationDate());
			case LastModBy:
				if (fileInfo == null)
					return "";
				else
					return UserHelper.getLocalizedUserNick(fileInfo.getLastVersionEditor());
			default:
				log.warn("Accessed invalid column:" + columnIndex);
				return "INVALIDCOLUMN";
		}
	}
}