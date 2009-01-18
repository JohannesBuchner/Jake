package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.panels.FilePanel;
import com.jakeapp.gui.swing.panels.NotesPanel;
import com.jakeapp.gui.swing.callbacks.FileSelectionChanged;
import com.jakeapp.gui.swing.callbacks.NodeSelectionChanged;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

public abstract class FileAction extends ProjectAction implements NodeSelectionChanged {
	private static final Logger log = Logger.getLogger(FileAction.class);

	private List<ProjectFilesTreeNode> nodes;
	private int selectedRowCount;

	public FileAction() {
		super();
		FilePanel.getInstance().addNodeSelectionListener(this);
		selectedRowCount = 0;
		nodes = new ArrayList<ProjectFilesTreeNode>();

		this.nodes = FilePanel.getInstance().getSelectedNodes();
		this.selectedRowCount = nodes.size();
	}

	@Override
	public void nodeSelectionChanged(NodeSelectedEvent event) {
		this.selectedRowCount = event.size();
		this.nodes = event.getNodes();
		this.updateAction();
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
