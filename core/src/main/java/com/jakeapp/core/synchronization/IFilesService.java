package com.jakeapp.core.synchronization;

import java.io.File;

import com.jakeapp.core.domain.FileObject;


public interface IFilesService {

	/**
	 * get the file in the filesystem 
	 * You should almost never use this!
	 * @param jo
	 */
	public File getFullpath(FileObject jo);
	
	/**
	 * open the file 
	 * @param jo
	 */
	public void launch(FileObject jo);
	
	
}
