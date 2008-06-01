package com.doublesignal.sepm.jake.core.dao;

import java.sql.*;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.Before;

public class DBTestTest extends DBTest {
	@Before
	public void setUp() throws Exception {
		setUp("test-test-create.sql", "test-test-insert.sql");
	}
	
	@Test
	public void testSelect() throws Exception{
		ResultSet rs = con.createStatement().executeQuery(
				"SELECT a,b from testtable");
		
		Assert.assertTrue(rs.next());
		Assert.assertEquals(rs.getInt("a"), 1);
		Assert.assertEquals(rs.getInt("b"), 2);
		Assert.assertFalse(rs.next());
	}

	@Test
	public void testInsert() throws Exception{
		con.createStatement().execute("INSERT INTO testtable (a, b) VALUES (2,3)");
		
		ResultSet rs = con.createStatement().executeQuery(
				"SELECT a,b from testtable");
		
		Assert.assertTrue(rs.next());
		Assert.assertEquals(rs.getInt("a"), 1);
		Assert.assertEquals(rs.getInt("b"), 2);
		Assert.assertTrue(rs.next());
		Assert.assertEquals(rs.getInt("a"), 2);
		Assert.assertEquals(rs.getInt("b"), 3);
		Assert.assertFalse(rs.next());
		
		con.createStatement().execute("DELETE FROM testtable WHERE a = 2 AND b = 3");
	}

}
