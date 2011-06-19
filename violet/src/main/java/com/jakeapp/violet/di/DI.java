package com.jakeapp.violet.di;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.violet.gui.JsonProjects;
import com.jakeapp.violet.gui.Projects;
import com.jakeapp.violet.model.JsonProjectPreferences;
import com.jakeapp.violet.model.ProjectPreferences;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.msg.impl.MessageMarshaller;

public class DI {

	private static HashMap<Class, Creator> map = new HashMap<Class, Creator>();

	private static HashMap<Class, ProjectDependentCreator> mapPerProject = new HashMap<Class, ProjectDependentCreator>();

	static {
		register(Projects.class, new Creator<Projects>() {

			Projects projects = null;

			@Override
			public Projects create() {
				if (projects == null) {
					projects = new JsonProjects(
							new File(
									DI.getProperty(KnownProperty.GLOBAL_SETTINGS_DIR),
									DI.getProperty(KnownProperty.GLOBAL_PROJECTS_FILENAME)));
				}
				return projects;
			}

		});
		register(MessageMarshaller.class, new MessageMarshaller());
	}

	private DI() {
		// not initializable
	}

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

	public static <C> void register(Class<C> c, final C instance) {
		map.put(c, new Creator<C>() {

			@Override
			public C create() {
				return instance;
			}
		});
	}

	public static UserId getUserId(String userId) {
		return new XmppUserId(userId);
	}

	public static ProjectPreferences getPreferencesImpl(File file) {
		return new JsonProjectPreferences(file);
	}

	private static Properties properties = new Properties();
	static {
		properties.putAll(DBQueries.getProperties());
		properties.put(KnownProperty.JDBCDB, "h2");
		properties.put(KnownProperty.JDBCUSER, "sa");
		properties.put(KnownProperty.JDBCPASSWORD, "");
		properties.put(KnownProperty.PROJECT_FILENAMES_LOG, ".jakelog");
		properties
				.put(KnownProperty.PROJECT_FILENAMES_PREFERENCES, ".jakeconf");
		properties.put(KnownProperty.ICS_RESOURCE_NAME, "Jake");
		properties
				.put(KnownProperty.ICS_RESOURCE_PROJECT_PREFIX, "JakeProject");
		properties.put(KnownProperty.GLOBAL_PROJECTS_FILENAME, "projects");
		// properties.put(KnownProperty.ICS_USE_SOCKETS, "true");
		// properties.put(KnownProperty.USE_TRASH, "true");

		File path;
		if (System.getenv("APPDATA") == null) {
			path = new File(System.getenv("HOME"), "Jake");
		} else {
			path = new File(System.getenv("APPDATA"), ".Jake");
		}
		path.mkdirs();

		properties.put(KnownProperty.GLOBAL_SETTINGS_DIR, path.getPath());
		properties.putAll(System.getProperties());
	}

	public static String getProperty(KnownProperty key) {
		return properties.getProperty(key.v);
	}

	public static String getProperty(String string) {
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
