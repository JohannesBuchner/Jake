package com.jakeapp.core.dao;

import java.util.List;

import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.LogEntry;

/**
 * A class for appending all dao methods. This is ugly, but the only way to do
 * it as far as I see.
 * 
 * @author johannes
 */
@Deprecated
public class DaoThreadBroker extends ThreadBroker {

	protected DaoThreadBroker() {
		super();
	}

}
