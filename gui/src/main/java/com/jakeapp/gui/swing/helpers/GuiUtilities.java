package com.jakeapp.gui.swing.helpers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * @author: studpete
 */
public class GuiUtilities {
	/**
	 * An invisible cursor, useful if you want to hide the cursor when the
	 * user is typing.
	 */
	public static final Cursor INVISIBLE_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR), new Point(0, 0), "invisible");
	private static final Color MAC_OS_ALTERNATE_ROW_COLOR = new Color(0.92f, 0.95f, 0.99f);
	private static final KeyStroke ESC = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

	public static javax.swing.border.Border makeEmptyBorderResource(int top, int left, int bottom, int right) {
		return new javax.swing.plaf.BorderUIResource.EmptyBorderUIResource(top, left, bottom, right);
	}

	public static void selectFileInFileViewer(String fullPathname) {
		// FIXME: add GNOME support, if possible. See https://launchpad.net/distros/ubuntu/+source/nautilus/+bug/57537
		// FIXME: make into an action that also supplies an appropriate name for the action on the current platform.
		if (Platform.isMac()) {
			ProcessUtilities.spawn(null, new String[]{"/usr/bin/osascript", "-e", "tell application \"Finder\" to select \"" + fullPathname + "\" as POSIX file", "-e", "tell application \"Finder\" to activate"});
		} else if (Platform.isWin()) {
			// See "Windows Explorer Command-Line Options", http://support.microsoft.com/default.aspx?scid=kb;EN-US;q152457
			ProcessUtilities.spawn(null, new String[]{"Explorer", "/select," + fullPathname});
		}
	}

	/**
	 * Returns the appropriate background color for the given row index.
	 */
	public static Color backgroundColorForRow(int row) {
		if (Platform.isLin()) {
			return (row % 2 == 0) ? Color.WHITE : UIManager.getColor("Table.background");
		} else if (Platform.isMac()) {
			return (row % 2 == 0) ? Color.WHITE : MAC_OS_ALTERNATE_ROW_COLOR;
		}
		return UIManager.getColor("Table.background");
	}


	public static void installMacCloseHandler(JFrame frame) {
		// Mac OS uses command-W to close a window using the keyboard. Unlike Linux and Windows' alt-f4, though, this isn't done by the window manager.
		if (Platform.isMac()) {
			KeyStroke commandW = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.META_MASK, false);
			closeOnKeyStroke(frame, commandW);
		}
	}

	public static void closeOnEsc(JDialog dialog) {
		closeOnKeyStroke(dialog.getRootPane(), ESC);
	}

	public static void closeOnEsc(JFrame frame) {
		closeOnKeyStroke(frame.getRootPane(), ESC);
	}

	public static void closeOnKeyStroke(JFrame frame, KeyStroke keyStroke) {
		closeOnKeyStroke(frame.getRootPane(), keyStroke);
	}

	private static void closeOnKeyStroke(JRootPane rootPane, KeyStroke keyStroke) {
		final String CLOSE_ACTION_NAME = "com.jakeapp.gui.swing.helpers.GuiUtilities.CloseFrameOnKeyStroke";
		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, CLOSE_ACTION_NAME);
		rootPane.getActionMap().put(CLOSE_ACTION_NAME, CLOSE_ACTION);
	}


	private static final Action CLOSE_ACTION = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			Window window = SwingUtilities.getWindowAncestor((Component) e.getSource());
			// Dispatch an event so it's as if the window's close button was clicked.
			// The client has to set up the right behavior for that case.
			window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
		}
	};

	// prevent instantiation
	private GuiUtilities() {

	}
}
