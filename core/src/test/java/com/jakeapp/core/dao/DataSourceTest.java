package com.jakeapp.core.dao;

import org.apache.log4j.Logger;

/**
 * TODO: Fill in purpose of this file
 * User: Dominik
 * Date: Dec 10, 2008
 * Time: 4:11:40 AM
 * Module: ${MAVEN-MODULE-NAME}
 * Version: ${MAVEN-VERSION}
 */
@Deprecated
public class DataSourceTest {
    private static Logger log = Logger.getLogger(DataSourceTest.class);


    String driverClassName = "";
    String url = "";
    String username = "";
    String password = "";

    public String getDriverClassName() {
        log.debug("getDriverClassName() : " + driverClassName);
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        log.debug("setDriverClassName: " + driverClassName);
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        log.debug("getUrl(): " + url);
        return url;
    }

    public void setUrl(String url) {
        log.debug("setUrl(): "+ url);
        this.url = url;
    }

    public String getUsername() {
        log.debug("getUsername(): " + username);
        return username;
    }

    public void setUsername(String username) {
        log.debug("setUsername(): "+ username);
        this.username = username;
    }

    public String getPassword() {
        log.debug("getPassword(): " + password);
        return password;
    }

    public void setPassword(String password) {
        log.debug("setPassword(): " + password);
        this.password = password;
    }

    public DataSourceTest() {
    }
}
