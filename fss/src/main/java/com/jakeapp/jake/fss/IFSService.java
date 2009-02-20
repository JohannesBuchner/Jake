package com.jakeapp.jake.fss;

import com.jakeapp.jake.fss.exceptions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * The file system service ought to provide a operating system independent way
 * of read/write and watch operations.
 * <p/>
 * <p/>
 * <p>
 * rootpath: The project root directory. Has to be set first.
 * </p>
 * <p/>
 * <p>
 * relpath: A relative path starting from the rootpath of a file or folder. It
 * may only contain characters supported by common operating systems (@see
 * <code>isValidRelpath</code>). relpaths contain slashes as path seperators.
 * NOTE: For the real file access, the FSService has to convert them to the
 * OS-specific way
 * </p>
 *
 * @author johannes
 */

public interface IFSService extends Serializable {
	/**
	 * Checks that the file exists and that it is a regular file (no link,
	 * device, pipe, ...)
	 *
	 * @param relativePath the relative path of the file to be checked
	 * @return wether the file exists
	 * @throws InvalidFilenameException if the Filename is invalid for jake
	 * @throws IOException				  if an I/O Error occured
	 */
	public Boolean fileExists(String relativePath)
			  throws InvalidFilenameException, IOException;

	/**
	 * Checks that the folder exists and that it is a folder
	 *
	 * @param relativePath the relative path of the folder in question
	 * @return wether the folder exists
	 * @throws InvalidFilenameException if the Filename is invalid for jake
	 * @throws IOException				  if an I/O Error occured
	 */
	public Boolean folderExists(String relativePath)
			  throws InvalidFilenameException, IOException;

	/**
	 * Joins the rootpath with the relativePath. The absolute filename is
	 * converted to the right path seperator.
	 * 
	 * @return the absolute path for the relativePath
	 * 
	 * @throws InvalidFilenameException
	 */
	public String getFullpath(String relativePath)
			  throws InvalidFilenameException;

	/**
	 * @return the rootpath set previously by <code>setRootPath</code> if no
	 *         valid rootpath was set, null is returned.
	 */
	public String getRootPath();

	/**
	 * Checks wether the relativePath contains characters acceptable for various
	 * operating systems and file systems
	 * <p/>
	 * It matches for: [A-Z a-z0-9\-+_./\(\)]+ and checks that no /../ can be
	 * applied (which could reference outside the rootpath)
	 *
	 * @param relativePath the relative path to be checked
	 * @return true if the path is valid, false otherwise
	 */
	public Boolean isValidRelpath(String relativePath);

	/**
	 * Concatinates the parentpath and the subpath together and converts to the
	 * right path seperator
	 *
	 * @param parentpath the parent path
	 * @param subpath	 the sub path
	 * @return a absolute path usable to the OS
	 */
	public String joinPath(String parentpath, String subpath);

	/**
	 * Launches the associated application and returns (i.e. does not wait for
	 * termination)
	 *
	 * @param relativePath the file to be edited/viewed
	 * @throws InvalidFilenameException if the filename is not valid for jake
	 * @throws LaunchException			 if the file couldn't be launched
	 * @throws IOException				  if an I/O Error occured
	 */
	public void launchFile(String relativePath)
			  throws InvalidFilenameException, LaunchException, IOException;

	/**
	 * Lists folder content following isValidRelpath
	 *
	 * @param relativePath Folder to be viewed
	 * @return directory content: file and folder names as relativePaths
	 * @throws InvalidFilenameException if the given relpath is not valid for {@link #isValidRelpath(String)}
	 * @throws IOException				  if an I/O Error occured
	 */
	public List<String> listFolder(String relativePath)
			  throws InvalidFilenameException, IOException;

	/**
	 * Lists all files in rootpath following isValidRelpath
	 *
	 * @return directory content: files as relativePaths
	 * @throws IOException if an I/O Error occured
	 */
	public List<String> recursiveListFiles() throws
			  IOException;

	/**
	 * Reads the full content of a given file into a String
	 *
	 * @param relativePath the relative path of the file
	 * @return content of the file
	 * @throws InvalidFilenameException  if the filename is not valid for jake
	 * @throws FileNotFoundException	  if the file is not found
	 * @throws NotAReadableFileException if the file is not readable
	 * @throws NotAFileException			if the relativePath isn't a file
	 * @deprecated use the streamed version
	 */
	@Deprecated
	public byte[] readFile(String relativePath)
			  throws InvalidFilenameException, NotAFileException,
			  FileNotFoundException, NotAReadableFileException;

