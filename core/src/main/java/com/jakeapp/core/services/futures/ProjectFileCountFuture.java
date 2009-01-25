package com.jakeapp.core.services.futures;

import java.util.List;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;


public class ProjectFileCountFuture extends AvailableLaterWrapperObject<Integer, List<FileObject>> {

	@Override
	public Integer calculate() {
		return this.getSource().get().size();
	}
}
