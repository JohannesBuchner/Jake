/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author studpete
 */
public abstract class TableMouseListener implements MouseListener {

    private JTable table;

    public TableMouseListener(JTable table) {
        this.table = table;
    }

    private boolean isTableEntrySelected() {
        return table.getSelectedRow() >= 0;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Right mouse click
        if (SwingUtilities.isRightMouseButton(e)) {
            // get the coordinates of the mouse click
            Point p = e.getPoint();

            // get the row index that contains that coordinate
            int rowNumber = table.rowAtPoint(p);

            // Get the ListSelectionModel of the JTable
            ListSelectionModel model = table.getSelectionModel();

            // set the selected interval of rows. Using the "rowNumber"
            // variable for the beginning and end selects only that one
            // row.
            model.setSelectionInterval(rowNumber, rowNumber);

            showPopup(table, (int) e.getPoint().getX(), (int) e.getPoint().getY());
        }


        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e) && isTableEntrySelected()) {
            editAction();
        }
    }

    public abstract void showPopup(JComponent comp, int x, int y);

    public abstract void editAction();

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

}
