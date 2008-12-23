/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.helpers.styler;

import javax.swing.*;
import java.awt.*;

/**
 * @author studpete
 */
public class MacStyler implements Styler {

    @Override
    public void MakeWhiteRecessedButton(JButton btn) {
        btn.setForeground(Color.WHITE);
        btn.putClientProperty("JButton.buttonType", "recessed");
    }
}
