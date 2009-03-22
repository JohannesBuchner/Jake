/**
 * 
 */
package com.jakeapp.core.services.futures;

import java.util.Collection;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.core.domain.NoteObject;


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
