package com.jakeapp.gui.swing.helpers;

import javax.swing.*;
import java.io.File;

/**
 * Creates a fancy label for file and folder nodes in the FileTreeTable
 */
public class FileIconLabelHelper {
    private static Icon conflict;
    private static Icon haveLatest;
    private static Icon newerAvailable;
    private static Icon locallyChanged;
    private static Icon onlyExistsRemotely;

    public static enum State {
        NONE, CONFLICT, HAVE_LATEST, NEWER_AVAILABLE, LOCALLY_CHANGED, ONLY_EXISTS_REMOTELY
    }

    private static JFileChooser fileChooser = new JFileChooser();

    /**
     * 
     * @param file
     * @param state
     * @return
     */
    public static Icon getIcon(File file, State state) {
        return fileChooser.getIcon(file);
    }
}
