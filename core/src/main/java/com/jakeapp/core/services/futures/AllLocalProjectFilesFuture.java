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
import java.util.LinkedList;
import java.util.List;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * gets all Project Files that lie in the project folder.
 * 
 * @author johannes
 */
public class AllLocalProjectFilesFuture extends AvailableLaterObject<List<FileObject>> {

	@SuppressWarnings("unused")
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

	public AllLocalProjectFilesFuture(Project project,IFileObjectDao dao, IFSService fss) {
		super();
		this.setFileObjectDao(dao);
		this.setFss(fss);
		this.setProject(project);
	}


	public AllLocalProjectFilesFuture(
					ProjectApplicationContextFactory applicationContextFactory,
					Project project, IFSService fss) {
		this(project, applicationContextFactory.getFileObjectDao(project), fss);
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
		FileObject fo;

		try {
			for (String localFile : this.getFss().recursiveListFiles()) {
				fo = new FileObject(this.getProject(), localFile);
				if (!sharedFiles.contains(fo))
					result.add(fo);
			}
			return result;
		} catch (IOException e) {
			return new ArrayList<FileObject>();
		}
	}

	@Override
	@Transactional
	public List<FileObject> calculate() {
		// contains method should only check the relpath
		SortedSet<FileObject> sortedFiles = new TreeSet<FileObject>(FileObject
				.getRelpathComparator());
		Collection<FileObject> localOnlyFiles;
		List<FileObject> result;

		for (FileObject fo : this.fileObjectDao.getAll()) {
			sortedFiles.add(fo);
		}

		localOnlyFiles = this.getLocalFiles("", sortedFiles);
		
		result = new LinkedList<FileObject>(sortedFiles);
		result.addAll(localOnlyFiles);

		return result;
	}
}