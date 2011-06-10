package com.jakeapp.violet.actions.project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAFileException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.attributes.Attributed;
import com.jakeapp.violet.model.exceptions.NoSuchLogEntryException;

public class AttributedCalculator {
	public static Attributed calculateAttributed(IFSService fss, Log log,
			JakeObject fo) throws SQLException, InvalidFilenameException,
			IOException, NotAReadableFileException, FileNotFoundException,
			NotAFileException {
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
		checksumDifferentFromLastNewVersionLogEntry = !loghash.equals(loghash);

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

		return new Attributed(fo, lastVersionLogEntry, objectExistsLocally,
				checksumDifferentFromLastNewVersionLogEntry,
				hasUnprocessedLogEntries, lastModificationDate, size);
	}
}
