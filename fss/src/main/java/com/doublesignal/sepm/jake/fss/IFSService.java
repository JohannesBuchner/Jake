package com.doublesignal.sepm.jake.fss;

import java.io.IOException;

/**
 * The file system service ought to provide a operating system
 * independent way of read/write and watch operations
 * 
 * @author johannes
 * 
 * rootpath: The project root directory. Has to be set first.
 * relpath:  A relative path starting from the rootpath of a file or folder. 
 *           It may only contain characters supported by common operating 
 *           systems.
 *           relpaths contain slashes as path seperators. 
 *           NOTE: For accessing files, they have to be converted to the OS-specific way  
 * 
 **/

public interface IFSService {
	/* TODO: Maybe we should return some streaming thingy in readFile() and 
	 *       writeFile()? */
	
	/**
	 * @return the hash over the file as a string
	 */
	public String calculateHash(String relpath) 
		throws InvalidFilenameException, IOException;
	
	/**
	 * Checks that the file exists and that it is a regular file
	 * @return wether the file exists
	 */
	public Boolean fileExists(String relpath) 
		throws InvalidFilenameException, IOException;
	
	/**
	 * Checks that the folder exists and that it is a folder
	 * @return wether the folder exists
	 */
	public Boolean folderExists(String relpath) 
		throws InvalidFilenameException, IOException;
	
	/**
	 * @return rootpath concatinated with the relpath
	 */
	public String getFullpath(String relpath);
	
	/**
	 * @return the rootpath set previously by SetRootRule
	 */
	public String getRootPath();
	
	/**
	 * Checks wether the relpath contains characters 
	 * acceptable for various operating systems and file systems 
	 */
	public Boolean isValidRelpath(String relpath);
	
	/**
	 * Concatinates the rootpath and the relpath together and
	 * converts to the right path seperator
	 * @return a absolute path usable to the OS
	 */
	public String joinPath(String rootPath, String relpath);
	
	/**
	 * Launches the associated application and returns (i.e. does not wait for 
	 * termination) 
	 * @param relpath the file to be edited/viewed
	 * @return wether launching was successful
	 */
	public Boolean launchFile(String relpath) 
		throws InvalidFilenameException;
	
	/**
	 * Lists folder content 
	 * @param relpath Folder to be viewed
	 * @return directory content
	 */
	public String[] listFolder(String relpath) 
		throws InvalidFilenameException, IOException;
	
	/**
	 * Reads the full content of a given file into a String
	 * @return content of the file
	 */
	public String readFile(String relpath) 
		throws InvalidFilenameException, IOException;
	
	/**
	 * Registers a callback for watching the rootpath.
	 * Events are create, modify, delete for files. 
	 * It is recursive and when a folder is created, the newly created folder is
	 * watched too. When a folder is removed a delete-Callback is issued for 
	 * each file.
	 * @see ModificationListener
	 */
	public void registerModificationCallBack(ModificationListener ob);
	
	/**
	 * Sets and stores the root path for operations that use a relpath.
	 */
	public void setRootPath(String path) 
		throws InvalidFilenameException;

	/**
	 * Writes the content to the file.
	 * Creates subdirectories, if needed.
	 * @param content The full, new file content as a String
	 */
	public Boolean writeFile(String relpath, String content) 
		throws InvalidFilenameException, IOException;

}
