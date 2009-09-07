package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;

import java.util.List;

/**
 * This event is fired when a node is selected in the FilePanel.
 * <p/>
 * Consumers include, for example, the main menu or the popup menu.
 */
public interface NodeSelectionChangedCallback {
	/**
	 * Inner class that saves files and provides convenience methods
	 * <p/>
	 * I think this is ugly, but Peter > me.
	 */
	public class NodeSelectedEvent {
		private List<ProjectFilesTreeNode> nodes;

		public NodeSelectedEvent(List<ProjectFilesTreeNode> nodes) {
			this.nodes = nodes;
		}

		public int size() {
			return nodes.size();
		}

		public boolean isSingleFileSelected() {
			return (nodes != null) && nodes.size() == 1;
		}

		public boolean isNoFileSelected() {
			return (nodes == null) || nodes.size() == 0;
		}

		public boolean isMultipleFilesSelected() {
			return (nodes != null) && nodes.size() > 1;
		}

		public ProjectFilesTreeNode getSingleNode() {
			return (nodes != null && nodes.size() == 1) ? nodes.get(0) : null;
		}

		public List<ProjectFilesTreeNode> getNodes() {
			return nodes;
		}
	}

	public void nodeSelectionChanged(NodeSelectedEvent event);
}