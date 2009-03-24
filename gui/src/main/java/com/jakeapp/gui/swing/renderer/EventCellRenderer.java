package com.jakeapp.gui.swing.renderer;

import com.explodingpixels.macwidgets.MacFontUtils;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ImageLoader;
import com.jakeapp.gui.swing.helpers.NotesHelper;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import com.jakeapp.gui.swing.helpers.Translator;
import com.jakeapp.gui.swing.helpers.UserHelper;
import com.jakeapp.gui.swing.panels.NewsPanel;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;

/**
 * The PeopleListCellRenderer.
 * Renders People info with Status Icon.
 */
public class EventCellRenderer extends DefaultJakeTableCellRenderer {
	private static final Logger log = Logger.getLogger(EventCellRenderer.class);

	// file actions
	private final static ImageIcon fileAddIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/file-add.png");

	private final static ImageIcon fileRemoveIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/file-remove.png");

	private final static ImageIcon fileMoveIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/file-moved.png");

	private final static ImageIcon fileUpdateIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/file-updated.png");

	private final static ImageIcon fileLockIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/file-lock.png");

	private final static ImageIcon fileUnlockIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/file-unlock.png");

	// project actions
	private final static ImageIcon projectCreatedIcon = new ImageIcon(
					Toolkit.getDefaultToolkit().getImage(
									JakeMainApp.class.getResource("/icons/project-created.png")));

	// users actions
	private final static ImageIcon peopleTrustIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/user-trust.png");

	private final static ImageIcon peopleNoTrustIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/user-trust-no.png");

	private final static ImageIcon peopleInviteIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/user-invited.png");

	private final static ImageIcon peopleAcceptInvitationIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/user-invite-ok.png");

	private final static ImageIcon peopleRejectInvitationIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/user-invite-rejected.png");

	private final static ImageIcon peopleTrustFullIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/user-trust-full.png");

	// tag actions
	private final static ImageIcon tagAddIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/" + "tags-add.png");

	private final static ImageIcon tagRemoveIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/" + "tags-remove.png");

	// note actions
	private final static ImageIcon noteAddIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/" + "note-add.png");

	private final static ImageIcon noteRemoveIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/" + "note-remove.png");

	private final static ImageIcon noteUpdateIcon =
					ImageLoader.get(JakeMainApp.class, "/icons/" + "note-updated.png");

	// get notes resource map
	private static final ResourceMap newsResourceMap =
					org.jdesktop.application.Application
									.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext()
									.getResourceMap(NewsPanel.class);

	public EventCellRenderer() {
		log.trace("Init EventCellRenderer.");
	}

	/* This is the only method defined by DefaultTableCellRenderer.  We just
		 * reconfigure the Jlabel each time we're called.
		 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {

		LogEntry loge = (LogEntry) value;
		String msg = "";

		// begin the string with e.g. "You" or "Peter" (Nicknames/FullNames)
		msg += UserHelper.getLocalizedUserNick(loge.getMember()) + " ";

		/* Build the String and set the operation Icon.
					*/
		boolean isNote = loge.getBelongsTo() instanceof NoteObject;
		String type = (isNote ? "Note" : "File");


