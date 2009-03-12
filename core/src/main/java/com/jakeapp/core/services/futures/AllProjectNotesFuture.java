package com.jakeapp.core.services.futures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.AvailableLaterWaiter;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;

public class AllProjectNotesFuture extends
		AvailableLaterWrapperObject<Collection<NoteObject>, Collection<JakeObject>> {

	private static Logger log = Logger.getLogger(AllProjectNotesFuture.class);


	private AvailableLaterObject<Collection<NoteObject>> localNotesFuture;

	public AllProjectNotesFuture(ProjectApplicationContextFactory context, Project p) {
		super(new AllJakeObjectsFuture(context, p));
		this.localNotesFuture = new AllLocalProjectNotesFuture(context, p);
	}

	@Override
	public Collection<NoteObject> calculate() throws Exception {
		Collection<JakeObject> all = this.getSource().get();
		log.debug("all objects in log: " + all.size());
		Collection<NoteObject> local = AvailableLaterWaiter.await(this.localNotesFuture);
		log.debug("local-only notes: " + local.size());

		Set<NoteObject> sortedNotes = new HashSet<NoteObject>();
		sortedNotes.addAll(local);

		for (JakeObject jo : all) {
			if (jo instanceof NoteObject)
				sortedNotes.add((NoteObject) jo);
		}
		log.debug("in total: " + sortedNotes.size());
		return sortedNotes;
	}
}
