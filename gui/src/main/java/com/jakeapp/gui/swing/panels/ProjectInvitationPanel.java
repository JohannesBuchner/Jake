package com.jakeapp.gui.swing.panels;

import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXTitledSeparator;

import javax.swing.*;

/**
 * User: studpete
 * Date: Dec 23, 2008
 * Time: 5:35:46 PM
 */
public class ProjectInvitationPanel extends JPanel {

    public ProjectInvitationPanel() {
        initComponents();
    }


    private void initComponents() {
        MigLayout layout = new MigLayout("wrap 3");
        this.setLayout(layout);

        JXTitledSeparator sep = new JXTitledSeparator("You have been invited to a new project!");
        this.add(sep, "");
    }

}
