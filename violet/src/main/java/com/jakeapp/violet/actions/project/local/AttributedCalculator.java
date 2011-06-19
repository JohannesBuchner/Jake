package com.jakeapp.violet.actions.project.local;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import com.jakeapp.jake.fss.HashValue;
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
			JakeObject fo) throws SQLException, InvalidFilenameException {
		LogEntry lastVersionLogEntry = null;
		try {
			lastVersionLogEntry = log.getLastOfJakeObject(fo, true);
		} catch (NoSuchLogEntryException e) {
		}
		boolean checksumDifferentFromLastNewVersionLogEntry = true;
		String loghash = null;
		if (lastVersionLogEntry != null) {
			loghash = lastVersionLogEntry.getHow();
		}
		if (loghash == null)
			loghash = "";
		HashValue fshash = null;
		boolean objectExistsLocally = false;
		long lastModificationDate = 0;
		long size = 0;
		try {
			objectExistsLocally = fss.fileExists(fo.getRelPath());
			lastModificationDate = fss.getLastModified(fo.getRelPath());
			size = fss.getFileSize(fo.getRelPath());

			fshash = fss.calculateHashOverFile(fo.getRelPath());
			checksumDifferentFromLastNewVersionLogEntry = loghash
					.equals(fshash);
		} catch (FileNotFoundException e) {
		} catch (NotAReadableFileException e) {
		} catch (IOException e) {
		}

		boolean hasUnprocessedLogEntries = log.getUnprocessed(fo).isEmpty();

		return new Attributed(fo, lastVersionLogEntry, objectExistsLocally,
				checksumDifferentFromLastNewVersionLogEntry,
				hasUnprocessedLogEntries, lastModificationDate, size);
	}
}
