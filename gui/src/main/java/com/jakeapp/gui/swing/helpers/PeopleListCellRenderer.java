package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.gui.swing.JakeMainApp;

import javax.swing.*;
import java.awt.*;

/**
 * The PeopleListCellRenderer.
 * Renders People info with Status Icon.
 * User: studpete
 * Date: Jan 4, 2009
 * Time: 2:34:20 PM
 */
public class PeopleListCellRenderer extends DefaultListCellRenderer {
    final static ImageIcon onlineFullTrustIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/user-online-fulltrust.png")));
    final static ImageIcon onlineTrustIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/user-online-trust.png")));
    final static ImageIcon onlineNoTrustIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/user-online-notrust.png")));
    final static ImageIcon offlineFullTrustIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/user-offline-fulltrust.png")));
    final static ImageIcon offlineTrustIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/user-offline-trust.png")));
    final static ImageIcon offlineNoTrustIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/user-offline-notrust.png")));


    /* This is the only method defined by ListCellRenderer.  We just
    * reconfigure the Jlabel each time we're called.
    */
    public Component getListCellRendererComponent(
            JList list,
            Object value,   // value to display
            int index,      // cell index
            boolean iss,    // is the cell selected
            boolean chf)    // the list and the cell have the focus
    {

        /*
         * Pre-format the data we wanna show for ProjectMember
         */
        ProjectMember member = (ProjectMember) value;

        String nickOrFullName;

        if (member.getUserId().getNickname().length() == 0) {
            nickOrFullName = member.getUserId().getFirstName() + " " + member.getUserId().getSurName();
        } else {
            nickOrFullName = member.getUserId().getNickname();
        }

        // change color on selection
        String subColor = iss ? "White" : "Gray";
        String shortStatusStr = member.getUserId().getUserId();
        String valStr = "<html><b>" + nickOrFullName + "</b><br><font color=" + subColor + ">" + shortStatusStr + "</font></html>";


        /* The DefaultListCellRenderer class will take care of
        * the JLabels text property, it's foreground and background
        * colors, and so on.
        */
        super.getListCellRendererComponent(list, valStr, index, iss, chf);

        /* We additionally set the JLabels icon property here.
         */

        // TODO: substitute with online/offline check!
        if (member.getUserId().getFirstName().length() > 4) {
            switch (member.getTrustState()) {
                case AUTO_ADD_REMOVE: {
                    setIcon(onlineFullTrustIcon);
                }
                break;
                case TRUST: {
                    setIcon(onlineTrustIcon);
                }
                break;
                case NO_TRUST: {
                    setIcon(onlineNoTrustIcon);
                }
            }
        } else {
            switch (member.getTrustState()) {
                case AUTO_ADD_REMOVE: {
                    setIcon(offlineFullTrustIcon);
                }
                break;
                case TRUST: {
                    setIcon(offlineTrustIcon);
                }
                break;
                case NO_TRUST: {
                    setIcon(offlineNoTrustIcon);
                }
            }
        }

        // TODO: replace check
        String statusStr = (true) ? "Online" : "Offline";
        statusStr += ", ";

        // TODO: localize + change labels
        switch (member.getTrustState()) {
            case AUTO_ADD_REMOVE: {
                statusStr += "Trusted + Trusting new people";
            }
            break;
            case TRUST: {
                statusStr += "Trusted";
            }
            break;
            case NO_TRUST: {
                statusStr += "Not Trusted";
            }
        }

        // set the tooltip text
        setToolTipText("<html><font size=5>" + member.getUserId().getFirstName() + " "
                + member.getUserId().getSurName() + "</font><br><b>'" +
                member.getUserId().getNickname() +
                "'</b><br>" + statusStr + "</html>");

        return this;
    }
}
