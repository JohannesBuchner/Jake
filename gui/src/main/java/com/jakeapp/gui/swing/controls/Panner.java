/* Copyright (c) 2006-2007 Timothy Wall, All Rights Reserved
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** Component which scales any given {@link JComponent} into its bounds.
 * The visible portion of the {@link JComponent} (as reported by 
 * {@link JComponent#getVisibleRect}) is drawn as a rectangle in the scaled
 * image.  Dragging the rectangle will move the visible portion of the 
 * panned component within its scrolling context. 
 */
public class Panner extends JComponent {
    public static final int MINIMUM_WIDTH = 64;
    public static final int MINIMUM_HEIGHT = 64;
    private static final Color VISIBLE_BOUNDS_COLOR =
        new Color(128, 128, 128, 64);
    private static final Color BORDER_COLOR = Color.black;
    private JComponent panned;
    private ScaledIcon thumbnail;
    private float transparency = 0.9f;
    private boolean preserveAspect = true;
    private boolean includeBorder = true;
    
    /** Whether to center the thumbnail if the component doesn't match
     * the panned component's aspect ratio.
     */
    private boolean centered = true;
    /** Whether the thumbnail shades to show visible (versus an outline). */
    private boolean shadeHidden = true;
    /** Whether this thumbnail is attached to its panned component. */
    private boolean attached;
    /** Listener for notifications when the panned component is scrolled. */
    private ComponentListener listener = new PannedListener();
    
    public Panner() {
        this(null);
    }

