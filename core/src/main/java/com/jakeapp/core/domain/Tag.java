package com.jakeapp.core.domain;

import javax.persistence.*;


import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.dao.HibernateTagPK;
import org.hibernate.annotations.ForeignKey;

import java.io.Serializable;

/**
 * A simple tag. It only consists of a <code>name</code>.
 */
@Entity(name = "tag")
@UniqueConstraint(columnNames = {"objectid", "text"})
@IdClass(HibernateTagPK.class)
public class Tag implements ILogable, Serializable {
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
        if (!name.equals(tag.name)) return false;

        return true;
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
}
