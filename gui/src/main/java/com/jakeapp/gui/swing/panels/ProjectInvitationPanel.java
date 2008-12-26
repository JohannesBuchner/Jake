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


        MigLayout layout = new MigLayout("wrap 1, fillx");
        this.setLayout(layout);

        JLabel title = new JLabel("You have been invited to a new project.");
        Font font = new Font("Lucida Grande", Font.BOLD, 18);
        title.setFont(font);

        this.add(title, "span 1, al center, wrap");

        JLabel projectName = new JLabel("Projectname");
        projectName.setFont(projectName.getFont().deriveFont(Font.BOLD));
        this.add(projectName, "span 1 ,al center, wrap");

        JLabel icon = new JLabel();
        icon.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/folder-new-large.png"))));

        this.add(icon, "span 1, al center, wrap");

        JLabel userName = new JLabel("From: studpete<at>gmail.com");
        userName.setFont(projectName.getFont().deriveFont(Font.BOLD));
        this.add(userName, "span 1 ,al center, wrap");

        JPanel folderSelectPanel = new JPanel(new MigLayout("nogrid, fillx"));
        folderSelectPanel.setOpaque(false);
        JTextField folder = new JTextField("/User/temp/Jake/Projektname");
        folderSelectPanel.add(folder, "");
        JButton folderChooserButton = new JButton("...");
        folderSelectPanel.add(folderChooserButton, "");
        this.add(folderSelectPanel, "span 2, al center, wrap");

        JPanel btnPanel = new JPanel(new MigLayout("nogrid"));
        btnPanel.setOpaque(false);

        JButton joinButton = new JButton("Join");
        JButton rejectButton = new JButton("Reject");

        btnPanel.add(joinButton, "tag ok");
        btnPanel.add(rejectButton, "tag cancel");

        this.add(btnPanel, "al center");

    }

}
