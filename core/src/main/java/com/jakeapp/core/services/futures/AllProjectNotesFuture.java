package com.jakeapp.core.services.futures;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.ProjectApplicationContextFactory;

/**
 * <code>AvailableLaterObject</code> returning all the <code>NoteObject</code>s belonging
 * to a certain <code>Project</code>.
 */
public class AllProjectNotesFuture extends
		AvailableLaterWrapperObject<Collection<NoteObject>, Collection<JakeObject>> {

	private static Logger log = Logger.getLogger(AllProjectNotesFuture.class);

	private AvailableLaterObject<Collection<NoteObject>> localNotesFuture;

	public AllProjectNotesFuture(ProjectApplicationContextFactory context, Project p) {
		super(new AllJakeObjectsFuture(context, p));
		this.localNotesFuture = new AllLocalProjectNotesFuture(context, p);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<NoteObject> calculate() throws Exception {
		Collection<JakeObject> all = this.getSource().get();
		log.debug("all objects in log: " + all.size());
		Collection<NoteObject> local = AvailableLaterWaiter.await(this.localNotesFuture);
		log.debug("local-only notes: " + local.size());

		Set<NoteObject> result = new TreeSet<NoteObject>(NoteObject.getUUIDComparator());
		result.addAll(local);

		for (JakeObject jo : all) {
			if (jo instanceof NoteObject)
				result.add((NoteObject) jo);
		}
		
		for(NoteObject no : result) {
			log.debug("got " + no + ":" + no.getProject());
		}
		log.debug("in total: " + result.size());
		return result;
	}
}
