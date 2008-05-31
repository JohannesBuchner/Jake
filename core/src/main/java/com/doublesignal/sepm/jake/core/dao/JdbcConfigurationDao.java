package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.util.Map;
import java.util.HashMap;

/**
 * JDBC implementation of the Configuration DAO
 */
public class JdbcConfigurationDao extends SimpleJdbcDaoSupport implements IConfigurationDao {
	private static final String CONFIG_SELECT = "SELECT value FROM configuration WHERE name=:name";
	private static final String CONFIG_INSERT = "INSERT INTO configuration (name, value) VALUES (:name, :value)";
	private static final String CONFIG_UPDATE = "UPDATE configuration SET value=:value WHERE name=:name";
	private static final String CONFIG_DELETE = "DELETE FROM configuration WHERE name=:name";

	public String getConfigurationValue(String name) throws NoSuchConfigOptionException {
		String value = getSimpleJdbcTemplate().queryForObject(CONFIG_SELECT, String.class, name);
		if(value == null) {
			throw new NoSuchConfigOptionException("Configuration option \"" + name + "\" not found");
		}
		return value;
	}

	public void setConfigurationValue(String name, String value) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", name);
		parameters.put("value", value);
		try {
			getConfigurationValue(name);
			/* If we're still here, the config option already exists and we should update */
			getSimpleJdbcTemplate().update(CONFIG_UPDATE, parameters);
		} catch (NoSuchConfigOptionException e) {
			/* If we get in here, the config option doesn't exist yet and we should insert */
			getSimpleJdbcTemplate().update(CONFIG_INSERT, parameters);
		}
	}

	public void deleteConfigurationValue(String name) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", name);
		getSimpleJdbcTemplate().update(CONFIG_DELETE, parameters);
	}

	public boolean existsConfigurationValue(String name) {
		try {
			this.getConfigurationValue(name);
			return true;
		} catch (NoSuchConfigOptionException e) {
			return false;
		}
	}
}
