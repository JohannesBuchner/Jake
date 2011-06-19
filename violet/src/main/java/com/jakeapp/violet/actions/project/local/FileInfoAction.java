package com.jakeapp.violet.actions.project.local;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.StatusUpdate;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAFileException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.attributes.Attributed;
import com.jakeapp.violet.model.exceptions.NoSuchLogEntryException;

/**
 * <code>AvailableLaterObject</code> fetching information about some
 * <code>FileObjects</code>
 */
public class FileInfoAction extends AvailableLaterObject<List<Attributed>> {

	private static final Logger log = Logger.getLogger(FileInfoAction.class);

	private Collection<JakeObject> files;

	private ProjectModel model;

	private int totalSteps;

	private int stepsDone;

	public FileInfoAction(ProjectModel model, Collection<JakeObject> files) {
		this.model = model;
		this.files = files;
		stepsDone = 1;
		totalSteps = files.size() + 1;
		this.setStatus(new StatusUpdate(totalSteps * 1. / stepsDone, "init"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Attributed> calculate() throws Exception {
		IFSService fss = this.model.getFss();
		Log log = this.model.getLog();
		List<Attributed> attributed = new ArrayList<Attributed>();

		for (JakeObject fo : files) {
			attributed.add(AttributedCalculator.calculateAttributed(fss, log,
					fo));

			stepsDone++;
			this.setStatus(new StatusUpdate(totalSteps * 1. / stepsDone,
					"working"));
		}

		return attributed;
	}
}
