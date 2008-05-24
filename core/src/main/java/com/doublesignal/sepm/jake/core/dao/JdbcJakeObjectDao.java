package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.QueryFailedException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.domain.exceptions.NoSuchFileException;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC implementation of the JakeObject DAO
 *
 * @author Chris
 */
public class JdbcJakeObjectDao extends SimpleJdbcDaoSupport implements IJakeObjectDao {
	private static final int FILEOBJECT_TYPE = 0;
	private static final int NOTEOBJECT_TYPE = 1;

	private static final String FILEOBJECT_SELECT = "SELECT name FROM objects " +
			  "WHERE type=" + FILEOBJECT_TYPE;
	private static final String NOTEOBJECT_SELECT = "SELECT o.name, n.content " +
			  "FROM objects o JOIN noteobjects n " +
			  "ON o.name = n.name WHERE type=" + NOTEOBJECT_TYPE;
	private static final String OBJECT_WHERE_NAME = " AND name=:name";

	private static final String OBJECT_INSERT = "INSERT INTO objects (name, " +
			  "type) VALUES (:name, :type)";
	private static final String NOTEOBJECT_INSERT = "INSERT INTO noteobjects " +
			  "(name, content) VALUES (:name, :content)";
	private static final String NOTEOBJECT_UPDATE = "UPDATE noteobjects SET " +
			  "content=:content WHERE name=:name";

	private static final String OBJECT_DELETE = "DELETE FROM objects WHERE " +
			  "name=:name AND type=:type";
	private static final String NOTEOBJECT_DELETE = "DELETE FROM noteobjects " +
			  "WHERE name=:name AND type=:type";

	private static final String TAGS_SELECT = "SELECT tag FROM tags WHERE " +
			  "object_name=:object_name AND object_type=:object_type";
	private static final String TAGS_INSERT = "INSERT INTO tags (object_name, " +
			  "object_type, tag) VALUES (:object_name, :object_type, :tag)";
	private static final String TAGS_DELETE = "DELETE FROM tags WHERE " +
			  "object_name=:object_name AND object_type=:object_type";

	/**
	 * Retrieves tags for a given JakeObject and injects them into the JakeObject,
	 *
	 * @param obj        The object to get tags for
	 * @param objectType The type of the object
	 */
	private void injectTags(JakeObject obj, int objectType) {
		List<Tag> matchTags = getSimpleJdbcTemplate().query(
				  TAGS_SELECT,
				  new JdbcTagRowMapper(),
				  obj.getName(),
				  objectType
		);

		for (Tag t : matchTags) {
			obj.addTag(t);
		}
	}

	/**
	 * Saves tags for a given JakeObject
	 *
	 * @param obj        The object whose tags should be saved
	 * @param objectType The type of the object
	 */
	private void saveTags(JakeObject obj, int objectType) {
		/* Delete tags first... */
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("object_name", obj.getName());
		parameters.put("object_type", objectType);
		getSimpleJdbcTemplate().update(TAGS_DELETE, parameters);

		/* ... then reinsert. */
		for (Tag t : obj.getTags()) {
			Map<String, Object> tagParameters = new HashMap<String, Object>();
			tagParameters.put("object_name", obj.getName());
			tagParameters.put("object_type", objectType);
			tagParameters.put("tag", t.getName());
			getSimpleJdbcTemplate().update(TAGS_INSERT, tagParameters);
		}
	}

	public FileObject getFileObjectByName(String name) throws NoSuchFileException {
		List<FileObject> matches = getSimpleJdbcTemplate().query(
				  FILEOBJECT_SELECT + OBJECT_WHERE_NAME,
				  new JdbcFileObjectRowMapper(),
				  name
		);

		if (matches.size() == 0) {
			throw new NoSuchFileException("File \"" + name + "\" does not exist in DB");
		}

		FileObject obj = matches.get(0);

		injectTags(obj, FILEOBJECT_TYPE);

		return obj;
	}

