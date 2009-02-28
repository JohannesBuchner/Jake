package com.jakeapp.core.services.futures;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class AllProjectFilesFuture extends AvailableLaterObject<List<FileObject>> {

	private static Logger log = Logger.getLogger(AllProjectFilesFuture.class);

	private IFileObjectDao fileObjectDao;

	private ProjectApplicationContextFactory applicationContextFactory;
	private Project project;

	private void setFileObjectDao(IFileObjectDao fileObjectDao) {
		this.fileObjectDao = fileObjectDao;
	}

	private IFileObjectDao getFileObjectDao() {
		return fileObjectDao;
	}

	public AllProjectFilesFuture(IFileObjectDao dao) {
		super();
		this.setFileObjectDao(dao);
	}


	public AllProjectFilesFuture(
					ProjectApplicationContextFactory applicationContextFactory,
					Project project) {
		super();

		log.debug("Creating a AllProjectFilesFuture with " +
										applicationContextFactory + "on project " + project);

		this.applicationContextFactory = applicationContextFactory;
		this.project = project;

		fileObjectDao = this.applicationContextFactory.getFileObjectDao(project);
	}


	@Override @Transactional
	public List<FileObject> calculate() {
		log.debug("starting thread & running AllProjectFilesFuture...");

		List<FileObject> result = fileObjectDao.getAll();

		log.debug("found " + result.size() + " files in the DB");
		for (FileObject file : result) {
			log.debug("file = " + file);
		}

		result.add(new FileObject(project, "blabla"));

		return result;
	}
}