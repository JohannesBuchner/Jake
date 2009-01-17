/**
 * 
 */
package com.jakeapp.core.services.futures;


import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;

import java.util.List;


/**
 * Calculates the total size of a list of files.
 * @author djinn
 */
public class ProjectSizeTotalFuture extends AvailableLaterWrapperObject<Long, List<FileObject>> {

	public ProjectSizeTotalFuture(AvailabilityListener listener) {
		super(listener);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		/*
		long result = 0;
		List<FileObject> files;

		files = this.getAllProjectFiles(project, null);
		for (FileObject file : files) {
			try {
				result += file.getAbsolutePath().length();
			} catch (SecurityException se) {
				// empty catch
			}
		}
		*/
	}
}
