/**
 * 
 */
package com.jakeapp.core.services.futures;


import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAFileException;


/**
 * Calculates the total size of a list of files.
 * @author djinn
 */
public class ProjectSizeTotalFuture extends AvailableLaterWrapperObject<Long, List<FileObject>> {

	private static final Logger log = Logger.getLogger(ProjectSizeTotalFuture.class);

	private IFSService fss;

	public ProjectSizeTotalFuture(IFSService fss) {
		this.fss = fss;
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
				try {
					result += fss.getFileSize(file.getRelPath());
				} catch (FileNotFoundException e) {
					log.error("unexpected exception", e);
				} catch (NotAFileException e) {
					log.error("unexpected exception", e);
				} catch (InvalidFilenameException e) {
					log.error("database is corrupt", e);
				}
			} catch (SecurityException se) {
				// empty catch
			}
			progress += singlestep;
			getListener().statusUpdate(progress, STATUS);
		}
		
		return result;
	}
}
