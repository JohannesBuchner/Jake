package com.jakeapp.gui.swing.controls;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;

import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;

/**
 * Renders a TagSet within a table cell
 *
 * ATTENTION: THIS IS A CONSTRUCTION SITE! WEAR HARD HATS AT ALL TIMES!
 */
public class TagSetRenderer extends JTextField implements TableCellRenderer {
    public TagSetRenderer() {
        super("tag1 tag2 tag3");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(value == null) return new JLabel("");
        return this;
    }
}
