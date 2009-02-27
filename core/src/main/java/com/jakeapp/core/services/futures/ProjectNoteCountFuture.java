/**
 * 
 */
package com.jakeapp.core.services.futures;

import java.util.List;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;


/**
 * @author djinn
 *
 */
public class ProjectNoteCountFuture extends AvailableLaterWrapperObject<Integer, List<Attributed<NoteObject>>> {
	@Override
	public Integer calculate() {
		return this.getSource().get().size();
	}
}
