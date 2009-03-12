/**
 * 
 */
package com.jakeapp.core.services.futures;


import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableLaterWrapperObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAFileException;


/**
 * Calculates the total size of a list of files.
 * @author djinn
 */
public class ProjectSizeTotalFuture extends AvailableLaterWrapperObject<Long, Collection<FileObject>> {

	private static final Logger log = Logger.getLogger(ProjectSizeTotalFuture.class);

	private IFSService fss;

	public ProjectSizeTotalFuture(IFSService fss, AvailableLaterObject<Collection<FileObject>> filesFuture) {
		super(filesFuture);
		this.fss = fss;
	}

	@Override
	public Long calculate() {
		final String STATUS = "";
		long result = 0;
		Collection<FileObject> files;
		double progress=0d;
		double singlestep = 0;
		getListener().statusUpdate(progress, STATUS);
		
		files = this.getSource().get();
		if (files.size() > 0) singlestep = 1d / files.size();
		for (FileObject file : files) {
			try {
				try {
					result += this.fss.getFileSize(file.getRelPath());
				} catch (FileNotFoundException e) {
					log.warn("unexpected exception", e);
				} catch (NotAFileException e) {
					log.warn("unexpected exception", e);
				} catch (InvalidFilenameException e) {
					log.warn("database is corrupt", e);
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
