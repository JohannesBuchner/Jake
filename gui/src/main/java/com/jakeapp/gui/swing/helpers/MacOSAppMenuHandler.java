package com.jakeapp.gui.swing.helpers;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.dialogs.JakeAboutDialog;

import javax.swing.*;

/**
 * User: studpete
 * Date: Dec 26, 2008
 * Time: 11:41:47 AM
 */
public class MacOSAppMenuHandler extends Application {

    public MacOSAppMenuHandler() {
        addApplicationListener(new AppMenuHandler());
    }

    /**
     * The Mac Java Application Handler is installed in JakeMainApp.
     */
    class AppMenuHandler extends ApplicationAdapter {

        /**
         * Uses the mac specific about box.
         *
         * @param event
         */
        public void handleAbout(ApplicationEvent event) {
            new JakeAboutDialog(new JFrame()).setVisible(true);
            event.setHandled(true);
        }

        /**
         * Captures Quit from application menu.
         *
         * @param event
         */
        public void handleQuit(ApplicationEvent event) {
            JakeMainView.getMainView().getApplication().quit(null);
        }

        /**
         * Captures Preferences... from application menu.
         *
         * @param event
         */
        public void handlePreferences(ApplicationEvent event) {
            // TODO: handle preferences
        }

        /**
         * Captures clicking the Dock Icon when App is invisible (closed)
         *
         * @param event
         */
        public void handleReOpenApplication(ApplicationEvent event) {
            JakeMainView.getMainView().getFrame().setVisible(true);
        }

        // TODO: handle .jake - files!
        /*
        public void handleOpenFile(ApplicationEvent event) {
            System.out.println("handleOpen!");
        }
        */
    }
}