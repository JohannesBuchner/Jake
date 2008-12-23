package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchConfigOptionException;

/**
 * Hibernate implementation of the IConfigurationInterface.
 *
 * @author Simon
 */
public class HibernateConfigurationDao implements IConfigurationDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public final void deleteConfigurationValue(final String name) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean existsConfigurationValue(final String name) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getConfigurationValue(final String name)
            throws NoSuchConfigOptionException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setConfigurationValue(final String name,
                                            final String value) {
        // TODO Auto-generated method stub

    }

}
