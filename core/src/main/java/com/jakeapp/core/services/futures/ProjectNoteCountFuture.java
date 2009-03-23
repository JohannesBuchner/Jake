/**
 * 
 */
package com.jakeapp.core.services.futures;

import java.util.Collection;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.core.domain.NoteObject;


/**
 * <code>AvailableLaterObject</code> returning an <code>Integer</code> representing the number
 * of <code>FileObject</code> within a <code>Project</code>
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
