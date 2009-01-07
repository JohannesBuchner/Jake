package com.jakeapp.core.dao;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.Tag;

import java.util.List;
import java.util.UUID;


/**
 * A hibernate file object DAO.
 */
public class HibernateFileObjectDao extends HibernateJakeObjectDao<FileObject> {
    /**
     * {@inheritDoc}
     */
    @Override
    public final FileObject persist(final FileObject jakeObject) {
        return super.persist(jakeObject);
    }

    @Override
    public FileObject get(Project project, UUID objectId) {
        return null; // TODO
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
    public final List<FileObject> getAll(final Project project) {
        return super.getAll(project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void makeTransient(final FileObject jakeObject) {
        super.makeTransient(jakeObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Tag> getTags(final FileObject jakeObject) {
        return super.getTags(jakeObject);
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
    public final FileObject removeTagFrom(FileObject jakeObject,
                                          final Tag tag) {
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
}
