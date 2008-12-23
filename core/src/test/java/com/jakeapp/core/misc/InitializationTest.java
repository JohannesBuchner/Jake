package com.jakeapp.core.misc;

import org.junit.Test;
import org.apache.log4j.Logger;

/**
 * TODO: Fill in purpose of this file
 * User: Dominik
 * Date: Dec 11, 2008
 * Time: 12:16:41 AM
 * Module: ${MAVEN-MODULE-NAME}
 * Version: ${MAVEN-VERSION}
 */
public class InitializationTest {

    private static Logger log = Logger.getLogger(InitializationTest.class);

    private Object someObject;
    private String someString;
    private int someInt;
    private byte someByte;

    public Object getSomeObject()
    {
        return this.someObject;
    }

    public String getSomeString()
    {
        return this.someString;
    }

    public int getSomeInt()
    {
        return this.someInt;
    }


    public byte getSomeByte()
    {
        return this.someByte;
    }

    /**
     * This test shows that an unitialized object just returns null if someone tries to access it
     */
    @Test
    public void getSomeObject_simpleTest()
    {
        log.debug("getSomeObject() = " + getSomeObject());
    }


    /**
     * This test shows, that an unitialized String behavies like an unitialized object
     */
    @Test
    public void getSomeString_simpleGetTest()
    {
        log.debug("this.getSomeString() = " + this.getSomeString());
    }

    /**
     * This test shows, that an unitialized primitive data (int) type simply returns 0 if accessed.
     */
    @Test
    public void getSomeInt_simpleGetTest()
    {
        log.debug("this.getSomeInt() = " + this.getSomeInt());   
    }                                         

    /**
     * This test shows, that an unitialized primitive data (int) type simply returns 0 if accessed.
     */
    @Test
    public void getSomeByte_siimpleGetTest()
    {
        log.debug("this.getSomeByte() = " + this.getSomeByte());
    }

}
