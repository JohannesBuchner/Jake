package com.jakeapp.core.services.futures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;

/**
 * gets all Project Files that lie in the project folder.
 * 
 * @author johannes
 */
public class AllLocalProjectFilesFuture extends
		AvailableLaterObject<Collection<FileObject>> {

	private static Logger log = Logger.getLogger(AllLocalProjectFilesFuture.class);

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
		return this.fss;
	}

	private void setProject(Project project) {
		this.project = project;
	}

	private Project getProject() {
		return this.project;
	}

	public AllLocalProjectFilesFuture(Project project, IFileObjectDao dao, IFSService fss) {
		super();
		this.setFileObjectDao(dao);
		this.setFss(fss);
		this.setProject(project);
	}


	public AllLocalProjectFilesFuture(
			ProjectApplicationContextFactory applicationContextFactory, Project project,
			IFSService fss) {
		this(project, applicationContextFactory.getFileObjectDao(project), fss);
	}


	/**
	 * Gets all *only local* files
	 * 
	 * @return A changeable List of only local files.
	 */
	private List<FileObject> getLocalFiles() {
		List<FileObject> result = new ArrayList<FileObject>();
		FileObject fo;

		try {
			for (String localFile : this.getFss().recursiveListFiles()) {
				fo = new FileObject(this.getProject(), localFile);
				result.add(fo);
			}
			return result;
		} catch (IOException e) {
			return new ArrayList<FileObject>();
		}
	}

	@Override
	@Transactional
	public Collection<FileObject> calculate() {
		// contains method should only check the relpath
		SortedSet<FileObject> sortedFiles = new TreeSet<FileObject>(FileObject
				.getRelpathComparator());

		sortedFiles.addAll(this.fileObjectDao.getAll());
		log.debug("in the database: " + sortedFiles.size());
		sortedFiles.addAll(this.getLocalFiles());
		log.debug("and including the ones in the folder: " + sortedFiles.size());

		return sortedFiles;
	}
}