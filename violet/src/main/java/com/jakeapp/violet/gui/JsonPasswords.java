package com.jakeapp.violet.gui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JsonPasswords extends Passwords {
	private File f;

	public JsonPasswords(File f) {
		this.f = f;
	}

	private Map<String, String> load() {
		// TODO JSON here, load from f
		return new HashMap<String, String>();
	}

	private void store(Map<String, String> map) {
		// TODO JSON here, store in f
	}

	@Override
	protected String loadForUser(String user) {
		Map<String, String> map = load();
		return map.get(user);
	}

	@Override
	protected void storeForUser(String user, String pw) {
		Map<String, String> map = load();
		map.put(user, pw);
		store(map);
	}

	@Override
	protected void forgetForUser(String user) {
		Map<String, String> map = load();
		map.remove(user);
		store(map);
	}

}
