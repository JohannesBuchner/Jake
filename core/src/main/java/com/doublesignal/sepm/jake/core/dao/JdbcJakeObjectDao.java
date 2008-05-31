package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.QueryFailedException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.*;

/**
 * JDBC implementation of the JakeObject DAO
 *
 * @author Chris
 */
public class JdbcJakeObjectDao extends SimpleJdbcDaoSupport
		  implements IJakeObjectDao {
	private final String JAKEOBJECT_SELECT = "SELECT name FROM objects";
	private final String JAKEOBJECT_WHERE = " WHERE name=?";
	private final String JAKEOBJECT_INSERT =
			  "INSERT INTO objects (name) VALUES (:name)";
	private final String JAKEOBJECT_DELETE =
			  "DELETE FROM objects WHERE name=:name";

	private final String NOTEOBJECT_SELECT =
			  "SELECT name, content FROM noteobjects n JOIN objects o ON n.name = o.name";
	private final String NOTEOBJECT_WHERE = " WHERE name=?";
	private final String NOTEOBJECT_INSERT =
			  "INSERT INTO noteobjects (name, content) VALUES (:name, :content)";
	private final String NOTEOBJECT_UPDATE =
			  "UPDATE noteobjects SET content=:content WHERE name=:name";
	private final String NOTEOBJECT_DELETE =
			  "DELETE FROM noteobjects WHERE name=:name";

	private final String TAGS_SELECT =
			  "SELECT tag FROM tags WHERE object_name=?";
	private final String TAGS_INSERT =
			  "INSERT INTO tags (object_name, tag) VALUES (:object_name, :tag)";
	private final String TAGS_DELETE =
			  "DELETE FROM tags WHERE object_name=:object_name AND tag=:tag";
	private final String TAGS_DELETE_ALL =
			  "DELETE FROM tags WHERE object_name=:object_name";

	private List<Tag> getTagsForObject(JakeObject jo) {
		return getSimpleJdbcTemplate().query(
				  TAGS_SELECT,
				  new JdbcTagRowMapper(),
				  jo.getName()
		);
	}

	private void saveTags(JakeObject object) {
		getSimpleJdbcTemplate().update(TAGS_DELETE_ALL, object.getName());
		
		for(Tag t: object.getTags()) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("object_name", object.getName());
			parameters.put("tag", t.getName());
			getSimpleJdbcTemplate().update(TAGS_INSERT, parameters);
		}
	}

	public FileObject getFileObjectByName(String name)
			  throws NoSuchFileException {
		try {
			FileObject fo = getSimpleJdbcTemplate().queryForObject(
					  JAKEOBJECT_SELECT + JAKEOBJECT_WHERE, new JdbcFileObjectRowMapper(), name);
			if(fo.getName().startsWith("note:")) {
				throw new NoSuchFileException("Requested a FileObject, but this is a NoteObject!");
			}
			for (Tag t : getTagsForObject(fo)) {
				fo.addTag(t);
			}
			return fo;
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchFileException(
					  "No file with the name '" + name + "' in the database.");
		}
	}

	public NoteObject getNoteObjectByName(String name)
			  throws NoSuchFileException {
		try {
			NoteObject no = getSimpleJdbcTemplate().queryForObject(
					  NOTEOBJECT_SELECT + NOTEOBJECT_WHERE, new JdbcNoteObjectRowMapper(), name);
			if(!no.getName().startsWith("note:")) {
				throw new NoSuchFileException("Requested a NoteObject, but this is something else!");
			}
			for (Tag t : getTagsForObject(no)) {
				no.addTag(t);
			}
			return no;
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchFileException(
					  "No note with the name '" + name + "' in the database.");
		}
	}

	public List<FileObject> getAllFileObjects() {
		List<FileObject> fos = getSimpleJdbcTemplate().query(
				  JAKEOBJECT_SELECT,
				  new JdbcFileObjectRowMapper()
		);

		List<FileObject> fos_return = new ArrayList<FileObject>();

		for(FileObject fo: fos) {
			if(!fo.getName().startsWith("note:")) {
				fos_return.add(fo);
			}
		}

		return fos_return;
	}

	public List<NoteObject> getAllNoteObjects() {
		return getSimpleJdbcTemplate().query(
				  NOTEOBJECT_SELECT,
				  new JdbcNoteObjectRowMapper()
		);
	}

	public void save(JakeObject object) {
		this.saveTags(object);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", object.getName());

		if(object instanceof FileObject) {
			try {
				getFileObjectByName(object.getName());
				/* If we get here, this FileObject already exists in DB - do nothing */
			} catch (NoSuchFileException e) {
				/* If we get here, this FileObject does not yet exist in DB - insert it*/
				getSimpleJdbcTemplate().update(JAKEOBJECT_INSERT, parameters);
			}
		} else if (object instanceof NoteObject) {
			try {
				getNoteObjectByName(object.getName());
				/* If we get here, this NoteObject already exists in DB */
				parameters.put("content", ((NoteObject)object).getContent());
				getSimpleJdbcTemplate().update(NOTEOBJECT_UPDATE, parameters);
			} catch (NoSuchFileException e) {
				/* If we get here, this NoteObject does not yet exist in DB - insert it */
				getSimpleJdbcTemplate().update(JAKEOBJECT_INSERT, parameters);
				parameters.put("content", ((NoteObject)object).getContent());
				getSimpleJdbcTemplate().update(NOTEOBJECT_INSERT, parameters);
			}
		}
	}

	public void delete(JakeObject object) {
		Set<Tag> tags = object.getTags();
		for (Tag t : tags) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("object_name", object.getName());
			parameters.put("tag", t.getName());
			getSimpleJdbcTemplate().update(TAGS_DELETE, parameters);
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", object.getName());
		getSimpleJdbcTemplate().update(JAKEOBJECT_DELETE, parameters);

		if (object instanceof NoteObject) {
			getSimpleJdbcTemplate().update(NOTEOBJECT_DELETE, parameters);
		}
	}

	public void addTagsTo(JakeObject object, Tag... tags) {
		for(Tag t: tags) {
			object.addTag(t);
		}
		this.save(object);
	}

	public void removeTagsFrom(JakeObject object, Tag... tags) {
		for(Tag t: tags) {
			object.removeTag(t);
		}
		this.save(object);
	}
}
