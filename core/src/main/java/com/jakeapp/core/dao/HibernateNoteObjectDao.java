package com.jakeapp.core.dao;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;

import java.util.List;
import java.util.UUID;

/**
 * A hibernate noteObject DAO.
 */
public class HibernateNoteObjectDao extends HibernateJakeObjectDao<NoteObject> implements INoteObjectDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public NoteObject persist(NoteObject jakeObject) {
        return super.persist(jakeObject);
    }

    @Override
    public NoteObject get(UUID objectId) throws NoSuchJakeObjectException {
        return super.get(objectId);
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
    public void delete(NoteObject jakeObject) {
        super.delete(jakeObject);
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

	@Override
	public boolean isNote(NoteObject jakeObject) {
		return true;
	}

	@Override
	public NoteObject complete(NoteObject jakeObject) throws NoSuchJakeObjectException {
		return get(jakeObject.getUuid());
	}
}
