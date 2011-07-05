package com.jakeapp.violet;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.test.TmpdirEnabledTestCase;
import com.jakeapp.violet.context.MockProjectModel;
import com.jakeapp.violet.context.ProjectModel;
import com.jakeapp.violet.model.JsonProjectPreferences;

public class JsonProjectPreferencesTest extends TmpdirEnabledTestCase {

	private File f;

	private JsonProjectPreferences prefs;

	private final String key1 = "my!#@^!@Key";

	private final String value1 = "myT$%!HJU^value";

	private final String key_bool = "foo";

	private final String value_boole = "true";

	private final String key_id = ProjectModel.PROJECT_ID_PROPERTY_KEY;

	private final UUID projectid = new UUID(12, 41);

	private final String value_id = projectid.toString();

	private final String key_name = "name";

	private final String value_name = "my awesome project";

	private final String key_user = ProjectModel.USERID_PROPERTY_KEY;

	private final String value_user = "me@localhost";

	@Test
	public void run() throws IOException {
		Assert.assertTrue(f.length() < key1.length());
		Assert.assertNull(prefs.get(key1));
		prefs.remove(key1);
		Assert.assertTrue(f.length() < key1.length());
		prefs.set(key1, value1);
		Assert.assertEquals(prefs.get(key1), value1);
		Assert.assertTrue(f.length() > key1.length());
		prefs.remove(key1);
		Assert.assertTrue(f.length() < key1.length());
		Assert.assertNull(prefs.get(key1));
		prefs.set(key1, value1);
		prefs.set(key_bool, value_boole);
		prefs.set(key_id, value_id);
		prefs.set(key_name, value_name);
		prefs.set(key1, value1);
		prefs.set(key_user, value_user);

		ProjectModel model = new MockProjectModel(null, null, prefs, null, null);
		Assert.assertEquals(projectid, model.getProjectid());
		Assert.assertEquals(value_user, model.getUser().getUserId());
		Assert.assertEquals(value_user, model.getUserid());
		Assert.assertEquals(value_name, model.getProjectname());
	}

	@Before
	public void setUp() throws Exception {
		super.setup();

		f = new File(tmpdir, "myprefs");
		prefs = new JsonProjectPreferences(f);
	}
}
