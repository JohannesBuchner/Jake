package com.jakeapp.gui.swing.controls;

import com.jakeapp.gui.swing.helpers.FileIconLabelHelper;

import javax.swing.tree.TreeCellRenderer;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Renders file and folder nodes in the ProjectFilesTree
 */
public class ProjectFilesTreeCellRenderer implements TreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if(!(value instanceof File)) return null;
        
        File file = (File) value;
        return FileIconLabelHelper.getIconLabel(file, FileIconLabelHelper.State.NONE);
    }
}
