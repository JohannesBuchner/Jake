package com.jakeapp.gui.swing.helpers;

import javax.swing.*;
import java.awt.*;

/**
 * Allows for an icon to be decorated with smaller sub-icons ("visual annotation").
 *
 * Based on code from R.J. Lorimer.
 */
public class DecoratedIcon implements Icon {
    private Icon originalIcon;
    private Icon decorationIcon;
    private int xDiff;
    private int yDiff;
    private Location location;

    public enum Location {
        UPPER_LEFT, UPPER_RIGHT, LOWER_LEFT, LOWER_RIGHT
    }

	public DecoratedIcon(Icon original, Icon decoration) {
        this(original, decoration, Location.LOWER_RIGHT);
    }

    public DecoratedIcon(Icon original, Icon decoration, Location loc) {

        this.location = loc;
        this.originalIcon = original;
        this.decorationIcon = decoration;
        if (
                decoration.getIconHeight() > original.getIconHeight() ||
                        decoration.getIconWidth() > original.getIconWidth()) {
            throw new IllegalArgumentException("Decoration must be smaller than the original");
        }
        this.xDiff = originalIcon.getIconWidth() - decorationIcon.getIconWidth();
        this.yDiff = originalIcon.getIconHeight() - decorationIcon.getIconHeight();
    }

    public int getIconHeight() {
        return originalIcon.getIconHeight();
    }

    public int getIconWidth() {
        return originalIcon.getIconWidth();
    }

    public void paintIcon(Component owner, Graphics g, int x, int y) {
        // paint original first
        originalIcon.paintIcon(owner, g, x, y);

        int decorationX = x;
        int decorationY = y;
        // augment x.
        if (location == Location.UPPER_RIGHT || location == Location.LOWER_RIGHT) {
            decorationX += xDiff;
        }
        // augment y.
        if (location == Location.LOWER_LEFT || location == Location.LOWER_RIGHT) {
            decorationY += yDiff;
        }

        decorationIcon.paintIcon(owner, g, decorationX, decorationY);
    }
}