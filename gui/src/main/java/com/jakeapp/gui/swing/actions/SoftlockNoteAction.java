package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Note Action to lock and unlock a note, on/off checkbox behaviour. Batch enabled.
 *
 * @author Simon
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
	public void actionPerformed(ActionEvent ignored) {
		log.debug("action performed; soft lock note");
		log.debug("selected notes size: " + this.getSelectedNotes().size());
		ICoreAccess core = JakeMainApp.getCore();
		boolean cachedNewLockingState = !this.isLocked;

		for (Attributed<NoteObject> attributedNote : this.getSelectedNotes()) {
			log.debug("attributed note isLocal: " + attributedNote.isOnlyLocal());
			if (!attributedNote.isOnlyLocal()) {
				log.debug("locking note: " + attributedNote + ", setting lock to: " + cachedNewLockingState);
				core.setSoftLock(attributedNote.getJakeObject(), cachedNewLockingState, null);	
			}
		}
	}

	@Override
	public void updateAction() {
		if (this.hasSelectedNotes()) {
			this.isLocked = this.getSelectedNote().isLocked();
			log.debug("the topmost selected note isLocked: " + this.isLocked + " isOnlyLocal: " + this.getSelectedNote().isOnlyLocal());
			
			if (this.getSelectedNote().isOnlyLocal()) {
				this.setEnabled(false);
			} else {
				this.setEnabled(true);
				if (this.isLocked) {
					if (this.hasSingleSelectedNote()) {
						this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("unlockNote"));
					} else {
						this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("unlockNotes"));
					}
				} else {
					if (this.getSelectedNotes().size() == 1) {
						this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("softLockNote"));
					} else {
						this.putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("softLockNotes"));
					}
				}
			}
		} else {
			setEnabled(false);
		}
	}
}