package com.jakeapp.core.services;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.ProjectMember;


public class NoteManagingService implements INoteManagingService {

	private INoteObjectDao dao;
	private ILogEntryDao ledao;
	private IProjectsManagingService pms;
	
	private void setDao(INoteObjectDao dao) {
		this.dao = dao;
		
	}

	private INoteObjectDao getDao() {
		return dao;
	}
	
	private ILogEntryDao getLogEntryDao() {
		return ledao;
	}
	
	public NoteManagingService(IProjectsManagingService pms,INoteObjectDao dao, ILogEntryDao ledao) {
		super();
		this.pms = pms;
		this.setDao(dao);
		this.ledao = ledao;
	}

	@Override
	@Transactional
	public List<NoteObject> getNotes() {
		return this.getDao().getAll();
	}

	@Override
	@Transactional
	public void addNote(NoteObject note) {
		this.getDao().persist(note);
	}

	@Override
	@Transactional
	public void deleteNote(NoteObject note,ProjectMember member) throws NoSuchJakeObjectException {
		LogEntry <? extends ILogable> logEntry = null;
		
		//If note is local - announce deletion
		if (pms.isLocalJakeObject(note)) {
			logEntry = new LogEntry<NoteObject>(
				UUID.randomUUID(),
				LogAction.JAKE_OBJECT_DELETE,
				Calendar.getInstance().getTime(),
				note.getProject(),
				note,
				member
			);
			
			this.getLogEntryDao().create(logEntry);
		}
		//Delete note from db
		this.getDao().delete(note);
	}

	@Override
	@Transactional
	public void saveNote(NoteObject note) {
		this.getDao().persist(note);
	}
}
