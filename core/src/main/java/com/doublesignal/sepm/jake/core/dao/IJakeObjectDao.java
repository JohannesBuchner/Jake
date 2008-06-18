package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.Tag;
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
	 * @throws NoSuchFileException if no fileObject by that name exists
	 *
	 */
	public FileObject getFileObjectByName(String name) throws NoSuchFileException;

	/**
	 * Retrieves a NoteObject from the database
	 *
	 * @param name The name of the NoteObject to be retrieved
	 * @return The NoteObject
	 * @throws NoSuchFileException  if no fileObject by that name exists
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
	 * Saves a JakeObject to the database (or updates an existing JakeObject should one with the same name exist)
	 *
	 * @param object The JakeObject to be saved
	 */
	public void save(JakeObject object);

	/**
	 * Deletes a JakeObject from the database
	 *
	 * @param object The object to be deleted
	 */
	public void delete(JakeObject object);

	/**
	 * Convenience method to add one or more tags to a JakeObject
	 * @param jakeObject the jakeObject in question
	 * @param tags one or more tags
	 */
	public void addTagsTo(JakeObject jakeObject, Tag... tags);

	/**
	 * Convenience method to remove one or more tags from a JakeObject
	 * @param jakeObject the jakeObject in question
	 * @param tags one or more tags
	 */
	public void removeTagsFrom(JakeObject jakeObject, Tag... tags);


    /**
     * Return all tags of a specific JakeObject. 
     * @param jakeObject the jakeObject in question
     * @return List of Tags
     */
    public List<Tag> getTagsForObject(JakeObject jakeObject);
}