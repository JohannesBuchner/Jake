package com.jakeapp.jake.fss;

import com.jakeapp.jake.fss.exceptions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * The file system service ought to provide a operating system independent way 
 * of read/write and watch operations.
 * 
 * 
 * <p>rootpath: The project root directory. Has to be set first.</p> 
 * 
 * <p>relpath:  A relative path starting from the rootpath of a file or folder. 
 *           It may only contain characters supported by common operating 
 *           systems (@see <code>isValidRelpath</code>).
 *           relpaths contain slashes as path seperators. 
 *           NOTE: For the real file access, the FSService has to convert them 
 *           to the OS-specific way
 * </p>
 * 
 * @author johannes
 **/

public interface IFSService {
	/**
	 * Checks that the file exists and that it is a regular file (no link, 
	 * device, pipe, ...)
	 * @param relativePath the relative path of the file to be checked
     * @return wether the file exists
     * @throws InvalidFilenameException if the Filename is invalid for jake
     * @throws IOException if an I/O Error occured
	 */
	public Boolean fileExists(String relativePath)
		throws InvalidFilenameException, IOException;
	
	/**
	 * Checks that the folder exists and that it is a folder
	 * @param relativePath the relative path of the folder in question
     * @return wether the folder exists
	 * @throws InvalidFilenameException if the Filename is invalid for jake
	 * @throws IOException if an I/O Error occured
	 */
	public Boolean folderExists(String relativePath)
		throws InvalidFilenameException, IOException;
	
	@Deprecated
	/**
	 * Joins the rootpath with the relativePath. The absolute filename is converted
	 * to the right path seperator.
	 * @return the absolute path for the relativePath
	 * @throws InvalidFilenameException 
	 */
	public String getFullpath(String relativePath) throws InvalidFilenameException;
	
	/**
	 * @return the rootpath set previously by <code>setRootPath</code>
	 *         if no valid rootpath was set, null is returned.
	 */
	public String getRootPath();
	
	/**
	 * Checks wether the relativePath contains characters
	 * acceptable for various operating systems and file systems
	 * 
	 * It matches for: [A-Z a-z0-9\-+_./\(\)]+ and checks that no /../ can be 
	 * applied (which could reference outside the rootpath)
     * @param relativePath the relative path to be checked
     * @return true if the path is valid, false otherwise
     */
	public Boolean isValidRelpath(String relativePath);
	
	/**
	 * Concatinates the parentpath and the subpath together and
	 * converts to the right path seperator
	 * @param parentpath the parent path
     * @param subpath the sub path
     * @return a absolute path usable to the OS
	 */
	public String joinPath(String parentpath, String subpath);
	
	/**
	 * Launches the associated application and returns (i.e. does not wait for 
	 * termination) 
	 * @param relativePath the file to be edited/viewed
	 * @throws InvalidFilenameException if the filename is not valid for jake
	 * @throws com.jakeapp.jake.fss.exceptions.LaunchException if the file couldn't be launched
	 * @throws IOException if an I/O Error occured
	 */
	public void launchFile(String relativePath)
		throws InvalidFilenameException, LaunchException, IOException;
	
	/**
	 * Lists folder content following isValidRelpath
	 * @param relativePath Folder to be viewed
	 * @return directory content: file and folder names as relativePaths
	 * @throws InvalidFilenameException if the filename/foldername is not valid for jake
	 * @throws IOException if an I/O Error occured
	 */
	public List<String> listFolder(String relativePath)
		throws InvalidFilenameException, IOException;
	
	/**
	 * Lists all files in rootpath following isValidRelpath
	 * @return directory content: files as relativePaths
	 * @throws InvalidFilenameException if a filename is not valid for jake
	 * @throws IOException if an I/O Error occured
	 */
	public List<String> recursiveListFiles() 
		throws InvalidFilenameException, IOException;
	
	/**
	 * Reads the full content of a given file into a String
	 * @param relativePath the relative path of the file
     * @return content of the file
	 * @throws InvalidFilenameException if the filename is not valid for jake
	 * @throws FileNotFoundException if the file is not found
	 * @throws NotAReadableFileException if the file is not readable
	 * @throws NotAFileException if the relativePath isn't a file
	 */
	public byte[] readFile(String relativePath)
		throws InvalidFilenameException, NotAFileException, 
			FileNotFoundException, NotAReadableFileException;
	
