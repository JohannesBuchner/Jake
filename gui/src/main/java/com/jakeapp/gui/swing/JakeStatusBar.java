package com.jakeapp.gui.swing;

import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.helpers.Platform;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 10:59:04 AM
 */
public class JakeStatusBar implements ConnectionStatus {
    private static final Logger log = Logger.getLogger(JakeStatusBar.class);

    private JLabel statusLabel;
    private JButton connectionButton;
    private ResourceMap resourceMap;
    private Project project;
    private ICoreAccess core;
    private TriAreaComponent statusBar;


    public JakeStatusBar(ResourceMap resourceMap, ICoreAccess core) {
        setResourceMap(resourceMap);
        this.core = core;

        // registering the connection status callback
        getCore().registerConnectionStatusCallback(this);

        statusBar = createStatusBar();
    }

    private ResourceMap getResourceMap() {
        return resourceMap;
    }

    private ICoreAccess getCore() {
        return core;
    }

    private void setResourceMap(ResourceMap resourceMap) {
        this.resourceMap = resourceMap;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Returns the Status Bar component.
     *
     * @return
     */
    public Component getComponent() {
        return statusBar.getComponent();
    }


    public void setConnectionStatus(ConnectionStati status, String msg) {
        updateConnectionButton();
    }

    /**
     * Updates the connection Button with new credentals informations
     */
    private void updateConnectionButton() {
        String msg;

        if (getCore().isSignedIn()) {
            msg = getCore().getSignInUser();
        } else {
            msg = getResourceMap().getString("statusLoginNotSignedIn");
        }
        connectionButton.setText(msg);


    }

    /**
     * Create status bar code
     *
     * @return
     */
    private TriAreaComponent createStatusBar() {
        // status bar creation code

        // only draw the 'fat' statusbar if we are in a mac. does not look good on win/linux -> USELESS?
        BottomBarSize bottombarSize = Platform.isMac() ? BottomBarSize.LARGE : BottomBarSize.SMALL;

        TriAreaComponent bottomBar = MacWidgetFactory.createBottomBar(bottombarSize);
        statusLabel = MacWidgetFactory.createEmphasizedLabel("200 Files, 2,9 GB");

        // make status label 2 px smaller
        statusLabel.setFont(statusLabel.getFont().deriveFont(statusLabel.getFont().getSize() - 2f));

        bottomBar.addComponentToCenter(statusLabel);

        //Font statusButtonFont = statusLabel.getFont().deriveFont(statusLabel.getFont().getSize()-2f)

        // control button code
        Icon plusIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/plus.png")));

        JButton addProjectButton = new JButton();
        addProjectButton.setIcon(plusIcon);
        addProjectButton.setToolTipText("Add Project...");

        addProjectButton.putClientProperty("JButton.buttonType", "segmentedTextured");
        addProjectButton.putClientProperty("JButton.segmentPosition", "first");

        if (Platform.isWin()) {
            addProjectButton.setFocusPainted(false);
        }

        bottomBar.addComponentToLeft(addProjectButton);

        Icon minusIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/minus.png")));
        JButton removeProjectButton = new JButton();
        removeProjectButton.setIcon(minusIcon);
        removeProjectButton.setToolTipText("Remove Project...");

        removeProjectButton.putClientProperty("JButton.buttonType", "segmentedTextured");
        removeProjectButton.putClientProperty("JButton.segmentPosition", "last");

        if (Platform.isWin()) {
            addProjectButton.setFocusPainted(false);
        }

        ButtonGroup group = new ButtonGroup();
        group.add(addProjectButton);
        group.add(removeProjectButton);


        bottomBar.addComponentToLeft(addProjectButton, 0);
        bottomBar.addComponentToLeft(removeProjectButton);

        /*
        JButton playPauseProjectButton = new JButton(">/||");
        if(!Platform.isMac()) playPauseProjectButton.setFont(statusButtonFont);
        playPauseProjectButton.putClientProperty("JButton.buttonType", "textured");
        bottomBar.addComponentToLeft(playPauseProjectButton, 0);


        playPauseProjectButton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent event) {
        new SheetTest();
        }
        });
         */

        // connection info
        Icon loginIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/login.png")));
        connectionButton = new JButton();
        connectionButton.setIcon(loginIcon);
        connectionButton.setHorizontalTextPosition(SwingConstants.LEFT);

        connectionButton.putClientProperty("JButton.buttonType", "textured");
        connectionButton.putClientProperty("JComponent.sizeVariant", "small");
        if (!Platform.isMac()) {
            connectionButton.setFont(connectionButton.getFont().deriveFont(connectionButton.getFont().getSize() - 2f));
        }

        connectionButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem signInOut = new JMenuItem(getResourceMap().getString(
                        getCore().isSignedIn() ? "menuSignOut" : "menuSignIn"));

                signInOut.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent actionEvent) {
                        if (!JakeMainView.getMainView().getCore().isSignedIn()) {
                            JakeMainView.getMainView().setContextPanelView(JakeMainView.ContextPanels.Login);
                        } else {
                            JakeMainView.getMainView().getCore().signOut();
                        }
                    }
                });

                menu.add(signInOut);

                // calculate contextmenu directly above signin-status button
                menu.show((JButton) event.getSource(), ((JButton) event.getSource()).getX(),
                        ((JButton) event.getSource()).getY() - 20);
            }
        });
        updateConnectionButton();
        bottomBar.addComponentToRight(connectionButton);

        return bottomBar;
    }
}
