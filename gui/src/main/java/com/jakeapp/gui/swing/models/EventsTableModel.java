package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.AttributedJakeObject;
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
	private AttributedJakeObject<? extends JakeObject> attributedJakeObject;
	private List<LogEntry> logEntries = new ArrayList<LogEntry>();
	private static final int MaxLogEntriesShown = 100;
	private static final String[] colNames = new String[]{"Action", "When"};
	
	private enum LogColumns {
		ACTION, WHEN
	}
	
	/**

	 * Creates a event table model that shows ALL events.
	 */
	public EventsTableModel(Project project) {
		this(project, null);
	}

	/**
	 * Creates a event model that is limited to events from a JakeObject
	 *
	 * @param attributedJakeObject
	 */
	public EventsTableModel(Project project, AttributedJakeObject<JakeObject> attributedJakeObject) {
		this.setJakeObject(attributedJakeObject);

		log.info("Initializing EventsTableModel with jakeObject: " + attributedJakeObject);

		this.updateData();
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
		//FIXME: make proper initialization to evade this ugly null tests...
		if (this.getProject() != null && this.getAttributedJakeObject() != null) {
			this.logEntries = JakeMainApp.getApp().getCore().getLog(
					  getProject(), getAttributedJakeObject().getJakeObject(), MaxLogEntriesShown);

		}
		log.debug("received logs: " + this.logEntries);
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;

		this.updateData();
	}


	public AttributedJakeObject<? extends JakeObject> getAttributedJakeObject() {
		return this.attributedJakeObject;
	}

	public void setJakeObject(AttributedJakeObject<? extends JakeObject> attributedJakeObject) {
		log.debug("setting attributedJakeObject: " + attributedJakeObject);
		this.attributedJakeObject = attributedJakeObject;

		this.updateData();
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public int getRowCount() {
		return this.logEntries.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		LogEntry logEntry = logEntries.get(rowIndex);

		LogColumns col = LogColumns.values()[columnIndex];
		switch (col) {
			case ACTION:
				return logEntry;

			case WHEN:
				return TimeUtilities.getRelativeTime(logEntry.getTimestamp());

			default:
				throw new IllegalArgumentException("Cannot get Information for column " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colNames[columnIndex];
	}

	public List<LogEntry> getLogEntries() {
		return this.logEntries;
	}
}
