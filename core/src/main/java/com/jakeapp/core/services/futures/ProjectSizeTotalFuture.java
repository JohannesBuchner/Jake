/**
 * 
 */
package com.jakeapp.core.services.futures;


import java.io.IOException;
import java.util.List;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.jake.fss.IFSService;


/**
 * Calculates the total size of a list of files.
 * @author djinn
 */
public class ProjectSizeTotalFuture extends AvailableLaterWrapperObject<Long, List<FileObject>> {

	private IFriendlySyncService sync;

	public ProjectSizeTotalFuture(IFriendlySyncService sync) {
		this.sync = sync;
	}

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
				result += sync.getFile(file).length();
			} catch (IOException e) {
				// doesn't have to be accurate
			} catch (SecurityException se) {
				// empty catch
			}
			progress += singlestep;
			getListener().statusUpdate(progress, STATUS);
		}
		
		return result;
	}
}
