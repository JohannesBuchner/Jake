package com.jakeapp.core.misc;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.apache.log4j.Logger;
import org.hsqldb.jdbc.jdbcDataSource;
import org.springframework.core.io.ClassPathResource;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.io.File;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.util.Scanner;

/**
 * TODO: Fill in purpose of this file
 * User: Dominik
 * Date: Dec 11, 2008
 * Time: 3:02:42 AM
 * Module: ${MAVEN-MODULE-NAME}
 * Version: ${MAVEN-VERSION}
 */
public class MultipleDatabaseTest {
    private static Logger log = Logger.getLogger(MultipleDatabaseTest.class);


    @BeforeClass
    public static void setupTest() {

    }

    @Before
    public void beforeTest() {


    }


    @After
    public void afterTest() {
        new File("root.script").delete();
        new File("root.properties").delete();
    }


    @Test
    public void simpleDatabaseSetupTest() {


        jdbcDataSource test = new org.hsqldb.jdbc.jdbcDataSource();

        //test.setDatabase("jdbc:hsqldb:file:/home/domdorn/root;ifexists=true;shutdown=false;create=true");
        test.setDatabase("jdbc:hsqldb:file:root;shutdown=true;create=true");
        test.setUser("sa");
        test.setPassword("");

        try {
            Connection conn = test.getConnection("sa", "");
            Statement stmt;
            conn.setAutoCommit(true);

            //conn.nativeSQL("INSERT INTO TEST (ID) VALUES (1);");

            stmt = conn.createStatement();
            stmt.execute("DROP TABLE IF EXISTS configuration;");
            stmt.executeUpdate("CREATE TABLE configuration ( key CHAR(36) PRIMARY KEY, value VARCHAR(255) );");
            stmt.execute("INSERT INTO configuration (key, value) VALUES ('aaaa', 'bbbbb');");
            stmt.executeUpdate("SHUTDOWN COMPACT");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        System.out.println("test.getDatabase() = " + test.getDatabase());
    }

    @Test
    public void simpleClassPathTest() throws IOException {
        log.debug(ClassLoader.getSystemResource("/_import.sql").openStream());
    }


    @Test
    public void loadSQLFileFromClassPath() throws IOException {
        ClassPathResource importScript = new ClassPathResource("/_import.sql");

        log.debug("importScript.getFilename() = " + importScript.getFilename());
        log.debug(importScript.getInputStream());

        BufferedInputStream inputStream = new BufferedInputStream(importScript.getInputStream());

        Scanner sc = new Scanner(inputStream);
         while (sc.hasNextLine()) {
            log.debug(sc.nextLine());
        }
    }


    @Test
    public void importRootDatabaseTest() throws IOException, SQLException {
        jdbcDataSource defaultDataSource = new org.hsqldb.jdbc.jdbcDataSource();

        //defaultDataSource.setDatabase("jdbc:hsqldb:file:/home/domdorn/root;ifexists=true;shutdown=false;create=true");
        defaultDataSource.setDatabase("jdbc:hsqldb:file:root;shutdown=true;create=true");
        defaultDataSource.setUser("sa");
        defaultDataSource.setPassword("");

        ClassPathResource importScript = new ClassPathResource("/_import.sql");


        Statement stmt = defaultDataSource.getConnection("sa", "").createStatement();
        Scanner sc = new Scanner(new BufferedInputStream(importScript.getInputStream()));

        while (sc.hasNextLine()) {
            stmt.addBatch(sc.nextLine());
        }
        sc.close();


        stmt.executeUpdate("SHUTDOWN COMPACT");
        stmt = null;
        sc = null;
    }



    @Test
    public void jakeRoutingDatasource_simpleCreateTest()
    {
        new JakeRoutingDatasource();
    }

    


}
