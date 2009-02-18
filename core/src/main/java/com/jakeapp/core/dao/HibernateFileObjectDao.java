package com.jakeapp.core.dao;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;

import java.util.List;
import java.util.UUID;


/**
 * A hibernate file object DAO.
 */
public class HibernateFileObjectDao extends HibernateJakeObjectDao<FileObject> implements
		IFileObjectDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final FileObject persist(final FileObject foin) {
		FileObject fo;
		if (foin.getUuid() == null)
			fo = new FileObject(UUID.randomUUID(), foin.getProject(), foin.getRelPath());
		else
			fo = foin;
		return super.persist(fo);
	}

	@Override
	public FileObject get(UUID objectId) throws NoSuchJakeObjectException {
		return super.get(objectId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<FileObject> getAll() {
		return super.getAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void delete(final FileObject jakeObject) {
		super.delete(jakeObject);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public final FileObject addTagTo(FileObject jakeObject, final Tag tag) {
		return super.addTagTo(jakeObject, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final FileObject removeTagFrom(FileObject jakeObject, final Tag tag) {
		return super.removeTagFrom(jakeObject, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addTagsTo(FileObject jakeObject, final Tag... tags) {
		super.addTagsTo(jakeObject, tags);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final List<Tag> getTagsFor(final FileObject jakeObject) {
		return super.getTagsFor(jakeObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void removeTagsFrom(FileObject jakeObject, Tag... tags) {
		super.removeTagsFrom(jakeObject, tags);
	}

	@Override
	public FileObject complete(FileObject jakeObject) throws NoSuchJakeObjectException {
		if(jakeObject.getUuid() != null) {
			return get(jakeObject.getUuid());
		} else if (jakeObject.getRelPath() != null ){
			return getFileObjectByRelpath(jakeObject.getRelPath());
		} else
			throw new NoSuchJakeObjectException("neither uuid nor relpath given");
	}

	/**
	 * This method returns a FileObject with the given relPath.
	 * @param relPath the relativ Path of this file
	 * @return the FileObject requested
     * @throws NoSuchJakeObjectException if no object with that path was found in the database
	 */
	private FileObject getFileObjectByRelpath(String relPath) throws NoSuchJakeObjectException {

        List<FileObject> result = this.
                getHibernateTemplate().
                getSessionFactory().getCurrentSession().createQuery("FROM fileobject WHERE relPath = ?").
                setString(0, relPath).list();

        if(result.size() > 0)
        {
            return result.get(0);
        }
        throw new NoSuchJakeObjectException("No fileObject by given relPath there");

	}

	@Override
	public FileObject get(String relpath) throws NoSuchJakeObjectException {
		return this.getFileObjectByRelpath(relpath);
	}
}
