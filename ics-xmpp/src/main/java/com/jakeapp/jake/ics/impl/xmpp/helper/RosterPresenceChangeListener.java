package com.jakeapp.jake.ics.impl.xmpp.helper;

import java.util.Collection;

import org.jivesoftware.smack.RosterListener;

/**
 * This just shortens a call, not important
 * 
 * @author johannes
 */
public abstract class RosterPresenceChangeListener implements
		RosterListener {

	public void entriesAdded(
			@SuppressWarnings("unused") Collection<String> addresses) {
		// doesn't concern us
	}

	public void entriesDeleted(
			@SuppressWarnings("unused") Collection<String> addresses) {
		// doesn't concern us
	}

	public void entriesUpdated(
			@SuppressWarnings("unused") Collection<String> addresses) {
		// doesn't concern us
	}
}