package com.jakeapp.violet.di;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class DBQueries {

	public static Properties getProperties() {
		Map<KnownProperty, String> p = new HashMap<KnownProperty, String>();
		p.put(KnownProperty.DB_CREATELOGTABLE,
				"CREATE TABLE IF NOT EXISTS log (" + " UUID id, "
						+ " Timestamp when, " + " String who, "
						+ " String what, " + " String why, " + " String how, "
						+ " Boolean known" + ")");
		p.put(KnownProperty.DB_CREATELOGINDEXWHEN,
				"CREATE INDEX IF NOT EXISTS whenindex ON log (when)");
		p.put(KnownProperty.DB_CREATELOGINDEXWHAT,
				"CREATE INDEX IF NOT EXISTS whatindex ON log (what)");
		p.put(KnownProperty.DB_INSERTLOG,
				"INSERT INTO log (id, when, who, what, why, how, known) VALUES (?, ?, ?, ?, ?, ?, ?)");
		p.put(KnownProperty.DB_GETLOGBYID,
				"SELECT id, when, who, what, why, how, known FROM log WHERE id=?");
		p.put(KnownProperty.DB_GETRELPATHSPROCESSED,
				"SELECT what FROM log WHERE known=true");
		p.put(KnownProperty.DB_GETRELPATHS, "SELECT what FROM log");
		p.put(KnownProperty.DB_SETPROCESSEDFORWHAT,
				"UPDATE log SET known = true WHERE what = ?");
		p.put(KnownProperty.DB_SETPROCESSEDBYID,
				"UPDATE log SET known = true WHERE id = ?");
		p.put(KnownProperty.DB_GETALL,
				"SELECT id, when, who, what, why, how, known FROM log");
		p.put(KnownProperty.DB_GETPROCESSED,
				"SELECT id, when, who, what, why, how, known FROM log WHERE known = true");
		p.put(KnownProperty.DB_GETUNPROCESSED,
				"SELECT id, when, who, what, why, how, known FROM log WHERE known = false");
		p.put(KnownProperty.DB_GETALLFORWHAT,
				"SELECT id, when, who, what, why, how, known FROM log WHERE what = ?");
		p.put(KnownProperty.DB_GETPROCESSEDFORWHAT,
				"SELECT id, when, who, what, why, how, known FROM log WHERE what = ? AND known = true");
		p.put(KnownProperty.DB_GETUNPROCESSEDFORWHAT,
				"SELECT id, when, who, what, why, how, known FROM log WHERE what = ? AND known = false");

		Properties props = new Properties();
		for (Entry<KnownProperty, String> pe : p.entrySet()) {
			props.put(pe.getKey(), pe.getValue());
		}
		return props;
	}
}
