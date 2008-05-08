package com.doublesignal.sepm.jake.fss;


/**
 * The file system service ought to provide a operating system
 * independent way of read/write and watch operations
 * 
 * @author johannes
 **/

public interface IFSService {
	/* TODO: @returns: Maybe some Streaming thingy? */
	public String readFile(String relpath) 
		throws InvalidFilenameException;
	
	public String[] listFolder(String relpath) 
		throws InvalidFilenameException;
	
	public Boolean launchFile(String relpath) 
		throws InvalidFilenameException;
	
	public void registerModificationCallBack(ModificationListener ob);
	
	public String getRootPath();
	
	public void setRootPath(String path) 
		throws InvalidFilenameException;
	
	public Boolean fileExists(String relpath) 
		throws InvalidFilenameException;
	
	public String calculateHash(String relpath) 
		throws InvalidFilenameException;
	
	public Boolean writeFile(String relpath, String content) 
		throws InvalidFilenameException;
	
}
