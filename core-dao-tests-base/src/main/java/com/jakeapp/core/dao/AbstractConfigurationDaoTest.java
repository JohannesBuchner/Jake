package com.jakeapp.core.dao;

import com.jakeapp.core.domain.Configuration;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ContextConfiguration
public class AbstractConfigurationDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger log = Logger.getLogger(AbstractConfigurationDaoTest.class);


	@Autowired
	private IConfigurationDao configurationDao;

	public void setConfigurationDao(IConfigurationDao configurationDao) {
		this.configurationDao = configurationDao;
	}

	public IConfigurationDao getConfigurationDao() {
		return configurationDao;
	}


	public AbstractConfigurationDaoTest() {
	}



	@Transactional()
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
	public void testUpdate() {
		Configuration conf;
		conf = new Configuration("testUpdate", "testValue");

		configurationDao.update(conf);


		List<Configuration> result = configurationDao.getAll();


		Assert.assertTrue(result.contains(conf));

	}

	@Transactional
	@Test
	public void testGetConfigurationValue() {
		Configuration conf;
		conf = new Configuration("testGetConfigurationValue", "testValue");

		configurationDao.update(conf);
		String result = configurationDao.getConfigurationValue(conf.getKey());
		Assert.assertEquals(result, conf.getValue());
	}

	@Transactional
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
