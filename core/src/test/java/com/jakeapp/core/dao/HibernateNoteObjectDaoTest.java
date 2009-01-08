package com.jakeapp.core.dao;

import org.junit.Assert;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;

import java.util.UUID;
import java.util.List;

@ContextConfiguration(locations = {"/com/jakeapp/core/dao/jake_core_test_hibernateLocal_context.xml"})
public class HibernateNoteObjectDaoTest extends AbstractJUnit4SpringContextTests {

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
                null, "Das ist ein Test");


        noteObjectDao.persist(noteObject);


        // Add your code here

    }

    @Transactional
    @Test
    public void testGetAll() {
        NoteObject noteObject1 = new NoteObject(UUID.fromString("0fa0028f-5035-4c39-9925-99a93cdc2b86"),
                null, "Das ist ein erster Test");
        NoteObject noteObject2 = new NoteObject(UUID.fromString("752ffdde-a9ee-4be4-b9e2-26e96971e010"),
                null, "Das ist ein zweiter Test");
        NoteObject noteObject3 = new NoteObject(UUID.fromString("e6a64ef9-5bf8-4e61-ad47-a76c27cd9913"),
                null, "Das ist ein dritter Test");
        NoteObject noteObject4 = new NoteObject(UUID.fromString("a7a4a23c-8e85-4b74-9e2d-6b0921f298dd"),
                null, "Das ist ein vierter Test");


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
                null, "Das ist ein Test");
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
                null, "Das ist ein Test");
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

    @Test
    public void testGetTags() {
        // Add your code here
        Assert.fail();
    }

    @Test
    public void testAddTagTo() throws InvalidTagNameException, NoSuchJakeObjectException {

        Tag t1 = new Tag("test");

        NoteObject noteObject1 = new NoteObject(UUID.fromString("0faaa8f-2222-4c39-9925-99a93cdc2b86"),
                null, "Das ist ein tag Test");

        noteObjectDao.persist(noteObject1);


        noteObjectDao.addTagsTo(noteObject1, t1);

        
    }

    @Test
    public void testRemoveTagFrom() {
        // Add your code here
        Assert.fail();
    }

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
    }
}
