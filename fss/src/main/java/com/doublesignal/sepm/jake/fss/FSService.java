package com.doublesignal.sepm.jake.fss;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FSService implements IFSService {
	
	private String rootPath = null;
	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String path) throws FileNotFoundException, NotADirectoryException {
		File f = new File(path);
		if(!f.exists()) 
			throw new FileNotFoundException();
		if(!f.isDirectory())
			throw new NotADirectoryException();
		rootPath = path;
	}


	public String calculateHash(String relpath) throws InvalidFilenameException {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean fileExists(String relpath) throws InvalidFilenameException {
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

	public byte[] readFile(String relpath) throws InvalidFilenameException, 
			NotAFileException, 
			FileNotFoundException, NotAReadableFileException{
		
		String filename = getFullpath(relpath);
		File f = new File(filename);
		if(!f.exists())
			throw new FileNotFoundException();
		if(!f.isFile())
			throw new NotAFileException();
		if(f.length() > Integer.MAX_VALUE)
			throw new FileTooLargeException();

		FileInputStream fr = null;
		try{
			fr = new FileInputStream(filename);
		}catch(FileNotFoundException e){ 
			/* This is thrown if permissions wrong. we already know the file 
			 * exists. */
			throw new NotAReadableFileException();
		}
		
		int len = (int)f.length();
		byte[] buf = new byte[len];
		int n;
		try{
			n = fr.read(buf, 0, len);
		}catch (IOException e) {
			throw new NotAReadableFileException();
		}
		if(len > n) 
			throw new NotAReadableFileException();
		return buf;
	}

	public String getFullpath(String relpath) throws InvalidFilenameException {
		if(!isValidRelpath(relpath))
			throw new InvalidFilenameException();
		return joinPath(getRootPath(), relpath);
	}

	public String joinPath(String rootPath, String relpath) {
		String p = rootPath + File.separator + relpath;
		if(File.separator != "/")
			p = p.replaceAll("/", File.separator);
		return p.replaceAll(File.separator + File.separator, File.separator);
	}

	public void registerModificationCallBack(ModificationListener ob) {
		// TODO Auto-generated method stub

	}
	
	public Boolean writeFile(String relpath, byte[] content)
			throws InvalidFilenameException, IOException {
		return null;
		
	}

	public Boolean folderExists(String relpath)
			throws InvalidFilenameException, IOException {
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

	public Boolean isValidRelpath(String relpath) {
        String regex = "[A-Z a-z0-9\\-+_./\\(\\)]+";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(relpath);

        if(!(m.find() && m.start() == 0 && m.end() == relpath.length())){
                return false;
        }
        if(relpath.contains("/..") || relpath.contains("../") ||
                        relpath.startsWith("..") || relpath.endsWith("..")){
                return false;
        }
		return true;
	}

	public void registerModificationListener(ModificationListener ob) {
		// TODO Auto-generated method stub
		
	}
}
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
