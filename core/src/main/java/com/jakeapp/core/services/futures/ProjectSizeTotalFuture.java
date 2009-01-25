/**
 * 
 */
package com.jakeapp.core.services.futures;


import java.util.List;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;


/**
 * Calculates the total size of a list of files.
 * @author djinn
 */
public class ProjectSizeTotalFuture extends AvailableLaterWrapperObject<Long, List<FileObject>> {

	@Override
	public Long calculate() {
		final String STATUS = "";
		long result = 0;
		List<FileObject> files;
		double progress=0d;
		double singlestep = 0;
		getListener().statusUpdate(progress, STATUS);
		
		files = this.getSource().get();
		if (files.size() > 0) singlestep = 1d / files.size();
		for (FileObject file : files) {
			try {
				result += file.getAbsolutePath().length();
			} catch (SecurityException se) {
				// empty catch
			}
			progress += singlestep;
			getListener().statusUpdate(progress, STATUS);
		}
		
		return result;
	}
}
