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
	private MenuItem showHideJakeTrayMenuItem;

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

					// update menu
					showHideJakeTrayMenuItem.setLabel(getShowHideWindowString());

				}

				public void mouseReleased(MouseEvent e) {
					System.out.println("Tray Icon - Mouse released!");
				}
			};

			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					quit();
				}
			};

			ActionListener showJakeListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					log.info("Showing main window");
					toggleShowHideMainWindow();
				}
			};

			PopupMenu popup = new PopupMenu();
			showHideJakeTrayMenuItem = new MenuItem(getShowHideWindowString());
			showHideJakeTrayMenuItem.addActionListener(showJakeListener);
			MenuItem defaultItem = new MenuItem("Quit Jake");
			defaultItem.addActionListener(exitListener);
			popup.add(showHideJakeTrayMenuItem);
			popup.add(defaultItem);

			trayIcon = new TrayIcon(image, "Jake", popup);


			/*  ActionListener actionListener = new ActionListener() {
												 public void actionPerformed(ActionEvent e) {
													  JOptionPane.showMessageDialog(null,
																 "An Action Event Has Been Performed!");
												 }
											};
							*/

			trayIcon.setImageAutoSize(true);
			// trayIcon.addActionListener(actionListener);
			trayIcon.addMouseListener(mouseListener);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				log.warn("TrayIcon could not be added.");
			}

		} else {

			//  System Tray is not supported

		}

	}

	private void quit() {
//        // TODO: make save shutdown
//        log.info("Exiting...");
//        System.exit(0);
		JakeMainView.getMainView().quit();
	}

	private String getShowHideWindowString() {
		return JakeMainView.getResouceMap().getString(isMainWindowVisible() ? "windowHide" : "windowShow");
	}

	private void toggleShowHideMainWindow() {
		if (!isMainWindowVisible()) {
			//JakeMainView.getMainView().getFrame().setExtendedState(JFrame.ICONIFIED);
		}
		JakeMainView.getMainView().getFrame().setVisible(!isMainWindowVisible());
		if (isMainWindowVisible()) {
			JakeMainView.getMainView().getFrame().requestFocus();
			//JakeMainView.getMainView().getFrame().setExtendedState(JFrame.NORMAL);
		}
	}

	private boolean isMainWindowVisible() {
		return JakeMainView.getMainView().getFrame().isVisible();
	}
}
