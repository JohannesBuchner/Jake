package com.jakeapp.gui.console;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;

import com.jakeapp.gui.console.commandline.CmdManager;
import com.jakeapp.gui.console.commandline.Command;
import com.jakeapp.gui.console.commandline.StoppableCmdManager;

public abstract class Commander {

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(Commander.class);

	private final CmdManager cmd = StoppableCmdManager.getInstance(new Runnable() {

		@Override
		public void run() {
			Commander.this.onShutdown();
		}
		
	});

	public Commander(String[] args) {
		boolean help = false;
		InputStream instream;
		if (args.length == 1) {
			try {
				instream = new FileInputStream(args[0]);
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
				return;
			}
		} else {
			instream = System.in;
			help = true;
		}
		this.run(instream, help);
	}

	/**
	 * don't do anything and wait for run()
	 */
	public Commander() {
	}

	abstract protected void onShutdown();

	abstract protected void onStartup();

	protected void run(InputStream instream) {
		this.run(instream, false);
	}

	protected void run(InputStream instream, boolean startwithhelp) {
		this.onStartup();
		addCommands();
		try {
			if (startwithhelp)
				cmd.help();

			cmd.handle(instream);
		} catch (IOException e) {
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addCommands() {
		// we are so cool, we use reflection
		for (Class<?> c : this.getClass().getDeclaredClasses()) {
			Command command;
			try {
				Constructor<Command> constructor = (Constructor<Command>) c
						.getConstructor(this.getClass());
				command = constructor.newInstance(this);
			} catch (Exception e) {
				continue;
			}
			this.cmd.registerCommand(command);
		}
	}
}
