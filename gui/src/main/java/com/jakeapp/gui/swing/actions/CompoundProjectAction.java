package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.JakeAction;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author: studpete
 */
public abstract class CompoundProjectAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(CompoundProjectAction.class);
	protected final PropertyChangeListener up = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateAction();
		}
	};

	public CompoundProjectAction() {
		super();
	}

	/**
	 * Update the action with the internal current action.
	 *
	 * @param action
	 */
	protected void internalActivateAction(JakeAction action) {
		log.debug("activating action: " + action);

		// for now, simply change name
		String oldName = (String) this.getValue(Action.NAME);
		String newName = (String) action.getValue(Action.NAME);
		this.putValue(Action.NAME, newName);
		firePropertyChange(Action.NAME, oldName, newName);

		this.setEnabled(action.isEnabled());
	}
}
