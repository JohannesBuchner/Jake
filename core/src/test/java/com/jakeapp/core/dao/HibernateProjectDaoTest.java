package com.jakeapp.core.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.*;

import java.io.File;
import java.util.UUID;
import java.sql.Statement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;


/**
 * Unit Tests for HibernateProjectDao
 */
@ContextConfiguration(locations = "/com/jakeapp/core/dao/jake_core_test_hibernateProjectDao_context.xml")
public class HibernateProjectDaoTest extends AbstractJUnit4SpringContextTests {

    private static Logger log = Logger.getLogger(HibernateProjectDaoTest.class);
    private static final String[] contextXML = {""};
    private static final String daoBeanId = "projectDao";
    private static IProjectDao projectDao;
    private File systemTmpDir = new File(System.getProperty("java.io.tmpdir"));

    private static final Project project = new Project("name", new UUID(1, 2), null, new File("test-path"));

    private String project_1_uuid = "480f3166-06f5-40db-93b7-a69789d1fcd2";
    private String project_2_uuid = "b310b970-9bf0-4d30-a028-c236c0ebc0cd";
    private String project_3_uuid = "9f4f23c3-2437-4e2b-9b76-50f8e3333a97";
    private String project_4_uuid = "abf7783a-f1f8-41fc-9268-650cce915a6e";
    Project project1 = new Project("someName", UUID.fromString(project_1_uuid), null, systemTmpDir);
    Project project2 = new Project("someName", UUID.fromString(project_2_uuid), null, systemTmpDir);


    @BeforeClass
    public static void setUpClass() {
        log.debug("setting up test class...");

    }

    @Transactional
    @Before
    public void setUp() {
        HibernateTemplate template = (HibernateTemplate) applicationContext.getBean("hibernateTemplate");
        template.getSessionFactory().getCurrentSession().getTransaction().begin();
        projectDao = (IProjectDao) applicationContext.getBean(daoBeanId);
    }

    @Transactional
    @After
    public void tearDown() {
        HibernateTemplate template = (HibernateTemplate) applicationContext.getBean("hibernateTemplate");

        /* commit transactions to test if they are really working */
//        template.getSessionFactory().getCurrentSession().getTransaction().commit();

        /* rollback for true unit testing */
        template.getSessionFactory().getCurrentSession().getTransaction().rollback();


    }


    /**
     * This test simply tries to persist a new Project into the database without getting an error
     */
    @Test
    @Transactional
    public final void createProjectTest() throws InvalidProjectException {
        projectDao.create(project1);
    }


    /**
     * This test tries to create a Project into the database and read it afterwards.
     *
     * @throws NoSuchProjectException if the Project is not found, indicating the persisting didn't work.
     */
    @Test
    @Transactional
    public final void createAndReadProjectTest() throws NoSuchProjectException, InvalidProjectException {
        Project project_result;
        projectDao.create(project2);
        project_result = projectDao.read(UUID.fromString(project_2_uuid));
        assertEquals(project2, project_result);
    }


    /**
     * This test tries to read a non-existing project from the database
     *
     * @throws NoSuchProjectException If the Project requested is not found.
     */
    @Transactional
    @Test(expected = NoSuchProjectException.class)
    public final void readNonExistingProject() throws NoSuchProjectException {
        UUID uuid = UUID.randomUUID();
        projectDao.read(uuid);
    }


    /**
     * This test creates a project and deletes it afterwards.
     *
     * @throws com.jakeapp.core.dao.exceptions.NoSuchProjectException
     *
     */
    @Test
    public final void delete_shouldCreateAndDeleteAProject() throws NoSuchProjectException, InvalidProjectException {
        UUID current = UUID.fromString("ee756d3d-3816-40f0-a66b-cb4b1f9bc022");
        Project project = new Project();
        project.setName("projectName");
        project.setProjectId(current);
        project.setRootPath(systemTmpDir);

        projectDao.create(project);
        projectDao.delete(project);
    }

