package com.jakeapp.core.services;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"/com/jakeapp/core/applicationContext.xml"})
public class SpringBeansTest extends AbstractJUnit4SpringContextTests {


    @Test
    public void testCreateFrontendService()
    {

        IFrontendService frontendService = (IFrontendService) this.applicationContext.getBean("frontendService");
        if(frontendService == null)
            Assert.fail();
    }

}
