package com.jakeapp.core.services.futures;

import java.util.Collection;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;


public class ProjectFileCountFuture extends AvailableLaterWrapperObject<Integer, Collection<FileObject>> {

	public ProjectFileCountFuture(AvailableLaterObject<Collection<FileObject>> filesFuture) {
		super(filesFuture);
	}

	@Override
	public Integer calculate() {
		return this.getSource().get().size();
	}
}
