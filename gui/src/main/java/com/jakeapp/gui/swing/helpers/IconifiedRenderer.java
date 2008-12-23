/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.helpers;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author studpete
 */
public class IconifiedRenderer extends DefaultTableCellRenderer {
    /*
     * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
     */

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ImageIcon icon = (ImageIcon) value;
        setText(table.getValueAt(row, column + 2).toString());
        setIcon(icon);
        return this;
    }
}