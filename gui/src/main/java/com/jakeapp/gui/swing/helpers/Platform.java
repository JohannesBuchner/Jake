/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.helpers.pftools.AbstractPfTools;
import com.jakeapp.gui.swing.helpers.pftools.MacPfTools;
import com.jakeapp.gui.swing.helpers.pftools.NullPfTools;
import com.jakeapp.gui.swing.helpers.pftools.WinPfTools;
import com.jakeapp.gui.swing.helpers.styler.MacStyler;
import com.jakeapp.gui.swing.helpers.styler.NullStyler;
import com.jakeapp.gui.swing.helpers.styler.Styler;
import com.jakeapp.gui.swing.helpers.styler.WinStyler;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author studpete
 */
public class Platform {
	private static final Logger log = Logger.getLogger(Platform.class);

	/**
	 * Tests if a dialog should be shown as sheet.
	 * Only enabled on mac.
	 *
	 * @return
	 */
	public static boolean isSetShowAsSheet() {
		return isMac();
	}

	enum OperatingSystem {
		Windows, Mac, Linux, Other
	}

	private static OperatingSystem os;

	private static Styler styler;
	private static AbstractPfTools pftools;

	// static initializeJakeMainHelper code
	static {
		if (System.getProperty("os.name").startsWith("Mac OS")) {
			os = OperatingSystem.Mac;
		} else if (System.getProperty("os.name").startsWith("Windows")) {
			os = OperatingSystem.Windows;
		} else if (System.getProperty("os.name").startsWith("Lin")) {
			os = OperatingSystem.Linux;
		} else {
			os = OperatingSystem.Other;
		}

		if (isMac()) {
			styler = new MacStyler();
			pftools = new MacPfTools();
		} else if (isWin()) {
			styler = new WinStyler();
			pftools = new WinPfTools();
		} else {
			styler = new NullStyler();
			pftools = new NullPfTools();
		}

		log.info("Detected Platform: " + os + " " + System.getProperty("os.name") + ", Loaded Styler: " +
				  styler.getClass().getName());
	}


	/**
	 * Get's the version of Java currently running.
	 *
	 * @return the version of Java that is running.
	 */
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}

	/**
	 * True if this JVM is running on a Mac.
	 *
	 * @return true if this JVM is running on a Mac.
	 */
	public static boolean isMac() {
		return os == OperatingSystem.Mac;
	}

	// FIXME: we don't support tiger anyway
	public static boolean isMacTiger() {
		return System.getProperty("os.version").startsWith("10.4");
	}

	public static boolean isMacLeopard() {
		return System.getProperty("os.version").startsWith("10.5");
	}

	/**
	 * True if this JVM is running on Windows.
	 *
	 * @return true if this JVM is running on Windows.
	 */
	public static boolean isWin() {
		return os == OperatingSystem.Windows;
	}

	/**
	 * True if this JVM is running on Linux.
	 *
	 * @return true if this JVM is running on Linux.
	 */
	public static boolean isLin() {
		return os == OperatingSystem.Linux;
	}

	/**
	 * Returns the pf dependant styler.
	 *
	 * @return
	 */
	public static Styler getStyler() {
		return styler;
	}

	/**
	 * Returns the pf dependant toolkit
	 *
	 * @return
	 */
	public static AbstractPfTools getToolkit() {
		return pftools;
	}

	/**
	 * Overrides AWT's default guess of what to use as our windows' WM_CLASS.
	 * <p/>
	 * AWT's XToolkit guesses a WM_CLASS for us based on the bottom-most class name in the stack trace of the thread that causes its construction.
	 * For those of our application that launch from e.util.Launcher, that means they all get the WM_CLASS "e-util-Launcher".
	 * Even those that don't, get a fully-qualified name such as "e-tools-FatBits" or "terminator-Terminator".
	 * These names aren't usually very important unless you're doing some ugly application-specific hacking in your window manager.
	 * Sadly, though, they show through in certain cases:
	 * 1. When space gets too tight for GNOME's panel to have an icon for each window, it starts collapsing them by application, and uses WM_CLASS as the application name.
	 * 2.If you use the GNOME/the Java Desktop System's Alt-PrtScr screenshot tool, its default filename is "Screenshot-<WM_CLASS>".
	 * There are probably more examples, but these are enough to warrant a solution.
	 * Given that we know what our application calls itself, we can use reflection to override AWT's default guess.
	 */
	public static void fixWmClass() {
		try {
			Toolkit xToolkit = Toolkit.getDefaultToolkit();
			java.lang.reflect.Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
			awtAppClassNameField.setAccessible(true);
			awtAppClassNameField.set(xToolkit, AppUtilities.getAppName());
		} catch (Throwable th) {
			log.warn("Failed to fix WM_CLASS for " + AppUtilities.getAppName() + " windows.", th);
		}
	}


	/**
	 * Currently not used.
	 * Event counter feature - like in mail.app.
	 * Should be possible on windows too.
	 * (tray icon change?)
	 *
	 * @param num
	 */
	public static void setEventCounter(int num) {
		log.debug("set event count to: " + num);
		java.awt.Image originalIcon = Toolkit.getDefaultToolkit().getImage(Platform.class.getResource("/icons/jakeapp-large.png"));
		ImageIcon ico = new ImageIcon(originalIcon);

		BufferedImage newIcon = new BufferedImage(ico.getIconWidth(), ico.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = (Graphics2D) newIcon.getGraphics();

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.decode("#E40000"));

		graphics.drawImage(originalIcon, 0, 0, null);

		graphics.fillOval(ico.getIconWidth() - 40, 0, 35, 35);

		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Helvetica", Font.BOLD, 23));
		graphics.drawString(Integer.toString(num), ico.getIconWidth() - 28, 25);

		graphics.dispose();

		// TODO: find a way to set app icon
		// may need jni interface!
		//setApplicationIconImage(newIcon);
	}

	// prevent construction
	private Platform() {

	}
}
