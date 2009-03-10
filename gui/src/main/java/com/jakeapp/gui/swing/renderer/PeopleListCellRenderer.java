package com.jakeapp.gui.swing.renderer;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.synchronization.UserInfo;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.UserHelper;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * The PeopleListCellRenderer.
 * Renders People info with Status Icon.
 */
// TODO: localize
public class PeopleListCellRenderer extends DefaultListCellRenderer {
	private static final Logger log = Logger.getLogger(PeopleListCellRenderer.class);
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

		// fix for empty user
		if(value == null) {
			return new JLabel();
		}

		UserInfo user = (UserInfo) value;
		boolean isYou = UserHelper.isCurrentProjectMember(user.getUser());


		String nickOrFullName = UserHelper.cleanUserId(UserHelper.getNickOrFullName(user));

		// change color on selection
		String subColor = iss ? "White" : "Gray";
		String shortStatusStr;

		shortStatusStr = user.getUser().getUserId();

		String valStr;

		if (!isYou) {
			valStr = String.format("<html><b>%s</b><br><font color=%s>%s</font></html>",
							nickOrFullName, subColor, shortStatusStr);
		} else {
			// TODO: localize!
			valStr = String.format("<html><b>You</b><br><font color=%s>%s</font></html>",
							subColor, shortStatusStr);
		}

		/* The DefaultListCellRenderer class will take care of
				  * the JLabels text property, it's foreground and background
				  * colors, and so on.
				  */
		super.getListCellRendererComponent(list, valStr, index, iss, chf);

		TrustState memberTrust = user.getTrust();
		if (memberTrust == null) {
			log.warn("Received NULL member trust from " + user);
			memberTrust = TrustState.NO_TRUST;
		}

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
			switch (memberTrust) {
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
				statusStr += "Trusted + Trusting new users";
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

		setToolTipText(String.format("<html><b>%s %s</b><br><b>'%s'</b><br>%s</html>",
						user.getFirstName(), user.getLastName(), user.getNickName(), statusStr));

		return this;
	}
}