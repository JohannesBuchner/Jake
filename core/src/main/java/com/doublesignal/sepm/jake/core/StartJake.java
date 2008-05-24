package com.doublesignal.sepm.jake.core;

import com.doublesignal.sepm.jake.gui.JakeGui;


/**
 * @author domdorn
 */
public class StartJake
{
	public static void main(String[] args)
	{
		System.out.println("==================================================================================");
		System.out.println("Welcome to Jake - a collaborative Environment\n\n");

		System.out.println("We're happy you're using our software.");
		System.out.println("For any acknowledgments, gifts or what else you want to give us,\n"
		+"feel free to send us an email! \n\n");

		System.out.println("==================================================================================\n\n");


		System.out.println("starting frontend....");

		// hier kommt noch eine art getOpt() hin, mit der man per kommandozeile (theoretisch) verschiedene frontends
		// laden koennte. auf jeden fall sollte das gui mit dependency injection geladen werden und nicht so statisch
		// wie jetzt.

		new JakeGui();

		System.out.println("some other output after gui thread started");
	}
}
