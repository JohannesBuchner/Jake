package com.jakeapp.gui.swing.renderer;

import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.gui.swing.JakeMainApp;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * The PeopleListCellRenderer.
 * Renders People info with Status Icon.
 * User: studpete
 * Date: Jan 4, 2009
 * Time: 2:34:20 PM
 */
public class EventCellRenderer extends DefaultTableCellRenderer {
    private static final Logger log = Logger.getLogger(EventCellRenderer.class);

    // file actions
    final static ImageIcon fileAddIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            JakeMainApp.class.getResource("/icons/file-add.png")));
    final static ImageIcon fileRemoveIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            JakeMainApp.class.getResource("/icons/file-remove.png")));
    final static ImageIcon fileMoveIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            JakeMainApp.class.getResource("/icons/file-moved.png")));
    final static ImageIcon fileUpdateIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            JakeMainApp.class.getResource("/icons/file-updated.png")));
    final static ImageIcon fileLockIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            JakeMainApp.class.getResource("/icons/file-lock.png")));
    final static ImageIcon fileUnlockIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            JakeMainApp.class.getResource("/icons/file-unlock.png")));

    // project actions
    final static ImageIcon projectCreatedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
            "project-created.png")));

    // people actions
    final static ImageIcon peopleAddIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            JakeMainApp.class.getResource("/icons/user-add.png")));
    final static ImageIcon peopleRemoveIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            JakeMainApp.class.getResource("/icons/user-remove.png")));
    final static ImageIcon peopleInviteIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            JakeMainApp.class.getResource("/icons/user-invited.png")));
    final static ImageIcon peopleAcceptInvitationIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            JakeMainApp.class.getResource("/icons/user-inviteok.png")));
    final static ImageIcon peopleChangeTrustIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            JakeMainApp.class.getResource("/icons/user-trust.png")));

    // tag actions
    final static ImageIcon tagAddIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
            "tags-add.png")));
    final static ImageIcon tagRemovecon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
            "tags-remove.png")));

    // note actions
    final static ImageIcon noteAddIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
            "note-add.png")));
    final static ImageIcon noteRemoveIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
            "note-remove.png")));
    final static ImageIcon noteUpdateIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
            "note-updated.png")));


    /* This is the only method defined by DefaultTableCellRenderer.  We just
    * reconfigure the Jlabel each time we're called.
    */
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        /*
         * Pre-format the data we wanna show for LogEntry
         */
        LogEntry loge = (LogEntry) value;

        String valStr = "<html>" + "Log: " + loge.getLogAction() + "</html>";

        /* The DefaultListCellRenderer class will take care of
        * the JLabels text property, it's foreground and background
        * colors, and so on.
        */
        super.getTableCellRendererComponent(table, valStr, isSelected, hasFocus, row, column);

        /* We additionally set the JLabels icon property here.
         */

        switch (loge.getLogAction()) {
            case FILE_ADD: {
                setIcon(fileAddIcon);
            }
            break;

            case FILE_DELETE: {
                setIcon(fileRemoveIcon);
            }
            break;

            case FILE_MOVEDTO: {
                setIcon(fileMoveIcon);
            }
            break;

            case FILE_NEW_VERSION: {
                setIcon(fileUpdateIcon);
            }
            break;

            case PROJECT_CREATED: {
                setIcon(projectCreatedIcon);
            }
            break;

            case NOTE_ADD: {
                setIcon(noteAddIcon);
            }
            break;

            case NOTE_DELETE: {
                setIcon(noteRemoveIcon);
            }
            break;

            case NOTE_NEW_VERSION: {
                setIcon(noteUpdateIcon);
            }
            break;

            case OBJECT_LOCK: {
                setIcon(fileLockIcon);
            }
            break;

            case OBJECT_UNLOCK: {
                setIcon(fileUnlockIcon);
            }
            break;

            case PROJECTMEMBER_ADDED: {
                setIcon(peopleAddIcon);
            }
            break;

            case PROJECTMEMBER_INVITED: {
                setIcon(peopleInviteIcon);
            }
            break;

            case PROJECTMEMBER_INVITATION_ACCEPTED: {
                setIcon(peopleAcceptInvitationIcon);
            }
            break;

            case PROJECTMEMBER_TRUSTCHANGE: {
                setIcon(peopleChangeTrustIcon);
            }
            break;

            // TODO: POSSIBLE???
            case PROJECTMEMBER_REMOVED: {
                setIcon(peopleRemoveIcon);
            }
            break;

            case TAG_ADD: {
                setIcon(tagAddIcon);
            }
            break;

            case TAG_REMOVE: {
                setIcon(tagRemovecon);
            }
            break;

            default: {
                log.warn("Unsupported action: " + loge.getLogAction());
                setIcon(null);
            }
        }

        // set the tooltip text
        setToolTipText("<html><font size=5>" + loge.getLogAction() + "</font><br><b>" +
                loge.getComment() + "</b><br>" + loge.getTimestamp().toGMTString() + "</html>");

        return this;
    }
}