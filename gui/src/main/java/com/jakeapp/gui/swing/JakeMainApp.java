/*
 * JakeMock2App.java
 */

package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.controls.SheetableJFrame;
import com.jakeapp.gui.swing.helpers.Platform;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

/**
 * The main class of the application.
 */
public class JakeMainApp extends SingleFrameApplication implements ProjectSelectionChanged {
    private static final Logger log = Logger.getLogger(JakeMainApp.class);
    private static JakeMainApp app;
    private ICoreAccess core;
    private Project project = null;
    private List<ProjectSelectionChanged> projectSelectionChanged =
            new LinkedList<ProjectSelectionChanged>();

    public JakeMainApp() {
        this.app = this;

        // initializeJakeMainHelper the core connection
        setCore(new CoreAccessMock());
    }

    /**
     * At startup create and show the main frame of the application.
     * (Called from the Swing Application Framework)
     */
    @Override
    protected void startup() {
        this.setMainFrame(new SheetableJFrame("Jake"));
        show(new JakeMainView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     *
     * @return the instance of JakeMock2App
     */
    public static JakeMainApp getApplication() {
        return Application.getInstance(JakeMainApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {

        // we use the system laf everywhere except linux.
        // gtk is ugly here - we us nimbus (when available)
        try {
            if (Platform.isWin() || Platform.isMac()) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                // try to use nimbus (avaailable starting j6u10)
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception r) {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            }
        } catch (Exception e) {
            log.warn("LAF Exception: ", e);
        }

        //System.setProperty("awt.useSystemAAFontSettings","on");
        //System.setProperty("swing.aatext", "true");

        // MacOSX specific: set menu name to 'Jake'
        // has to be called VERY early to succeed (prior to any gui stuff, later calls will be ignored)
        if (Platform.isMac()) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Jake");
        }

        launch(JakeMainApp.class, args);
    }


    /**
     * Returns single instance of the App.
     *
     * @return
     */
    public static JakeMainApp getApp() {
        return app;
    }

    public ICoreAccess getCore() {
        return core;
    }

    public void setCore(ICoreAccess core) {
        this.core = core;
    }

    public Project getProject() {
        return project;
    }


    public void setProject(Project project) {

        //TODO: hack to test the "no project-state"
        if (project != null && project.getRootPath() == null) {
            project = null;
        }

        if (this.project != project) {
            this.project = project;

            // fire the event and relay to all items/components/actions/panels
            fireProjectSelectionChanged();
        }
    }

    /**
     * Fires a project selection change event, calling all
     * registered members of the event.
     */
    private void fireProjectSelectionChanged() {
        for (ProjectSelectionChanged psc : projectSelectionChanged) {
            try {
                psc.setProject(getProject());
            } catch (RuntimeException ex) {
                log.error("Catched an exception while setting the new project: ", ex);
            }
        }
    }

    public void addProjectSelectionChangedListener(ProjectSelectionChanged psc) {
        projectSelectionChanged.add(psc);
    }

    public void removeProjectSelectionChangedListener(ProjectSelectionChanged psc) {
        if (projectSelectionChanged.contains(psc)) {
            projectSelectionChanged.remove(psc);
        }
    }
}
