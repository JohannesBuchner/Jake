package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.helpers.FileObjectLockedCell;
import com.jakeapp.gui.swing.helpers.FileObjectStatusCell;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.gui.swing.xcore.ObjectCache;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.util.EnumSet;
import java.util.List;

/**
 * Flat representation of FolderObjectTreeTableModel
 */
public class FileObjectsTableModel extends AbstractTableModel
				implements ProjectSelectionChanged, DataChanged {
	private static final Logger log =
					Logger.getLogger(FolderObjectsTreeTableModel.class);

	private List<FileObject> files;

	@Override public void setProject(Project pr) {
		updateData();
	}

	private void updateData() {
		this.files = ObjectCache.get().getFiles(JakeMainApp.getProject());
	}

	@Override public void dataChanged(EnumSet<Reason> reason, Project p) {
		// fixme react on specific project only
		if(reason.contains(Reason.Files)) {
			fireUpdate();
		}
	}

	enum Columns {
		FLock, FState, Name, Path, Size, LastMod, Tags
	}

	public FileObjectsTableModel() {
		log.trace("Created FileObjectsTableModel");

		// register for selection changes
		EventCore.get().addDataChangedCallbackListener(this);
		JakeMainApp.getApp().addProjectSelectionChangedListener(this);

		updateData();
	}

		public void fireUpdate() {
			updateData();
			this.fireTableDataChanged();
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
		return files != null ? files.size() : 0;
	}

	@Override
	public int getColumnCount() {
		return Columns.values().length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(files == null || rowIndex >= files.size() || rowIndex<0) {
			log.warn("Attemt to get value for invalid row " + rowIndex);
			return null;
		}

		ProjectFilesTreeNode ournode = new ProjectFilesTreeNode(files.get(rowIndex));

		// FIXME cache!! get async?
		Attributed<FileObject> fileInfo = JakeMainApp.getCore().getAttributed(
						JakeMainApp.getProject(),
						ournode.getFileObject());

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
					return System.getProperty("file.separator") + s
									.substring(0, s.lastIndexOf(System.getProperty("file.separator")));
				} else {
					return System.getProperty("file.separator");
				}
			case Size:
				return FileUtilities.getSize(fileInfo.getSize());
			case LastMod:
				return TimeUtilities.getRelativeTime(fileInfo.getLastModificationDate());
			case Tags:
				return ""; // FIXME
			default:
				log.warn("Accessed invalid column:" + columnIndex);
				return "INVALIDCOLUMN";
		}
	}
}
