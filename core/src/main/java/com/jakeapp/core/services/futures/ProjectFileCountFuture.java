package com.jakeapp.core.services.futures;

import java.util.List;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;


public class ProjectFileCountFuture extends AvailableLaterWrapperObject<Integer, List<FileObject>> {
	public ProjectFileCountFuture(AvailabilityListener listener) {
		super(listener);
	}

	@Override
	public void run() {
		this.set(this.getSource().get().size());
	}
}
