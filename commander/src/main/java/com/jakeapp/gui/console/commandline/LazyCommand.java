package com.jakeapp.gui.console.commandline;

public abstract class LazyCommand extends Command {
	private String command;
	private String syntax;
	private String help;

	public LazyCommand(String command) {
		this(command, command, "");
	}
	public LazyCommand(String command, String syntax) {
		this(command, syntax, "");
	}
	public LazyCommand(String command, String syntax, String help) {
		this.command = command;
		this.syntax = syntax;
		this.help = help;
	}

	public String getHelpString() {
		return help;
	}

	@Override
	public String getSyntaxString() {
		return syntax;
	}

	@Override
	public boolean handleLine(String line) {
		String[] arguments = line.split(" ");
		if (line.equals(command))
			return handleArguments(arguments);
		if (!line.startsWith(command + " "))
			return false;
		return handleArguments(arguments);
	}

	/**
	 * @param args parameters. first element is the command name (has been verified already)
	 * @return is this the right command? false if you are not the right command. true if the 
	 * 		command got handled (independent of success)   
	 */
	public abstract boolean handleArguments(String[] args);
}
