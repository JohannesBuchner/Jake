package com.doublesignal.sepm.jake.core.dao;
import java.io.*;
import java.sql.*;

import junit.framework.Assert;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DBTest {
	@Before
	public void setUp() throws Exception {
		setUp("test-create.sql", "test-insert.sql");
	}
	
	public void setUp(String createFilename, String insertFilename) throws Exception {
		connect();
		String rootpath = "src/resources/";
		if(! new File(rootpath + createFilename).exists()){
			rootpath = "./";
			if(! new File(rootpath + createFilename).exists()){
				throw new IOException("Create statements (" + createFilename + 
						") not found");
			}
		}
		Assert.assertTrue(new File(rootpath + insertFilename).exists());
		executeCommandsFromFile(rootpath + createFilename);
		executeCommandsFromFile(rootpath + insertFilename);
		con.commit();
	}
	
	@After
	public void tearDown() throws Exception {
		con.rollback();
		disconnect();
	}
	
	@Test
	public void notest(){
		
	}
	
	protected Connection con;
	
	protected String connect_string = "jdbc:hsqldb:test.db";
	
	public void connect() throws Exception{
		con = null;
		Class.forName("org.hsqldb.jdbcDriver");
		con = DriverManager.getConnection(connect_string);
		con.setAutoCommit(false);
	}
	
	public void disconnect() throws Exception {
		con.close();
	}
	
	public void executeCommandsFromFile(String filename) throws SQLException, IOException {
		String[] commands = null; 
		char cbuf[] = new char[10000]; 
		try {
			BufferedReader d = new BufferedReader(new FileReader(filename));
			d.read(cbuf);
			d.close();
		} catch (IOException e) {
			System.err.println("Error while loading DB-Statements: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
		String sbuf = new String(cbuf);
		commands = sbuf.split(";");
		
		for (int i = 0; i < commands.length; i++) {
			String cmd = commands[i].trim();
			
			if(cmd.length() == 0)
				continue;
			
			try {
				con.createStatement().execute(cmd);
			} catch (SQLException e) {
				System.err.println("Executing setup command failed: " + cmd + 
						"\n => \n" + e.getLocalizedMessage());
				try {
					con.rollback();
				} catch (SQLException e1) {
					System.err.println("Rollback failed: " + e1.getLocalizedMessage());
				}
			}
		}
		try {
			con.commit();
		} catch (SQLException e) {
			System.err.println("Committing failed: " + e.getLocalizedMessage());
		}
	}
}
