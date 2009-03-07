package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.synchronization.attributes.Attributed;
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

	public static String getPath(Attributed<FileObject> afo) {
		return getPath(afo.getJakeObject());
	}

	/**
	 * Get File Size Human Readable (MB, KB, ...)
	 * Do not use until you want to resolve conflicts - usually returns the same as std. call!
	 *
	 * @param afo: FileObject
	 * @return human readable file size.
	 */
	public static String getSizeHR(Attributed<FileObject> afo) {
		return FileUtilities.getSize(afo.getSize(), 1, false);
	}

	/**
	 * Get File Size Human Readable of local FileObject version (MB, KB, ...)
	 *
	 * @param fo: FileObject
	 * @return human readable file size.
	 */
	public static String getLocalSizeHR(FileObject fo) {
		return FileUtilities
						.getSize(JakeMainApp.getCore().getLocalFileSize(fo), 1, false);
	}


	/**
	 * Returns the realtive Time of the last edit for the FileObject. ("a minute ago", ...)
	 *
	 * @param afo: AttributedJakeObject<FileObject>
	 * @return relative time for last edit of file
	 */
	public static String getTimeRel(Attributed<FileObject> afo) {
		return TimeUtilities.getRelativeTime(afo.getLastModificationDate());
	}

	/**
	 * Returns the realtive Time of the last edit for the local version of FileObject. ("a minute ago", ...)
	 * Do not use until you want to resolve conflicts - usually returns the same as std. call!
	 *
	 * @param afo: AttributedJakeObject<FileObject>
	 * @return relative time for last edit of file
	 */
	public static String getLocalTimeRel(Attributed<FileObject> afo) {
		return TimeUtilities.getRelativeTime(
						JakeMainApp.getCore().getLocalFileLastModified(afo.getJakeObject()));
	}

	/**
	 * Returns the tile of the last edit for the FileObject, absolut.
	 *
	 * @param afo: AttributedJakeObject<FileObject>
	 * @return absolute time for last edit of file
	 */
	public static String getTime(Attributed<FileObject> afo) {
		DateFormat df = new SimpleDateFormat();
		return df.format(afo.getLastModificationDate());
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
		return df.format(JakeMainApp.getCore().getLocalFileLastModified(fo));
	}

	/**
	 * Returns the last modifier for a FileObject as Nick/Full Name
	 *
	 * @param afo: FileObject
	 * @return string of nick/full name.
	 */
	public static String getLastModifier(Attributed<FileObject> afo) {
		return UserHelper.getNickOrFullName(afo.getLastVersionEditor());
	}
}
