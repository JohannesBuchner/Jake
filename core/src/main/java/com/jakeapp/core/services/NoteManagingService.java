package com.jakeapp.core.services;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.IUserIdDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.synchronization.UserTranslator;
import com.jakeapp.core.util.ProjectApplicationContextFactory;


public class NoteManagingService extends JakeService implements INoteManagingService {

    public NoteManagingService(ProjectApplicationContextFactory applicationContextFactory, IUserIdDao userIdDao)
    {
    	super(applicationContextFactory,userIdDao);
    }

    @Override
	@Transactional
	public List<NoteObject> getNotes(Project project) {
//        List<NoteObject> notesList = new ArrayList<NoteObject>();
//
//
//        notesList.add(new NoteObject(new UUID(1, 1), project, "If you have five dollars and Chuck Norris has five dollars, Chuck Norris has more money than you"));
//        notesList.add(new NoteObject(new UUID(2, 1), project, "Apple pays Chuck Norris 99 cents every time he listens to a song."));
//        notesList.add(new NoteObject(new UUID(3, 1), project, "Chuck Norris is suing Myspace for taking the name of what he calls everything around you."));
//        notesList.add(new NoteObject(new UUID(4, 1), project, "Chuck Norris destroyed the periodic table, because he only recognizes the element of surprise."));
//        notesList.add(new NoteObject(new UUID(4, 1), project, "Chuck Norris can kill two stones with one bird."));
//        notesList.add(new NoteObject(new UUID(5, 1), project, "The leading causes of death in the United States are: 1. Heart Disease 2. Chuck Norris 3. Cancer."));
//        notesList.add(new NoteObject(new UUID(6, 1), project, "Chuck Norris does not sleep. He waits."));
//        notesList.add(new NoteObject(new UUID(7, 1), project, "There is no theory of evolution. Just a list of animals Chuck Norris allows to live. "));
//        notesList.add(new NoteObject(new UUID(8, 1), project, "Guns don't kill people, Chuck Norris does."));
//        notesList.add(new NoteObject(new UUID(1, 1), project, "Chuck Norris does not need an undo function"));
//
//
//        return notesList;
//        try
//        {
            return this.getApplicationContextFactory().getNoteObjectDao(project).getAll();
//        }
//		return this.getDao().getAll();
	}

	@Override
	@Transactional
	public void addNote(NoteObject note) {
        Project project = note.getProject();
        this.getApplicationContextFactory().getNoteObjectDao(project).persist(note);
	}

	@Override
	@Transactional
	public void deleteNote(NoteObject note) throws NoSuchJakeObjectException {
		Project project = note.getProject();


        LogEntry <? extends ILogable> logEntry = null;
		
		//If note is local - announce deletion
		if (this.isLocalJakeObject(note)) {
			//fetch real member
			ProjectMember member = null;
			try {
				member = new UserTranslator(this.getApplicationContextFactory(), getUserIdDao()).getProjectMemberFromUserId(project,project.getUserId());
			} catch (IllegalArgumentException e) {
				throw new NoSuchJakeObjectException(e);
			} catch (NoSuchProjectMemberException e) {
				throw new NoSuchJakeObjectException(e);
			}
				
			
			logEntry = new LogEntry<NoteObject>(
				UUID.randomUUID(),
				LogAction.JAKE_OBJECT_DELETE,
				Calendar.getInstance().getTime(),
				note.getProject(),
				note,
				member
			);
			this.getApplicationContextFactory().getLogEntryDao(project).create(logEntry);
//			this.getLogEntryDao().create(logEntry);
		}
        this.getApplicationContextFactory().getNoteObjectDao(project).delete(note);
		//Delete note from db
//		this.getDao().delete(note);
	}

	@Override
	@Transactional
	public void saveNote(NoteObject note) {
        Project project = note.getProject();
        this.getApplicationContextFactory().getNoteObjectDao(project).persist(note);
//		this.getDao().persist(note);
	}
}
