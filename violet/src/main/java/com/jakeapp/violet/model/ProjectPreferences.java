package com.jakeapp.violet.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * The string name refers to the ProjectDir
 */
public abstract class ProjectPreferences extends Observable implements Model {

	protected Map<String, String> preferences = new HashMap<String, String>();

	public ProjectPreferences() {
		load();
	}

	protected abstract void load();

	protected abstract void store() throws IOException;

	public void set(String key, String value) throws IOException {
		preferences.put(key, value);
		store();
		setChanged();
	}

	public void remove(String key) throws IOException {
		preferences.remove(key);
		store();
		setChanged();
	}

	public String get(String key) {
		return preferences.get(key);
	}
}
