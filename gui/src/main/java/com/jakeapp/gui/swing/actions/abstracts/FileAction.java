package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;

public abstract class FileAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(FileAction.class);

	private JTable fileTable;

	/**
	 * Creates the file action.
	 * Depends on a JXTreeTable
	 *
	 * @param fileTable
	 */
	// TODO: abstract this! no depend on whole component!
	public FileAction(JTable fileTable) {
		super();
		this.fileTable = fileTable;
	}

	/**
	 * Checks if a single file (no folder) is selected
	 *
	 * @return true if single file, no folder
	 */
	protected boolean isSingleFileSelected() {
		boolean enabled = (fileTable.getSelectedRowCount() == 1 &&
			 ((ProjectFilesTreeNode) fileTable.getValueAt(fileTable.getSelectedRow(), 0)).isFile());
		return enabled;
	}

	/**
	 * Returns a FileObject from the File Table.
	 *
	 * @return file object or null of no/multiple selected.
	 */
	protected FileObject getSelectedFile() {
		if (!isSingleFileSelected()) {
			return null;
		} else {
			return ((ProjectFilesTreeNode) fileTable.getValueAt(
				 fileTable.getSelectedRow(), 0)).getFileObject();
		}
	}


	public JTable getFileTable() {
		return fileTable;
	}

	protected void setFileTable(JTable fileTable) {
		this.fileTable = fileTable;
	}
}
