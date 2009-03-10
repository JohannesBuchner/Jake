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
 * Basic Tray Menu Implementation
 * Will be expanded in later versions!
 */
public class JakeTrayIcon {
	private static final Logger log = Logger.getLogger(JakeTrayIcon.class);
	private MenuItem showHideJakeTrayMenuItem;

	public JakeTrayIcon() {
		final TrayIcon trayIcon;

		if (SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();
			ImageIcon image = ImageLoader.get(getClass(), "/icons/jakeapp.png");

			MouseListener mouseListener = new MouseListener() {

				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2 && SwingUtilities
									.isLeftMouseButton(e) && !Platform.isMac()) {
						JakeMainView.toggleShowHideMainWindow();
					}
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
					// update menu
					showHideJakeTrayMenuItem.setLabel(getShowHideWindowString());

				}

				public void mouseReleased(MouseEvent e) {
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
					JakeMainView.toggleShowHideMainWindow();
				}
			};

			PopupMenu popup = new PopupMenu();
			showHideJakeTrayMenuItem = new MenuItem(getShowHideWindowString());
			showHideJakeTrayMenuItem.addActionListener(showJakeListener);
			MenuItem defaultItem = new MenuItem("Quit Jake");
			defaultItem.addActionListener(exitListener);
			popup.add(showHideJakeTrayMenuItem);
			popup.add(defaultItem);

			trayIcon = new TrayIcon(image.getImage(), "Jake", popup);


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
		JakeMainView.getMainView().quit();
	}

	private String getShowHideWindowString() {
		return JakeMainView.getResouceMap().getString(
						JakeMainView.isMainWindowVisible() ? "windowHide" : "windowShow");
	}
}
