package com.jakeapp.core.domain;

import javax.persistence.Entity;
import java.util.UUID;

/**
 * A Representation of a Note. The note consists of a <code>uuid</code>, <code>
 * content</code> and is associated with one <code>Project</code>.
 */
@Entity
public class NoteObject extends JakeObject {
	private static final long serialVersionUID = -8838089386183264658L;
    private String content;

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
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
	    this.content = content;
    }
}
