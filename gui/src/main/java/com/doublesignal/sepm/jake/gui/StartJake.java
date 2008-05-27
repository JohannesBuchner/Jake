package com.doublesignal.sepm.jake.gui;

import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
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



		log.info("starting frontend....");
		JakeGui gui = new JakeGui();
		log.info("should set JakeGUIAccess now");
		IJakeGuiAccess jakeGuiAccess = new JakeGuiAccess();
		System.out.println("test");
		gui.setJakeGuiAccess(jakeGuiAccess);
		System.out.println("test2");

		System.out.println("some other output after gui thread started");
	}
}
