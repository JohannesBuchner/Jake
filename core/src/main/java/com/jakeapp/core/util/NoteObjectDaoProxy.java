/**
 * 
 */
package com.jakeapp.core.util;

import java.util.List;
import java.util.UUID;

import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.Tag;

final class NoteObjectDaoProxy implements INoteObjectDao {

	private final INoteObjectDao innerDao;

	private final Project project;

	NoteObjectDaoProxy(INoteObjectDao innerDao, Project p) {
		this.innerDao = innerDao;
		this.project = p;
	}

	private NoteObject getWithProject(NoteObject fo) {
		fo.setProject(this.project);
		return fo;
	}

	@Override
	public NoteObject addTagTo(NoteObject jakeObject, Tag tag)
			throws NoSuchJakeObjectException {
		return getWithProject(this.innerDao.addTagTo(jakeObject, tag));
	}

	@Override
	public void addTagsTo(NoteObject jakeObject, Tag... tags)
			throws NoSuchJakeObjectException {
		this.innerDao.addTagsTo(jakeObject, tags);
	}

	@Override
	public NoteObject complete(NoteObject jakeObject)
			throws NoSuchJakeObjectException {
		return getWithProject(this.innerDao.complete(jakeObject));
	}

	@Override
	public void delete(NoteObject jakeObject) throws NoSuchJakeObjectException {
		this.innerDao.delete(jakeObject);
	}

	@Override
	public NoteObject get(UUID objectId) throws NoSuchJakeObjectException {
		return getWithProject(this.innerDao.get(objectId));
	}

	@Override
	public List<NoteObject> getAll() {
		List<NoteObject> results = this.innerDao.getAll();
		for(NoteObject fo : results) {
			fo.setProject(this.project);
		}
		return results;
	}

	@Override
	public List<Tag> getTagsFor(NoteObject jakeObject)
			throws NoSuchJakeObjectException {
		return this.innerDao.getTagsFor(jakeObject);
	}

	@Override
	public NoteObject persist(NoteObject jakeObject) {
		return getWithProject(this.innerDao.persist(jakeObject));
	}

	@Override
	public NoteObject removeTagFrom(NoteObject jakeObject, Tag tag)
			throws NoSuchJakeObjectException {
		return getWithProject(this.innerDao.removeTagFrom(jakeObject, tag));
	}

	@Override
	public void removeTagsFrom(NoteObject jakeObject, Tag... tags)
			throws NoSuchJakeObjectException {
		this.innerDao.removeTagsFrom(jakeObject, tags);
	}
}