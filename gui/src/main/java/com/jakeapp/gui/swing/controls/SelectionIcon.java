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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.swing.Icon;

/** Paint an icon appropriately for a selection to ensure that the icon is
 * visible and to avoid having to generate minor icon variations.  This may
 * mean a mouse-over, a selected button, or a selected row in a tree or
 * table.  
 * Primarily to make icons in selected rows of a table on Windows 2000 show up
 * properly, since that setup has a dark blue selection background which makes
 * any black pixels difficult to see.  This class replaces any pixels of the
 * given foreground/background with a different foreground/background.
 * @author twall
 */
public class SelectionIcon implements Icon {

    private static int TOLERANCE = 56;
    private static int TOTAL_TOLERANCE = 2*TOLERANCE;
    private static float TINT = 0.2f;

    public static void setTolerance(int tol) {
        TOLERANCE = tol;
    }
    public static int getTolerance() { return TOLERANCE; }
    public static void setTint(float tint) {
        TINT = tint;
    }
    public static float getTint() { return TINT; }

    private Icon icon;
    private Color fg, bg, sfg, sbg;
    private Composite composite;

    /** Create an icon that paints a modified version given {@link Icon}.
     */
    public SelectionIcon(Icon icon, Color fg, Color bg, Color sfg, Color sbg) {
        if (fg == null)
            throw new NullPointerException("Foreground may not be null");
        if (bg == null)
            throw new NullPointerException("Background may not be null");
        if (sfg == null)
            throw new NullPointerException("Selection foreground may not be null");
        if (sbg == null)
            throw new NullPointerException("Selection Background may not be null");
        this.fg = fg;
        this.bg = bg;
        this.sfg = sfg;
        this.sbg = sbg;
        this.icon = icon;
        this.composite = new CustomComposite();
    }

    /** Width is same as the original. */
    public int getIconWidth() { return icon.getIconWidth(); }
    /** Height is same as the original. */
    public int getIconHeight() { return icon.getIconHeight(); }
    /** Returns same value as the original. */
    public String toString() { return icon.toString() + " (selection)"; }
    /** Paints the original, but replaces the original fg/bg pixels with new
     * ones.  Transparency is preserved.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        // Paint the icon into an image buffer so we can capture its
        // pixels, then re-paint those pixels into the real target.
        // A bug in JRE 1.4 prevents us from setting the composite on the
        // original Graphics, so we use the one from the BufferedImage
        // instead.
        int w = getIconWidth();
        int h = getIconHeight();
        Image image = c == null ? new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
            : c.getGraphicsConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, w, h);
        // Use the selection icon whenever the selection fg is different
        // from the normal foreground
        if (!sfg.equals(fg)) {
            graphics.setComposite(composite);
        }
        graphics.translate(-x, -y);
        icon.paintIcon(c, graphics, x, y);
        g.drawImage(image, x, y, c);
    }

    /** Composites the source image directly into the destination, ignoring the
     * destination pixels.  Pixels found in the source which match the
     * foreground or background colors are modified to new values. 
     */
    private class CustomComposite implements Composite {
        public CompositeContext createContext(final ColorModel srcCM, 
                                              final ColorModel dstCM, 
                                              RenderingHints hints) {
            //System.out.println("create");
            return new CompositeContext() {
                private boolean near(Color c1, Color c2, int tol) {
                    int dr = Math.abs(c1.getRed() - c2.getRed());
                    int dg = Math.abs(c1.getGreen() - c2.getGreen());
                    int db = Math.abs(c1.getBlue() - c2.getBlue());
                    return dr < TOLERANCE 
                        && dg < TOLERANCE
                        && db < TOLERANCE
                        && dr + dg + db < TOTAL_TOLERANCE;
                }
                private int mix(int base, int tint, float pct) {
                    return (int)(pct*base + (1-pct)*tint);
                }

                public void dispose() { }
                public void compose(Raster src, Raster dst, 
                                    WritableRaster out) {
                    int[] pout = new int[4];
                    int w = Math.min(src.getWidth(), out.getWidth());
                    int h = Math.min(src.getHeight(), out.getHeight());
                    int xs = src.getMinX();
                    int ys = src.getMinY();
                    for (int x=0;x < w;x++) {
                        for (int y=0;y < h;y++) {
                            Object pixel = src.getDataElements(xs+x, ys+y, null);
                            int alpha = srcCM.getAlpha(pixel);
                            if (alpha != 0) {
                                pout[0] = srcCM.getRed(pixel);
                                pout[1] = srcCM.getGreen(pixel);
                                pout[2] = srcCM.getBlue(pixel);
                                pout[3] = alpha;
                                Color color = new Color(pout[0], pout[1],
                                                        pout[2], pout[3]); 
                                if (near(color, fg, TOLERANCE)
                                    || near(color, sbg, TOLERANCE)) {
                                    pout[0] = sfg.getRed();
                                    pout[1] = sfg.getGreen();
                                    pout[2] = sfg.getBlue();
                                }
                                else if (near(color, bg, TOLERANCE)) {
                                    pout[0] = sbg.getRed();
                                    pout[1] = sbg.getGreen();
                                    pout[2] = sbg.getBlue();
                                }
                                else {
                                    // mix in the selection background
                                    pout[0] = mix(sbg.getRed(), pout[0], TINT);
                                    pout[1] = mix(sbg.getGreen(), pout[1], TINT);
                                    pout[2] = mix(sbg.getBlue(), pout[2], TINT);
                                }
                                out.setPixel(out.getMinX()+x, 
                                             out.getMinY()+y, pout);
                            }
                        }
                    }
                }
            };
        }
    }
}
