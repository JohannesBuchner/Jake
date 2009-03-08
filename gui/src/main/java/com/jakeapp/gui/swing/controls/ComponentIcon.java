/* Copyright (c) 20006-2007 Timothy Wall, ALL Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package com.jakeapp.gui.swing.controls;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JComponent;

/** Provides the contents of a {@link Component} in Icon form.  Use this in
 * conjunction with {@link ScaledIcon} to provide component thumbnails.<p>
 * NOTE: {@link JComponent#paintAll} requires that {@link Component#isShowing}
 * return true before it will paint anything.
 */
// TODO: Use SwingUtilities.paintComponent
public class ComponentIcon implements Icon {
    private JComponent component;
    private boolean includeBorder;
    /** Iconify the given component, excluding its border. */
    public ComponentIcon(JComponent c) {
        this(c, false);
    }
    /** Iconify the given component, indicating whether to include its border. 
     */
    public ComponentIcon(JComponent c, boolean includeBorder) {
        this.component = c;
        this.includeBorder = includeBorder;
    }
    public int getIconHeight() {
        int h = component.getHeight();
        if (h != 0 && !includeBorder) {
            Insets insets = component.getInsets();
            if (insets != null) {
                h -= insets.top + insets.bottom;
            }
        }
        return h;
    }
    
    public int getIconWidth() {
        int w = component.getWidth();
        if (w != 0 && !includeBorder) {
            Insets insets = component.getInsets();
            if (insets != null) {
                w -= insets.left + insets.right;
            }
        }
        return w;
    }

    public void paintIcon(Component c, Graphics graphics, int x, int y) {
        boolean db = component.isDoubleBuffered();
        try {
            if (!includeBorder) {
                Insets insets = component.getInsets();
                x -= insets.left;
                y -= insets.top;
            }
            Graphics2D g = (Graphics2D)graphics.create(x, y, getIconWidth(), getIconHeight());
            if (!component.isDisplayable()) {
                component.addNotify();
            }
            component.setDoubleBuffered(false);
            component.paintAll(g);
        }
        finally {
            component.setDoubleBuffered(db);
        }
    }
}
