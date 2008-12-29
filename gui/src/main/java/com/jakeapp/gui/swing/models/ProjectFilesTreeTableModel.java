package com.jakeapp.gui.swing.models;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.ArrayList;

import com.jakeapp.gui.swing.helpers.JakeMainHelper;
import com.jakeapp.gui.swing.helpers.TagSet;

import javax.swing.*;

/**
 * A tree table model representing a folder (including subfolders and files contained
 * therein) in the filesystem and offering Jake-specific functionality. 
 *
 * Based on FileSystemModel in SwingX by Ramesh Gupta and Karl Schaefer (LGPL).
 */
public class ProjectFilesTreeTableModel extends AbstractTreeTableModel {
    private static final Logger log = Logger.getLogger(ProjectFilesTreeTableModel.class);
    
    // The returned file length for directories.
    private static final Long DIRECTORY = 0L;

    /**
     * Hideous and crappy performing way of getting the children of a folder node
     * that are NOT hidden.
     * 
     * @param parent
     * @return
     */
    protected static String[] getChildren(File parent) {
        String[] children = parent.list();
        ArrayList<String> returnChildren = new ArrayList<String>();
        for(String c: children) {
            File f = new File(parent, c);

            if(!f.isHidden()) {
                returnChildren.add(c);
            }
        }

        return returnChildren.toArray(new String[]{});
    }

    /**
     * Creates a file system model using the root directory as the model root.
     */
    public ProjectFilesTreeTableModel() {
        this(new File(File.separator));
    }

    /**
     * Creates a file system model using the specified {@code root}.
     *
     * @param root
     *            the root for this model; this may be different than the root
     *            directory for a file system.
     */
    public ProjectFilesTreeTableModel(File root) {
        super(root);
    }

    private boolean isValidFileNode(Object file) {
        boolean result = false;

        if (file instanceof File) {
            File f = (File) file;

            while (!result && f != null) {
                result = f.equals(root);

                f = f.getParentFile();
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public File getChild(Object parent, int index) {
        if (!isValidFileNode(parent)) {
            throw new IllegalArgumentException("parent is not a file governed by this model");
        }

        File parentFile = (File) parent;
        String[] children = getChildren(parentFile);

        if (children != null) {
            return new File(parentFile, children[index]);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getChildCount(Object parent) {
        if (parent instanceof File) {
            String[] children = getChildren((File) parent);

            if (children != null) {
                return children.length;
            }
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0:
            return String.class;
        case 1:
            return String.class;
        case 2:
            return String.class;
        case 3:
            return TagSet.class;
        default:
            return super.getColumnClass(column);
        }
    }

    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Name";
        case 1:
            return "Size";
        case 2:
            return "Last modified";
        case 3:
            return "Tags";
        default:
            return super.getColumnName(column);
        }
    }

    public Object getValueAt(Object node, int column) {
        if (node instanceof File) {
            File file = (File) node;

            switch (column) {
            case 0:
                return file.getName();
            case 1:
                return isLeaf(node) ? JakeMainHelper.getSize(file.length(), 0, false, true) : "";
            case 2:
                return JakeMainHelper.getRelativeTime(new Date(file.lastModified()));
            case 3:
                return isLeaf(node) ? new TagSet() : null;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof File && child instanceof File) {
            File parentFile = (File) parent;
            File[] files = parentFile.listFiles();

            Arrays.sort(files);

            for (int i = 0, len = files.length; i < len; i++) {
                if (files[i].equals(child)) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getRoot() {
        return (File) root;
    }

    /**
     * Sets the root for this tree table model. This method will notify
     * listeners that a change has taken place.
     *
     * @param root
     *            the new root node to set
     */
    public void setRoot(File root) {
        this.root = root;

        modelSupport.fireNewRoot();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLeaf(Object node) {
        if (node instanceof File) {
            //do not use isFile(); some system files return false
            return ((File) node).list() == null;
        }

        return true;
    }

    public boolean isCellEditable(Object node, int column) {
        if (node instanceof File) {
            File file = (File) node;

            switch (column) {
            case 0:
                return false;   // TODO: Modify me to allow renaming of files & folders
            case 1:
                return false;
            case 2:
                return false;
            case 3:
                return isLeaf(node);
            }
        }

        return false;
    }
}
