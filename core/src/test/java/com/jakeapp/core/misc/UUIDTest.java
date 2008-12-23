package com.jakeapp.core.misc;

import org.junit.Test;
import org.apache.log4j.Logger;

import java.util.UUID;

/**
 * This test class is used to figure out how the java implementation of UUIDs work
 * User: Dominik
 * Date: Dec 11, 2008
 * Time: 12:08:54 AM
 * Module: ${MAVEN-MODULE-NAME}
 * Version: ${MAVEN-VERSION}
 */
public class UUIDTest {
    private static Logger log = Logger.getLogger(UUIDTest.class);

    /**
     * This test simply tries to create ten new UUIDs to find out how this stuff works
     */
    @Test
    public void randomUUID_simpleTest()
    {

        for(int i = 0; i < 20; i++)
        {
            log.debug("UUID.randomUUID().toString() = " + UUID.randomUUID().toString());
        }
    }


    /**
     * This test creates an UUID from a given String representation
     */
    @Test
    public void createUUIDfromGivenString()
    {
        String uuidString = "2391847a-4753-4225-bee5-da01862993fc";
        UUID uuid = UUID.fromString(uuidString);
        assert(uuid.toString().equals(uuidString));
    }

}
