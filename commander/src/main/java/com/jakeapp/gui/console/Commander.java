package com.jakeapp.gui.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.core.synchronization.IFriendlySyncService;
import com.jakeapp.core.util.SpringThreadBroker;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.gui.console.commandline.CmdManager;
import com.jakeapp.gui.console.commandline.Command;
import com.jakeapp.gui.console.commandline.LazyCommand;
import com.jakeapp.gui.console.commandline.StoppableCmdManager;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

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
