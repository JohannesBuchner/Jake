package com.jakeapp.gui.swing.helpers;

import javax.swing.*;
import java.io.File;
import java.util.Random;
import java.awt.*;

/**
 * Creates a fancy label for file and folder nodes in the FileTreeTable
 */
public class FileIconLabelHelper {
    private static Icon conflict = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                FileIconLabelHelper.class.getResource("/annotations/conflict.png")));
    private static Icon haveLatest = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                FileIconLabelHelper.class.getResource("/annotations/have_latest.png")));
    private static Icon newerAvailable = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                FileIconLabelHelper.class.getResource("/annotations/newer_available.png")));
    private static Icon locallyChanged = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                FileIconLabelHelper.class.getResource("/annotations/locally_changed.png")));
    private static Icon onlyExistsRemotely = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                FileIconLabelHelper.class.getResource("/annotations/only_exists_remotely.png")));
    

    private static Color conflictColor = new Color(255, 0, 0);
    private static Color haveLatestColor = new Color(51, 153, 0);
    private static Color newerAvailableColor = new Color(255, 153, 0);
    private static Color locallyChangedColor = new Color(0, 153, 255);
    private static Color onlyExistsRemotelyColor = new Color(153, 153, 153);

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
    public static Component getIconLabel(File file, State state) {
        JLabel label = new JLabel();
        Icon nativeIcon = fileChooser.getIcon(file);

        // File or folder name
        label.setText(file.getName());

        // Folders don't get annotations
        if(file.isDirectory()) {
            label.setIcon(nativeIcon);
            return label;
        }

        Icon annotatedIcon;

        // TODO: Fix me once we stop using random states
        // should be: switch(state) {

        State randomState = getRandomState();

        switch(randomState) {
            case CONFLICT:
                annotatedIcon = new DecoratedIcon(nativeIcon, conflict);
                label.setForeground(conflictColor);
                break;
            case HAVE_LATEST:
                annotatedIcon = new DecoratedIcon(nativeIcon, haveLatest);
                label.setForeground(haveLatestColor);
                break;
            case NEWER_AVAILABLE:
                annotatedIcon = new DecoratedIcon(nativeIcon, newerAvailable);
                label.setForeground(newerAvailableColor);
                break;
            case LOCALLY_CHANGED:
                annotatedIcon = new DecoratedIcon(nativeIcon, locallyChanged);
                label.setForeground(locallyChangedColor);
                break;
            case ONLY_EXISTS_REMOTELY:
                annotatedIcon = new DecoratedIcon(nativeIcon, onlyExistsRemotely);
                label.setForeground(onlyExistsRemotelyColor);
                break;
            case NONE:
            default:
                annotatedIcon = nativeIcon;
        }

        label.setIcon(annotatedIcon);

        return label;
    }

    /**
     * Testing method that gets a random state so we can demo the way it looks in the UI
     * 
     * TODO: Remove me
     * @return A random state
     */
    private static State getRandomState() {
        Random r = new Random();
        int n = r.nextInt(6);
        State[] values = State.values(); // this static method will get an array of 'e'
        return values[n];
    }
}
