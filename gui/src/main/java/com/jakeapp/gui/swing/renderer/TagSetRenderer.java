package com.jakeapp.gui.swing.renderer;

import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Jan 5, 2009
 * Time: 10:29:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagSetRenderer extends JXPanel implements TableCellRenderer {
    public TagSetRenderer() {
        this.add(new JLabel("fuck"));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}
