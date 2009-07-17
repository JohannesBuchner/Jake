package com.jakeapp.core.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jakeapp.TestingConstants;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;


/**
 * @author christopher
 */
public class TagTest {
	
	private Tag t;
	
	@Before
	public void setUp() {
		try {
			t = new Tag("t");
		} catch (InvalidTagNameException e) {
		}
	}

	@Test(timeout = TestingConstants.UNITTESTTIME,expected=InvalidTagNameException.class)
	public void Tag_shouldThrowOnInvalidTagname() throws InvalidTagNameException {
	    /* CALL */
	    new Tag("my new tag");
	    /* CALL */
	}
	
	@Test(timeout = TestingConstants.UNITTESTTIME,expected=InvalidTagNameException.class)
	public void setName_shouldThrowOnInvalidTagname() throws InvalidTagNameException {
	    /* CALL */
	    t.setName("my new tag");
	    /* CALL */
	}
	
	@Test(timeout = TestingConstants.UNITTESTTIME,expected=InvalidTagNameException.class)
	public void setName_shouldThrowOnTagnameContainingWhitespace() throws InvalidTagNameException {
	    /* CALL */
	    t.setName("\t\n");
	    /* CALL */
	}
	
	@Test(timeout = TestingConstants.UNITTESTTIME)
	public void getName_shouldReturntheNameSetBefore() throws InvalidTagNameException {
		final String tagname = "tag"; 
		String retval;
		
	    //Test setup
		t.setName(tagname);

	    /* CALL */
	    retval = t.getName();
	    /* CALL */
	    
	    //Test evaluation
	    assertEquals(retval,tagname);
	}
}
