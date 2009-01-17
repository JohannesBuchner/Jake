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
		final String STATUS = "";
		long result = 0;
		List<FileObject> files;
		double progress=0d;
		double singlestep = 0;
		listener.statusUpdate(progress, STATUS);
		
		files = this.getSource().get();
		if (files.size() > 0) singlestep = 1d / files.size();
		for (FileObject file : files) {
			try {
				result += file.getAbsolutePath().length();
			} catch (SecurityException se) {
				// empty catch
			}
			progress += singlestep;
			listener.statusUpdate(progress, STATUS);
		}
		
		this.set(result);
	}
}
