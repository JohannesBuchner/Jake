package com.jakeapp.core.dao;

import org.junit.Assert;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;

import java.util.UUID;
import java.util.List;

@ContextConfiguration(locations = {"/com/jakeapp/core/dao/jake_core_test_hibernateLocal_context.xml"})
public class HibernateNoteObjectDaoTest extends AbstractJUnit4SpringContextTests {

    private static Logger log = Logger.getLogger(HibernateNoteObjectDaoTest.class);

    private IJakeObjectDao noteObjectDao;
    private HibernateTemplate template;

    public void setNoteObjectDao(IJakeObjectDao noteObjectDao) {
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
        this.setNoteObjectDao((IJakeObjectDao) this.applicationContext.getBean("noteObjectDao"));
        this.setTemplate((HibernateTemplate) applicationContext.getBean("hibernateTemplate"));
        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().begin();
    }

    @After
    public void tearDown() {
        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().commit();

        /* rollback for true unit testing */
//        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().rollback();
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
        result = (NoteObject) noteObjectDao.get(noteObject.getUuid());

        Assert.assertEquals(noteObject, result);
    }

    @Test(expected = NoSuchJakeObjectException.class)
    public void testDelete() throws Exception, NoSuchJakeObjectException {
        // Add your code here
        NoteObject noteObject;
        NoteObject result1;
        NoteObject result2;


        noteObject = new NoteObject(UUID.fromString("0fa0028f-5035-4c39-3333-99a93cdc2b86"),
                null, "testDelete");
        noteObjectDao.persist(noteObject);
        result1 = (NoteObject) noteObjectDao.get(noteObject.getUuid());

        Assert.assertEquals(noteObject, result1);

        try {
            noteObjectDao.delete(result1);
        } catch (NoSuchJakeObjectException e) {
            throw new Exception("Object not found!");
        }

        result2 = (NoteObject) noteObjectDao.get(result1.getUuid()); // here exception gets thrown

        Assert.assertNull(result2);

    }

    /*  @Test
    public void testGetTags() {
        // Add your code here
        Assert.fail();
    }*/


    @Transactional
    @Test
    public void testAddTagTo_withoutRetrieval() throws NoSuchJakeObjectException, InvalidTagNameException {
        Tag t1 = new Tag("testAddTagTo_withoutRetrieval_test");
        Tag t2 = new Tag("testAddTagTo_withoutRetrieval_test2");


                NoteObject noteObject1 = new NoteObject(UUID.fromString("0faaa8f-8888-4c39-9925-99a93cdc2b86"),
                null, "testAddTagTo_withoutRetrieval");

        noteObjectDao.persist(noteObject1);
        noteObjectDao.addTagTo(noteObject1, t1);
        noteObjectDao.addTagTo(noteObject1, t2);
    }


    @Transactional
    @Test
    public void testAddTagTo() throws InvalidTagNameException, NoSuchJakeObjectException {

        Tag t1 = new Tag("testAddTagTo_test");
        Tag t2 = new Tag("testAddTagTo_test2");

        NoteObject noteObject1 = new NoteObject(UUID.fromString("0faaa8f-aaaa-4c39-9925-99a93cdc2b86"),
                null, "testAddTagTo");

        noteObjectDao.persist(noteObject1);
        noteObjectDao.addTagTo(noteObject1, t1);
        noteObjectDao.addTagTo(noteObject1, t2);

        List<Tag> results;
        results = noteObjectDao.getTagsFor(noteObject1);

        Assert.assertTrue(results.contains(t1));
        Assert.assertTrue(results.contains(t2));


    }

    @Transactional
    @Test
    public void testRemoveTagFrom() throws NoSuchJakeObjectException, InvalidTagNameException {

        log.debug("==========================\n\n\n\n\n\n");
        Tag t1 = new Tag("testRemoveTagFrom_test3");
        Tag t2 = new Tag("testRemoveTagFrom_test4");

        NoteObject noteObject1 = new NoteObject(UUID.fromString("0faaa8f-4444-4c39-9925-99a93cdc2b86"),
                null, "testRemoveTagFrom");

        noteObjectDao.persist(noteObject1);
        noteObjectDao.addTagTo(noteObject1, t1);
        noteObjectDao.addTagTo(noteObject1, t2);

        List<Tag> results = noteObjectDao.getTagsFor(noteObject1);

        Assert.assertTrue(results.contains(t1));
        Assert.assertTrue(results.contains(t2));

        noteObjectDao.removeTagFrom(noteObject1, t1);
        results.clear();
        results = noteObjectDao.getTagsFor(noteObject1);

        Assert.assertTrue(results.contains(t2));
        Assert.assertFalse(results.contains(t1));

        log.debug("\n\n\n\n\n\n==========================");
    }

    @Transactional
    @Test
    public void testRemoveTagsFrom() throws NoSuchJakeObjectException, InvalidTagNameException {

        log.debug("==========================\n\n\n\n\n\n");
        Tag t1 = new Tag("testRemoveTagsFrom_test1");
        Tag t2 = new Tag("testRemoveTagsFrom_test2");
        Tag t3 = new Tag("testRemoveTagsFrom_test3");
        Tag t4 = new Tag("testRemoveTagsFrom_test4");

        NoteObject noteObject1 = new NoteObject(UUID.fromString("0faaa8f-5555-4c39-9925-99a93cdc2b86"),
                null, "testRemoveTagsFrom");

        noteObjectDao.persist(noteObject1);
        noteObjectDao.addTagTo(noteObject1, t1);
        noteObjectDao.addTagTo(noteObject1, t2);
        noteObjectDao.addTagTo(noteObject1, t3);
        noteObjectDao.addTagTo(noteObject1, t4);

        List<Tag> results = noteObjectDao.getTagsFor(noteObject1);

        Assert.assertTrue(results.contains(t1));
        Assert.assertTrue(results.contains(t2));
        Assert.assertTrue(results.contains(t3));
        Assert.assertTrue(results.contains(t4));

        noteObjectDao.removeTagsFrom(noteObject1, new Tag[]{ t1, t2, t3 });
        results.clear();
        results = noteObjectDao.getTagsFor(noteObject1);


        Assert.assertFalse(results.contains(t1));
        Assert.assertFalse(results.contains(t2));
        Assert.assertFalse(results.contains(t3));
        Assert.assertTrue(results.contains(t4));

        log.debug("\n\n\n\n\n\n==========================");
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
