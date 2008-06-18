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
		log.info(translator.get("ConsoleWelcome"));
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
