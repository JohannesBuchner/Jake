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

    private static Styler styler;

    // static initializeJakeMainHelper code
    static {

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
        System.out.println(System.getProperty("os.name"));
        return System.getProperty("os.name").startsWith("Mac OS");
    }

    /**
     * True if this JVM is running on Windows.
     *
     * @return true if this JVM is running on Windows.
     */
    public static boolean isWin() {
        System.out.println(System.getProperty("os.name"));
        return System.getProperty("os.name").startsWith("Win");
    }

    /**
     * True if this JVM is running on Linux.
     *
     * @return true if this JVM is running on Linux.
     */
    public static boolean isLin() {
        System.out.println(System.getProperty("os.name"));
        return System.getProperty("os.name").startsWith("Linux");
    }

    public static Styler getStyler() {
        return styler;
    }
}
