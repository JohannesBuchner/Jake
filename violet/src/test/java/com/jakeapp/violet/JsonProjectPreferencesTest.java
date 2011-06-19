package com.jakeapp.violet;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.status.IStatusService;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;
import com.jakeapp.violet.actions.global.CreateAccountAction;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.JsonProjectPreferences;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.ProjectModelImpl;
import com.jakeapp.violet.model.ProjectPreferences;
import com.jakeapp.violet.model.User;

public class JsonProjectPreferencesTest extends TmpdirEnabledTestCase {

	private File f;

	private JsonProjectPreferences prefs;

	private String key1 = "my!#@^!@Key";

	private String value1 = "myT$%!HJU^value";

	private String key_bool = "foo";

	private String value_boole = "true";

	private String key_id = "id";

	private UUID projectid = new UUID(12, 41);

	private String value_id = projectid.toString();

	private String key_name = "name";

	private String value_name = "my awesome project";

	private String key_user = "user";

	private String value_user = "me@localhost";

	@Before
	public void setUp() throws Exception {
		super.setup();

		f = new File(tmpdir, "myprefs");
		prefs = new JsonProjectPreferences(f);
	}

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

		ProjectModelImpl model = new ProjectModelImpl();
		model.setPreferences(prefs);
		Assert.assertEquals(projectid, model.getProjectid());
		Assert.assertEquals(value_user, model.getUser().getUserId());
		Assert.assertEquals(value_user, model.getUserid());
		Assert.assertEquals(value_name, model.getProjectname());
	}
}
