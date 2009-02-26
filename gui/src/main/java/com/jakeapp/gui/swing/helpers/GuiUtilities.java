package com.jakeapp.gui.swing.helpers;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author: studpete
 */
@SuppressWarnings("serial")
public class GuiUtilities {
	private static final Logger log = Logger.getLogger(GuiUtilities.class);

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
	 * @param row
	 * @return
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

	/**
	 * Guesses whether setFrameAlpha is likely to work.
	 * @return
	 */
	public static boolean canSetFrameAlpha() {
		// setFrameAlpha works on any version of Mac OS we can still run on.
		// setFrameAlpha may or may not work on any given Linux, and we've no good way of knowing.
		// setFrameAlpha only works on Windows if you're running Java 6 "6u10" (it doesn't even work on Java 7 yet).
		return !Platform.isWin() || getAwtUtilitiesSetWindowOpacity() != null;
	}

	private static Method getAwtUtilitiesSetWindowOpacity() {
		// When we require Java 6, we can move this inside setFrameAlpha (and remove the workarounds currently there).
		// Until then, it's useful as a kind of "feature test" for setFrameAlpha on Windows (via canSetFrameAlpha).
		try {
			// This is only available on 6u10 and later (see Sun bug 6633275).
			// com.sun.awt.AWTUtilities.setWindowOpacity(frame, alpha);
			final Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
			return awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
		} catch (Exception ex) {
			// No setWindowOpacity for you, then. Not unexpected.
			return null;
		}
	}

	/**
	 * Sets the opacity (1.0 => fully opaque, 0.0 => fully transparent) of the given Frame.
	 * http://elliotth.blogspot.com/2007/08/transparent-java-windows-on-x11.html
	 * @param frame
	 * @param alpha
	 */
	public static void setFrameAlpha(JFrame frame, double alpha) {
		final Method setWindowOpacityMethod = getAwtUtilitiesSetWindowOpacity();
		if (setWindowOpacityMethod != null) {
			try {
				setWindowOpacityMethod.invoke(null, frame, (float) alpha);
			} catch (Throwable th) {
				//log.warn("com.sun.awt.AWTUtilities.setWindowOpacity failed.", th);
			}
			return;
		}

		try {
			Field peerField = Component.class.getDeclaredField("peer");
			peerField.setAccessible(true);
			Object peer = peerField.get(frame);
			if (peer == null) {
				return;
			}

			if (Platform.isMac()) {
				frame.getRootPane().putClientProperty("Window.alpha", alpha);

			} else if (Platform.isWin()) {
				// If you weren't taken care of above, we have no work-around for you.
			} else {
				// FIXME: remove this when everyone has setWindowOpacity, which is likely to be long before Compiz becomes less trouble than it's worth.

				// long windowId = peer.getWindow();
				Class<?> xWindowPeerClass = Class.forName("sun.awt.X11.XWindowPeer");
				Method getWindowMethod = xWindowPeerClass.getMethod("getWindow");
				long windowId = (Long) getWindowMethod.invoke(peer, new Object[0]);

				long value = (int) (0xff * alpha) << 24;
				// sun.awt.X11.XAtom.get("_NET_WM_WINDOW_OPACITY").setCard32Property(windowId, value);
				Class<?> xAtomClass = Class.forName("sun.awt.X11.XAtom");
				Method getMethod = xAtomClass.getMethod("get", String.class);
				Method setCard32PropertyMethod = xAtomClass.getMethod("setCard32Property", long.class, long.class);
				setCard32PropertyMethod.invoke(getMethod.invoke(null, "_NET_WM_WINDOW_OPACITY"), windowId, value);
			}
		} catch (Throwable th) {
			log.warn("Failed to apply frame alpha.", th);
		}
	}

	// prevent instantiation
	private GuiUtilities() {

	}
}
