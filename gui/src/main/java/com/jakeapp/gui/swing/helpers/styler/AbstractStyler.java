package com.jakeapp.gui.swing.helpers.styler;

import com.jakeapp.gui.swing.helpers.Colors;
import org.jdesktop.swingx.painter.*;

import javax.swing.*;

/**
 * User: studpete
 * Date: Dec 25, 2008
 * Time: 2:37:18 PM
 */
public abstract class AbstractStyler implements Styler {

    @Override
    public void MakeWhiteRecessedButton(JButton btn) {
    }

    public void styleToolbarButton(JToggleButton jToggleButton) {
    }

    public Painter getContentPanelBackgroundPainter() {
        MattePainter mp = new MattePainter(Colors.LightBlue.alpha(0.6f));
        GlossPainter gp = new GlossPainter(Colors.White.alpha(0.5f),
                GlossPainter.GlossPosition.TOP);
        PinstripePainter pp = new PinstripePainter(Colors.Gray.alpha(0.01f),
                45d);
        return new CompoundPainter(mp, pp, gp);
    }
}