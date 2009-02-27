package com.jakeapp.gui.swing.renderer;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.synchronization.UserInfo;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.UserHelper;

import javax.swing.*;
import java.awt.*;

/**
 * The PeopleListCellRenderer.
 * Renders People info with Status Icon.
 */
// TODO: localize
public class PeopleListCellRenderer extends DefaultListCellRenderer {
	final static ImageIcon projectMemberIcon = new ImageIcon(
					Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource(
									"/icons/user-online-projectmember.png")));
	// TODO: offline projectmember!
	final static ImageIcon onlineFullTrustIcon = new ImageIcon(
					Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource(
									"/icons/user-online-fulltrust.png")));
	final static ImageIcon onlineTrustIcon = new ImageIcon(
					Toolkit.getDefaultToolkit().getImage(
									JakeMainApp.class.getResource("/icons/user-online-trust.png")));
	final static ImageIcon onlineNoTrustIcon = new ImageIcon(
					Toolkit.getDefaultToolkit().getImage(
									JakeMainApp.class.getResource("/icons/user-online-notrust.png")));
	final static ImageIcon offlineFullTrustIcon = new ImageIcon(
					Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResource(
									"/icons/user-offline-fulltrust.png")));
	final static ImageIcon offlineTrustIcon = new ImageIcon(
					Toolkit.getDefaultToolkit().getImage(
									JakeMainApp.class.getResource("/icons/user-offline-trust.png")));
	final static ImageIcon offlineNoTrustIcon = new ImageIcon(
					Toolkit.getDefaultToolkit().getImage(
									JakeMainApp.class.getResource("/icons/user-offline-notrust.png")));


	/* This is the only method defined by ListCellRenderer.  We just
		 * reconfigure the Jlabel each time we're called.
		 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
					// value to display
					int index,		// cell index
					boolean iss,	 // is the cell selected
					boolean chf)	 // the list and the cell have the focus
	{

		boolean isYou = index == 0;

		UserInfo user = (UserInfo) value;

		String nickOrFullName = UserHelper.getNickOrFullName(user);

		// change color on selection
		String subColor = iss ? "White" : "Gray";
		String shortStatusStr;

		shortStatusStr = user.getUser().getUserId();

		String valStr;

		if (!isYou) {
			valStr =
							"<html><b>" + nickOrFullName + "</b><br><font color=" + subColor + ">" + shortStatusStr + "</font></html>";
		} else {
			// TODO: localize!
			valStr =
							"<html><b>" + "You (" + nickOrFullName + ")</b><br><font color=" + subColor + ">" + shortStatusStr + "</font></html>";
		}

		/* The DefaultListCellRenderer class will take care of
				  * the JLabels text property, it's foreground and background
				  * colors, and so on.
				  */
		super.getListCellRendererComponent(list, valStr, index, iss, chf);

		TrustState memberTrust = user.getTrust();

		if (user.isOnline()) {
			switch (memberTrust) {
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
			switch (user.getTrust()) {
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

		// override icon for own project user
		if (isYou) {
			setIcon(projectMemberIcon);
		}

		String statusStr = (user.isOnline()) ? "Online" : "Offline";
		statusStr += ", ";

		// TODO: localize + change labels
		switch (memberTrust) {
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

		setToolTipText("<html><b>" + user.getFirstName() + " " + user
						.getLastName() + "</b><br><b>'" + user
						.getNickName() + "'</b><br>" + statusStr + "</html>");

		return this;
	}
}
