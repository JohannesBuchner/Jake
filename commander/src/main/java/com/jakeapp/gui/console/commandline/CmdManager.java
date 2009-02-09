package com.jakeapp.gui.console.commandline;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.List;

public class CmdManager {
	List<Command> commands = new LinkedList<Command>();

	boolean status_ok = true;
	
	public CmdManager(){
		commands.add(new Command(){
			@Override
			public String getHelpString() {
				return "get this clutter";
			}

			@Override
			public String getSyntaxString() {
				return "help | ?";
			}

			@Override
			public boolean handleLine(String line) {
				if(line.equals("help") || line.equals("?")){
					help();
					return true;
				}
				return false;
			}
			
		});
	}
	
	public void registerCommand(Command c) {
		commands.add(c);
	}

	public void handle(InputStream in) throws IOException {
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(in));
		status_ok = true;
		while (status_ok) {
			String line = lnr.readLine();
			if(line == null)
				break;
			handleLine(line.trim());
		}
	}

	public void handleLine(String line) {
		for (Command c : commands) {
			if (c.handleLine(line))
				return;
		}
		System.out.println("Unknown command: " + line);
		help();
	}

	public void help() {
		System.out.println("SYNTAX: ");
		for (Command c : commands) {
			System.out.println("\t" + c.getSyntaxString());
			System.out.println("\t\t" + c.getHelpString());
		}
	}
	
	public void stop() {
		status_ok = false;
	}
}
