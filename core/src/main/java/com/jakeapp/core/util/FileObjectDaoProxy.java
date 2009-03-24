/**
 * 
 */
package com.jakeapp.core.util;

import java.util.List;
import java.util.UUID;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import org.apache.log4j.Logger;

/**
 * This backpacks the Project attribute onto each retrieved JakeObject
 * 
 * @author johannes
 */
final class FileObjectDaoProxy implements IFileObjectDao {
	private static Logger log = Logger.getLogger(FileObjectDaoProxy.class);

	private final IFileObjectDao innerDao;

	private final Project project;

	FileObjectDaoProxy(IFileObjectDao innerDao, Project project) {
		this.innerDao = innerDao;
		this.project = project;
	}

	private FileObject getWithProject(FileObject fo) {
		fo.setProject(this.project);
		return fo;
	}

	@Override
	public FileObject get(String relpath) throws NoSuchJakeObjectException {
		return getWithProject(this.innerDao.get(relpath));
	}

	@Override
	public FileObject complete(FileObject jakeObject)
			throws NoSuchJakeObjectException {
		return getWithProject(this.innerDao.complete(jakeObject));
	}

	@Override
	public void delete(FileObject jakeObject) throws NoSuchJakeObjectException {
		this.innerDao.delete(jakeObject);
	}

	@Override
	public FileObject get(UUID objectId) throws NoSuchJakeObjectException {
		return getWithProject(this.innerDao.get(objectId));
	}

	@Override
	public List<FileObject> getAll() {
		if(this.innerDao == null)
		{
			log.warn("innerDao is null");
			System.exit(1);
		}
		List<FileObject> results = this.innerDao.getAll();
		for(FileObject fo : results) {
			fo.setProject(this.project);
		}
		return results;
	}

	@Override
	public FileObject persist(FileObject jakeObject) {
		return getWithProject(this.innerDao.persist(jakeObject));
	}
}