package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * The Events Table Model. Used on the News Panel to show last events.
 *
 * @author: studpete
 */
public class EventsTableModel extends AbstractTableModel {

	private static final Logger log = Logger.getLogger(EventsTableModel.class);
	private Project project;
	private JakeObject jakeObject;
	private List<LogEntry> logEntries = new ArrayList<LogEntry>();
	private static final int MaxLogEntriesShown = 100;

	/**
	 * Creates a event table model that shows ALL events.
	 */
	public EventsTableModel(Project project) {
		this(project, null);
	}

	/**
	 * Creates a event model that is limited to events from a JakeObject
	 *
	 * @param jakeObject
	 */
	public EventsTableModel(Project project, JakeObject jakeObject) {
		this.setJakeObject(jakeObject);

		log.info("Initializing EventsTableModel with jakeObject: " + jakeObject);

		updateData();
	}

	/**
	 * Create EvetsTableModel, empty.
	 */
	public EventsTableModel() {
		this(null, null);
	}

	public void updateData() {
		//test if current Jake object is null, if so all log entries will be shown
		log.info("Updating events data...");

		this.logEntries = JakeMainApp.getApp().getCore().getLog(
				  getProject(), getJakeObject(), MaxLogEntriesShown);
	}

	String[] colNames = new String[]{"Action", "When"};


	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;

		updateData();
	}


	public JakeObject getJakeObject() {
		return jakeObject;
	}

	public void setJakeObject(JakeObject jakeObject) {
		this.jakeObject = jakeObject;

		updateData();
	}


	enum LogColumns {
		Action, User
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public int getRowCount() {
		return logEntries.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		LogEntry logEntry = logEntries.get(rowIndex);

		LogColumns col = LogColumns.values()[columnIndex];
		switch (col) {
			case Action:
				return logEntry;

			case User:
				return TimeUtilities.getRelativeTime(logEntry.getTimestamp());

			default:
				throw new IllegalArgumentException(
						  "Cannot get Information for column " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colNames[columnIndex];
	}

	public List<LogEntry> getLogEntries() {
		return logEntries;
	}
}
