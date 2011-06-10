package com.jakeapp.violet.actions.project;

import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;
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
public class FileInfoAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(FileInfoAction.class);
	private Collection<JakeObject> toDelete;
	private ProjectModel model;
	private boolean trash;
	private int totalSteps = toDelete.size() + 1;
	private int stepsDone = 1;
	private Collection<JakeObject> files;

	public FileInfoAction(ProjectModel model, Collection<JakeObject> files) {
		this.model = model;
		this.files = files;
		this.getListener().statusUpdate(stepsDone * 1. / totalSteps, "init");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		IFSService fss = this.model.getFss();
		Log log = this.model.getLog();

		for (JakeObject fo : toDelete) {
			LogEntry lastVersionLogEntry = null;
			try {
				lastVersionLogEntry = log.getLastOfJakeObject(fo, true);
			} catch (NoSuchLogEntryException e) {
			}
			boolean objectExistsLocally = fss.fileExists(fo.getRelPath());
			boolean checksumDifferentFromLastNewVersionLogEntry = true;
			String loghash = null;
			if (lastVersionLogEntry != null) {
				loghash = lastVersionLogEntry.getHow();
			}
			if (loghash == null)
				loghash = "";
			String fshash = null;
			if (objectExistsLocally) {
				fss.calculateHashOverFile(fo.getRelPath());
			}
			if (fshash == null)
				fshash = "";
			checksumDifferentFromLastNewVersionLogEntry = !loghash
					.equals(loghash);

			boolean hasUnprocessedLogEntries = log.getUnprocessed(fo).isEmpty();

			long lastModificationDate = 0;
			lastModificationDate = fss.getLastModified(fo.getRelPath());
			long size = 0;

			if (objectExistsLocally) {
				size = fss.getFileSize(fo.getRelPath());
			} else {
				// TODO: We don't know the file size when it's remote.
				size = 0;
			}

			new Attributed(fo, lastVersionLogEntry, objectExistsLocally,
					checksumDifferentFromLastNewVersionLogEntry,
					hasUnprocessedLogEntries, lastModificationDate, size);
			stepsDone++;
			this.getListener().statusUpdate(stepsDone * 1. / totalSteps,
					"working");
		}

		return null;
	}

}
