package com.jakeapp.violet.di;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.violet.gui.JsonProjects;
import com.jakeapp.violet.gui.Projects;
import com.jakeapp.violet.model.JsonProjectPreferences;
import com.jakeapp.violet.model.ProjectPreferences;
import com.jakeapp.violet.model.User;

public abstract class DI {
	static {
		register(Projects.class, new Creator<Projects>() {
			Projects projects = new JsonProjects(new File(".jakeprojects"));

			@Override
			public Projects create() {
				return projects;
			}

		});
	}

	private DI() {
	}

	private static HashMap<Class, Creator> map = new HashMap<Class, Creator>();
	private static HashMap<Class, ProjectDependentCreator> mapPerProject = new HashMap<Class, ProjectDependentCreator>();

	public static <C> C getImpl(Class<C> c) {
		Creator<C> creator = map.get(c);
		if (creator == null)
			throw new IllegalArgumentException("creator for " + c
					+ " not defined");
		return creator.create();
	}

	public static <C> C getImplForProject(Class<C> c, UUID projectid) {
		ProjectDependentCreator<C> creator = mapPerProject.get(projectid);
		if (creator == null)
			throw new IllegalArgumentException("creator for " + c
					+ " not defined");
		return creator.create(projectid);
	}

	public static <C> void register(Class<C> c, Creator<C> creator) {
		map.put(c, creator);
	}

	public static UserId getUserId(String userId) {
		return new XmppUserId(userId);
	}

	public static ProjectPreferences getPreferencesImpl(File file) {
		return new JsonProjectPreferences(file);
	}

	public static String getProperty(String string) {
		// TODO: call a DI injection for "properties" which loads a ini file or
		// smth

		if (string.equals("jdbcdb"))
			return "h2";
		if (string.equals("jdbcuser"))
			return "sa";
		if (string.equals("jdbcpassword"))
			return "";
		if (string.equals("project.logFilename"))
			return ".jakelog";
		if (string.equals("project.preferenceFilename"))
			return ".jakeconf";
		if (string.equals("db.createlogtable")) {
			return "CREATE TABLE IF NOT EXISTS log (" + " UUID id, "
					+ " Timestamp when, " + " String who, " + " String what, "
					+ " String why, " + " String how, " + " Boolean known"
					+ ")";
		}
		if (string.equals("db.createlogindexwhen")) {
			return "CREATE INDEX IF NOT EXISTS whenindex ON log (when)";
		}
		if (string.equals("db.createlogindexwhat")) {
			return "CREATE INDEX IF NOT EXISTS whatindex ON log (what)";
		}
		if (string.equals("db.insertlog"))
			return "INSERT INTO log (id, when, who, what, why, how, known) VALUES (?, ?, ?, ?, ?, ?, ?)";
		if (string.equals("db.getlogbyid"))
			return "SELECT id, when, who, what, why, how, known FROM log WHERE id=?";
		if (string.equals("db.getrelpathsprocessed"))
			return "SELECT what FROM log WHERE known=true";
		if (string.equals("db.getrelpaths"))
			return "SELECT what FROM log";
		if (string.equals("db.setprocessedForWhat"))
			return "UPDATE log SET known = true WHERE what = ?";
		if (string.equals("db.getall"))
			return "SELECT id, when, who, what, why, how, known FROM log";
		if (string.equals("db.getprocessed"))
			return "SELECT id, when, who, what, why, how, known FROM log WHERE known = true";
		if (string.equals("db.getunprocessed"))
			return "SELECT id, when, who, what, why, how, known FROM log WHERE known = false";
		if (string.equals("db.getallforwhat"))
			return "SELECT id, when, who, what, why, how, known FROM log WHERE what = ?";
		if (string.equals("db.getunprocessedforwhat"))
			return "SELECT id, when, who, what, why, how, known FROM log WHERE what = ? and known = false";
		if (string.equals("db.getprocessedforwhat"))
			return "SELECT id, when, who, what, why, how, known FROM log WHERE what = ? and known = true";
		if (string.equals("db.setprocessed"))
			return "UPDATE log SET known = true WHERE id = ?";
		throw new IllegalArgumentException("keyword " + string + " not defined");
	}

	private static Map<User, ICService> icsMap = new HashMap<User, ICService>();

	public static ICService getICService(User user) {
		ICService ics = icsMap.get(user);
		if (ics == null) {
			ics = getImpl(ICService.class);
			icsMap.put(user, ics);
		}
		return ics;
	}

}
