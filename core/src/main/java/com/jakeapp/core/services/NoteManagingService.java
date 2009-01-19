package com.jakeapp.core.services;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.util.ApplicationContextFactory;


public class NoteManagingService implements INoteManagingService {

    private ApplicationContextFactory applicationContextFactory;

    public NoteManagingService(ApplicationContextFactory applicationContextFactory)
    {
        this.applicationContextFactory = applicationContextFactory;
    }

    @Override
	@Transactional
	public List<NoteObject> getNotes(Project project) {
//        try
//        {
            return this.applicationContextFactory.getNoteObjectDao(project).getAll();
//        }
//		return this.getDao().getAll();
	}

	@Override
	@Transactional
	public void addNote(NoteObject note) {
        Project project = note.getProject();
        this.applicationContextFactory.getNoteObjectDao(project).persist(note);
//		this.getDao().persist(note);
	}


    @Transactional
    public boolean isLocalJakeObject(JakeObject jo) {
        // TODO THIS IS DUPLICATED CODE FROM ProjectsManagingServiceImpl. remove/refactor when possible!
        boolean result = false;

        try {
            this.applicationContextFactory.getLogEntryDao(jo.getProject()).getMostRecentFor(jo);
//            this.getLogEntryDao(jo.getProject()).getMostRecentFor(jo);
        } catch (NoSuchLogEntryException e) {
            /*
                * There is not Logentry for this note. Therefore it has never been
                * announced and is only local.
                */
            result = true;
        }

        return result;
    }

	@Override
	@Transactional
	public void deleteNote(NoteObject note,ProjectMember member) throws NoSuchJakeObjectException {
		Project project = note.getProject();



        LogEntry <? extends ILogable> logEntry = null;
		
		//If note is local - announce deletion
		if (this.isLocalJakeObject(note)) {
			logEntry = new LogEntry<NoteObject>(
				UUID.randomUUID(),
				LogAction.JAKE_OBJECT_DELETE,
				Calendar.getInstance().getTime(),
				note.getProject(),
				note,
				member
			);
			this.applicationContextFactory.getLogEntryDao(project).create(logEntry);
//			this.getLogEntryDao().create(logEntry);
		}
        this.applicationContextFactory.getNoteObjectDao(project).delete(note);
		//Delete note from db
//		this.getDao().delete(note);
	}

	@Override
	@Transactional
	public void saveNote(NoteObject note) {
        Project project = note.getProject();
        this.applicationContextFactory.getNoteObjectDao(project).persist(note);
//		this.getDao().persist(note);
	}
}
