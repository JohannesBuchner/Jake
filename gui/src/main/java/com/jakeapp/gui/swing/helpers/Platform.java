/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.helpers.styler.MacStyler;
import com.jakeapp.gui.swing.helpers.styler.NullStyler;
import com.jakeapp.gui.swing.helpers.styler.Styler;
import com.jakeapp.gui.swing.helpers.styler.WinStyler;

/**
 * @author studpete
 */
public class Platform {

    /**
     * Returns the Path separator.
     * The Java version returns ':' for mac, which is outdated.
     * So we use this custom function to get the separator.
     *
     * @return pf specific path separator
     */
    public static String getPathSeparator() {
        if (isMac()) {
            return "/";
        } else {
            return System.getProperty("path.separator");
        }
    }

    enum OperatingSystem {
        Windows, Mac, Linux, Other
    }

    private static OperatingSystem os;

    private static Styler styler;

    // static initializeJakeMainHelper code
    static {
        if (System.getProperty("os.name").startsWith("Mac OS")) {
            os = OperatingSystem.Mac;
        } else if (System.getProperty("os.name").startsWith("Win")) {
            os = OperatingSystem.Windows;
        } else if (System.getProperty("os.name").startsWith("Lin")) {
            os = OperatingSystem.Linux;
        } else {
            os = OperatingSystem.Other;
        }

        if (isMac()) {
            styler = new MacStyler();
        } else if (isWin()) {
            styler = new WinStyler();
        } else {
            styler = new NullStyler();
        }
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

    public static Styler getStyler() {
        return styler;
    }
}
