package com.jakeapp.core.commander.commandline;

public abstract class Command {
	/**
	 * @param line full input line
	 * @return is this the right command? false if you are not the right command. true if the 
	 * 		command got handled (independent of success)   
	 */
	public abstract boolean handleLine(String line);

	public abstract String getHelpString();

	public abstract String getSyntaxString();
}
