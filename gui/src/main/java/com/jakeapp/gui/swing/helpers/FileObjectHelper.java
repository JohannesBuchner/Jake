package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author: studpete
 */
public class FileObjectHelper {
	private static final Logger log = Logger.getLogger(FileObjectHelper.class);

	/**
	 * Returns the name of a file.
	 *
	 * @param file
	 * @return
	 */
	public static String getName(File file) {
		return file.getName();
	}

	/**
	 * Returns the name of a file.
	 *
	 * @param file
	 * @return
	 */
	public static String getName(String file) {
		return getName(new File(file));
	}

	/**
	 * Returns the path of a file (without name)
	 *
	 * @param file
	 * @return
	 */
	public static String getPath(File file) {
		return getPath(file.getPath());
	}

	/**
	 * Returns the Path of a file (without name)
	 *
	 * @param fo: The jake fileobject
	 * @return string of file path (without name) OR ""
	 */
	public static String getPath(FileObject fo) {
		try {
			return getPath(JakeMainApp.getCore().getFile(fo));
		} catch (FileOperationFailedException e) {
			ExceptionUtilities.showError(e);
			return "";
		}
	}

	public static String getPath(String path) {
		return FileUtilities.getPathFromPathWithFile(path);
	}

	/**
	 * Get File Size Human Readable (MB, KB, ...)
	 * Do not use until you want to resolve conflicts - usually returns the same as std. call!
	 *
	 * @param fo: FileObject
	 * @return human readable file size.
	 */
	public static String getSizeHR(FileObject fo) {
		return FileUtilities.getSize(JakeMainApp.getCore().getLocalFileSize(fo), 1, false);
	}

	/**
	 * Get File Size Human Readable of local FileObject version (MB, KB, ...)
	 *
	 * @param fo: FileObject
	 * @return human readable file size.
	 */
	public static String getLocalSizeHR(FileObject fo) {
		return FileUtilities.getSize(JakeMainApp.getApp().getCore().getFileSize(fo), 1, false);
	}


	/**
	 * Returns the realtive Time of the last edit for the FileObject. ("a minute ago", ...)
	 *
	 * @param fo: FileObject
	 * @return relative time for last edit of file
	 */
	public static String getTimeRel(FileObject fo) {
		return TimeUtilities.getRelativeTime(JakeMainApp.getApp().getCore().getFileLastModified(fo));
	}

	/**
	 * Returns the realtive Time of the last edit for the local version of FileObject. ("a minute ago", ...)
	 * Do not use until you want to resolve conflicts - usually returns the same as std. call!
	 *
	 * @param fo: FileObject
	 * @return relative time for last edit of file
	 */
	public static String getLocalTimeRel(FileObject fo) {
		return TimeUtilities.getRelativeTime(JakeMainApp.getApp().getCore().getLocalFileLastModified(fo));
	}

	/**
	 * Returns the tile of the last edit for the FileObject, absolut.
	 *
	 * @param fo: FileObject
	 * @return absolute time for last edit of file
	 */
	public static String getTime(FileObject fo) {
		DateFormat df = new SimpleDateFormat();
		return df.format(JakeMainApp.getApp().getCore().getFileLastModified(fo));
	}

	/**
	 * Returns the tile of the last edit for the local version of FileObject, absolut.
	 * Do not use until you want to resolve conflicts - usually returns the same as std. call!
	 *
	 * @param fo: FileObject
	 * @return absolute time for last edit of file
	 */
	public static String getLocalTime(FileObject fo) {
		DateFormat df = new SimpleDateFormat();
		return df.format(JakeMainApp.getApp().getCore().getLocalFileLastModified(fo));
	}

	/**
	 * Returns the last modifier for a FileObject as Nick/Full Name
	 *
	 * @param fo: FileObject
	 * @return string of nick/full name.
	 */
	public static String getLastModifier(FileObject fo) {
		try {
			return UserHelper.getNickOrFullName(JakeMainApp.getApp().getCore().getLastModifier(fo));
		} catch (NoSuchLogEntryException e) {
			log.warn("No log entry found for " + fo, e);
			return "<no log found>";
		}
	}
}
