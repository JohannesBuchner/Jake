package com.jakeapp.core.services.futures;

import java.util.Collection;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.core.domain.FileObject;


public class ProjectFileCountFuture extends AvailableLaterWrapperObject<Integer, Collection<FileObject>> {

	public ProjectFileCountFuture(AvailableLaterObject<Collection<FileObject>> filesFuture) {
		super(filesFuture);
	}

	@Override
	public Integer calculate() {
		return this.getSource().get().size();
	}
}