	/**
	 * Gives access to the content of a given file
	 *
	 * @param relativePath the relative path of the file
	 * @return content of the file
	 * @throws InvalidFilenameException  if the filename is not valid for jake
	 * @throws FileNotFoundException	  if the file is not found
	 * @throws NotAReadableFileException if the file is not readable
	 * @throws NotAFileException			if the relativePath isn't a file
	 */
	public InputStream readFileStream(String relativePath)
			  throws InvalidFilenameException, NotAFileException,
			  FileNotFoundException, NotAReadableFileException;

	/**
	 * Sets and stores the root path for operations that use a relativePath.
	 *
	 * @param absolutePath the path in the filesystem to jakes root path (/)
	 * @throws IOException				if an I/O Error occured
	 * @throws NotADirectoryException if the path is not a folder
	 */
	public void setRootPath(String absolutePath) throws IOException,
			  NotADirectoryException;

	/**
	 * Unsets the root path (e.g. stops listeners)
	 *
	 * @throws IOException				if an I/O Error occured
	 * @throws NotADirectoryException if the path is not a folder
	 */
	public void unsetRootPath();

	/**
	 * Writes the content to the file. Creates subdirectories, if needed.
	 *
	 * @param relativePath the relative path to the file
	 * @param content		The full, new file content as a String
	 * @throws InvalidFilenameException if the filename is not valid for jake
	 * @throws IOException				  if an I/O Error occured
	 * @throws NotAFileException		  if the relativePath is not a file
	 * @throws FileTooLargeException	 if the file is to large to be handled by jake
	 * @throws CreatingSubDirectoriesFailedException
	 *                                  if jake couldn't create subdirectories
	 * @deprecated use the streamed version
	 */
	@Deprecated
	public void writeFile(String relativePath, byte[] content)
			  throws InvalidFilenameException, IOException,
			  FileTooLargeException, NotAFileException,
			  CreatingSubDirectoriesFailedException;

	/**
	 * Writes the content to the file. Creates subdirectories, if needed.
	 *
	 * @param relativePath the relative path to the file
	 * @param content		The new file content
	 * @throws InvalidFilenameException if the filename is not valid for jake
	 * @throws IOException				  if an I/O Error occured
	 * @throws NotAFileException		  if the relativePath is not a file
	 * @throws FileTooLargeException	 if the file is to large to be handled by jake
	 * @throws CreatingSubDirectoriesFailedException
	 *                                  if jake couldn't create subdirectories
	 */
	public void writeFileStream(String relativePath, InputStream stream)
			  throws InvalidFilenameException, IOException,
			  FileTooLargeException, NotAFileException,
			  CreatingSubDirectoriesFailedException;

	/**
	 * Gets the operating system preferred temporary directory It is deleted
	 * eventually by the operating system after program termination
	 *
	 * @return a temporary directory
	 * @throws IOException if an I/O Error occured
	 */
	public String getTempDir() throws IOException;

	/**
	 * Get the path to a new temporary file that can be written to. The file
	 * resides in a temporary directory. It is deleted eventually by the
	 * operating system after program termination
	 *
	 * @return the path
	 * @throws IOException if an I/O Error occured
	 */
	public String getTempFile() throws IOException;

	/**
	 * Deletes the file and recursively removes parent folders if they are empty
	 *
	 * @param relativePath the relative path to the file to be deleted
	 * @return wether the delete was successful
	 * @throws InvalidFilenameException if the filename is invalid for jake
	 * @throws FileNotFoundException	 if no file is found at this relativePath
	 * @throws NotAFileException		  if the relativePath is not a file
	 */
	public boolean deleteFile(String relativePath)
			  throws InvalidFilenameException, FileNotFoundException,
			  NotAFileException;

	/**
	 * Deletes the folder, recursively
	 *
	 * @param relativePath the relative path to the file to be deleted
	 * @return wether the delete was successful
	 * @throws InvalidFilenameException if the filename is invalid for jake
	 * @throws FileNotFoundException	 if no file is found at this relativePath
	 * @throws NotAFileException		  if the relativePath is not a file
	 */
	// TODO: unit tests!
	public boolean deleteFolder(String relativePath)
			  throws InvalidFilenameException, FileNotFoundException,
			  NotADirectoryException;

	/**
	 * Deletes the file and removes parent folders if they are empty by moving
	 * them to the Systems trash.
	 *
	 * @param relativePath the relative path to the file to be deleted
	 * @return wether the delete was successful
	 * @throws InvalidFilenameException if the filename is invalid for jake
	 * @throws FileNotFoundException	 if no file is found at this relativePath
	 */
	public boolean trashFile(String relativePath)
			  throws InvalidFilenameException, FileNotFoundException;

