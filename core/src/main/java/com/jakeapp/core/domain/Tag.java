package com.jakeapp.core.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.jakeapp.core.domain.exceptions.InvalidTagNameException;

/**
 * A simple tag. It only consists of a <code>name</code>.
 */
@Entity
public class Tag implements ILogable, Serializable, Comparable<Tag> {
    private static final long serialVersionUID = -2201488676480921149L;
    private JakeObject jakeObject;
    private String name;


    /**
     * Construct a new Tag.
     *
     * @param name the name of the tag to be created
     * @throws InvalidTagNameException if the name is not valid
     * @see #setName(String)
     */
    public Tag(String name) throws InvalidTagNameException {
        super();
        this.setName(name);
    }

    public Tag() {
    }

    /**
     * Get the name of the tag.
     *
     * @return the name of the tag
     */
    @Id
    public String getName() {
        return this.name;
    }

    /**
     * Set the name of the <code>tag</code>.
     *
     * @param name the new name of the tag. It must not contain a
     *             whitespace-character.
     * @throws InvalidTagNameException Raised if the <code>name</code> contains
     *                                 a whitespace-character
     */
    public void setName(String name) throws InvalidTagNameException {
        if (name.matches(".*\\s.*")) {
            throw new InvalidTagNameException(
                    "A Tag may not contain a whitespace");
        }
        this.name = name;
    }

    @Id
    public JakeObject getObject() {
        return this.jakeObject;
    }


    public void setObject(JakeObject object) {
        this.jakeObject = object;
    }

    /**
     * Test if two <code>tag</code>s are equal.
     *
     * @param o The object to compare this object to.
     * @return <code>true</code> iff the <code>name</code>s are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (jakeObject != null ? !jakeObject.equals(tag.jakeObject) : tag.jakeObject != null) return false;
			return name.equals(tag.name);

		}

    @Override
    public int hashCode() {
        int result = jakeObject != null ? jakeObject.hashCode() : 0;
        result = 31 * result + name.hashCode();
        return result;
    }

    /**
     * Returns the string representation of the <code>tag</code>.
     *
     * @return the name of the tag.
     */
    @Override
    public String toString() {
        return this.name;
	}

	@Override
	public int compareTo(Tag arg0) {
		if (arg0==null) return 1;
		else return this.getName().compareTo(arg0.getName());
	}
}
