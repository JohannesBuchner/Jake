package com.doublesignal.sepm.jake.gui;

import java.io.File;
import java.sql.SQLException;

import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.ExistingProjectException;
import com.doublesignal.sepm.jake.core.services.exceptions.InvalidDatabaseException;
import com.doublesignal.sepm.jake.core.services.exceptions.InvalidRootPathException;
import com.doublesignal.sepm.jake.core.services.exceptions.NonExistantDatabaseException;

import org.apache.log4j.Logger;

/**
 * @author domdorn
 */
public class StartJake
{
	private static Logger log = Logger.getLogger(StartJake.class); 
	public static void main(String[] args)
	{
		log.info("===================================================");
		log.info("Welcome to Jake - a collaborative Environment");

		log.info("We're happy you're using our software.");
		log.info("For any acknowledgments, gifts or what else you ");
		log.info("want to give us, feel free to send us an email!");

		log.info("===================================================");
		
		log.debug("starting frontend....");
		
		String tmpdir = System.getProperty("java.io.tmpdir","") + File.separator;
		
		
		File rootPath = new File(tmpdir, "testProject");
		File jakeFile = new File(tmpdir, "testProject" + ".script");
		jakeFile.delete();
		rootPath.mkdir();
		String rootfolder = rootPath.getAbsolutePath();
		if(!(rootPath.exists() && rootPath.isDirectory()))
			return;
		try {
			new JakeGui(JakeGuiAccess.openProjectByRootpath(rootfolder));
		} catch (NonExistantDatabaseException e) {
			e.printStackTrace();
			try {
				new JakeGui(JakeGuiAccess.createNewProjectByRootpath(rootfolder, "testProject"));
			} catch (ExistingProjectException e1) {
				e1.printStackTrace();
			} catch (InvalidDatabaseException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (InvalidDatabaseException e) {
			e.printStackTrace();
		} catch (InvalidRootPathException e) {
			e.printStackTrace();
		}
		log.debug("loading done.");
	}
}
