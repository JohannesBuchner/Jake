package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.JakeMainView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * User: studpete
 * Date: Dec 25, 2008
 * Time: 12:33:18 PM
 */
public class JakeTrayIcon {
    private static final Logger log = Logger.getLogger(JakeTrayIcon.class);

    public JakeTrayIcon() {
        final TrayIcon trayIcon;

        if (SystemTray.isSupported()) {

            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/jakeapp.png"));

            MouseListener mouseListener = new MouseListener() {

                public void mouseClicked(MouseEvent e) {
                    System.out.println("Tray Icon - Mouse clicked!");

                    if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e) && !Platform.isMac()) {
                        toggleShowHideMainWindow();
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    System.out.println("Tray Icon - Mouse entered!");
                }

                public void mouseExited(MouseEvent e) {
                    System.out.println("Tray Icon - Mouse exited!");
                }

                public void mousePressed(MouseEvent e) {
                    System.out.println("Tray Icon - Mouse pressed!");
                }

                public void mouseReleased(MouseEvent e) {
                    System.out.println("Tray Icon - Mouse released!");
                }
            };

            ActionListener exitListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // TODO: make save shutdown
                    log.info("Exiting...");
                    System.exit(0);
                }
            };

            ActionListener showJakeListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    log.info("Showing main window");
                    toggleShowHideMainWindow();
                }
            };

            PopupMenu popup = new PopupMenu();
            MenuItem showJakeItem = new MenuItem("Show/Hide");
            showJakeItem.addActionListener(showJakeListener);
            MenuItem defaultItem = new MenuItem("Quit Jake");
            defaultItem.addActionListener(exitListener);
            popup.add(showJakeItem);
            popup.add(defaultItem);

            trayIcon = new TrayIcon(image, "Jake", popup);

            /*
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    trayIcon.displayMessage("Action Event",
                            "An Action Event Has Been Performed!",
                            TrayIcon.MessageType.INFO);
                }
            };
            */

            trayIcon.setImageAutoSize(true);
            //trayIcon.addActionListener(actionListener);
            trayIcon.addMouseListener(mouseListener);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }

        } else {

            //  System Tray is not supported

        }

    }

    private void toggleShowHideMainWindow() {
        JakeMainView.getMainView().getFrame().setVisible(!JakeMainView.getMainView().getFrame().isVisible());
    }
}
