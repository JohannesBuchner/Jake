/*
 * JakeMock2App.java
 */

package com.jakeapp.gui.swing;

import javax.swing.UIManager;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import com.jakeapp.gui.swing.controls.SheetableJFrame;

/**
 * The main class of the application.
 */
public class JakeMainApp extends SingleFrameApplication {

    public JakeMainApp() {
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        this.setMainFrame(new SheetableJFrame("Jake Long"));
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

        try {
            // use the improved win laf
            // if (Platform.isWin()) {
            //     UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
            // } else {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // }
            //  UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            //  UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

        } catch (Exception e) {
            System.out.println("LAF Error: " + e.getMessage());
        }


        // MacOSX specific: set menu name to 'Jake'
        // has to be called VERY early to succeed (prior to any gui stuff, later calls will be ignored)
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Jake");

        launch(JakeMainApp.class, args);
    }
}
