package com.jakeapp.core.dao;

import org.junit.Assert;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.NoteObject;

import java.util.UUID;

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
        this.setNoteObjectDao((IJakeObjectDao) this.applicationContext.getBean("noteObjectDao") );
        this.setTemplate( (HibernateTemplate) applicationContext.getBean("hibernateTemplate") );
    	this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().begin();
    }

    @After
    public void tearDown() {
        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().commit();

        /* rollback for true unit testing */
        //this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().rollback();
    }

    @Test
    public void testPersist() {
        NoteObject noteObject = new NoteObject(UUID.fromString("0fa0028f-5035-4c39-9925-99a93cdc2b86"),
                null, "Das ist ein Test"      );


        noteObjectDao.persist(noteObject);


        // Add your code here

    }

    @Test
    public void testGetAll() {
        // Add your code here
//        Assert.fail();
    }

//    @Test
//    public void testGetAll() {
//        // Add your code here
//    }

    @Test
    public void testMakeTransient() {
        // Add your code here
//        Assert.fail();
    }

    @Test
    public void testGetTags() {
        // Add your code here
//        Assert.fail();
    }

    @Test
    public void testAddTagTo() {
        // Add your code here
//        Assert.fail();
    }

    @Test
    public void testRemoveTagFrom() {
        // Add your code here
//        Assert.fail();
    }

    @Test
    public void testAddTagsTo() {
        // Add your code here
//        Assert.fail();
    }

    @Test
    public void testGetTagsFor() {
        // Add your code here
//        Assert.fail();
    }

    @Test
    public void testRemoveTagsFrom() {
        // Add your code here
//        Assert.fail();
    }
}
