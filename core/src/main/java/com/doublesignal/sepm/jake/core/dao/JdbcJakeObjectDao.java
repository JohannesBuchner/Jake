package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.QueryFailedException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JDBC implementation of the JakeObject DAO
 *
 * @author Chris
 */
public class JdbcJakeObjectDao extends SimpleJdbcDaoSupport
		  implements IJakeObjectDao {
	private final String JAKEOBJECT_SELECT = "SELECT name FROM objects";
	private final String JAKEOBJECT_INSERT =
			  "INSERT INTO objects (name) VALUES (:name)";
	private final String JAKEOBJECT_DELETE =
			  "DELETE FROM objects WHERE name=:name";

	private final String NOTEOBJECT_SELECT =
			  "SELECT name, content FROM noteobjects NATURAL JOIN objects WHERE name=?";
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

	private List<Tag> getTagsForObject(JakeObject jo) {
		return getSimpleJdbcTemplate().query(
				  TAGS_SELECT,
				  new JdbcTagRowMapper(),
				  jo.getName()
		);
	}

	public FileObject getFileObjectByName(String name)
			  throws NoSuchFileException {
		try {
			FileObject fo = getSimpleJdbcTemplate().queryForObject(
					  JAKEOBJECT_SELECT, new JdbcFileObjectRowMapper(), name);
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
					  NOTEOBJECT_SELECT, new JdbcNoteObjectRowMapper(), name);
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
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public List<NoteObject> getAllNoteObjects() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void save(JakeObject object) {
		//To change body of implemented methods use File | Settings | File Templates.
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
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void removeTagsFrom(JakeObject object, Tag... tags) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
