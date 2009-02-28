/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Provide animation of auto-generated animations.  Makes use of the repaint
 * tracking structure established by {@link AnimatedIcon}. 
 */
public abstract class AbstractAnimatedIcon extends AnimatedIcon {
    private static final int DEFAULT_INTERVAL = 1000/24;

    private Timer timer;
    private int repaintInterval;
    private int frame;
    private int frameCount;
    
    protected AbstractAnimatedIcon() {
        this(0);
    }
    
    protected AbstractAnimatedIcon(int frameCount) {
        this(frameCount, DEFAULT_INTERVAL);
    }
    
    protected AbstractAnimatedIcon(int frameCount, int interval) {
        this.frameCount = frameCount;
        setFrameInterval(interval);
    }
    
    /** Ensure the timer stops running, so it, too can be GC'd. */
    protected void finalize() {
        timer.stop();
    }
    
    /** Setting a frame interval of zero stops automatic animation. */
    public void setFrameInterval(int interval) {
        repaintInterval = interval;
        if (interval != 0) {
            if (timer == null) {
                timer = new Timer(interval, new AnimationUpdater(this));
                timer.setRepeats(true);
            }
            else {
                timer.setDelay(interval);
            }
        }
        else if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    public int getFrameInterval() {
        return repaintInterval;
    }
    
    /** Returns the total number of frames. */
    public int getFrameCount() {
        return frameCount;
    }
    
    /** Advance to the next animation frame. */
    public void nextFrame() {
        setFrame(getFrame() + 1);
    }
    
    /** Set the current animation frame number. */
    public void setFrame(int f) {
        this.frame = f;
        if (frameCount != 0)
            frame = frame % frameCount;
        repaint();
    }
    
    /** Returns the current animation frame number. */
    public int getFrame() {
        return frame;
    }

    /** Implement this method to paint the icon. */
    protected abstract void paintFrame(Component c, Graphics g, int x, int y);

    public abstract int getIconWidth();
    public abstract int getIconHeight();
    
    protected synchronized void registerRepaintArea(Component c, int x, int y, int w, int h) {
        if (timer != null && !timer.isRunning()) {
            timer.start();
        }
        super.registerRepaintArea(c, x, y, w, h);
    }
    
    private static class AnimationUpdater implements ActionListener {
        private WeakReference ref;
        public AnimationUpdater(AbstractAnimatedIcon icon) {
            this.ref = new WeakReference(icon);
        }
        public void actionPerformed(ActionEvent e) {
            AbstractAnimatedIcon icon = (AbstractAnimatedIcon)ref.get();
            if (icon != null) {
                icon.nextFrame();
            }
        }
    }
}
