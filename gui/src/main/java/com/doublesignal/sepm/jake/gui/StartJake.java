package com.doublesignal.sepm.jake.gui;

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
		
		JakeGuiAccess jga = null;
		
		if(args.length >= 1){
			/*String rootfolder = args[0];
			try{
				jga = JakeGuiAccess.openProjectByRootpath(rootfolder);
			} catch (NonExistantDatabaseException e) {
				log.info("Database not found, creating it.");
				try {
					jga = JakeGuiAccess.createNewProjectByRootpath(rootfolder, "testProject");
				} catch (ExistingProjectException e1) {
					UserDialogHelper.error(gui, "Database already exists? That's odd.");
					System.exit(-1);
				} catch (InvalidDatabaseException e1) {
					UserDialogHelper.error(gui, "Database invalid.");
					System.exit(-1);
				} catch (SQLException e1) {
					e.printStackTrace();
					UserDialogHelper.error(gui, "An unexpected error occured. (SQLException)");
					System.exit(-1);
				}
			} catch (InvalidDatabaseException e) {
				UserDialogHelper.error(gui, "Database invalid.");
				System.exit(-1);
			} catch (InvalidRootPathException e) {
				UserDialogHelper.error(gui, "Folder path invalid.");
				System.exit(-1);
			}catch (NullPointerException e){
				e.printStackTrace();
			}
			gui.setJakeGuiAccess(jga);*/
		}else{
			JakeGui.showSelectProjectDialog();
		}
		log.debug("loading done.");
	}
}
