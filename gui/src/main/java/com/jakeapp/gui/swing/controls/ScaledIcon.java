/* Copyright (c) 2006-2007 Timothy Wall, ALL Rights Reserved
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import javax.swing.Icon;

/** Provides a scaled version of a given icon.  Aspect ratio
 * is optionally preserved; the icon will be centered in any extra space
 * given its size.
 * @author twall
 */
// TODO: implement gravity when aspect ratio doesn't match
public class ScaledIcon implements Icon {
    
    private final Icon icon;
    private int width;
    private int height;
    private boolean preserveAspect;

    /** Create a scalable version of the given Icon. */
    public ScaledIcon(Icon icon) {
        this(icon, icon.getIconWidth(), icon.getIconHeight());
    }
    
    /** Create an icon that properly scales to the desired size.  The aspect
     * ratio will be preserved.
     */
    public ScaledIcon(Icon icon, int width, int height) {
        this(icon, width, height, true);
    }
    
    /** Create an icon that properly scales to the desired size, and
     * whether to preserve the aspect ratio.
     */
    public ScaledIcon(Icon icon, int width, int height, boolean preserveAspect) {
        this.icon = icon;
        this.preserveAspect = preserveAspect;
        setSize(width, height);
    }

    public void setPreserveAspect(boolean p) {
        preserveAspect = p;
    }
    
    public void setSize(Dimension size) {
        setSize(size.width, size.height);
    }
    
    public void setSize(int w, int h) {
        if (preserveAspect) {
            double requested = (double)w/h;
            double required = (double)icon.getIconWidth()/icon.getIconHeight();
            if (requested < required) {
                h = Math.max(1, (int)(w / required));
            }
            else if (requested > required) {
                w = Math.max(1, (int)(h * required));
            }
        }
        this.width = w;
        this.height = h;
    }

    /** Paint the icon scaled appropriately. */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D)g;
        RenderingHints oldHints = g2d.getRenderingHints();
        AffineTransform oldTransform = g2d.getTransform();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                             RenderingHints.VALUE_RENDER_QUALITY);
        double xscale = (double)getIconWidth() / icon.getIconWidth();
        double yscale = (double)getIconHeight() / icon.getIconHeight();
        g2d.translate(x, y);
        g2d.scale(xscale, yscale);
        try {
            icon.paintIcon(c, g, 0, 0);
        }
        finally {
            g2d.setTransform(oldTransform);
            g2d.setRenderingHints(oldHints);
        }
    }

    public int getIconWidth() {
        return width;
    }

    public int getIconHeight() {
        return height;
    }
}
