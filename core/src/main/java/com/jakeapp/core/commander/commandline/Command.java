package com.jakeapp.core.commander.commandline;

public abstract class Command {
	public abstract boolean handleLine(String line);

	public abstract String getHelpString();

	public abstract String getSyntaxString();
}
