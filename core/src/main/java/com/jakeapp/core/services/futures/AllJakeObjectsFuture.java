package com.jakeapp.core.services.futures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.UnprocessedBlindLogEntryDaoProxy;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;

/**
 * Gets all JakeObject that ever were in the log (including remote-only, deleted, ...).
 * 
 * @author johannes
 */
public class AllJakeObjectsFuture extends AvailableLaterObject<Collection<JakeObject>> {

	private static Logger log = Logger.getLogger(AllJakeObjectsFuture.class);

	private UnprocessedBlindLogEntryDaoProxy logEntryDao;

	private void setLogEntryDao(UnprocessedBlindLogEntryDaoProxy logEntryDao) {
		this.logEntryDao = logEntryDao;
	}
	
	public AllJakeObjectsFuture(UnprocessedBlindLogEntryDaoProxy logEntryDao) {
		super();
		this.setLogEntryDao(logEntryDao);
	}


	public AllJakeObjectsFuture(
					ProjectApplicationContextFactory applicationContextFactory,
					Project project) {
		super();

		log.debug("Creating a " + getClass().getSimpleName() + " with "
				+ applicationContextFactory + "on project " + project);

		this.setLogEntryDao(applicationContextFactory.getLogEntryDao(project));
	}

	
	@Override
	@Transactional
	public Collection<JakeObject> calculate() {
		Set<JakeObject> objects = new HashSet<JakeObject>(); 
		
		for(LogEntry<JakeObject> le : this.logEntryDao.getAllVersions() ){
			objects.add(le.getBelongsTo());
		}
		return objects;
	}
}