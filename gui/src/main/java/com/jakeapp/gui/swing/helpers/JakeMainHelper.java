package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainView;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * JakeMainHelper has static functions that are used all across the ui codebase.
 * User: studpete
 * Date: Dec 21, 2008
 * Time: 5:41:44 PM
 */
public class JakeMainHelper {
    private static final Logger log = Logger.getLogger(JakeMainHelper.class);

    public static void initializeJakeMainHelper() {
    }


    public static String getPluralModifer(int clickCount) {
        return clickCount == 1 ? "" : "s";
    }

    public static String printProjectStatus(Project project) {
        // TODO: determine status
        return "Project is ...TODO!";
    }

    /**
     * Evaluates the Project and returns a Start/Stop-String depending on its state.
     *
     * @param project
     * @return String with either Start or Stop.
     */
    public static String getProjectStartStopString(Project project) {
        String startStopString;
        if (!project.isStarted()) {
            startStopString = JakeMainView.getMainView().getResourceMap().getString("projectTreeStartProject");
        } else {
            startStopString = JakeMainView.getMainView().getResourceMap().getString("projectTreeStopProject");
        }

        return startStopString;
    }


    static public String getSize(long size) {
        return getSize(size, 2, true, true);
    }

    static public String getSize(long size, int precision) {
        return getSize(size, precision, true, true);
    }

    static public String getSize(long size, int precision, boolean longName) {
        return getSize(size, precision, longName, true);
    }

    /**
     * Get the human-readable size for an amount of bytes
     *
     * @param size      : the number of bytes to be converted
     * @param precision : number of decimal places to round to;
     *                  optional - defaults to 2
     * @param longName  : whether or not the returned size tag
     *                  should be unabbreviated (ie "Gigabytes"
     *                  or "GB"); optional - defaults to true
     * @param realSize  : whether or not to use the real (base
     *                  1024) or commercial (base 1000) size;
     *                  optional - defaults to true
     * @return String           : the converted size
     */
    static public String getSize(long size, int precision,
                                 boolean longName, boolean realSize) {
        int base = realSize ? 1024 : 1000;
        int pos = 0;
        double decSize = (double) size;
        while (decSize > base) {
            decSize /= base;
            pos++;
        }
        String prefix = getSizePrefix(pos);
        String sizeName = longName ? prefix + "bytes" : "" + prefix.charAt(0) + "B";
        sizeName = sizeName.substring(0, 1).toUpperCase() + sizeName.substring(1);
        int num = (int) Math.pow(10, precision);
        return (Math.round(decSize * num) / num) + " " + sizeName;
    }

    /**
     * @param pos : the distence along the metric scale relitive to 0
     * @return string : the prefix
     */
    static public String getSizePrefix(int pos) {
        switch (pos) {
            case 0:
                return " ";
            case 1:
                return "kilo";
            case 2:
                return "mega";
            case 3:
                return "giga";
            case 4:
                return "tera";
            case 5:
                return "peta";
            case 6:
                return "exa";
            case 7:
                return "zetta";
            case 8:
                return "yotta";
            case 9:
                return "xenna";
            case 10:
                return "w-";
            case 11:
                return "vendeka";
            case 12:
                return "u-";
            default:
                return "?-";
        }
    }

    public static void showJakeWebsite() {
        try {
            Desktop.getDesktop().browse(new URI(JakeMainView.getMainView().getResourceMap().getString("JakeWebsite")));
        } catch (IOException e) {
            log.warn("Unable to open Website!", e);
        } catch (URISyntaxException e) {
            log.warn("Unable to open Website, invalid syntax", e);
        }
    }

    @Deprecated
    /**
     * Checks if the project has a valid root path (i.e. one that exists and
     * is a directory)
     *
     * @param project The project to validate
     * @return Whether or not the root path is valid
     */
    public static boolean hasValidRootPath(Project project) {
        // TODO: THIS SHOULD NOT BE DONE HERE!
        // Fucking ugly hack until we decide on how to handle invalid project root paths
        File prjfolder = new File(project.getRootPath());
        return !(!prjfolder.exists() || !prjfolder.isDirectory());
    }

    /**
     * Makes a fancy relative time description from a date object (e.g.
     * "2 minutes ago", "5 days ago", "2 years ago", ...)
     *
     * @param date Any date
     * @return A string containing a relative description of the date
     */
    public static String getRelativeTime(Date date) {
        // TODO: Implement me
        return "Implement me :)";
    }
}
