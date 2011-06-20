package com.jakeapp.violet.gui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonPasswords extends Passwords {

	private static final Logger log = Logger.getLogger(JsonPasswords.class);

	private File f;

	private ObjectMapper objectMapper = new ObjectMapper();

	public JsonPasswords(File f) {
		this.f = f;
	}

	private Map<String, String> load() {
		Map<String, String> entries = new HashMap<String, String>();
		try {
			entries.putAll(objectMapper.readValue(f, Map.class));
		} catch (IOException e) {
			log.error(e);
		}
		return entries;
	}

	private void store(Map<String, String> map) throws IOException {
		objectMapper.writeValue(f, map);
	}

	@Override
	public String loadForUser(String user) {
		Map<String, String> map = load();
		return map.get(user);
	}

	@Override
	public void storeForUser(String user, String pw) throws IOException {
		Map<String, String> map = load();
		map.put(user, pw);
		store(map);
	}

	@Override
	public void forgetForUser(String user) throws IOException {
		Map<String, String> map = load();
		map.remove(user);
		store(map);
	}

}
