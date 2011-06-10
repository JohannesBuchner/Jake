package com.jakeapp.violet.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.di.KnownProperty;
import com.jakeapp.violet.model.ILogModificationListener.ModifyActions;
import com.jakeapp.violet.model.exceptions.NoSuchLogEntryException;

/**
 * Implementation of Log
 * 
 * @author user
 * 
 */
public class LogImpl implements Log {
	private Connection conn;
	private File file;

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void connect() throws Exception {
		// open db
		try {
			Class.forName("org." + DI.getProperty(KnownProperty.JDBCDB)
					+ ".Driver");
			conn = DriverManager.getConnection(
					"jdbc:" + DI.getProperty(KnownProperty.JDBCDB) + ":"
							+ file.getAbsolutePath(),
					DI.getProperty(KnownProperty.JDBCUSER),
					DI.getProperty(KnownProperty.JDBCPASSWORD));
			// if it doesn't exist:
			Statement stmt = conn.createStatement();
			// create schema of table
			stmt.execute(DI.getProperty(KnownProperty.DB_CREATELOGTABLE));

			stmt.execute(DI.getProperty(KnownProperty.DB_CREATELOGINDEXWHEN));
			// create index on relpath
			stmt.execute(DI.getProperty(KnownProperty.DB_CREATELOGINDEXWHAT));
		} catch (Exception e) {
			throw new IllegalStateException("could not initialize db", e);
		}
	}

	@Override
	public void disconnect() throws Exception {
		if (conn != null)
			conn.close();
		conn = null;
		queries.clear();
	}

	@Override
	public void add(LogEntry logEntry) throws SQLException {
		PreparedStatement addStmt = getPrepared(KnownProperty.DB_INSERTLOG);
		addStmt.setObject(0, logEntry.getId());
		addStmt.setTimestamp(1, logEntry.getWhen());
		addStmt.setString(2, logEntry.getWho().getUserId());
		addStmt.setString(3, logEntry.getWhat().getRelPath());
		addStmt.setString(4, logEntry.getWhy());
		addStmt.setString(5, logEntry.getHow());
		addStmt.setBoolean(6, logEntry.getKnown());
		addStmt.execute();
	}

