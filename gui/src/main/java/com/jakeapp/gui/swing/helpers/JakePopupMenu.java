package com.jakeapp.gui.swing.helpers;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: studpete
 * Date: Jan 4, 2009
 * Time: 9:51:07 PM
 */
public class JakePopupMenu extends JPopupMenu {
    private static final Logger log = Logger.getLogger(JakePopupMenu.class);
    private PopupMenu pm = null;

    /**
     * Override the show.
     * Construct a awt menu if looks nicer on the platform.
     *
     * @param invoker
     * @param x
     * @param y
     */
    @Override
    public void show(Component invoker, int x, int y) {
        log.info("Show JakePopupMenu: x:" + x + " y:" + y);

        /**
         * This is sort of a hack.
         * JPopupMenu is really ugly on mac.
         * This wraps the JPopupMenu and creates the awt-pendant
         * on the fly.
         */
        if (Platform.isMac()) {
            // awt
            PopupMenu pm = getAWTMenu();

            // TODO: cleaner add/remove
            // invoker.remove(pm);
            invoker.add(pm);
            pm.show(invoker, x, y);

        } else {
            super.show(invoker, x, y);
        }
    }

    private PopupMenu getAWTMenu() {
        if (pm == null) {
            log.info("Wrapping JPopupMenu into PopupMenu");
            pm = new PopupMenu();

            for (int i = 0; i < this.getComponentCount(); i++) {
                final Component co = this.getComponent(i);

                if (JMenuItem.class.isInstance(co)) {
                    final JMenuItem mi = (JMenuItem) co;

                    final MenuItem ami;

                    if (JCheckBoxMenuItem.class.isInstance(co)) {
                        CheckboxMenuItem cami = new CheckboxMenuItem(mi.getText(), ((JCheckBoxMenuItem) co).getState());

                        // relay item state changed events
                        cami.addItemListener(new ItemListener() {

                            @Override
                            public void itemStateChanged(ItemEvent itemEvent) {
                                for (ItemListener il : ((JCheckBoxMenuItem) co).getItemListeners()) {
                                    il.itemStateChanged(itemEvent);
                                }

                                // emulate actionPerfomed-event that would be sended from JMenuItem.
                                if (mi.getAction() != null) {
                                    mi.getAction().actionPerformed(new ActionEvent(mi, 0, null));
                                } else {
                                    for (ActionListener al : mi.getActionListeners()) {
                                        al.actionPerformed(new ActionEvent(mi, 0, null));
                                    }
                                }
                            }
                        });
                        ami = cami;
                    }
                    // Disabeld code because it's not used currently.
                    /*
                    else if(JRadioButtonMenuItem.class.isInstance(co)){
                        CheckboxMenuItem cami = new CheckboxMenuItem(mi.getText(), ((JRadioButtonMenuItem) co).isSelected());

                        // relay item state changed events
                        cami.addItemListener(new ItemListener() {

                            @Override
                            public void itemStateChanged(ItemEvent itemEvent) {
                                for (ItemListener il : ((JRadioButtonMenuItem) co).getItemListeners()) {
                                    il.itemStateChanged(itemEvent);
                                }
                            }
                        });
                        ami = cami;
                    } */
                    else {
                        ami = new MenuItem(mi.getText());
                    }

                    ami.setEnabled(mi.isEnabled());
                    // listen for action event changes.
                    if (mi.getAction() != null) {
                        mi.getAction().addPropertyChangeListener(new PropertyChangeListener() {
                            @Override
                            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                                ami.setLabel(mi.getText());
                                ami.setEnabled(mi.isEnabled());
                            }
                        });
                    }

                    ami.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            //log.debug("referring action to JMenuItem... " + actionEvent);
                            if (mi.getAction() != null) {
                                mi.getAction().actionPerformed(actionEvent);
                            } else {
                                for (ActionListener al : mi.getActionListeners()) {
                                    al.actionPerformed(actionEvent);
                                }
                            }
                        }
                    });
                    pm.add(ami);

                } else if (JSeparator.class.isInstance(co)) {
                    pm.addSeparator();
                }
            }
        }
        return pm;
    }
}