		switch (loge.getLogAction()) {
			/*case JAKE_OBJECT_NEW_VERSION: {
				setIcon(fileAddIcon);
				msg += Translator.get(newsResourceMap, "eventsAddedFile", loge.getBelongsTo().toString());
			}
			break;*/

			case JAKE_OBJECT_DELETE: {
				setIcon((isNote ? noteRemoveIcon : fileRemoveIcon));
				msg += Translator.get(newsResourceMap, "eventsRemoved" + type,
								getJakeObjectTitle(loge.getBelongsTo()));
			}
			break;

			case JAKE_OBJECT_NEW_VERSION: {
				setIcon((isNote ? noteUpdateIcon : fileUpdateIcon));
				msg += Translator.get(newsResourceMap, "eventsUpdated" + type,
								getJakeObjectTitle(loge.getBelongsTo()));
			}
			break;

			case PROJECT_CREATED: {
				setIcon(projectCreatedIcon);
				msg += Translator.get(newsResourceMap, "eventsProjectCreated",
								((Project) loge.getBelongsTo()).getName());
			}
			break;

			case JAKE_OBJECT_LOCK: {
				setIcon(fileLockIcon);
				msg += Translator.get(newsResourceMap, "eventsObjectLock",
								getJakeObjectTitle(loge.getBelongsTo()));
			}
			break;

			case JAKE_OBJECT_UNLOCK: {
				setIcon(fileUnlockIcon);
				msg += Translator.get(newsResourceMap, "eventsObjectUnlock",
								getJakeObjectTitle(loge.getBelongsTo()));
			}
			break;

			case PROJECT_JOINED: {
				setIcon(peopleAcceptInvitationIcon);
				msg += Translator
								.get(newsResourceMap, "eventsProjectMemberInvitationAccepted");
			}
			break;

			case PROJECT_REJECTED: {
				setIcon(peopleRejectInvitationIcon);
				msg += Translator
								.get(newsResourceMap, "eventsProjectMemberInvitationRejected");
			}
			break;

			case START_TRUSTING_PROJECTMEMBER: {
				setIcon(peopleTrustIcon);
				msg += Translator.get(newsResourceMap, "eventsProjectMemberTrust",
								((User) loge.getBelongsTo()).getUserId());
			}
			break;

			case STOP_TRUSTING_PROJECTMEMBER: {
				setIcon(peopleNoTrustIcon);
				msg += Translator.get(newsResourceMap, "eventsProjectMemberStopTrust",
								((User) loge.getBelongsTo()).getUserId());
			}
			break;

			case FOLLOW_TRUSTING_PROJECTMEMBER: {
				setIcon(peopleTrustFullIcon);
				msg += Translator.get(newsResourceMap, "eventsProjectMemberFullTrust",
								((User) loge.getBelongsTo()).getUserId());
			}
			break;

			case PROJECTMEMBER_INVITED: {
				setIcon(peopleInviteIcon);
				msg += Translator.get(newsResourceMap, "eventsProjectMemberInvited",
								((User) loge.getBelongsTo()).getUserId());
			}
			break;

			case TAG_ADD: {
				setIcon(tagAddIcon);
				msg += Translator.get(newsResourceMap, "eventsTagsAdd",
								((User) loge.getBelongsTo()).getUserId());
			}
			break;

			case TAG_REMOVE: {
				setIcon(tagRemoveIcon);
				msg += Translator.get(newsResourceMap, "eventsTagsRemove",
								((User) loge.getBelongsTo()).getUserId());
			}
			break;

			default: {
				log.warn("Unsupported action: " + loge.getLogAction());
				setIcon(null);
				msg += loge.getLogAction();
			}
		}

		// do not insert html as this auto-wraps messages (not wanted)
		String valStr = msg;

		/* The DefaultListCellRenderer class will take care of
				  * the JLabels text property, it's foreground and background
				  * colors, and so on.
				  */
		super.getTableCellRendererComponent(table, valStr, isSelected, hasFocus, row,
						column);

		String comment = "";
		if (loge.getComment() != null && loge.getComment().length() > 0) {
			comment = "<br><b>Comment: " + loge.getComment() + "</b>";
		}

		// set the tooltip text
		setToolTipText(
						"<html><font size=3>" + this.getText() + "</font><br>" + TimeUtilities
										.getRelativeTime(loge.getTimestamp()) + " (" + loge
										.getTimestamp().toGMTString() + ")" + comment + "<br>" + this
										.toString() + "</html>");

		setFont(MacFontUtils.ITUNES_FONT);
		return this;
	}

	private String getJakeObjectTitle(ILogable belongsTo) {
		if (belongsTo instanceof NoteObject) {
			NoteObject note = (NoteObject) belongsTo;
			return NotesHelper.getTitle(note);
		} else if (belongsTo instanceof FileObject) {
			FileObject file = (FileObject) belongsTo;
			return file.getRelPath();
		}
		// fallback
		return belongsTo.toString();
	}
}