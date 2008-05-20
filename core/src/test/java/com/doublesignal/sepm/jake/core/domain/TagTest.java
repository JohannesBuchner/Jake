package com.doublesignal.sepm.jake.core.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 18, 2008
 * Time: 3:54:50 AM
 */
public class TagTest {

    @Before
    public void Setup() throws Exception {
        // nothing to setup here
    }

    @After
    public void TearDown() throws Exception {
        // nopthint to tear down here
    }


    /**
     * correctNameTest
     * Create a Tag with a valid name
     * namee: SomethingUsefull
     * expected result: Tag object with correct name
     *
     * @author Dominik
     * @Date 2008-05-18
     * @revision 1
     */
    @Test
    public void correctNameTest() {
        String tagname = "SomethignUsefull";
        try {

            Tag tmpTag = new Tag(tagname);
            Assert.assertTrue(tmpTag.getName().equals(tagname));
        }
        catch (Exception e) {
            Assert.fail();
        }
    }


    /**
     * correctNameTest
     * Create a Tag with an invalid name containing whitespaces
     * namee: "Some tag with withspace"
     * expected result: Exception InvalidTagNameException gets thrown
     *
     * @author Dominik
     * @Date 2008-05-18
     * @revision 1
     */
    @Test(expected = InvalidTagNameException.class)
    public void nameWithWitespaceTest() throws InvalidTagNameException 
    {
        String tagname = "Some tag with withspace";
        Tag tmpTag = new Tag(tagname);
    }



	@Test(expected = InvalidTagNameException.class)
	public void nullNameTest() throws InvalidTagNameException
	{
		new Tag(null);
	}


	@Test(expected = InvalidTagNameException.class)
	public void emptyTagTest() throws InvalidTagNameException
	{
		new Tag(""); // minimum length is 1 character
	}

	@Test
	public void maximumLengthTagTest()
	{
		try
		{
			new Tag("dasistdashausvomnikolaus127893");
		}
		catch (Exception e)
		{
			Assert.fail("Tag should be able to handle 30 characters");
		}
	}


	@Test(expected = InvalidTagNameException.class)
	public void tooLongTagTest() throws InvalidTagNameException
	{
		String tooLongName = "dasistdashausvomnikolaus127893X"; // 31 characters
		new Tag(tooLongName);
	}


	@Test
	public void toStringTest()
	{
		String sampleTag = "sampleTag";

		try
		{
			Tag t = new Tag(sampleTag);
			Assert.assertTrue(t.toString().equals(sampleTag));
		}
		catch (InvalidTagNameException e)
		{
			Assert.fail("shouldn't fail with a valid tagname");
		}

	}


	
	


	// TODO check equals()
	// TODO check hashCode()

}
