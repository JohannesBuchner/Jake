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
        File file = (File) value;

        JLabel label = new JLabel();

        Icon icon = FileIconLabelHelper.getIcon(file, FileIconLabelHelper.State.NONE);

        label.setIcon(icon);
        label.setText(file.getName());

        return label;
    }
}
