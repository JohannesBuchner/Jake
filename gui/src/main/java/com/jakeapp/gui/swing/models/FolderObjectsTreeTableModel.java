package com.jakeapp.gui.swing.models;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.jakeapp.gui.swing.helpers.*;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.exceptions.InvalidTagStringFormatException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.synchronization.FileStatus;

import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.*;

public class FolderObjectsTreeTableModel implements TreeTableModel {
	private static final Logger log = Logger.getLogger(FolderObjectsTreeTableModel.class);

	private ProjectFilesTreeNode root;

	private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

	public FolderObjectsTreeTableModel(ProjectFilesTreeNode root) {
		this.root = root;
	}

	public void fireRepaint() {
		for (TreeModelListener l : listeners) {
			TreeModelEvent e = new TreeModelEvent(root, new TreePath(root));
			l.treeNodesChanged(e);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0:
				return FileObjectStatusCell.class;
			case 1:
				return FileObjectLockedCell.class;
			case 2:
				return ProjectFilesTreeNode.class;
			case 3:
				return String.class;
			case 4:
				return String.class;
			case 5:
				return String.class;
			default:
				return null;
		}
	}

	@Override
	public int getColumnCount() {
		return 6;
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
				return "Size";
			case 4:
				return "Last Modified";
			case 5:
				return "Tags";
			default:
				return null;
		}
	}

	@Override
	public int getHierarchicalColumn() {
		// Third column is the hierarchical one (filename)
		return 2;
	}

	@Override
	public Object getValueAt(Object node, int column) {
		if (!(node instanceof ProjectFilesTreeNode))
			throw new IllegalArgumentException("Not a ProjectFilesTreeNode but a " + node.getClass().toString());
		ProjectFilesTreeNode ournode = (ProjectFilesTreeNode) node;

		if (ournode.isFile()) {
			switch (column) {
				case 0:
					return new FileObjectStatusCell(ournode.getFileObject());
				case 1:
					return new FileObjectLockedCell(ournode.getFileObject());
				case 2:
					return ournode;
				case 3:
					return FileUtilities.getSize(JakeMainApp.getApp().getCore().getFileSize(ournode.getFileObject()));
				case 4:
					return TimeUtilities.getRelativeTime(JakeMainApp.getApp().getCore().getFileLastModified(ournode.getFileObject()));
				case 5:
					return TagHelper.tagsToString(JakeMainApp.getApp().getCore().getTagsForFileObject(ournode.getFileObject()));
				default:
					return "INVALIDCOLUMN";
			}
		} else {
			switch (column) {
				case 0:
					return null;
				case 1:
					return null;
				case 2:
					return ournode;
				case 3:
					return "";
				case 4:
					return "";
				case 5:
					return "";
				default:
					return "INVALIDCOLUMN";
			}
		}
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		// Tags are editable, but only for files
		ProjectFilesTreeNode ournode = (ProjectFilesTreeNode) node;
		return column == 5 && ournode.isFile();
	}

	@Override
	public void setValueAt(Object value, Object node, int column) {
		ProjectFilesTreeNode ournode = (ProjectFilesTreeNode) node;
		if (column == 5 && ournode.isFile()) {
			// Change tags
			FileObject fileobj = ournode.getFileObject();

			try {
				Set<Tag> tags = TagHelper.stringToTags(fileobj, (String) value);
				JakeMainApp.getApp().getCore().setTagsForFileObject(fileobj, tags);
			} catch (InvalidTagStringFormatException e) {
				log.warn("Invalid tag string ('" + value + "')");
			}
		}
	}

	@Override
	public ProjectFilesTreeNode getRoot() {
		return root;
	}

	@Override
	public ProjectFilesTreeNode getChild(Object parent, int index) {
		if (!(parent instanceof ProjectFilesTreeNode)) throw new IllegalArgumentException("Not a ProjectFilesTreeNode");
		ProjectFilesTreeNode ournode = (ProjectFilesTreeNode) parent;

		// Files have no children
		if (ournode.isFile()) return null;

		FolderObject fo = ournode.getFolderObject();

		return new ProjectFilesTreeNode(fo.getAllChildren().get(index));
	}

	@Override
	public int getChildCount(Object parent) {
		if (!(parent instanceof ProjectFilesTreeNode)) throw new IllegalArgumentException("Not a ProjectFilesTreeNode");
		ProjectFilesTreeNode ournode = (ProjectFilesTreeNode) parent;

		// Files have no children
		if (ournode.isFile()) return 0;

		FolderObject fo = ournode.getFolderObject();

		return fo.getAllChildren().size();
	}

	@Override
	public boolean isLeaf(Object node) {
		// All leaves must be files and all files must be leaves
		if (!(node instanceof ProjectFilesTreeNode)) throw new IllegalArgumentException("Not a ProjectFilesTreeNode");
		ProjectFilesTreeNode ournode = (ProjectFilesTreeNode) node;
		return ournode.isFile();
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO: WTF is this good for?
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		// TODO: WTF is this good for?
		return 0;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
		System.err.println("Listener added! " + l.toString());
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}
}
