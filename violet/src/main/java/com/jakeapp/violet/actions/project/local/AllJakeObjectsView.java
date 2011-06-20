package com.jakeapp.violet.actions.project.local;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.jakeapp.violet.model.IJakeObjectModificationListener;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.ProjectModel;

/**
 * A view of all JakeObjects. Notification on changes.
 * 
 * @author johannes
 */
public class AllJakeObjectsView {

	private static Logger log = Logger.getLogger(AllJakeObjectsView.class);

	private Set<JakeObject> objects = new HashSet<JakeObject>();

	private HashSet<IJakeObjectModificationListener> listeners = new HashSet<IJakeObjectModificationListener>();

	public AllJakeObjectsView(Set<JakeObject> initial) {
		this.objects = initial;
	}

	public void onModification(JakeObject jo) {
		for (IJakeObjectModificationListener l : this.listeners) {
			l.modified(jo);
		}
	}

	public Set<JakeObject> getObjects() {
		return objects;
	}

	public void addModificationListener(IJakeObjectModificationListener l) {
		this.listeners.add(l);
	}

	public void removeModificationListener(IJakeObjectModificationListener l) {
		this.listeners.remove(l);
	}

}