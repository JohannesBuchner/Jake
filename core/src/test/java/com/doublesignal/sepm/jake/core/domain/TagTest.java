package com.doublesignal.sepm.jake.core.domain;

import org.junit.Test;
import org.junit.Assert;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException;

/**
 * @author domdorn
 */
public class TagTest {

    /**
     * correctNameTest
     * Create a Tag with a valid name
     * name: SomethingUsefull
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
     * nameWithWitespaceTest
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
        new Tag(tagname);
    }



	@Test(expected = InvalidTagNameException.class)
	public void nullNameTest() throws InvalidTagNameException
	{
		new Tag(null);
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


	@Test
	public void tooLongTagTest()
	{
		String tooLongName = "dasistdashausvomnikolaus127893X"; // 31 characters
		try {
			new Tag(tooLongName);
		} catch (InvalidTagNameException e) {
			Assert.fail("InvalidTagNameException");
		}
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
