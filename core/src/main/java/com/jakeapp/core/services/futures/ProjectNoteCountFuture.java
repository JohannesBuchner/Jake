/**
 * 
 */
package com.jakeapp.core.services.futures;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;

import java.util.List;


/**
 * @author djinn
 *
 */
public class ProjectNoteCountFuture extends AvailableLaterWrapperObject<Integer, List<NoteObject>> {
	@Override
	public Integer calculate() {
		return this.getSource().get().size();
	}
}
