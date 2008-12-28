package com.jakeapp.core.dao;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.Tag;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A generic hibernate DAO for <code>JakeObject</code>s.
 * @param <T> The type of the persisted Entity, subtype of
 * 	<code>JakeObject</code>
 */
public abstract class HibernateJakeObjectDao<T extends JakeObject>
        extends HibernateDaoSupport implements IJakeObjectDao<T> {

    /**
     * {@inheritDoc}
     */
    public T persist(T jakeObject) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<T> getAll() {
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public List<T> getAll(Project project) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void makeTransient(T jakeObject) {
    }

    /**
     * {@inheritDoc}
     */
    public List<Tag> getTags(T jakeObject) {
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public T addTagTo(T jakeObject, Tag tag) {
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public T removeTagFrom(T jakeObject, Tag tag) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public void addTagsTo(T jakeObject, Tag... tags) {

	}

    /**
     * {@inheritDoc}
     */
	@Override
	public List<Tag> getTagsFor(T jakeObject) {
		return null;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void removeTagsFrom(T jakeObject, Tag... tags) {

	}
}
