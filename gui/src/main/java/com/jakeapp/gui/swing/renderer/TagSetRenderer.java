package com.jakeapp.gui.swing.renderer;

import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 */
public class TagSetRenderer extends JXPanel implements TableCellRenderer {
    public TagSetRenderer() {
        this.add(new JLabel(""));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}
