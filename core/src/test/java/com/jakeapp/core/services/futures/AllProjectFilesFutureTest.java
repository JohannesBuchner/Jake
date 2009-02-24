package com.jakeapp.core.services.futures;

import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;

import java.util.Properties;
import java.util.UUID;
import java.util.List;
import java.io.File;

import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;

import javax.sql.DataSource;

@ContextConfiguration(locations = {
        "/com/jakeapp/core/services/futures/AllProjectFilesFutureTest_ApplicationContext.xml"
})
public class AllProjectFilesFutureTest extends AbstractJUnit4SpringContextTests {

    private static Logger log = Logger.getLogger(AllProjectFilesFutureTest.class);


    @Autowired
    private IProjectDao projectDao;

    @Autowired
    private IServiceCredentialsDao serviceCredentialsDao;

    @Autowired
    private IFileObjectDao fileObjectDao;


    @Autowired
    private SessionFactory sessionFactory;

    @Before
    public void setUp() {
        log.debug("calling setUP");


    }

    @After
    public void tearDown() {

        DataSource ds = (DataSource) this.applicationContext.getBean("dataSource");

//        ds.getConnection().c?lose();

        log.debug("calling tearDown");
    }

    @Transactional
    public void createExampleProject() throws InvalidProjectException {

        Project project = new Project("testProject", UUID.fromString("6662b7bc-188f-424d-a2cc-aba6cadd931c"), null, new File("/tmp/"));
        
        project.setCredentials(new ServiceCredentials("foo@bar", "", ProtocolType.XMPP));
        
        serviceCredentialsDao.persist(project.getCredentials());
        projectDao.create(project);

        System.out.println("success");
    }


    @Test
    public void testSimpleCreation() throws InvalidProjectException {
        log.debug("testing simple creation");


        sessionFactory.getCurrentSession().beginTransaction();



//        AllProjectFilesFutureTest test = (AllProjectFilesFutureTest) this.applicationContext.getBean("allProjectFilesFutureTest");


        createExampleProject();

        List<Project> projects = projectDao.getAll();

        for(Project proj : projects )
        {
            System.out.println("proj = " + proj);
        }


        sessionFactory.getCurrentSession().getTransaction().commit();



        System.out.println("projectDao = " + projectDao);
        System.out.println("fileObjectDao = " + fileObjectDao);
//        AllProjectFilesFuture allProjectFilesFuture = new AllProjectFilesFuture()

    }                            
}
