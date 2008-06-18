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
		int i = args.length;
		if(i>1 && args[0].startsWith("--lang")){
			try {
				System.out.println("setting language to " + args[1]);
				translator.setLanguage(args[1]);
			}catch(IOException e) {
				log.fatal("language file " + args[1] + " not found");
				System.err.println("language file " + args[1] + " not found");
				System.exit(-1);
			}
			i = i - 2;
		}else{
			System.err.println("using default language");
		}
		JakeGui.setSystemProperties();
		JakeGui.setNativeLookAndFeel();
		if(i==0){
			JakeGui.showSelectProjectDialog(null);
		}else{
			JakeGui.showSelectProjectDialog(args[i - 1]);
		}
		
		log.debug("loading done.");
	}
}
