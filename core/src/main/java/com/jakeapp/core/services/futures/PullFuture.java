package com.jakeapp.core.services.futures;

import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.synchronization.ISyncService;

/**
 * <code>AvailableLaterObject</code> downloading (<code>Pull</code>ing) a <code>List</code> of <code>JakeObject</code>s
 * with the given <code>ISyncService</code>
 */
public class PullFuture extends AvailableLaterObject<Void> {
	private static final Logger log = Logger.getLogger(PullFuture.class);
	private ISyncService iss;
	private List<JakeObject> jakeObjects;

	public PullFuture(ISyncService iss, List<JakeObject> jakeObjects) {
		this.iss = iss;
		this.jakeObjects = jakeObjects;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		Exception lastException = null; 
		int i = 0;
		for (JakeObject jakeObject : this.jakeObjects) {
			try {
				this.iss.pullObject(jakeObject);
			} catch (Exception e) {
				log.warn("pulling failed, skipping " + jakeObject, e);
				lastException = e;
			}
			i++;
			this.getListener().statusUpdate(i / this.jakeObjects.size(), "");
		}
		if(lastException != null)
			throw lastException;
		return null;
	}
}