package com.doublesignal.sepm.jake.core.services;

import org.junit.Test;
import org.junit.Assert;

import com.doublesignal.sepm.jake.core.domain.Project;
import junit.framework.TestCase;

/**
 * @author johannes, philipp
 */
public class JakeGuiAccessTest extends TestCase{
	
	String validUsername = "validusername@domain.com";
	
	
	public void test() throws Exception{
	}
	
	
	/**
	 * Test if Project Member can be added to a Project
	 */
	@Test
	public void addProjectMemberTest() {
		
		
		JakeGuiAccess jga = new JakeGuiAccess();
		
		jga.addProjectMember(validUsername);
		Project p = jga.getProject();
		Assert.assertTrue(validUsername.equals(p.getMembers().get(p.getMembers().size()).getUserId()));
		
	}
	
}
