package com.jakeapp.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.UUID;

/**
 * A Representation of a Note. The note consists of a <code>uuid</code>, <code>
 * content</code> and is associated with one <code>Project</code>.
 */
@Entity(name = "note")
public class NoteObject extends JakeObject {
	private static final long serialVersionUID = -8838089386183264658L;
    private String content;

    private NoteObject(){}

    /**
     * Construct a new <code>NoteObject</code> with the given params.
     * @param uuid the <code>uuid</code> of the note
     * @param project the associated <code>Project</code>
     * @param content the content of the note
     */
    public NoteObject(UUID uuid, Project project, String content) {
        super(uuid, project);
        this.setContent(content);
    }

    /**
     * Get the content (text) of the note.
     * @return the content of the note
     */
    @Column(name = "text", nullable = false)
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
	    this.content = content;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		return result;
	}

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
    

}
