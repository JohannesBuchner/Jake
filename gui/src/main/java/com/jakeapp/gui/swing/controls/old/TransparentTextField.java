/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.controls.old;

import javax.swing.*;
import java.awt.*;

/**
 * @author studpete
 */
public class TransparentTextField extends JTextField {

    public TransparentTextField() {
        this.setOpaque(false);
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        this.setBackground(Color.white);
        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f);
        g2d.setComposite(alphaComp);
        g2d.setColor(getBackground());
        Rectangle tBounds = g2d.getClip().getBounds();
        g2d.fillRect((int) tBounds.getX(), (int) tBounds.getY(), (int) tBounds.getWidth(), (int) tBounds.getHeight());
        super.paintComponent(g2d);
        this.setForeground(Color.black);
    }

}
