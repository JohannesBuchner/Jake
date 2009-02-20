package com.jakeapp.core;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.remoting.rmi.RmiServiceExporter;

import java.util.Scanner;
import java.rmi.RemoteException;


/**
 * This class is the main entry point for jake CORE (server mode) respectively
 * the jake CORE &amp; GUI mode.
 */
public class Jake {

    private static Logger log = Logger.getLogger(Jake.class);

    public static void main(String[] args) {

        System.out.println("Welcome to Jake-Core daemon.");
        System.out.println("This daemon does not provide any user-interface (neither GUI nor console-ui).");
        System.out.println("If you want to have a user-interface use the swing-gui component or JakeCommander.");



        Scanner in = new Scanner(System.in);




        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                new String[]
                        {
                                "/com/jakeapp/core/applicationContext-rmi.xml"
                        });


        System.out.println("RMI Server up. Hit enter to close");
        if(in.hasNextLine());
            in.nextLine();
      

        System.out.println("Server is shuting down.");

        RmiServiceExporter exporter = (RmiServiceExporter) applicationContext.getBean("rmiExporter");
        try {
            exporter.destroy();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
    }
}