	@Override
	public LogEntry getById(UUID uuid, boolean includeUnprocessed)
			throws NoSuchLogEntryException, SQLException {
		PreparedStatement stmt = getPrepared(KnownProperty.DB_GETLOGBYID);
		stmt.setObject(0, uuid);
		stmt.setBoolean(1, includeUnprocessed);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return resultToLogEntry(rs);
		} else {
			throw new NoSuchLogEntryException();
		}
	}

	private LogEntry resultToLogEntry(ResultSet rs) throws SQLException {
		return new LogEntry((UUID) rs.getObject(0), rs.getTimestamp(0),
				new User(rs.getString(1)), new JakeObject(rs.getString(2)),
				rs.getString(3), rs.getString(4), rs.getBoolean(5));
	}

	@Override
	public void setProcessed(LogEntry logEntry) throws NoSuchLogEntryException,
			SQLException {
		PreparedStatement stmt = getPrepared(KnownProperty.DB_SETPROCESSEDBYID);
		stmt.setObject(0, logEntry.getId());
		if (stmt.executeUpdate() == 0) {
			throw new NoSuchLogEntryException();
		}
	}

	@Override
	public List<LogEntry> getUnprocessed() throws SQLException {
		return getAll(true, false, null);
	}

	@Override
	public List<LogEntry> getUnprocessed(JakeObject jakeobject)
			throws SQLException {
		return getAll(true, false, jakeobject);
	}

	@Override
	public boolean hasUnprocessed(JakeObject jakeObject) throws SQLException {
		if (getUnprocessed(jakeObject).isEmpty())
			return false;
		else
			return true;
	}

	@Override
	public LogEntry getNextUnprocessed() throws NoSuchLogEntryException,
			SQLException {
		List<LogEntry> unprocessed = getUnprocessed();
		if (unprocessed.isEmpty())
			throw new NoSuchLogEntryException();
		else
			return unprocessed.iterator().next();
	}

	private List<LogEntry> getAll(boolean includeUnprocessed,
			boolean includeProcessed, JakeObject jo) throws SQLException {
		PreparedStatement stmt = getStatement(includeUnprocessed,
				includeProcessed, jo);
		ResultSet rs = stmt.executeQuery();
		ArrayList<LogEntry> all = new ArrayList<LogEntry>();
		while (rs.next()) {
			all.add(resultToLogEntry(rs));
		}
		return all;
	}

	private LogEntry getLast(boolean includeUnprocessed,
			boolean includeProcessed, JakeObject jo) throws SQLException,
			NoSuchLogEntryException {
		List<LogEntry> all = getAll(includeUnprocessed, includeProcessed, jo);
		if (all.isEmpty())
			throw new NoSuchLogEntryException();
		return all.get(all.size() - 1);
	}

	private PreparedStatement getStatement(boolean includeUnprocessed,
			boolean includeProcessed, JakeObject jo) throws SQLException {
		PreparedStatement stmt = null;
		if (jo == null) {
			if (includeProcessed)
				// for getAll() calls
				if (includeUnprocessed)
					stmt = getPrepared(KnownProperty.DB_GETALL);
				else
					stmt = getPrepared(KnownProperty.DB_GETPROCESSED);
			else if (includeUnprocessed)
				// for getUnprocessed calls
				stmt = getPrepared(KnownProperty.DB_GETUNPROCESSED);
		} else {
			if (includeUnprocessed)
				if (includeProcessed)
					// for getAllOfJakeObject(jo) calls
					stmt = getPrepared(KnownProperty.DB_GETALLFORWHAT);
				else
					// for getUnprocessed(jo) calls
					stmt = getPrepared(KnownProperty.DB_GETUNPROCESSEDFORWHAT);
			else if (includeProcessed)
				// for getAllOfJakeObject(jo) calls
				stmt = getPrepared(KnownProperty.DB_GETPROCESSEDFORWHAT);
		}
		if (stmt == null) {
			if (!includeProcessed && !includeUnprocessed) {
				throw new IllegalArgumentException(
						"neither processed nor unprocessed? "
								+ "you are asking for the impossible!");
			}
			throw new IllegalStateException("query not found!");
		}
		return stmt;
	}

	private Map<KnownProperty, PreparedStatement> queries = new HashMap<KnownProperty, PreparedStatement>();
	private Set<ILogModificationListener> listeners = new HashSet<ILogModificationListener>();

	private PreparedStatement getPrepared(KnownProperty key)
			throws SQLException {
		PreparedStatement q = queries.get(key);
		if (q == null) {
			q = conn.prepareStatement(DI.getProperty(key));
			queries.put(key, q);
		}
		return q;
	}

	@Override
	public List<LogEntry> getAll(boolean includeUnprocessed)
			throws SQLException {
		return getAll(includeUnprocessed, true, null);
	}

	@Override
	public List<LogEntry> getAllOfJakeObject(JakeObject jakeObject,
			boolean includeUnprocessed) throws SQLException {
		return getAll(includeUnprocessed, true, jakeObject);
	}

	@Override
	public LogEntry getLastOfJakeObject(JakeObject jakeObject,
			boolean includeUnprocessed) throws NoSuchLogEntryException,
			SQLException {
		return getLast(includeUnprocessed, true, jakeObject);
	}

	@Override
	public List<JakeObject> getExistingFileObjects(boolean includeUnprocessed)
			throws SQLException {
		PreparedStatement stmt;
		if (includeUnprocessed)
			stmt = getPrepared(KnownProperty.DB_GETRELPATHSPROCESSED);
		else
			stmt = getPrepared(KnownProperty.DB_GETRELPATHS);
		ResultSet rs = stmt.executeQuery();
		ArrayList<JakeObject> all = new ArrayList<JakeObject>();
		while (rs.next()) {
			all.add(new JakeObject(rs.getString(0)));
		}
		return all;
	}

	@Override
	public LogEntry getFirstEntry() throws NoSuchLogEntryException,
			SQLException {
		List<LogEntry> all = getAll(true, true, null);
		if (all.isEmpty())
			throw new NoSuchLogEntryException();
		return all.get(0);
	}

	@Override
	public void setAllPreviousProcessed(LogEntry logEntry) throws SQLException {
		PreparedStatement stmt = getPrepared(KnownProperty.DB_SETPROCESSEDFORWHAT);
		stmt.setObject(0, logEntry.getWhat());
		stmt.executeUpdate();
		for (ILogModificationListener l : this.listeners) {
			l.logModified(logEntry.getWhat(), ModifyActions.MODIFIED);
		}
	}

	@Override
	public void addModificationListener(ILogModificationListener l) {
		this.listeners.add(l);
	}

	@Override
	public void removeModificationListener(ILogModificationListener l) {
		this.listeners.remove(l);
	}
}
