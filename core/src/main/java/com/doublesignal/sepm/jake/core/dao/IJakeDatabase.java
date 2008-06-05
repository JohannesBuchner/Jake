package com.doublesignal.sepm.jake.core.dao;

import java.sql.SQLException;

/**
 * The DAOs have to be connected to the database. That's what this class does.
 * Without it, we couldn't have dependency injection. 
 * @author johannes
 */
public interface IJakeDatabase {
	/**
	 * specify database at runtime.
	 * @param database
	 * @throws SQLException 
	 */
	public void connect(String database) throws SQLException;

	public void close() throws SQLException;
	
	public IConfigurationDao getConfigurationDao();

	public void setConfigurationDao(IConfigurationDao configurationDao);

	public IJakeObjectDao getJakeObjectDao();

	public void setJakeObjectDao(IJakeObjectDao jakeObjectDao);

	public ILogEntryDao getLogEntryDao();

	public void setLogEntryDao(ILogEntryDao logEntryDao);

	public IProjectMemberDao getProjectMemberDao();

	public void setProjectMemberDao(IProjectMemberDao projectMemberDao);

}
