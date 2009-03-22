package com.jakeapp.core.services.futures;

import java.util.Collection;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.core.domain.FileObject;

/**
 * <code>AvailableLaterObject</code> returning an <code>Integer</code> representing the number
 * of <code>FileObject</code> within a <code>Project</code>
 */
public class ProjectFileCountFuture extends AvailableLaterWrapperObject<Integer, Collection<FileObject>> {

	public ProjectFileCountFuture(AvailableLaterObject<Collection<FileObject>> filesFuture) {
		super(filesFuture);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer calculate() {
		return this.getSource().get().size();
	}
}