	/**
	 * Trashes a folder with all its files/folders in it.
	 *
	 * @param relativePath the relative path to the folder to be deleted
	 * @return wether the delete was successful
	 * @throws InvalidFilenameException if the filename is invalid for jake
	 * @throws FileNotFoundException	 if no file is found at this relativePath
	 */
	// TODO: unit tests!
	boolean trashFolder(String relativePath)
			  throws InvalidFilenameException, FileNotFoundException;

	/**
	 * @param from the relative path to the file to be moved
	 * @param to	the relative path of the destination to move the file to
	 * @return <code>true</code> if the operation was successful.
	 * @throws InvalidFilenameException	if destination's filename is invalid
	 * @throws NotAReadableFileException  if <code>from</code> points to a directory rather than to a
	 *                                    file or if there is no file at <code>from</code> that can be
	 *                                    read.
	 * @throws FileAlreadyExistsException if the file <code>to</code> points to already exists.
	 * @throws IOException					 if an IO-Error occured when reading or writing the files
	 * @throws CreatingSubDirectoriesFailedException
	 *                                    If creating the directories, that should contain the file
	 *                                    after the move operation, failed.
	 */
	public boolean moveFile(String from, String to)
			  throws InvalidFilenameException, NotAReadableFileException,
			  FileAlreadyExistsException, IOException,
			  CreatingSubDirectoriesFailedException;

	/**
	 * @param relativePath the relative path to the file
	 * @return the hash of the file
	 * @throws InvalidFilenameException  if the filename is invalid for jake
	 * @throws NotAReadableFileException if the file is not readable (directory, not enough rights
	 *                                   etc.)
	 * @throws FileNotFoundException	  if no file is found at this relativePath
	 */
	String calculateHashOverFile(String relativePath)
			  throws InvalidFilenameException, NotAReadableFileException,
			  FileNotFoundException;

	/**
	 * @param bytes content to calculate the hash of
	 * @return hash over the bytes
	 * @deprecated use the streamed version
	 */
	String calculateHash(byte[] bytes);

	/**
	 * @param stream to calculate the hash of
	 * @return hash over the content
	 */
	String calculateHash(InputStream stream);

	/**
	 * @return length of the returning String of the implemented Hash operation
	 */
	int getHashLength();

	/**
	 * @param relativePath the relative path of the file
	 * @return size of the file in Bytes
	 * @throws InvalidFilenameException if the filename is not valid for jake
	 * @throws FileNotFoundException	 if the file is not found
	 * @throws NotAFileException		  if the relativePath isn't a file
	 */
	long getFileSize(String relativePath) throws InvalidFilenameException,
			  FileNotFoundException, NotAFileException;

	/**
	 * Registers a callback for watching the rootpath. Events are create,
	 * modify, delete for files. It is recursive and when a folder is created,
	 * the newly created folder is watched too. When a folder is removed a
	 * delete-Callback is issued for each file.
	 *
	 * @param projectModificationListener the object that registers itself as
	 *                                    ProjectModificationListener
	 * @see IProjectModificationListener
	 */
	public void addModificationListener(
			  IProjectModificationListener projectModificationListener);

	/**
	 * Removes a callback for watching the rootpath.
	 *
	 * @param projectModificationListener the object that cancels its registration as
	 *                                    ProjectModificationListener
	 * @see IProjectModificationListener
	 */
	public void removeModificationListener(
			  IProjectModificationListener projectModificationListener);

	/**
	 * get the last modified date for a file
	 *
	 * @param relativePath the relative path of the file
	 * @return the timestamp of the last modification
	 * @throws InvalidFilenameException if the filename is invalid for jake
	 * @throws NotAFileException		  if the relativePath is not a file
	 */
	public long getLastModified(String relativePath)
			  throws InvalidFilenameException, NotAFileException;

	/**
	 * Extracts the file/foldername from a relpath.
	 *
	 * @param relpath A valid relpath to a file or folder
	 * @return the name of a file
	 * @throws InvalidFilenameException
	 */
	String getFileName(String relpath) throws InvalidFilenameException;

	/**
	 * creates a folder
	 * this is only a convenience-method  since folders are not managed by
	 * the jake core.
	 *
	 * @throws InvalidFilenameException if the relpath is not valid
	 * @throws IOException				  if the folder cannot be created
	 */
	void createFolder(String relpath) throws InvalidFilenameException, IOException;
}
