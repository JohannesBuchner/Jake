package com.jakeapp.core.dao;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.Tag;

import java.util.List;

/**
 * A hibernate noteObject DAO.
 */
public class HibernateNoteObjectDao extends HibernateJakeObjectDao<NoteObject> {

    /**
     * {@inheritDoc}
     */
    @Override
    public NoteObject persist(NoteObject jakeObject) {
        return super.persist(jakeObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NoteObject> getAll() {
        return super.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NoteObject> getAll(Project project) {
        return super.getAll(project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeTransient(NoteObject jakeObject) {
        super.makeTransient(jakeObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> getTags(NoteObject jakeObject) {
        return super.getTags(jakeObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NoteObject addTagTo(NoteObject jakeObject, Tag tag) {
        return super.addTagTo(jakeObject, tag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NoteObject removeTagFrom(NoteObject jakeObject, Tag tag) {
        return super.removeTagFrom(jakeObject, tag);
    }
}
