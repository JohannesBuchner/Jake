package com.jakeapp.core.services.futures;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Gets all JakeObject that ever were in the log (including remote-only, deleted, ...).
 *
 * @author johannes
 */
public class AllJakeObjectsFuture
				extends AvailableLaterObject<Collection<JakeObject>> {

	private static Logger log = Logger.getLogger(AllJakeObjectsFuture.class);

	private ILogEntryDao logEntryDao;

	private Project project;

	private void setLogEntryDao(ILogEntryDao logEntryDao) {
		this.logEntryDao = logEntryDao;
	}

	public AllJakeObjectsFuture(ProjectApplicationContextFactory context,
					Project project) {
		super();

		log.debug("Creating a " + getClass()
						.getSimpleName() + " with " + context + "on project " + project);

		this.project = project;
		this.setLogEntryDao(context.getUnprocessedAwareLogEntryDao(project));

		// sanity check
		if(this.logEntryDao == null) {
			throw new IllegalStateException("No Log Entry Dao available!?!");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Collection<JakeObject> calculate() {
		Set<JakeObject> objects = new HashSet<JakeObject>();
		List<LogEntry<JakeObject>> logs = this.logEntryDao.getAllVersions(true);
		if (logs != null) {
			for (LogEntry<JakeObject> le : logs) {
				JakeObject jo = le.getBelongsTo();
				jo.setProject(project);
				objects.add(jo);
				log.trace("we have " + jo);
			}
		}
		return objects;
	}
}