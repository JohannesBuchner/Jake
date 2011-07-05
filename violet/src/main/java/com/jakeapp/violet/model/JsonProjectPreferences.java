package com.jakeapp.violet.model;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * The string name refers to the ProjectDir
 */
public class JsonProjectPreferences extends ProjectPreferences {

	private final File f;

	private static final Logger log = Logger
			.getLogger(JsonProjectPreferences.class);

	private final ObjectMapper mapper = new ObjectMapper();

	public JsonProjectPreferences(File f) {
		this.f = f;
		load();
	}

	@Override
	protected void load() {
		preferences.clear();
		try {
			if (f.exists()) {
				preferences.putAll(mapper.readValue(f, Map.class));
			}
		} catch (IOException e) {
			log.info(e);
		}
	}

	@Override
	protected void store() throws IOException {
		mapper.writeValue(f, preferences);
	}
}