	/**
	 * Sets and stores the root path for operations that use a relativePath.
	 * @param absolutePath the path in the filesystem to jakes root path (/)
     * @throws IOException if an I/O Error occured
	 * @throws NotADirectoryException if the path is not a folder
	 */
	public void setRootPath(String absolutePath)
		throws IOException, NotADirectoryException;
	/**
	 * Unsets the root path (e.g. stops listeners)
	 * @throws IOException if an I/O Error occured
	 * @throws NotADirectoryException if the path is not a folder
	 */
	public void unsetRootPath();

	/**
	 * Writes the content to the file.
	 * Creates subdirectories, if needed.
	 * @param relativePath the relative path to the file
     * @param content The full, new file content as a String
	 * @throws InvalidFilenameException if the filename is not valid for jake
	 * @throws IOException if an I/O Error occured
	 * @throws NotAFileException if the relativePath is not a file
	 * @throws FileTooLargeException if the file is to large to be handled by jake
	 * @throws CreatingSubDirectoriesFailedException if jake couldn't create subdirectories
	 */
	public void writeFile(String relativePath, byte[] content)
		throws InvalidFilenameException, IOException, FileTooLargeException,
			NotAFileException, CreatingSubDirectoriesFailedException;
	
	
	/**
	 * Gets the operating system preferred temporary directory
	 * It is deleted eventually by the operating system after program 
	 * termination 
	 * @return a temporary directory
	 * @throws IOException if an I/O Error occured
	 */
	public String getTempDir() 
		throws IOException;
	
	/**
	 * Get the path to a new temporary file that can be written to.
	 * The file resides in a temporary directory.
	 * It is deleted eventually by the operating system after program 
	 * termination 
	 * @return the path
	 * @throws IOException if an I/O Error occured
	 */
	public String getTempFile()
		throws IOException;
	
	/**
	 * Deletes the file and recursively removes parent folders if they are empty
	 * @param relativePath the relative path to the file to be deleted
     * @return wether the delete was successful
	 * @throws InvalidFilenameException if the filename is invalid for jake
	 * @throws FileNotFoundException if no file is found at this relativePath
	 * @throws NotAFileException if the relativePath is not a file
	 */
	public boolean deleteFile(String relativePath) throws InvalidFilenameException, FileNotFoundException, NotAFileException;
	
	
	/**
	 * @param relativePath the relative path to the file
	 * @return the hash of the file
	 * @throws InvalidFilenameException if the filename is invalid for jake
	 * @throws NotAReadableFileException if the file is not readable (directory, not enough rights etc.)
	 * @throws FileNotFoundException if no file is found at this relativePath
	 */
	String calculateHashOverFile(String relativePath)
		throws InvalidFilenameException, NotAReadableFileException, FileNotFoundException;

	/**
	 * @param bytes content to calculate the hash of
	 * @return hash over the bytes
	 */
	String calculateHash(byte[] bytes);
	
	/**
	 * @return length of the returning String of the implemented Hash operation
	 */
	int getHashLength();
	
	/**
	 * @param relativePath the relative path of the file
	 * @return size of the file in Bytes
	 * @throws InvalidFilenameException if the filename is not valid for jake
	 * @throws FileNotFoundException if the file is not found
	 * @throws NotAFileException if the relativePath isn't a file
	 */
	long getFileSize(String relativePath)
		throws InvalidFilenameException, FileNotFoundException, NotAFileException; 


	/**
	 * Registers a callback for watching the rootpath.
	 * Events are create, modify, delete for files. 
	 * It is recursive and when a folder is created, the newly created folder is
	 * watched too. When a folder is removed a delete-Callback is issued for 
	 * each file.
	 * @param projectModificationListener the object that registers itself as ProjectModificationListener
     * @see IProjectModificationListener
	 */
	public void addModificationListener(IProjectModificationListener projectModificationListener);

	/**
	 * Removes a callback for watching the rootpath.
	 * @param projectModificationListener the object that cancels its registration as ProjectModificationListener
     * @see IProjectModificationListener
	 */
	public void removeModificationListener(IProjectModificationListener projectModificationListener);
	
	/**
	 * get the last modified date for a file
	 * @param relativePath the relative path of the file
     * @throws InvalidFilenameException if the filename is invalid for jake
	 * @throws NotAFileException if the relativePath is not a file
     * @return the timestamp of the last modification
	 */
	public long getLastModified(String relativePath)
		throws InvalidFilenameException, NotAFileException;
	
}
