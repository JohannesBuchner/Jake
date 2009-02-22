package com.jakeapp.core.services.futures;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.availablelater.AvailableNowObject;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class AllProjectFilesFuture extends AvailableNowObject<List<FileObject>> {

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
		super(new ArrayList<FileObject>());
		this.setFileObjectDao(dao);
	}


	public AllProjectFilesFuture(
					ProjectApplicationContextFactory applicationContextFactory,
					Project project) {
		super(new ArrayList<FileObject>());

		log.debug(
						"\n\n\n\n\n\n\n\n\nCreating a AllProjectFilesFuture\n\n\n\n\n\n\n\n\n");

		this.applicationContextFactory = applicationContextFactory;
		this.project = project;
	}


	@Override
	@Transactional
	public List<FileObject> calculate() {
		log.debug("\n\n\n\n starting thread & running AllProjectFilesFuture ... \n\n\n\n");

		List<FileObject> result = this.applicationContextFactory
						.getFileObjectDao(project).getAll();

		log.debug("found " + result.size() + " files in the DB ");
		for (FileObject file : result) {
			System.out.println("file = " + file);
		}

		result.add(new FileObject(project, "blabla"));

		log.debug("\n\n\n\n\n\n\n\n\nfinished calculation\n\n\n\n\n\n\n\n\n");

		return result;
	}
}