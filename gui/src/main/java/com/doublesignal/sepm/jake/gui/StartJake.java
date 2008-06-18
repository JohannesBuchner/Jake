package com.doublesignal.sepm.jake.gui;

import java.io.IOException;

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
		if(args[0].startsWith("--lang=")){
			String file = args[0].substring("--lang=".length());
			try{
				translator.setLanguage(file);
			}catch(IOException e){
				log.fatal("language file " + file + " not found");
				return -1;
			}
		}
		JakeGui.setSystemProperties();
		JakeGui.setNativeLookAndFeel();
		if(args.length==0){
			JakeGui.showSelectProjectDialog(null);
		}else{
			JakeGui.showSelectProjectDialog(args[args.length - 1]);
		}
		
		log.debug("loading done.");
	}
}
