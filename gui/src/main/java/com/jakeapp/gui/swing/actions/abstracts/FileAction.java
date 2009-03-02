package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.callbacks.NodeSelectionChanged;
import com.jakeapp.gui.swing.helpers.FolderObject;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.panels.FilePanel;
import com.jakeapp.gui.swing.xcore.EventCore;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class FileAction extends ProjectAction implements NodeSelectionChanged {
	private static final Logger log = Logger.getLogger(FileAction.class);

	private List<ProjectFilesTreeNode> nodes;
	private int selectedRowCount;

	public FileAction() {
		super();
		EventCore.get().addNodeSelectionListener(this);
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

	protected ArrayList<FileObject> getSelectedFiles() {
		ArrayList<FileObject> files = new ArrayList<FileObject>();

		for (ProjectFilesTreeNode pf : getNodes()) {
			if (pf.isFile()) {
				files.add(pf.getFileObject());
			} else if (pf.isFolder()) {
				files.addAll(recurseNodes(pf.getFolderObject()));
			}
		}
		return files;
	}

	private List<FileObject> recurseNodes(FolderObject folder) {
		ArrayList<FileObject> files = new ArrayList<FileObject>();

		for (FileObject f : folder.getFileChildren()) {
			files.add(f);
		}

		for (FolderObject f : folder.getFolderChildren()) {
			files.addAll(recurseNodes(f));
		}

		return files;
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

	@Override
	public void updateAction() {
		super.updateAction();
	}
}
