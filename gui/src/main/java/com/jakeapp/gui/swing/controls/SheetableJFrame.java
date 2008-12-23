package com.jakeapp.gui.swing.controls;

import javax.swing.*;

public class SheetableJFrame extends JFrame {

    JComponent sheet;
    JPanel glass;

    public SheetableJFrame(String name) {
        super(name);
        glass = (JPanel) getGlassPane();
    }


    public void hideSheet() {
        glass.setVisible(false);
    }
}