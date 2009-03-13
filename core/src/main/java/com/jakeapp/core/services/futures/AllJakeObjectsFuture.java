package com.jakeapp.core.services.futures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;

/**
 * Gets all JakeObject that ever were in the log (including remote-only, deleted, ...).
 * 
 * @author johannes
 */
public class AllJakeObjectsFuture extends AvailableLaterObject<Collection<JakeObject>> {

	private static Logger log = Logger.getLogger(AllJakeObjectsFuture.class);

	private ILogEntryDao logEntryDao;

	private void setLogEntryDao(ILogEntryDao logEntryDao) {
		this.logEntryDao = logEntryDao;
	}
	
	public AllJakeObjectsFuture(ILogEntryDao logEntryDao) {
		super();
		this.setLogEntryDao(logEntryDao);
	}


	public AllJakeObjectsFuture(
					ProjectApplicationContextFactory context,
					Project project) {
		super();

		log.debug("Creating a " + getClass().getSimpleName() + " with "
				+ context + "on project " + project);

		this.setLogEntryDao(context.getUnprocessedAwareLogEntryDao(project));
	}

	
	@Override
	@Transactional
	public Collection<JakeObject> calculate() {
		Set<JakeObject> objects = new HashSet<JakeObject>(); 
		
		for(LogEntry<JakeObject> le : this.logEntryDao.getAllVersions(true) ){
			objects.add(le.getBelongsTo());
			log.debug("we have " + le.getBelongsTo());
		}
		return objects;
	}
}