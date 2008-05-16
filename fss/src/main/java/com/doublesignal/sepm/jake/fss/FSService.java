package com.doublesignal.sepm.jake.fss;

import java.io.*;

import com.doublesignal.sepm.jake.fss.IFSService;

public class FSService implements IFSService {

	public String calculateHash(String relpath) throws InvalidFilenameException {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean fileExists(String relpath) throws InvalidFilenameException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRootPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public void launchFile(String relpath) throws InvalidFilenameException {
		// TODO Auto-generated method stub
	}

	public String[] listFolder(String relpath) throws InvalidFilenameException {
		// TODO Auto-generated method stub
		return null;
	}

	public String readFile(String relpath) throws InvalidFilenameException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFullpath(String relpath) {
		// TODO Auto-generated method stub
		return null;
	}

	public String joinPath(String rootPath, String relpath) {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerModificationCallBack(ModificationListener ob) {
		// TODO Auto-generated method stub

	}

	public void setRootPath(String path) throws InvalidFilenameException {
		// TODO Auto-generated method stub

	}

	public Boolean writeFile(String relpath, String content)
			throws InvalidFilenameException, IOException {
		return null;
	}

	public Boolean folderExists(String relpath) throws InvalidFilenameException {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isValidRelpath(String relpath) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTempDir() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTempFile() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerModificationListener(ModificationListener ob) {
		// TODO Auto-generated method stub
		
	}
	
}
