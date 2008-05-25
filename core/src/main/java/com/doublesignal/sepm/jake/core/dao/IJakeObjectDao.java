package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException;

import java.util.List;

/**
 * Serves as a frontend for database-independent JakeObject management.
 *
 * @author Chris
 */
public interface IJakeObjectDao {
	/**
	 * Retrieves a FileObject from the database
	 *
	 * @param name The name of the FileObject to be retrieved
	 * @return The FileObject
	 * @throws com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException
	 *
	 */
	public FileObject getFileObjectByName(String name) throws NoSuchFileException;

	/**
	 * Retrieves a NoteObject from the database
	 *
	 * @param name The name of the NoteObject to be retrieved
	 * @return The NoteObject
	 * @throws com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException
	 *
	 */
	public NoteObject getNoteObjectByName(String name) throws NoSuchFileException;

	/**
	 * Retrieves all FileObjects from the database.
	 *
	 * @return A list of all FileObjects in the database
	 */
	public List<FileObject> getAllFileObjects();

	/**
	 * Retrieves all NoteObjects from the database.
	 *
	 * @return A list of all NoteObjects in the database
	 */
	public List<NoteObject> getAllNoteObjects();

	/**
	 * Saves a FileObject to the database (or updates an existing FileObject should one with the same relpath exist)
	 *
	 * @param object The FileObject to be saved
	 */
	public void save(FileObject object);

	/**
	 * Saves a NoteObject to the database (or updates an existing NoteObject should one with the same name exist)
	 *
	 * @param object The NoteObject to be saved
	 */
	public void save(NoteObject object);

	/**
	 * Deletes a FileObject from the database
	 *
	 * @param object The object to be deleted
	 */
	public void delete(FileObject object);

	/**
	 * Deletes a NoteObject from the database
	 *
	 * @param object The object to be deleted
	 */
	public void delete(NoteObject object);
}