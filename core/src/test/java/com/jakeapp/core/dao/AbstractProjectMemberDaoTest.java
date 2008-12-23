package com.jakeapp.core.dao;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.apache.log4j.Logger;

/**
 * TODO: Fill in purpose of this file
 * User: Dominik
 * Date: Dec 10, 2008
 * Time: 3:40:54 AM
 * Module: ${MAVEN-MODULE-NAME}
 * Version: ${MAVEN-VERSION}
 */
@Deprecated
public abstract class AbstractProjectMemberDaoTest {
    private static final Logger log = Logger.getLogger(AbstractProjectMemberDaoTest.class);
    private static IProjectMemberDao projectMemberDao;


    @BeforeClass
    public static void setupClass()
    {



        log.debug("starting setupClass");
        ApplicationContext context = new ClassPathXmlApplicationContext(
                        new String[] {"jake_core_test_context.xml"});
        log.debug("created applicationContext");
        

        projectMemberDao = (IProjectMemberDao) context.getBean("projectMemberDao");
        log.debug("got projectMemberDao");
        log.debug("finished with setup of AbstractProjectMemberDaoTest");
    }


    @AfterClass
    public static void afterClass()
    {
        log.debug("calling cleanup method");
        projectMemberDao = null;
        log.debug("cleaned up");
    }


    @Before
    public void beforeEachClass()
    {
        log.debug("this gets called before each test");
    }


    @Test
    public void doSomething_works()
    {
        assert(true);
    }


    @Test (expected=Exception.class)
    public void doSomething_fails() throws Exception
    {
        throw new Exception("adfadsf");
    }


}
