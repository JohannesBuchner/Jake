package com.jakeapp.gui.swing.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.ICoreAccess;
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
		 ICoreAccess core = JakeMainApp.getCore();
		 boolean newLockingState = !this.isLocked; //FIXME: davelish hack: this.isLocked is reset after every note change.
		 log.debug("selection count: " + this.getSelectedNotes().size());
		 for (NoteObject note : this.getSelectedNotes()) {
			  log.debug("setting soft lock for note: " + note + "---------------------------------------------------");
			  core.setSoftLock(note, newLockingState, null);
		 }
	}

	@Override
	public void updateAction() {
		log.debug("update action");
		 log.debug("getting " + this.getSelectedNotes().size() + " notes...");
		if (this.getSelectedNotes().size() > 0) {
			this.setEnabled(true);

			this.isLocked = JakeMainApp.getCore().isSoftLocked(this.getSelectedNotes().get(0));
			
			if(this.isLocked) {
				if(this.getSelectedNotes().size() == 1) {
					this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("unlockNote"));
				} else {
					this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("unlockNotes"));
				}
			} else {
				if(this.getSelectedNotes().size() == 1) {
					this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("softLockNote"));
				} else {
					this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("softLockNotes"));
				}
			}
		} else {
			this.setEnabled(false);
			this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("softLockNote"));
		}
		log.debug("where still having " + this.getSelectedNotes().size() + " notes!");
		
	}



}
