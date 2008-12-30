package com.jakeapp;

import org.junit.Test;


/**
 * Constants for Testing-Constraints.
 * @author Djinn
 */
public class TestingConstants {
	/**
	 * Maximal amount of time (in milliseconds) one unittest should run.
	 */
	public static final int UNITTESTTIME = 1000;
	
	public TestingConstants() {
		
	}


    /**
     * this is here to prevent the
     * initializationError0(com.jakeapp.TestingConstants)
     * error. If someone knows a better way, please tell us.
     */
    @Test
    public void defaultTest()
    {
        
    }
}
