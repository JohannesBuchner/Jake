package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * The Events Table Model. Used on the News Panel to show last events.
 *
 * @author: studpete, simon
 */
public class EventsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3604724857190594625L;
	private static final Logger log = Logger.getLogger(EventsTableModel.class);

	private Project project;
	private Attributed<? extends JakeObject> attributedJakeObject;
	private List<LogEntry<? extends ILogable>> logEntries = new ArrayList<LogEntry<? extends ILogable>>();
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
	public EventsTableModel(Project project, Attributed<JakeObject> attributedJakeObject) {
		this.setProject(project);
		this.setJakeObject(attributedJakeObject);

		log.info("Initializing EventsTableModel with jakeObject: " + attributedJakeObject 
				+ ", and project: " + this.getProject());

		this.updateData();
	}

	/**
	 * Create EvetsTableModel, empty.
	 */
	public EventsTableModel() {
		this(null, null);
	}

	public void updateData() {
		log.info("Updating events data...");
		//FIXME: make proper initialization to evade this ugly null tests...
		if (this.getProject() != null && this.attributedJakeObject != null) {
			log.info("Update in progress. Please stand by...");
			this.logEntries = JakeMainApp.getCore().getLog(
					  getProject(), getAttributedJakeObject().getJakeObject(), MaxLogEntriesShown);
		} else if (this.getProject() != null) { //FIXME: DRY, general crap
			this.logEntries = JakeMainApp.getCore().getLog(getProject(), null, MaxLogEntriesShown);
		}
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;

		this.updateData();
	}


	public Attributed<? extends JakeObject> getAttributedJakeObject() {
		return this.attributedJakeObject;
	}

	public void setJakeObject(Attributed<? extends JakeObject> attributed) {
		log.debug("setting attributed: " + attributed);
		this.attributedJakeObject = attributed;

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

	public List<LogEntry<? extends ILogable>> getLogEntries() {
		return this.logEntries;
	}
}
