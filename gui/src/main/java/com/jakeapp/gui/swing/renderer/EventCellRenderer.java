package com.jakeapp.gui.swing.renderer;

import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ProjectMemberHelpers;
import com.jakeapp.gui.swing.helpers.Translator;
import com.jakeapp.gui.swing.panels.NewsPanel;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

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
	private final static ImageIcon fileAddIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  JakeMainApp.class.getResource("/icons/file-add.png")));
	private final static ImageIcon fileRemoveIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  JakeMainApp.class.getResource("/icons/file-remove.png")));
	private final static ImageIcon fileMoveIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  JakeMainApp.class.getResource("/icons/file-moved.png")));
	private final static ImageIcon fileUpdateIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  JakeMainApp.class.getResource("/icons/file-updated.png")));
	private final static ImageIcon fileLockIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  JakeMainApp.class.getResource("/icons/file-lock.png")));
	private final static ImageIcon fileUnlockIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  JakeMainApp.class.getResource("/icons/file-unlock.png")));

	// project actions
	private final static ImageIcon projectCreatedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
			  "project-created.png")));

	// people actions
	private final static ImageIcon peopleAddIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  JakeMainApp.class.getResource("/icons/user-add.png")));
	private final static ImageIcon peopleRemoveIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  JakeMainApp.class.getResource("/icons/user-remove.png")));
	private final static ImageIcon peopleInviteIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  JakeMainApp.class.getResource("/icons/user-invited.png")));
	private final static ImageIcon peopleAcceptInvitationIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  JakeMainApp.class.getResource("/icons/user-inviteok.png")));
	private final static ImageIcon peopleChangeTrustIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  JakeMainApp.class.getResource("/icons/user-trust.png")));

	// tag actions
	private final static ImageIcon tagAddIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
			  "tags-add.png")));
	private final static ImageIcon tagRemovecon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
			  "tags-remove.png")));

	// note actions
	private final static ImageIcon noteAddIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
			  "note-add.png")));
	private final static ImageIcon noteRemoveIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
			  "note-remove.png")));
	private final static ImageIcon noteUpdateIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource("/icons/" +
			  "note-updated.png")));

	// get notes resource map
	private static final ResourceMap newsResourceMap = org.jdesktop.application.Application.getInstance(
			  com.jakeapp.gui.swing.JakeMainApp.class).getContext()
			  .getResourceMap(NewsPanel.class);
	;

	public EventCellRenderer() {
		log.debug("Init EventCellRenderer.");
	}

	/* This is the only method defined by DefaultTableCellRenderer.  We just
		 * reconfigure the Jlabel each time we're called.
		 */
	public Component getTableCellRendererComponent(
			  JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		/*
					* Pre-format the data we wanna show for LogEntry
					*/
		LogEntry loge = (LogEntry) value;
		String msg = "";

		// HACK: do not crash!
		if (loge.getBelongsTo() == null) {
			try {
				loge.setBelongsTo(new Tag("BelongsToStub"));
			} catch (InvalidTagNameException e) {
				e.printStackTrace();
			}
		}

		// begin the string with e.g. "You" or "Peter" (Nicknames/FullNames)
		msg += ProjectMemberHelpers.getLocalizedUserNick(loge.getMember()) + " ";

		/* Build the String and set the operation Icon.
					*/
		switch (loge.getLogAction()) {
			case FILE_ADD: {
				setIcon(fileAddIcon);
				msg += Translator.get(newsResourceMap, "eventsAddedFile", loge.getBelongsTo().toString());
			}
			break;

			case FILE_DELETE: {
				setIcon(fileRemoveIcon);
				msg += Translator.get(newsResourceMap, "eventsRemovedFile", loge.getBelongsTo().toString());
			}
			break;

			case FILE_MOVEDTO: {
				setIcon(fileMoveIcon);
				msg += Translator.get(newsResourceMap, "eventsMovedFile", loge.getBelongsTo().toString());
			}
			break;

			case FILE_NEW_VERSION: {
				setIcon(fileUpdateIcon);
				msg += Translator.get(newsResourceMap, "eventsUpdatedFile", loge.getBelongsTo().toString());
			}
			break;

			case PROJECT_CREATED: {
				setIcon(projectCreatedIcon);
				msg += Translator.get(newsResourceMap, "eventsProjectCreated", loge.getBelongsTo().toString());
			}
			break;

			case NOTE_ADD: {
				setIcon(noteAddIcon);
				msg += Translator.get(newsResourceMap, "eventsNoteAdded", loge.getBelongsTo().toString());
			}
			break;

			case NOTE_DELETE: {
				setIcon(noteRemoveIcon);
				msg += Translator.get(newsResourceMap, "eventsNoteRemoved", loge.getBelongsTo().toString());
			}
			break;

			case NOTE_NEW_VERSION: {
				setIcon(noteUpdateIcon);
				msg += Translator.get(newsResourceMap, "eventNoteUpdated", loge.getBelongsTo().toString());
			}
			break;

			case OBJECT_LOCK: {
				setIcon(fileLockIcon);
				msg += Translator.get(newsResourceMap, "eventsObjectLock", loge.getBelongsTo().toString());
			}
			break;

			case OBJECT_UNLOCK: {
				setIcon(fileUnlockIcon);
				msg += Translator.get(newsResourceMap, "eventsObjectUnlock", loge.getBelongsTo().toString());
			}
			break;

			case PROJECTMEMBER_ADDED: {
				setIcon(peopleAddIcon);
				msg += Translator.get(newsResourceMap, "eventsProjectMemberAdded", loge.getBelongsTo().toString());
			}
			break;

			case PROJECTMEMBER_INVITED: {
				setIcon(peopleInviteIcon);
				msg += Translator.get(newsResourceMap, "eventsProjectMemberInvited", loge.getBelongsTo().toString());
			}
			break;

			case PROJECTMEMBER_INVITATION_ACCEPTED: {
				setIcon(peopleAcceptInvitationIcon);
				msg += Translator.get(newsResourceMap, "eventsProjectMemberInvitationAccepted");
			}
			break;

			case PROJECTMEMBER_TRUSTCHANGE: {
				setIcon(peopleChangeTrustIcon);
				// TODO: how to get that info???
				msg += Translator.get(newsResourceMap, "eventsProjectMemberTrustChanged", loge.getBelongsTo().toString(), "");
			}
			break;

			// TODO: POSSIBLE???
			case PROJECTMEMBER_REMOVED: {
				setIcon(peopleRemoveIcon);
				msg += Translator.get(newsResourceMap, "eventsProjectMemberRemoved", loge.getBelongsTo().toString());
			}
			break;

			case TAG_ADD: {
				setIcon(tagAddIcon);
				msg += Translator.get(newsResourceMap, "eventsTagsAdd", loge.getBelongsTo().toString());
			}
			break;

			case TAG_REMOVE: {
				setIcon(tagRemovecon);
				msg += Translator.get(newsResourceMap, "eventsTagsRemove", loge.getBelongsTo().toString());
			}
			break;

			default: {
				log.warn("Unsupported action: " + loge.getLogAction());
				setIcon(null);
				msg += loge.getLogAction();
			}
		}

		// do not insert html as this auto-wraps messages (not wanted)
		String valStr = msg; //"<html>" + msg + "</html>";

		/* The DefaultListCellRenderer class will take care of
				  * the JLabels text property, it's foreground and background
				  * colors, and so on.
				  */
		super.getTableCellRendererComponent(table, valStr, isSelected, hasFocus, row, column);


		// set the tooltip text
		setToolTipText("<html><font size=5>" + loge.getLogAction() + "</font><br><b>" +
				  loge.getComment() + "</b><br>" + loge.getTimestamp().toGMTString() + "</html>");

		return this;
	}
}