    /**
     * This test creates a project, deletes it and then tries to query for the deleted project.
     *
     * @throws com.jakeapp.core.dao.exceptions.NoSuchProjectException
     *
     */
    @Test(expected = NoSuchProjectException.class)
    public final void delete_shouldCreateAndReallyDeleteProject() throws NoSuchProjectException, InvalidProjectException {
        UUID current = UUID.fromString("53430cae-94d8-46e6-a5fc-9557435c233d");
        Project project = new Project();
        project.setName("projectName");
        project.setProjectId(current);
        project.setRootPath(systemTmpDir);

        projectDao.create(project);
        projectDao.delete(project);

        projectDao.read(current);
        fail();
    }


    /**
     * This test tries to delete a non-existing project.
     *
     * @throws com.jakeapp.core.dao.exceptions.NoSuchProjectException
     *
     */
    @Test(expected = NoSuchProjectException.class)
    public final void deleteNonExistingProject_shouldThrowException() throws NoSuchProjectException {
        UUID random_non_exisitng = UUID.fromString("649cda58-fcfb-49a2-9a3d-bc4e6b5e8939");
        Project example = new Project();
        example.setProjectId(random_non_exisitng);


        projectDao.delete(example);
        fail();
    }


    /**
     * This test tries to create a null-project
     */
    @Test(expected = InvalidProjectException.class)
    public final void create_persistNullshouldFail() throws InvalidProjectException {
        projectDao.create(null);
    }

    /**
     * This test tries to update a null-project
     */
    @Test
    public final void update_persistNullShouldFail() throws NoSuchProjectException {
        projectDao.update(null);
    }

    /**
     * This test tries to update a null-project
     */
    @Test
    public final void delete_persistNullShouldFail() throws NoSuchProjectException {
        projectDao.delete(null);
    }


    /**
     * This test tries to get a null-project
     */
    @Test
    public final void read_persistNullShouldFail() throws NoSuchProjectException {
        projectDao.read(null);
    }


        /**
     * This test tries to persist a project with null as uuid
     *
     * @throws InvalidProjectException
     */
    @Test
    public final void create_persistProjectWithNullUUID() throws InvalidProjectException {
        Project project = new Project();
        project.setProjectId((UUID) null);

        projectDao.create(project);
    }

    /**
     * This test tries to persist a project with null as uuid
     *
     * @throws InvalidProjectException
     */
    @Test
    public final void update_persistProjectWithNullUUID() throws NoSuchProjectException {
        Project project = new Project();
        project.setProjectId((UUID) null);

        projectDao.update(project);
    }

        /**
     * This test tries to get a project with null as uuid
     *
     * @throws InvalidProjectException
     */
    @Test
    public final void read_persistProjectWithNullUUID() throws InvalidProjectException, NoSuchProjectException {
        Project project = new Project();
        project.setProjectId((UUID) null);

        projectDao.read(null);
    }


        /**
     * This test tries to delete a project with null as uuid
     *
     * @throws InvalidProjectException
     */
    @Test
    public final void delete_persistProjectWithNullUUID() throws NoSuchProjectException {
        Project project = new Project();
        project.setProjectId((UUID) null);

        projectDao.delete(project);
    }


//
//    @Test(expected = NoSuchProjectException.class)
//    public void persistAndRead() throws NoSuchProjectException {
//        projectDao.update(project);
//        List<Project> result = projectDao.getAll();
//        assertTrue(result.contains(project));
//    }
//
//    @Test
//    public void persistReadMakeTransientRead() {
//        projectDao.update(project);
//        List<Project> result = projectDao.getAll();
//        assertTrue(result.contains(project));
//
//        try {
//            projectDao.delete(project);
//            assertFalse(result.contains(project));
//        } catch (NoSuchProjectException e) {
//            log.debug("failed to delete projec");
//            fail();
//        }
//    }
}
