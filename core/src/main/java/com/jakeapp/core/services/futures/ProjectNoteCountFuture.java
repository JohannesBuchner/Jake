/**
 * 
 */
package com.jakeapp.core.services.futures;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;

import java.util.Collection;
import java.util.List;


/**
 * @author djinn
 * 
 */
public class ProjectNoteCountFuture extends
		AvailableLaterWrapperObject<Integer, Collection<NoteObject>> {

	public ProjectNoteCountFuture(AvailableLaterObject<Collection<NoteObject>> source) {
		super(source);
	}

	@Override
	public Integer calculate() {
		return this.getSource().get().size();
	}
}
