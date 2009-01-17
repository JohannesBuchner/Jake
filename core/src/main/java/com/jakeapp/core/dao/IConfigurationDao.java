package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchConfigOptionException;
import com.jakeapp.core.domain.Configuration;

import java.util.List;

/**
 * Serves as a frontend for database-independent Configuration management.
 *
 * @author Christopher
 */
public interface IConfigurationDao {
	/**
	 * Retrieves a configuration value from a data source
	 * @param name The name of the configuration option to be queried
	 * @return The value corresponding to the given configuration option
	 * @throws NoSuchConfigOptionException if there is no
     * configuration option by that name
	 */
	public String getConfigurationValue(String name)
            throws NoSuchConfigOptionException;

	/**
	 * Sets a configuration option.
	 * @param name The name of the configuration option to be set
	 * @param value
	 * 	The value to be assigned to the given configuration option
	 */
	public void setConfigurationValue(String name, String value);

	/**
	 * Removes a configuration option entirely
	 * @param name The name of the configuration option to be removed
     * (nothing will be done if there is no configuration
	 *             option by that name)
	 */
	public void deleteConfigurationValue(String name);

	/**
	 * Checks if a configuration option is set (i.e. if a configuration
     *  option by that name exists)
	 * @param name The name of the configuration option to check
	 * @return if the configuration option is set
	 */
	public boolean configurationValueExists(String name);

    /**
     * Updates the given Configuration Object in the database. Create it
     * if it doesn't exist.
     * @param configuration the configuration object to be updated/created
     * @return the configuration object
     */
    public Configuration update(final Configuration configuration);


    /**
     * Returns a List of all known configuration options 
     * @return a list of Configuration options
     */
    public List<Configuration> getAll();
}
