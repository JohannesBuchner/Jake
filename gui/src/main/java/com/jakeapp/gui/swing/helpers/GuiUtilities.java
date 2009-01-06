package com.jakeapp.gui.swing.helpers;

import javax.swing.*;
import java.awt.*;
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

    // prevent instantiation
    private GuiUtilities() {

    }
}
