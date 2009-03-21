package com.jakeapp.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import java.util.Comparator;
import java.util.UUID;

/**
 * A Representation of a Note. The note consists of a <code>uuid</code>,
 * <code>content</code>
 * and is associated with one <code>Project</code>.
 */
@Entity(name = "note")
public class NoteObject extends JakeObject {

	private static final long serialVersionUID = -8838089386183264658L;

	private String content;

	@SuppressWarnings("unused")
	private NoteObject() {
	}
	
	/**
	 * Construct a new <code>NoteObject</code> with the given params.
	 * A new UUID is generated for you.
	 * 
	 * @param project
	 *            the associated <code>Project</code>
	 * @param content
	 *            the content of the note
	 */
	public NoteObject(Project project, String content) {
		super(UUID.randomUUID(), project);
		this.setContent(content);
	}

	/**
	 * Construct a new <code>NoteObject</code> with the given params.
	 * 
	 * @param uuid
	 *            the <code>uuid</code> of the note
	 * @param project
	 *            the associated <code>Project</code>
	 * @param content
	 *            the content of the note
	 */
	public NoteObject(UUID uuid, Project project, String content) {
		super(uuid, project);
		this.setContent(content);
	}

	/**
	 * Get the content (text) of the note.
	 * 
	 * @return the content of the note
	 */
	@Column(name = "text", nullable = false)
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		NoteObject other = (NoteObject) obj;
		if (this.getUuid() == null) {
			if (other.getUuid() != null)
				return false;
		} else if (!getUuid().equals(other.getUuid()))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String shortContent = content;
		if (content.length() > 13)
			shortContent = content.substring(0, 10) + "...";
		return "Note [" + super.toString() + "]:" + shortContent;
	}

	/**
	 * Returns a <code>Comparator</code> thats aware of the semantics of a <code>NoteObject</code>.
	 * @return a <code>NoteObject</code> aware <code>Comparator</code>
	 */
	public static Comparator<NoteObject> getUUIDComparator() {
		return new Comparator<NoteObject>() {
			@Override
			public int compare(NoteObject arg0, NoteObject arg1) {
				if (arg0==null) return (arg1==null)?0:-1;
				if (arg1==null) return 1;
				
				if (arg0.getUuid()==null) return (arg1.getUuid()==null)?0:-1;
				if (arg1.getUuid()==null) return 1;
				
				return arg0.getUuid().compareTo(arg1.getUuid());
			}
		};
	}
}
