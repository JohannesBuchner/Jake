/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.controls.CloseableDialogCallback;
import com.jakeapp.gui.swing.controls.DisabledGlassPane;
import com.jakeapp.gui.swing.interfaces.Sheet;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * @author studpete
 */
public class SheetHelper {
    public static JComponent ShowJDialogAsSheet(JFrame frame, JPanel panel) {
        JComponent sheet;
        Sheet sheetItf = (Sheet) panel;

        DisabledGlassPane glass = new DisabledGlassPane();
        frame.setGlassPane(glass);

        JDialog dialog = new JDialog(frame);
        dialog.add(panel);
        dialog.getRootPane().setDefaultButton(sheetItf.defaultEnterButton());
        sheetItf.setClosableCallback(new CloseableDialogCallback(frame, dialog));

        // JPanel glass = (JPanel) frame.getGlassPane();
        sheet = (JComponent) dialog.getRootPane();

        sheet.setBackground(new Color(sheet.getBackground().getRed(), sheet.getBackground().getGreen(), sheet.getBackground().getBlue(), 192));
        sheet.setOpaque(true);

        glass.setLayout(new GridBagLayout());
        sheet.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        glass.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(65, 65, 65, 65);
        glass.add(sheet, gbc);

        gbc.gridy = 1;
        gbc.weighty = Integer.MAX_VALUE;
        glass.add(Box.createGlue(), gbc);


        //  glass.setVisible(true);
        glass.activate("");
        return sheet;
    }
}
