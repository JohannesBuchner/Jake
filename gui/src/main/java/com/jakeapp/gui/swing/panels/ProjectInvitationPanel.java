package com.jakeapp.gui.swing.panels;

import com.jakeapp.gui.swing.helpers.Platform;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;

/**
 * The Project Invitation Panel.
 * A Unjoined project is displayed here, the user can join or reject.
 * User: studpete
 * Date: Dec 23, 2008
 * Time: 5:35:46 PM
 */
public class ProjectInvitationPanel extends JXPanel {

    public ProjectInvitationPanel() {
        initComponents();
    }


    private void initComponents() {
        // set the background painter
        this.setBackgroundPainter(Platform.getStyler().getContentPanelBackgroundPainter());


        MigLayout layout = new MigLayout("wrap 3");
        this.setLayout(layout);

        JLabel title = new JLabel("You have been invited to a new project!");
        Font font = new Font("Lucida Grande", Font.BOLD, 18);
        title.setFont(font);

        this.add(title, "span 3");

        JLabel icon = new JLabel();
        icon.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/folder-new-large.png"))));

        this.add(icon, "");

    }

}