	public NoteObject getNoteObjectByName(String name) throws NoSuchFileException {
		List<NoteObject> matches = getSimpleJdbcTemplate().query(
				  NOTEOBJECT_SELECT + OBJECT_WHERE_NAME,
				  new JdbcNoteObjectRowMapper(),
				  name
		);

		if (matches.size() == 0) {
			throw new NoSuchFileException("File \"" + name + "\" does not exist in DB");
		}

		NoteObject obj = matches.get(0);

		injectTags(obj, NOTEOBJECT_TYPE);

		return obj;
	}

	public List<FileObject> getAllFileObjects() {
		List<FileObject> matches = getSimpleJdbcTemplate().query(
				  FILEOBJECT_SELECT,
				  new JdbcFileObjectRowMapper()
		);

		for (FileObject obj : matches) {
			injectTags(obj, FILEOBJECT_TYPE);
		}

		return matches;
	}

	public List<NoteObject> getAllNoteObjects() {
		List<NoteObject> matches = getSimpleJdbcTemplate().query(
				  NOTEOBJECT_SELECT,
				  new JdbcNoteObjectRowMapper()
		);

		for (NoteObject obj : matches) {
			injectTags(obj, NOTEOBJECT_TYPE);
		}

		return matches;
	}

	public void save(FileObject object) {
		/* We shouldn't EVER have to update a FileObject. In case it already
		 * exists --> we have a problem. */
		try {
			this.getFileObjectByName(object.getName());
			throw new QueryFailedException("Trying to add an existing FileObject");
		} catch (NoSuchFileException e) {
			/* We WANT this to happen, so do nothing */
		}

		/* Save tags via helper method */
		saveTags(object, FILEOBJECT_TYPE);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", object.getName());
		parameters.put("type", FILEOBJECT_TYPE);
		getSimpleJdbcTemplate().update(OBJECT_INSERT, parameters);
	}

	public void save(NoteObject object) {
		/* We need to figure out if a NoteObject of that name already exists */
		try {
			this.getNoteObjectByName(object.getName());
			/* If we're still in here, the NoteObject already exists, so update.
			 * Nothing but the content can have changed, so just update that. */
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("name", object.getName());
			parameters.put("content", object.getContent());
			getSimpleJdbcTemplate().update(NOTEOBJECT_UPDATE, parameters);
		} catch (NoSuchFileException e) {
			/* The NoteObject doesn't yet exist, so create it */
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("name", object.getName());
			parameters.put("type", NOTEOBJECT_TYPE);
			getSimpleJdbcTemplate().update(OBJECT_INSERT, parameters);

			parameters.clear();
			parameters.put("name", object.getName());
			parameters.put("content", object.getContent());
			getSimpleJdbcTemplate().update(NOTEOBJECT_INSERT, parameters);
		}

		/* Save tags via helper method */
		saveTags(object, FILEOBJECT_TYPE);


	}

	public void delete(FileObject object) {
		/* Delete tags */
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("object_name", object.getName());
		parameters.put("object_type", FILEOBJECT_TYPE);
		getSimpleJdbcTemplate().update(TAGS_DELETE, parameters);

		/* Delete object */
		parameters.clear();
		parameters.put("name", object.getName());
		parameters.put("type", FILEOBJECT_TYPE);
		getSimpleJdbcTemplate().update(OBJECT_DELETE, parameters);
	}

	public void delete(NoteObject object) {
		/* Delete tags */
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("object_name", object.getName());
		parameters.put("object_type", NOTEOBJECT_TYPE);
		getSimpleJdbcTemplate().update(TAGS_DELETE, parameters);

		/* Delete object */
		parameters.clear();
		parameters.put("name", object.getName());
		parameters.put("type", NOTEOBJECT_TYPE);
		getSimpleJdbcTemplate().update(OBJECT_DELETE, parameters);

		/* Delete NoteObject */
		parameters.clear();
		parameters.put("name", object.getName());
		parameters.put("type", NOTEOBJECT_TYPE);
		getSimpleJdbcTemplate().update(NOTEOBJECT_DELETE, parameters);
	}
}
