package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Invitation;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import net.roydesign.ui.FolderDialog;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of Utilities for working with files.
 */
public class FileUtilities {
	private static final Logger log = Logger.getLogger(FileUtilities.class);

	/**
	 * Returns the last folder from a path.
	 *
	 * @param path (e.g. /User/user/foobar))
	 * @return last folder of part (e.g. foobar)
	 */
	public static String getLastFolderFromPath(String path) {
		if (path.indexOf(getPathSeparator()) != -1) {
			return path.substring(path.lastIndexOf(getPathSeparator()) + 1,
					  path.length());
		} else {
			return path;
		}
	}

	/**
	 * Opens the Directory Chooser.
	 *
	 * @param defaultFolder
	 * @param title:        beware! No title is shown when sheets are enabled.
	 * @return
	 */
	public static String openDirectoryChooser(String defaultFolder, String title) {
		log.info("user is choosing a directory");
		// uses the awt native folder dialog on mac
		if (Platform.isMac()) {
			// TODO: can we put that into a sheet?
			FolderDialog fod = new FolderDialog(JakeMainView.getMainView().getFrame(), "Choose Directory");
			if (defaultFolder != null) {
				fod.setFile(defaultFolder);
			}
			if (title != null) {
				fod.setTitle(title);
			}

			fod.setVisible(true);
			return fod.getDirectory();
		} else {
			// falls back to standard swing folder chooser
			// on the other platforms
			// TODO: maybe we can get the native folder chooser for windows?

			JFileChooser fileChooser = new JFileChooser();
			if (defaultFolder != null) {
				fileChooser.setCurrentDirectory(new File(defaultFolder));
			}
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setDialogTitle(title);

			int returnCode = fileChooser.showOpenDialog(null);
			if (returnCode == JFileChooser.APPROVE_OPTION) {
				return fileChooser.getSelectedFile().getAbsolutePath();
			} else {
				return null;
			}
		}
	}

	/**
	 * Returns a new File for the given filename, coping with "~/".
	 * Try not to ever use "new File(String)": use this instead.
	 * @param filename
	 * @return
	 */
	public static File fileFromString(String filename) {
		return new File(FileUtilities.parseUserFriendlyName(filename));
	}

	/**
	 * Returns a default location for the new project.
	 *
	 * @param invitation
	 * @return
	 */
	// fixme: do something when folder already exists!
	public static String getDefaultProjectLocation(Invitation invitation) {
		if (invitation == null) {
			return "";
		}

		JFileChooser fr = new JFileChooser();
		javax.swing.filechooser.FileSystemView fw = fr.getFileSystemView();

		// TODO: make customizeable, cleanup invitation name
		return fw.getDefaultDirectory() + getPathSeparator() + invitation.getProjectName();
	}


