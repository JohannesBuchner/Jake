package com.jakeapp.core.misc;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.core.io.ClassPathResource;
import org.hsqldb.jdbc.jdbcDataSource;


import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.BufferedInputStream;

import com.jakeapp.core.domain.Project;

/**
 * Datasource specific to a Project.
 * @author dominik
 *
 * @param <UUID>
 */
public class JakeRoutingDatasource<UUID> /* extends AbstractRoutingDataSource
*/
{
 /*
    Map<UUID, DataSource> lookupMap = new HashMap<UUID, DataSource>();


    private void setupDefaultDatabase() {
        jdbcDataSource defaultDataSource = new org.hsqldb.jdbc.jdbcDataSource();

        //defaultDataSource.setDatabase("jdbc:hsqldb:file:/home/domdorn/root;ifexists=true;shutdown=false;create=true");
        defaultDataSource.setDatabase("jdbc:hsqldb:file:jake-root;shutdown=true;create=true");
        defaultDataSource.setUser("sa");
        defaultDataSource.setPassword("");


        try {
            ClassPathResource importScript = new ClassPathResource("hsql-db-setup-global.sql");
            Statement stmt = defaultDataSource.getConnection("sa", "").createStatement();
            Scanner sc = new Scanner(new BufferedInputStream(importScript.getInputStream()));

            while (sc.hasNextLine()) {
                stmt.addBatch(sc.nextLine());
            }
            sc.close();
            stmt.executeUpdate("SHUTDOWN COMPACT");
            

            stmt = null;
            sc = null;

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        this.setDefaultTargetDataSource(defaultDataSource);
    }


    private void setupProjectDatabase(UUID projectUuid) {
        jdbcDataSource projectDataSource = new org.hsqldb.jdbc.jdbcDataSource();

        //defaultDataSource.setDatabase("jdbc:hsqldb:file:/home/domdorn/root;ifexists=true;shutdown=false;create=true");
        projectDataSource.setDatabase("jdbc:hsqldb:file:" + projectUuid.toString() + ";shutdown=true;create=true");
        projectDataSource.setUser("sa");
        projectDataSource.setPassword("");


        try {
            ClassPathResource importScript = new ClassPathResource("hsql-db-setup.sql");
            Statement stmt = projectDataSource.getConnection("sa", "").createStatement();
            Scanner sc = new Scanner(new BufferedInputStream(importScript.getInputStream()));

            while (sc.hasNextLine()) {
                stmt.addBatch(sc.nextLine());
            }
            sc.close();
            stmt.executeUpdate("SHUTDOWN COMPACT");
            

            stmt = null;
            sc = null;

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        lookupMap.put(projectUuid, projectDataSource);

        this.setTargetDataSources(lookupMap);

    }


    public jakeRoutingDatasource() {
        setupDefaultDatabase();

    }


    public void loadProject(UUID projectUuid) {
        setupProjectDatabase(projectUuid);
    }

    @Override
    protected Object resolveSpecifiedLookupKey(Object jakeProject) {
        if (jakeProject instanceof Project)
            return ((Project) jakeProject).getProjectId();

        return jakeProject;
    }

    @Override
    protected Object determineCurrentLookupKey() {

        return null;
    }


    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isWrapperFor(UUID iface) throws SQLException {

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
    */
}
