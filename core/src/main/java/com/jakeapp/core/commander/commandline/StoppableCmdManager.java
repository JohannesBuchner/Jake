package com.jakeapp.core.commander.commandline;


public class StoppableCmdManager {
	
	public static CmdManager getInstance(){
		return getInstance("stop");
	}
	public static CmdManager getInstance(final String stopcommand){
		final CmdManager m = new CmdManager();
		m.registerCommand(new Command() {
			@Override
			public String getHelpString() {
				return "shut down";
			}
	
			@Override
			public String getSyntaxString() {
				return stopcommand;
			}
	
			@Override
			public boolean handleLine(String line) {
				if (line.equals(stopcommand)) {
					System.err.println("User requested shutdown");
					m.stop();
					return true;
				}
				return false;
			}
		});
		return m;
	}
}
