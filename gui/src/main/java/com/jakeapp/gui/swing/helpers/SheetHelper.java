package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.actions.users.InviteUsersAction;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.dialogs.generic.SheetEvent;
import com.jakeapp.gui.swing.dialogs.generic.SheetListener;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.panels.FilePanel;

import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;

/**
 * @author studpete
 */
public class SheetHelper {

	/**
	 * Helper class for a simple question with a message and a confirm button.
	 * Returns true if the User presses the ok btn.
	 * @param msg
	 * @param btn
	 * @return
	 */
	public static boolean showConfirm(String msg, String btn) {
		
		final boolean[] ret = new boolean[]{false};
			ResourceMap map = FilePanel.getInstance().getResourceMap();
			String[] options = {btn, map.getString("genericCancel")};

			//ask user and do the real work with a Worker!
			JSheet.showOptionSheet(JakeContext.getFrame(),
							msg, JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options, options[0],
							new SheetListener() {
								@Override
								public void optionSelected(SheetEvent evt) {
									if (evt.getOption() == 0) {
										ret[0] = true;
									}
								}
							});

		return ret[0];
	}
}
