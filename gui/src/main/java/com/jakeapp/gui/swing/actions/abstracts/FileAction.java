package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import org.apache.log4j.Logger;

import java.util.List;

public abstract class FileAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(FileAction.class);

	private List<ProjectFilesTreeNode> nodes;
	private int selectedRowCount;

	/**
	 * Creates the file action.
	 * Depends on a JXTreeTable
	 *
	 * @param nodes
	 */
	public FileAction(List<ProjectFilesTreeNode> nodes) {
		super();
		this.nodes = nodes;
		this.selectedRowCount = nodes != null ? nodes.size() : 0;
	}

	/**
	 * Checks if a single file (no folder) is selected
	 *
	 * @return true if single file, no folder
	 */
	protected boolean isSingleFileSelected() {
		return (selectedRowCount == 1 && nodes.get(0).isFile());
	}

	/**
	 * Returns a FileObject from the File Table.
	 *
	 * @return file object or null if no/multiple selected.
	 */
	protected FileObject getSelectedFile() {
		return !isSingleFileSelected() ? null : nodes.get(0).getFileObject();
	}


	public ProjectFilesTreeNode getSingleNode() {
		return nodes.size() > 0 ? nodes.get(0) : null;
	}

	public List<ProjectFilesTreeNode> getNodes() {
		return nodes;
	}

	public int getSelectedRowCount() {
		return this.selectedRowCount;
	}
}
