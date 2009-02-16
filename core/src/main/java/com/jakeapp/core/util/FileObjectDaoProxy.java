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
import com.jakeapp.core.domain.Tag;

final class FileObjectDaoProxy implements IFileObjectDao {

	private final IFileObjectDao innerDao;

	private final Project project;

	FileObjectDaoProxy(IFileObjectDao innerDao, Project p) {
		this.innerDao = innerDao;
		this.project = p;
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
	public FileObject addTagTo(FileObject jakeObject, Tag tag)
			throws NoSuchJakeObjectException {
		return getWithProject(this.innerDao.addTagTo(jakeObject, tag));
	}

	@Override
	public void addTagsTo(FileObject jakeObject, Tag... tags)
			throws NoSuchJakeObjectException {
		this.innerDao.addTagsTo(jakeObject, tags);
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
		List<FileObject> results = this.innerDao.getAll();
		for(FileObject fo : results) {
			fo.setProject(this.project);
		}
		return results;
	}

	@Override
	public List<Tag> getTagsFor(FileObject jakeObject)
			throws NoSuchJakeObjectException {
		return this.innerDao.getTagsFor(jakeObject);
	}

	@Override
	public FileObject persist(FileObject jakeObject) {
		return getWithProject(this.innerDao.persist(jakeObject));
	}

	@Override
	public FileObject removeTagFrom(FileObject jakeObject, Tag tag)
			throws NoSuchJakeObjectException {
		return getWithProject(this.innerDao.removeTagFrom(jakeObject, tag));
	}

	@Override
	public void removeTagsFrom(FileObject jakeObject, Tag... tags)
			throws NoSuchJakeObjectException {
		this.innerDao.removeTagsFrom(jakeObject, tags);
	}
}