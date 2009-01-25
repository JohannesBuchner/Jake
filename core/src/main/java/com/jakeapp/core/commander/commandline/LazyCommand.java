package com.jakeapp.core.commander.commandline;

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

	public abstract boolean handleArguments(String[] args);
}
