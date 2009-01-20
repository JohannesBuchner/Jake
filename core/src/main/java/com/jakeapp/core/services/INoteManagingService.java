package com.jakeapp.core.services;

import java.util.List;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;


/**
 * Interface for classes that offer Services for manipulating Notes
 * @author christopher
 */
public interface INoteManagingService {
	/**
	 * Retrieves all Notes for a Project
	 * 
	 * @param project
	 *            The Project to retrieve all notes for
	 * @return all Notes
	 */
	List<NoteObject> getNotes(Project project);

	/**
	 * Deletes a Note from the Database. Creates a Logentry indicating
	 * the Note has been deleted.
	 * @param note
	 * @throws NoSuchJakeObjectException 
	 */
	void deleteNote(NoteObject note) throws NoSuchJakeObjectException;

	/**
	 * Adds a Note to the Database, but does not announce it yet.
	 * @param note
	 */
	void addNote(NoteObject note);

	/**
	 * Saves a new Version of a NoteObject
	 * @param note The updated Version of a note. It must exist.
	 */
	void saveNote(NoteObject note);
}
