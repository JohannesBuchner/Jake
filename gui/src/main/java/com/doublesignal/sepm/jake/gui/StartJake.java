package com.doublesignal.sepm.jake.gui;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

/**
 * @author domdorn
 */
public class StartJake
{
	private static Logger log = Logger.getLogger(StartJake.class); 
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
	
	public static void main(String[] args)
	{
		log.info("===================================================");
		log.info("Welcome to Jake - a collaborative Environment");

		log.info("We're happy you're using our software.");
		log.info("For any acknowledgments, gifts or what else you ");
		log.info("want to give us, feel free to send us an email!");

		log.info("===================================================");
		
		log.debug("starting frontend....");
		
		JakeGui.setSystemProperties();
		JakeGui.setNativeLookAndFeel();
		if(args.length==0){
			JakeGui.showSelectProjectDialog(null);
		}else{
			JakeGui.showSelectProjectDialog(args[0]);
		}
		
		log.debug("loading done.");
	}
}
