package com.jakeapp.gui.console.commandline;


public class StoppableCmdManager {

	public static CmdManager getInstance() {
		return getInstance("stop");
	}

	public static CmdManager getInstance(Runnable extraShutdownCode) {
		return getInstance("stop", extraShutdownCode);
	}

	public static CmdManager getInstance(final String stopcommand) {
		return getInstance(stopcommand, null);
	}

	public static CmdManager getInstance(final String stopcommand,
			final Runnable extraShutdownCode) {
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
					if (extraShutdownCode != null)
						extraShutdownCode.run();
					return true;
				}
				return false;
			}
		});
		return m;
	}
}
