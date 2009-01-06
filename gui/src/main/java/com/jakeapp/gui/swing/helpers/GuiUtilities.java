package com.jakeapp.gui.swing.helpers;

/**
 * @author: studpete
 */
public class GuiUtilities {
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

    // prevent instantiation
    private GuiUtilities() {

    }
}
