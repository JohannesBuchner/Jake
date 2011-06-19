package com.jakeapp.violet.gui;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;

public class JsonProjectsTest extends TmpdirEnabledTestCase {

	private File f;

	private JsonProjects projects;

	private ProjectDir dir1;

	private ProjectDir dir2;

	@Before
	public void setUp() throws Exception {
		super.setup();

		dir1 = new ProjectDir(new File(tmpdir, "dir1"));
		dir2 = new ProjectDir(new File(tmpdir, "dir2/dir3"));
		f = new File(tmpdir, "myprojects");
		Assert.assertNotNull(f);
		projects = new JsonProjects(f);
	}

	@Test
	public void run() throws IOException {
		Assert.assertTrue(projects.getAll().isEmpty());
		projects.remove(dir1);
		Assert.assertTrue(f.length() < dir1.getAbsolutePath().length());
		projects.add(dir1);
		Assert.assertEquals(projects.getAll().size(), 1);
		Assert.assertEquals(projects.getAll().iterator().next(), dir1);
		Assert.assertTrue(f.length() > dir1.getAbsolutePath().length());
		Assert.assertTrue(f.length() < dir1.getAbsolutePath().length()
				+ dir2.getAbsolutePath().length());
		projects.add(dir2);
		Assert.assertEquals(projects.getAll().size(), 2);
		Assert.assertTrue(projects.getAll().contains(dir1));
		Assert.assertTrue(projects.getAll().contains(dir2));
		Assert.assertTrue(f.length() > dir1.getAbsolutePath().length()
				+ dir2.getAbsolutePath().length());
		projects.remove(dir1);
		Assert.assertEquals(projects.getAll().size(), 1);
		Assert.assertTrue(projects.getAll().contains(dir1));
		projects.remove(dir2);
		Assert.assertEquals(projects.getAll().size(), 0);
	}
}
