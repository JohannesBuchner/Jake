package com.jakeapp.core.services.futures;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.AvailableLaterWaiter;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;

public class AllProjectFilesFuture extends AvailableLaterObject<Collection<FileObject>> {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(AllProjectFilesFuture.class);

	private AllJakeObjectsFuture jakeObjectsFuture;

	private AllLocalProjectFilesFuture localFilesFuture;

	public AllProjectFilesFuture(ProjectApplicationContextFactory context, Project p,
			IFSService fss) {
		this.jakeObjectsFuture = new AllJakeObjectsFuture(context, p);
		this.localFilesFuture = new AllLocalProjectFilesFuture(context, p, fss);
	}

	@Override
	public Collection<FileObject> calculate() throws Exception {
		// this does parallelization!
		this.jakeObjectsFuture.start();
		this.localFilesFuture.start();
		
		Collection<JakeObject> all = AvailableLaterWaiter.await(this.jakeObjectsFuture);
		log.debug("all objects in log: " + all.size());
		Collection<FileObject> local = AvailableLaterWaiter.await(this.localFilesFuture);
		log.debug("local-only files: " + local.size());

		SortedSet<FileObject> sortedFiles = new TreeSet<FileObject>(FileObject
				.getRelpathComparator());
		sortedFiles.addAll(local);

		for (JakeObject jo : all) {
			if (jo instanceof FileObject)
				sortedFiles.add((FileObject) jo);
		}
		log.debug("in total: " + sortedFiles.size());
		return sortedFiles;
	}


}