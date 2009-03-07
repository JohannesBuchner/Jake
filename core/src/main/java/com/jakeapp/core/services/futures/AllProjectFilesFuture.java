package com.jakeapp.core.services.futures;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class AllProjectFilesFuture extends AvailableLaterObject<List<FileObject>> {

	private static Logger log = Logger.getLogger(AllProjectFilesFuture.class);

	private IFileObjectDao fileObjectDao;

	private Project project;
	
	private IFSService fss;

	private void setFileObjectDao(IFileObjectDao fileObjectDao) {
		this.fileObjectDao = fileObjectDao;
	}
	
	private void setFss(IFSService fss) {
		this.fss = fss;
	}

	private IFSService getFss() {
		return fss;
	}
	
	private void setProject(Project project) {
		this.project = project;
	}

	private Project getProject() {
		return project;
	}

	public AllProjectFilesFuture(Project project,IFileObjectDao dao, IFSService fss) {
		super();
		this.setFileObjectDao(dao);
		this.setFss(fss);
		this.setProject(project);
	}


	public AllProjectFilesFuture(
					ProjectApplicationContextFactory applicationContextFactory,
					Project project) {
		super();

		log.debug("Creating a AllProjectFilesFuture with " +
										applicationContextFactory + "on project " + project);

		this.setProject(project);

		fileObjectDao = applicationContextFactory.getFileObjectDao(project);
	}

	
	/**
	 * Gets all *only local* files 
	 * @param dir The dir to test recursively
	 * @param sharedFiles Files that are already in the project-database and should not
	 * 	returned.
	 * @return A Collection of only local files.
	 */
	private Collection<FileObject> getLocalFiles(String dir, Collection<FileObject> sharedFiles) {
		List<FileObject> result = new ArrayList<FileObject>();
		List<String> localFiles;
		FileObject fo;
		
		try {
			localFiles = this.getFss().recursiveListFiles();
		} catch (IOException e) {
			localFiles = new ArrayList<String>();
		}
		for (String localFile : localFiles) {
			fo = new FileObject(this.getProject(),localFile);
			if (!sharedFiles.contains(fo))
				result.add(fo);
		}
		
		return result;
	}

	@Override
	@Transactional
	public List<FileObject> calculate() {
		List<FileObject> result = fileObjectDao.getAll();
		Collection<FileObject> localfiles;
		
		//get *only local* files
		localfiles = this.getLocalFiles("", result);
		result.addAll(localfiles);
		
		//TODO get remote files

		return result;
	}
}