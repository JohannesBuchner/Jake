package com.jakeapp.core;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * This class is the main entry point for jake CORE (server mode) respectively
 * the jake CORE &amp; GUI mode.
 */
public class Jake {

    private static Logger log = Logger.getLogger(Jake.class);

    public static void main(String[] args) {

        boolean console = false;
        boolean gui = true;


        if (args.length > 0) {
            for (String argument : args) {
                if (argument.startsWith("--nogui")) {
                    log.debug("set the dedicated nogui option");
                    console = true;
                }


                if (argument.startsWith("--help")) {
                    console = false;
                    gui = false;


                    log.debug("printing help screen");

                    System.out.println("Welcome to jake. Here are your options:");
                    System.out.println("\n\n");

                }
            }
        }


        if (console || gui) {
            log.debug("either gui or console mode selected");

            if (console) {
                log.debug("Load main application context");
                log.debug("selected console");
                
                ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                        new String[]{"/com/jakeapp/core/applicationContext.xml"});




            }

            if(gui)
            {
                log.debug("Load main application context");
                log.debug("selected gui");

//                ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
//                        new String[]{"/com/jakeapp/core/applicationContext.xml"});

//                JakeGuiStartHelper.launch((ICoreAccess) applicationContext.getBean("coreAccess"));
                
                //JakeMainApp app = (JakeMainApp) applicationContext.getBean("swingGui");
               // JakeMainApp.main(null);
                //app.setCore((ICoreAccess) applicationContext.getBean("coreAccess"));

                //app.startGui();

            }

        }


    }
}
