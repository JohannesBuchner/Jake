package com.jakeapp.core.dao;

import java.util.List;

import com.jakeapp.core.domain.Configuration;

/**
 * Serves as a frontend for database/project-independent <code>Configuration</code> management.
 * 
 * @author Christopher
 */
public interface IConfigurationDao {

	/**
	 * Retrieves a <code>Configuration</code> value from a data source
	 * 
	 * @param name
	 *            The name of the <code>Configuration</code> option to be queried
	 * @return The value corresponding to the given <code>Configuration</code> option, or an
	 *         empty string if not set
	 */
	public String getConfigurationValue(String name);

	/**
	 * Sets a <code>Configuration</code> option.
	 * 
	 * @param name
	 *            The name of the <code>Configuration</code> option to be set
	 * @param value
	 *            The value to be assigned to the given <code>Configuration</code> option
	 */
	public void setConfigurationValue(String name, String value);

	/**
	 * Removes a <code>Configuration</code> option entirely
	 * 
	 * @param name
	 *            The name of the <code>Configuration</code> option to be removed (nothing
	 *            will be done if there is no <code>Configuration</code> option by that name)
	 */
	public void deleteConfigurationValue(String name);

	/**
	 * Checks if a <code>Configuration</code> option is set (i.e. if a <code>Configuration</code> option
	 * by that name exists)
	 * 
	 * @param name
	 *            The name of the <code>Configuration</code> option to check
	 * @return true, iff the <code>Configuration</code> option is set
	 */
	public boolean configurationValueExists(String name);

	/**
	 * Updates the given <code>Configuration</code> Object in the database. Create it if it
	 * doesn't exist.
	 * 
	 * @param configuration
	 *            the <code>Configuration</code> object to be updated/created
	 * @return the <code>Configuration</code> object updated.
	 */
	public Configuration update(final Configuration configuration);


	/**
	 * Returns a <code>List</code> of all known configuration options
	 * 
	 * @return a <code>List</code> of Configuration options, empty <code>List</code> if no options are available
	 */
	public List<Configuration> getAll();
}
