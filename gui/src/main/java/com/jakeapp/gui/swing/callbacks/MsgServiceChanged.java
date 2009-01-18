package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.core.services.MsgService;

/**
 * Called when the Message Service changes (the current user is switched)
 *
 * @author: studpete
 */
public interface MsgServiceChanged {
	void msgServiceChanged(MsgService msg);
}
