package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.NoteObject;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@ContextConfiguration // local
public class AbstractNoteObjectDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static Logger log = Logger.getLogger(AbstractNoteObjectDaoTest.class);

    private INoteObjectDao noteObjectDao;
    private HibernateTemplate template;

    public void setNoteObjectDao(INoteObjectDao noteObjectDao) {
        this.noteObjectDao = noteObjectDao;
    }

    public HibernateTemplate getTemplate() {
        return template;
    }

    public void setTemplate(HibernateTemplate template) {
        this.template = template;
    }

    @Before
    public void setUp() {
        // Add your code here
        this.setNoteObjectDao((INoteObjectDao) this.applicationContext.getBean("noteObjectDao"));
        this.setTemplate((HibernateTemplate) applicationContext.getBean("hibernateTemplate"));
        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().begin();
    }

    @After
    public void tearDown() {
//        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().commit();

        /* rollback for true unit testing */
        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().rollback();
    }

    @Transactional
    @Test
    public void testPersist() {
        NoteObject noteObject = new NoteObject(UUID.fromString("0fa0028f-bbbb-4c39-9925-99a93cdc2b86"),
                null, "testPersist_Das ist ein Test");


        noteObjectDao.persist(noteObject);


        // Add your code here

    }

    @Transactional
    @Test
    public void testGetAll() {
        NoteObject noteObject1 = new NoteObject(UUID.fromString("0fa0028f-5035-4c39-9925-99a93cdc2b86"),
                null, "testGetAll_Das ist ein erster Test");
        NoteObject noteObject2 = new NoteObject(UUID.fromString("752ffdde-a9ee-4be4-b9e2-26e96971e010"),
                null, "testGetAll_Das ist ein zweiter Test");
        NoteObject noteObject3 = new NoteObject(UUID.fromString("e6a64ef9-5bf8-4e61-ad47-a76c27cd9913"),
                null, "testGetAll_Das ist ein dritter Test");
        NoteObject noteObject4 = new NoteObject(UUID.fromString("a7a4a23c-8e85-4b74-9e2d-6b0921f298dd"),
                null, "testGetAll_Das ist ein vierter Test");


        noteObjectDao.persist(noteObject1);
        noteObjectDao.persist(noteObject2);
        noteObjectDao.persist(noteObject3);
        noteObjectDao.persist(noteObject4);

        List<NoteObject> results = noteObjectDao.getAll();

        Assert.assertTrue(results.contains(noteObject1));
        Assert.assertTrue(results.contains(noteObject2));
        Assert.assertTrue(results.contains(noteObject3));
        Assert.assertTrue(results.contains(noteObject4));
    }

    @Transactional
    @Test
    public void testGet() throws NoSuchJakeObjectException {
        NoteObject noteObject;
        NoteObject result;


        noteObject = new NoteObject(UUID.fromString("0fa0028f-5555-4c39-9925-99a93cdc2b86"),
                null, "testGet");
        noteObjectDao.persist(noteObject);
        result = noteObjectDao.get(noteObject.getUuid());

        Assert.assertEquals(noteObject, result);
    }

    @Test(expected = NoSuchJakeObjectException.class)
    public void testDelete() throws Exception {
        // Add your code here
        NoteObject noteObject;
        NoteObject result1;
        NoteObject result2;


        noteObject = new NoteObject(UUID.fromString("0fa0028f-5035-4c39-3333-99a93cdc2b86"),
                null, "testDelete");
        noteObjectDao.persist(noteObject);
        result1 = noteObjectDao.get(noteObject.getUuid());

        Assert.assertEquals(noteObject, result1);

        try {
            noteObjectDao.delete(result1);
        } catch (NoSuchJakeObjectException e) {
            throw new Exception("Object not found!");
        }

        result2 = noteObjectDao.get(result1.getUuid()); // here exception gets thrown

        Assert.assertNull(result2);

    }

    /*
    @Test
    public void testAddTagsTo() {
        // Add your code here
        Assert.fail();
    }

    @Test
    public void testGetTagsFor() {
        // Add your code here
        Assert.fail();
    }

    @Test
    public void testRemoveTagsFrom() {
        // Add your code here
        Assert.fail();
    }*/
}
