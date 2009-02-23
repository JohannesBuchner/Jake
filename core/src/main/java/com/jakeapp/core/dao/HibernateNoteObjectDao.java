package com.jakeapp.core.dao;

import java.util.List;
import java.util.UUID;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Tag;

/**
 * A hibernate noteObject DAO.
 */
public class HibernateNoteObjectDao extends HibernateJakeObjectDao<NoteObject> implements
		INoteObjectDao {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NoteObject persist(NoteObject noin) {
		NoteObject no;
		if (noin.getUuid() == null)
			no = new NoteObject(noin.getProject(), noin.getContent());
		else
			no = noin;
		return super.persist(no);
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


	@Override
	public NoteObject complete(NoteObject jakeObject) throws NoSuchJakeObjectException {
		return get(jakeObject.getUuid());
	}
}
