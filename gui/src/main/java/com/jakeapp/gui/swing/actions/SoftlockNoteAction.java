package com.jakeapp.gui.swing.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;

/**
 * Note Action to lock and unlock a note, on/off checkbox behaviour. Batch enabled.
 * @author Simon
 *
 */
public class SoftlockNoteAction extends NoteAction {
	
	private static final long serialVersionUID = -3793566528638754529L;
	private static Logger log = Logger.getLogger(SoftlockNoteAction.class);

	private boolean isLocked;

	public SoftlockNoteAction() {
		super();
		
		this.isLocked = false;
		String actionStr = JakeMainView.getMainView().getResourceMap().getString("softLockNote");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: 
	}

	@Override
	public void updateAction() {
		log.debug("update action");
		List<NoteObject> selectedNotes = this.getSelectedNotes();
		if (selectedNotes.size() > 0) {
			this.setEnabled(true);

			this.isLocked = JakeMainApp.getCore().isSoftLocked(selectedNotes.get(0));
			
			if(this.isLocked) {
				if(selectedNotes.size() == 1) {
					this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("unlockNote"));
				} else {
					this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("unlockNotes"));
				}
			} else {
				if(selectedNotes.size() == 1) {
					this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("softLockNote"));
				} else {
					this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("softLockNotes"));
				}
			}
		} else {
			this.setEnabled(false);
			this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("softLockNote"));
		}
		
	}



}
