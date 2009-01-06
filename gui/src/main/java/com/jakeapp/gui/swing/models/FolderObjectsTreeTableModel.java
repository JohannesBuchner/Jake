package com.jakeapp.gui.swing.models;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.jakeapp.gui.swing.helpers.*;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.core.domain.FileObject;

import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;

public class FolderObjectsTreeTableModel implements TreeTableModel {
	private static final Logger log = Logger.getLogger(FolderObjectsTreeTableModel.class);

	private ProjectFilesTreeNode root;

	public FolderObjectsTreeTableModel(ProjectFilesTreeNode root) {
		this.root = root;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0:
				return ProjectFilesTreeNode.class;
			case 1:
				return String.class;
			case 2:
				return String.class;
			default:
				return null;
		}
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return "Name";
			case 1:
				return "Size";
			case 2:
				return "Last Modified";
			default:
				return null;
		}
	}

	@Override
	public int getHierarchicalColumn() {
		// First column is the hierarchical one (filename)
		return 0;
	}

	@Override
	public Object getValueAt(Object node, int column) {
		if (!(node instanceof ProjectFilesTreeNode))
			throw new IllegalArgumentException("Not a ProjectFilesTreeNode but a " + node.getClass().toString());
		ProjectFilesTreeNode ournode = (ProjectFilesTreeNode) node;

		if (ournode.isFile()) {
			switch (column) {
				case 0:
					return ournode;
				case 1:
					return FileUtilities.getSize(JakeMainApp.getApp().getCore().getFileSize(ournode.getFileObject()));
				case 2:
					return TimeUtilities.getRelativeTime(JakeMainApp.getApp().getCore().getFileLastModified(ournode.getFileObject()));
				default:
					return "INVALIDCOLUMN";
			}
		} else {
			switch (column) {
				case 0:
					return ournode;
				case 1:
					return "";
				case 2:
					return "";
				default:
					return "INVALIDCOLUMN";
			}
		}
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		// TODO: Some cells may need to be editable
		return false;
	}

	@Override
	public void setValueAt(Object value, Object node, int column) {
		// TODO: Maybe we'll need to do something here
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
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		// TODO: Do we need one?
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		// TODO: Do we need one?
	}
}
