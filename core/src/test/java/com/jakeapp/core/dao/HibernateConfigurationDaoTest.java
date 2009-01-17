package com.jakeapp.core.dao;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;
import com.jakeapp.core.domain.Configuration;
import com.jakeapp.core.dao.exceptions.NoSuchConfigOptionException;

import java.util.List;

@ContextConfiguration(locations = {"/com/jakeapp/core/dao/jake_core_test_hibernateGlobal_context.xml"})
public class HibernateConfigurationDaoTest extends AbstractJUnit4SpringContextTests {
    private static Logger log = Logger.getLogger(HibernateConfigurationDaoTest.class);
    private IConfigurationDao configurationDao;
    private HibernateTemplate template;

    public void setConfigurationDao(IConfigurationDao configurationDao) {
        this.configurationDao = configurationDao;
    }

    public void setTemplate(HibernateTemplate template) {
        this.template = template;
    }


    public IConfigurationDao getConfigurationDao() {
        return configurationDao;
    }

    public HibernateTemplate getTemplate() {
        return template;
    }

    public HibernateConfigurationDaoTest() {
//        this.setConfigurationDao((IConfigurationDao) this.applicationContext.getBean("configurationDao"));
//        this.setTemplate((HibernateTemplate) this.applicationContext.getBean("hibernateTemplate"));

    }

    @Before
    public void setUp() {
        this.setConfigurationDao((IConfigurationDao) this.applicationContext.getBean("configurationDao"));
        this.setTemplate((HibernateTemplate) this.applicationContext.getBean("hibernateTemplate"));
        this.getTemplate().getSessionFactory().getCurrentSession().beginTransaction();
    }

    @After
    public void tearDown() {
        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().commit();
        /* rollback for true unit testing */
//        this.getTemplate().getSessionFactory().getCurrentSession().getTransaction().rollback();
    }


    @Transactional
    @Test
    public void testDeleteConfigurationValue() {
             Configuration conf;
        conf = new Configuration("testDeleteConfigurationValue", "testValue");
        configurationDao.update(conf);

        configurationDao.deleteConfigurationValue(conf.getKey());
        List<Configuration> result = configurationDao.getAll();


        Assert.assertFalse(result.contains(conf));




    }

    @Transactional
    @Test
    public void testConfigurationValueExists() {
        // Add your code here
    }

    @Transactional
    @Test
    public void testUpdate() {
        Configuration conf;
        conf = new Configuration("testUpdate", "testValue");

        configurationDao.update(conf);


        List<Configuration> result = configurationDao.getAll();


        Assert.assertTrue(result.contains(conf));

    }

    @Test
    public void testGetConfigurationValue() throws NoSuchConfigOptionException {
                Configuration conf;
        conf = new Configuration("testGetConfigurationValue", "testValue");

        configurationDao.update(conf);
        String result = configurationDao.getConfigurationValue(conf.getKey());
        Assert.assertEquals(result, conf.getValue());
    }

    @Test
    public void testSetConfigurationValue() {
                Configuration conf, confOther;
        conf = new Configuration("testGetConfigurationValue", "testValue");
        confOther = new Configuration("testGetConfigurationValue", "someOtherValue");
        configurationDao.setConfigurationValue(confOther.getKey(), confOther.getValue());

        List<Configuration> result = configurationDao.getAll();


        Assert.assertFalse(result.contains(conf));
        Assert.assertTrue(result.contains(confOther));        
    }


}