	/**
	 * Checks that a name exists and is a directory.
	 * @param name
	 * @return
	 */
	public static boolean checkDirectoryExistence(String name) {
		File proposedDirectory = FileUtilities.fileFromString(name);
		if (!proposedDirectory.exists()) {
			return false;
		} else if (!proposedDirectory.isDirectory()) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the user's home directory. Assumes that on Cygwin a user
	 * who's set $HOME wants it to override Windows' notion of the home
	 * directory, which is what the "user.home" system property gets you.
	 * Removes any trailing File.separators.
	 * @return
	 */
	public static String getUserHomeDirectory() {
		String result = System.getenv("HOME");
		if (result == null) {
			result = System.getProperty("user.home");
		}
		if (result != null && result.endsWith(File.separator)) {
			result = result.replaceAll(File.separator + "+$", "");
		}
		return result;
	}


	/**
	 * Get the Desktop directory
	 * @return
	 */
	public static String getUserDesktopDirectory() {
		return new File(getUserHomeDirectory(), "Desktop").getAbsolutePath();
	}	

	/**
	 * Returns a new File for the given filename, coping with "~/".
	 * Try not to ever use "new File(String, String)": use this instead.
	 * @param parent
	 * @param filename
	 * @return
	 */
	public static File fileFromParentAndString(String parent, String filename) {
		return fileFromString(FileUtilities.parseUserFriendlyName(parent) + File.separator + filename);
	}

	/**
	 * Converts paths of the form ~/src to /Users/elliotth/src (or
	 * whatever the user's home directory is). Also copes with the
	 * special case of ~ on its own, and with ~someone-else/tmp.
	 * @param filename
	 * @return
	 */
	public static String parseUserFriendlyName(String filename) {
		String result = filename;
		if (filename.startsWith("~" + File.separator) || filename.equals("~")) {
			result = getUserHomeDirectory();
			if (filename.length() > 1) {
				result += File.separator + filename.substring(2);
			}
		} else if (filename.startsWith("~")) {
			// Assume that "~user/bin/vi" is equivalent to "~/../user/bin/vi".
			Pattern pattern = Pattern.compile("^~([^" + Pattern.quote(File.separator) + "]+)(.*)$");
			Matcher matcher = pattern.matcher(filename);
			if (matcher.find()) {
				String user = matcher.group(1);
				File home = fileFromString(getUserHomeDirectory());
				File otherHome = fileFromParentAndString(home.getParent(), user);
				if (otherHome.exists() && otherHome.isDirectory()) {
					result = otherHome.toString() + matcher.group(2);
				}
			}
		}
		return result;
	}

	/**
	 * Strips the user's home directory from the beginning of the string
	 * if it's there, replacing it with ~. It would be nice to do other users'
	 * home directories too, but I can't think of a pure Java way to do
	 * that.
	 * Also adds a trailing separator to the name of a directory.
	 * @param filename
	 * @return
	 */
	public static String getUserFriendlyName(String filename) {
		boolean isDirectory = fileFromString(filename).isDirectory();
		if (isDirectory && !filename.endsWith(File.separator)) {
			filename += File.separatorChar;
		}
		String home = getUserHomeDirectory() + File.separator;
		// We can't use startsWith because Windows requires case-insensitivity.
		if (filename.length() >= home.length()) {
			File homeFile = new File(home);
			File file = new File(filename.substring(0, home.length()));
			if (homeFile.equals(file)) {
				return "~" + File.separator + filename.substring(home.length());
			}
		}
		return filename;
	}

	public static String getUserFriendlyName(File file) {
		return getUserFriendlyName(file.getAbsolutePath());
	}

	/**
	 * Returns a temporary file whose name begins with 'prefix'.
	 * The file will be deleted on exit.
	 * On error, a RuntimeException is thrown which will refer to the file using 'humanReadableName'.
	 * @param prefix
	 * @param humanReadableName
	 * @return
	 */
	public static File createTemporaryFile(String prefix, String humanReadableName) {
		try {
			File file = File.createTempFile(prefix, null);
			file.deleteOnExit();
			return file;
		} catch (IOException ex) {
			throw new RuntimeException("Couldn't create " + humanReadableName + ": " + ex.getMessage());
		}
	}

	/**
	 * Creates a temporary file containing 'content' where the temporary file's name begins with 'prefix'.
	 * The file will be deleted on exit.
	 * Returns the name of the temporary file.
	 * On error, a RuntimeException is thrown which will refer to the file using 'humanReadableName'.
	 * @param prefix
	 * @param humanReadableName
	 * @param content
	 * @return
	 */
	public static String createTemporaryFile(String prefix, String humanReadableName, String content) {
		try {
			File file = createTemporaryFile(prefix, humanReadableName);
			PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			out.print(content);
			out.close();
			return file.toString();
		} catch (IOException ex) {
			throw new RuntimeException("Couldn't write " + humanReadableName + ": " + ex.getMessage());
		}
	}


	public static void copyFile(File source, File destination) {
		try {
			// From http://java.sun.com/developer/JDCTechTips/2002/tt0507.html.
			FileInputStream fileInputStream = new FileInputStream(source);
			FileOutputStream fileOutputStream = new FileOutputStream(destination);
			FileChannel fileInputChannel = fileInputStream.getChannel();
			FileChannel fileOutputChannel = fileOutputStream.getChannel();

			fileInputChannel.transferTo(0, fileInputChannel.size(), fileOutputChannel);

			fileInputChannel.close();
			fileOutputChannel.close();
			fileInputStream.close();
			fileOutputStream.close();
		} catch (IOException ex) {
			throw new RuntimeException("Couldn't copy " + source + " to " + destination + ": " + ex.getMessage());
		}
	}

	public static File findOnPath(String executableName) {
		File result = findOnPath0(executableName);
		if (result == null && Platform.isWin()) {
			result = findOnPath0(executableName + ".exe");
		}
		return result;
	}

	private static File findOnPath0(String executableName) {
		String path = System.getenv("PATH");
		String[] directories = path.split(File.pathSeparator);
		for (String directory : directories) {
			File file = fileFromParentAndString(directory, executableName);
			if (file.exists()) {
				// FIXME: in Java 6, check for executable permission too.
				return file;
			}
		}
		return null;
	}


	static public String getSize(long size) {
		return getSize(size, 2, false, true);
	}

	static public String getSize(long size, int precision) {
		return getSize(size, precision, true, true);
	}

	static public String getSize(long size, int precision, boolean longName) {
		return getSize(size, precision, longName, true);
	}

	/**
	 * Get the human-readable size for an amount of bytes
	 *
	 * @param size		: the number of bytes to be converted
	 * @param precision : number of decimal places to round to;
	 *                  optional - defaults to 2
	 * @param longName  : whether or not the returned size tag
	 *                  should be unabbreviated (ie "Gigabytes"
	 *                  or "GB"); optional - defaults to true
	 * @param realSize  : whether or not to use the real (base
	 *                  1024) or commercial (base 1000) size;
	 *                  optional - defaults to true
	 * @return String			  : the converted size
	 */
	static public String getSize(long size, int precision,
										  boolean longName, boolean realSize) {
		int base = realSize ? 1024 : 1000;
		int pos = 0;
		double decSize = (double) size;
		while (decSize > base) {
			decSize /= base;
			pos++;
		}
		String prefix = getSizePrefix(pos);
		String sizeName = longName ? prefix + "bytes" : "" + prefix.charAt(0) + "B";
		sizeName = sizeName.substring(0, 1).toUpperCase() + sizeName.substring(1);
		int num = (int) Math.pow(10, precision);
		return (Math.round(decSize * num) / num) + " " + sizeName;
	}

	/**
	 * @param pos : the distence along the metric scale relitive to 0
	 * @return string : the prefix
	 */
	static public String getSizePrefix(int pos) {
		switch (pos) {
			case 0:
				return " ";
			case 1:
				return "kilo";
			case 2:
				return "mega";
			case 3:
				return "giga";
			case 4:
				return "tera";
			case 5:
				return "peta";
			case 6:
				return "exa";
			case 7:
				return "zetta";
			case 8:
				return "yotta";
			case 9:
				return "xenna";
			case 10:
				return "w-";
			case 11:
				return "vendeka";
			case 12:
				return "u-";
			default:
				return "?-";
		}
	}

	/**
	 * Returns the Path separator.
	 *
	 * @return pf specific path separator
	 */
	public static String getPathSeparator() {
		return System.getProperty("file.separator");
	}

	/**
	 * returns the path of a pathfile string
	 *
	 * @param path
	 * @return
	 */
	public static String getPathFromPathWithFile(String path) {
		log.debug("in: " + path);
		if (path == null || path.length() == 0) {
			return path;
		}

		int sepPos = 0, sepPosLast = 0;

		// find last pos
		while ((sepPos = path.indexOf(getPathSeparator(), sepPos)) > -1) {
			sepPosLast = sepPos;
			sepPos++;
		}

		log.debug("out: " + path.substring(0, sepPosLast));
		return path.substring(0, sepPosLast);
	}

	/**
	 * Get the absolute Path for a FileObject
	 * Does error handling, may return "" for the file.
	 *
	 * @param fo : The File Object
	 * @return		: FileString or "". File may not exist on harddisk.
	 */
	public static String getAbsPath(FileObject fo) {
		try {
			return JakeMainApp.getCore().getFile(fo).getAbsolutePath();
		} catch (FileOperationFailedException e) {
			ExceptionUtilities.showError(e);
			return "";
		}
	}

	// FIXME: remove and us FSSservice:lauchFile
	public static void launchFile(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