    public Panner(JComponent reference) {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (panned != null)
                    setViewportCenter(e.getPoint());
            }
        });
        addMouseMotionListener(new MouseMotionListener() {
            // TODO: revert if dragged far enough away?
            public void mouseDragged(MouseEvent e) {
                if (panned != null)
                    setViewportCenter(e.getPoint());
            }
            public void mouseMoved(MouseEvent e) { }
        });
        setPannedComponent(reference);
    }

    public void setPreserveAspect(boolean preserve) {
        preserveAspect = preserve;
        thumbnail.setPreserveAspect(preserveAspect);
    }
    
    public void setIncludeBorder(boolean border) {
        if (border != includeBorder) {
            boolean old = includeBorder;
            this.includeBorder = border;
            firePropertyChange("includeBorder", old, includeBorder);
        }
    }
    
    public void setTransparency(float t) {
        if (t != transparency) {
            float old = this.transparency;
            this.transparency = t;
            firePropertyChange("transparency", old, transparency);
        }
    }
    
    /** "Attach" to the panned component at the given location within
     * the component.  Returns whether the attach was successful.
     */
    public boolean attach(int x, int y) {
        int layerOffset = 1000;
        JRootPane root = panned.getRootPane();
        if (root != null) {
            JLayeredPane lp = root.getLayeredPane();
            Component child = panned;
            while (child.getParent() != lp) {
                child = child.getParent();
            }
            Point pt = SwingUtilities.convertPoint(panned, x, y, lp);
            Dimension size = getPreferredSize();
            // Don't adjust the thumbnail size!
            super.setBounds(pt.x, pt.y, size.width, size.height);

            int layer = lp.getLayer(child);
            // NOTE: JLayeredPane doesn't properly repaint an overlapping
            // child when an obscured child calls repaint(), if the two
            // are in the same layer.
            lp.add(this, new Integer(layer + layerOffset), 0);
            panned.repaint();
            revalidate();
            repaint();
            boolean wasAttached = attached;
            attached = true;
            firePropertyChange("attached", wasAttached, true);
        }
        return attached;
    }
    
    public boolean isAttached() { return attached; }
    
    public void detach() {
        if (attached) {
            Container parent = getParent();
            if (parent != null) {
                parent.remove(this);
                parent.invalidate();
                parent.repaint();
            }
            attached = false;
            firePropertyChange("attached", true, false);
        }
    }
    
    /** Sets the center point of the current viewport.  Coordinates are relative
     * to the Panner bounds.  The viewport bounds will always be contained
     * within the thumbnail image.
     * @param where
     */
    public void setViewportCenter(Point where) {
        Rectangle bounds = getThumbnailBounds();
        Rectangle visible = getViewportBounds();
        visible.x = where.x - bounds.x - (int)Math.round(visible.width/2.0);
        visible.y = where.y - bounds.y - (int)Math.round(visible.height/2.0);
        double[] scale = scaleFactor();
        Rectangle current = panned.getVisibleRect();
        Dimension size = getDrawingSize(panned);
        current.x = Math.min(size.width, 
                             Math.max(0, (int)
                                      Math.round(visible.x / scale[0])));
        current.y = Math.min(size.height,
                             Math.max(0, (int)
                                      Math.round(visible.y / scale[1])));
        panned.scrollRectToVisible(current);
    }
    
    protected Dimension getDrawingSize(JComponent component) {
        Dimension size = check(component.getSize());
        Insets insets = component.getInsets();
        if (insets != null) {
            size.width -= insets.left + insets.right;
            size.height -= insets.top + insets.bottom;
        }
        return size;
    }
    
    /** Set the thumbnail alignment within the available space. */
    public void setCentered(boolean set) {
        boolean oldCentered = centered;
        centered = set;
        repaint();
        firePropertyChange("centered", oldCentered, centered);
    }
    /** @return whether the thumbnail is centered within the available space. */
    public boolean isCentered() { return centered; }
    /** Set the component being panned. */
    public void setPannedComponent(JComponent panned) {
        JComponent oldPanned = this.panned;
        if (oldPanned != null) {
            detach();
            oldPanned.removeComponentListener(listener);
        }
        this.panned = panned;
        thumbnail = new ScaledIcon(new ComponentIcon(panned, includeBorder));
        thumbnail.setPreserveAspect(preserveAspect);
        setThumbnailSize();
        panned.addComponentListener(listener);
        revalidate();
        repaint();
        firePropertyChange("panned", oldPanned, panned);
    }

    protected Dimension check(Dimension size) {
        size.width = Math.max(size.width, MINIMUM_WIDTH);
        size.height = Math.max(size.height, MINIMUM_HEIGHT);
        return size;
    }
    
    /** Return the actual thumbnail bounds, accounting for extra space 
     * required for this component's border and to maintain proper aspect ratio.
     */
    public Rectangle getThumbnailBounds() {
        Dimension thumb = getThumbnailSize();
        int x = 0;
        int y = 0;
        if (centered) {
            x += (getWidth() - thumb.width)/2;
            y += (getHeight() - thumb.height)/2;
        }
        return new Rectangle(x, y, thumb.width, thumb.height);
    }
    
    /** Return a rectangle within the current component content bounds
     * equivalent to the visible rectangle within the panned component's
     * content bounds.
     */
    public Rectangle getViewportBounds() {
        if (panned == null)
            return getVisibleRect();
        Rectangle bounds = getThumbnailBounds();
        Rectangle rect = panned.getVisibleRect();
        double[] scale = scaleFactor();
        rect.x = bounds.x + (int)Math.round(rect.x * scale[0]);
        rect.y = bounds.y + (int)Math.round(rect.y * scale[1]);
        rect.width = Math.min(bounds.width, (int)Math.round(rect.width * scale[0]));
        rect.height = Math.min(bounds.height, (int)Math.round(rect.height* scale[1]));
        if (rect.x + rect.width > bounds.x + bounds.width) {
            rect.x = bounds.x + bounds.width - rect.width;
        }
        if (rect.y + rect.height > bounds.y + bounds.height) {
            rect.y = bounds.y + bounds.height - rect.height;
        }
        return rect;
    }

    private Dimension getThumbnailSize() {
        return new Dimension(thumbnail.getIconWidth(), 
                             thumbnail.getIconHeight());
    }
    
    private double[] scaleFactor() {
        if (panned == null)
            return new double[] { 1.0, 1.0 };
        
        Dimension full = getDrawingSize(panned);
        Rectangle bounds = getThumbnailBounds();
        return new double[] { 
            (double)bounds.width / full.width,
            (double)bounds.height / full.height    
        };
    }
    
    /** Returns the preferred size, which will be the set preferred size
     * or the current size with an appropriate aspect ratio applied.
     * If there is no current panned component, no aspect ratio will be
     * applied.
     */
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) 
            return super.getPreferredSize();
        Dimension size = check(getThumbnailSize());
        Insets insets = getInsets();
        if (insets != null) {
            size.width += insets.left + insets.right;
            size.height += insets.top + insets.bottom;
        }
        return size;
    }
    
    /** Ensure the maximum size always has the correct aspect ratio. */
    public Dimension getMaximumSize() {
        return isMaximumSizeSet() ? super.getMaximumSize() : getPreferredSize();
    }
    
    /** Ensure the minimum size always has the correct aspect ratio. */
    public Dimension getMinimumSize() {
        return isMinimumSizeSet() ? super.getMinimumSize() : getPreferredSize();
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        setThumbnailSize();
    }
    
    private void setThumbnailSize() {
        thumbnail.setPreserveAspect(preserveAspect);
        thumbnail.setSize(getDrawingSize(this));
        if (thumbnail.getIconWidth() < MINIMUM_WIDTH
            || thumbnail.getIconHeight() < MINIMUM_HEIGHT) {
            thumbnail.setPreserveAspect(false);
            thumbnail.setSize(getDrawingSize(this));
        }
    }
    
    /** Paint the panned component in a thumbnail. */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        Composite oldComposite = g2d.getComposite();
        if (attached) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, transparency));
        }
        try {
            super.paint(g);
            if (panned == null)
                return;
            
            Rectangle bounds = getThumbnailBounds();
            Shape oldClip = g2d.getClip();
            try {
                Area mask = new Area(bounds);
                g2d.setClip(mask);
                thumbnail.paintIcon(this, g2d, bounds.x, bounds.y);
            }
            finally {
                g2d.setClip(oldClip);
            }
            
            Color oldColor = g.getColor();
            
            // Indicate the visible rect
            Rectangle visible = getViewportBounds();
            g.setColor(VISIBLE_BOUNDS_COLOR);
            if (shadeHidden) {
                oldClip = g.getClip();
                Area area = new Area(bounds);
                area.subtract(new Area(visible));
                ((Graphics2D)g).setClip(area);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                ((Graphics2D)g).setClip(oldClip);
            }
            else {
                g.drawRect(visible.x, visible.y, visible.width, visible.height);
            }
            // Always paint a thin border (NOT the same thing as setBorder, since
            // the thumbnail outline may be smaller than the actual component
            g.setColor(BORDER_COLOR);
            g.drawRect(bounds.x, bounds.y, bounds.width-1, bounds.height-1);
            
            g.setColor(oldColor);
        }
        finally {
            g2d.setComposite(oldComposite);
        }
    }
    
    private final class PannedListener extends ComponentAdapter 
        implements HierarchyListener, PropertyChangeListener {
        public void hierarchyChanged(HierarchyEvent e) {
            if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0) {
                detach();
            }
        }
        public void propertyChange(PropertyChangeEvent e) {
            if (JLayeredPane.LAYER_PROPERTY.equals(e.getPropertyName())) {
                detach();
            }
        }
        public void componentResized(ComponentEvent e) {
            setThumbnailSize();
            revalidate();
            repaint();
        }
        
        public void componentMoved(ComponentEvent e) {
            repaint();
        }
    }
}
