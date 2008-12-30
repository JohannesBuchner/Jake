package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.JoinProjectAction;
import com.jakeapp.gui.swing.actions.RejectProjectAction;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.helpers.JakeMainHelper;
import com.jakeapp.gui.swing.helpers.Platform;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Project Invitation Panel.
 * A Unjoined project is displayed here, the user can join or reject.
 * User: studpete
 * Date: Dec 23, 2008
 * Time: 5:35:46 PM
 */
public class ProjectInvitationPanel extends JXPanel implements ProjectSelectionChanged, ProjectChanged {
    private Project project;
    private JTextField folderTextField;
    private JLabel projectNameLabel;
    private JLabel userNameLabel;
    private JoinProjectAction joinProjectAction;

    public ProjectInvitationPanel() {
        JakeMainApp.getApp().addProjectSelectionChangedListener(this);
        JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);

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

        projectNameLabel = new JLabel();
        projectNameLabel.setFont(projectNameLabel.getFont().deriveFont(Font.BOLD));
        this.add(projectNameLabel, "span 1 ,al center, wrap");

        JLabel icon = new JLabel();
        icon.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/folder-new-large.png"))));

        this.add(icon, "span 1, al center, wrap");

        userNameLabel = new JLabel();
        userNameLabel.setFont(userNameLabel.getFont().deriveFont(Font.BOLD));
        this.add(userNameLabel, "span 1 ,al center, wrap");

        JPanel folderSelectPanel = new JPanel(new MigLayout("nogrid, fillx"));
        folderSelectPanel.setOpaque(false);

        folderTextField = new JTextField(JakeMainHelper.getDefaultProjectLocation(getProject()));
        folderTextField.setEditable(false);
        folderSelectPanel.add(folderTextField);

        JButton folderChooserButton = new JButton("...");
        folderChooserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String folder = JakeMainHelper.openDirectoryChooser(null);
                if (folder != null) {
                    folderTextField.setText(folder);
                    joinProjectAction.setProjectLocation(folder);
                }
            }
        });
        folderSelectPanel.add(folderChooserButton, "");

        this.add(folderSelectPanel, "span 2, al center, wrap");

        JPanel btnPanel = new JPanel(new MigLayout("nogrid"));
        btnPanel.setOpaque(false);

        JButton joinButton = new JButton("Join");
        joinProjectAction = new JoinProjectAction();
        joinButton.setAction(joinProjectAction);

        JButton rejectButton = new JButton("Reject");
        rejectButton.setAction(new RejectProjectAction());

        btnPanel.add(joinButton, "tag ok");
        btnPanel.add(rejectButton, "tag cancel");

        this.add(btnPanel, "al center");
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project pr) {
        this.project = pr;
        updatePanel();
        setProjectDefaultLocation();
    }

    private void setProjectDefaultLocation() {
        folderTextField.setText(JakeMainHelper.getDefaultProjectLocation(getProject()));
    }

    public void projectChanged(ProjectChangedEvent ev) {
        updatePanel();
    }

    private void updatePanel() {
        if (getProject() != null) {
            projectNameLabel.setText(getProject().getName());

            // TODO: is this user id the id from the inviter?
            // TODO: enable when this works without mock!
            String userId = "<needs real impl>"; //getProject().getUserId().toString();
            userNameLabel.setText(JakeMainView.getMainView().getResourceMap().getString("projectInvitedFrom") + " " + userId);
        }
    }
}
