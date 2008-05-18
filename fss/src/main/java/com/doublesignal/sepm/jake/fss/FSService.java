package com.doublesignal.sepm.jake.fss;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link IFSService}
 * @author johannes
 * @see IFSService
 */
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
		File f = new File(getFullpath(relpath));
		return f.exists() && f.isFile();
	}

	public void launchFile(String relpath) throws InvalidFilenameException {
		// TODO Auto-generated method stub
	}

	public String[] listFolder(String relpath) throws InvalidFilenameException {
		File f = new File(getFullpath(relpath));
		return f.list();
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
		File f = new File(joinPath(getRootPath(), relpath));
		return f.getAbsolutePath();
	}

	public String joinPath(String rootPath, String relpath) {
		String p = rootPath + File.separator + relpath;
		if(File.separator != "/")
			p = p.replaceAll("/", File.separator);
		return p.replaceAll(File.separator + File.separator, File.separator);
	}

	public void writeFile(String relpath, byte[] content)
		throws InvalidFilenameException, IOException, FileTooLargeException,
			NotAFileException, CreatingSubDirectoriesFailedException
	{
		String filename = getFullpath(relpath);
		File f = new File(filename);
		
		if(f.exists() && !f.isFile())
			throw new NotAFileException();
		if(content.length > Integer.MAX_VALUE)
			throw new FileTooLargeException();
		if(f.getParentFile().exists()){
			if(!f.getParentFile().isDirectory())
				throw new CreatingSubDirectoriesFailedException();
		}else{
			if(!f.getParentFile().mkdirs())
				throw new CreatingSubDirectoriesFailedException();
		}
		
		FileOutputStream fr = null;
		fr = new FileOutputStream(filename);
		fr.write(content);
		fr.close();
	}

	public Boolean folderExists(String relpath)
			throws InvalidFilenameException, IOException {
		File f = new File(getFullpath(relpath));
		return f.exists() && f.isDirectory();
	}

	public String getTempDir() throws IOException {
		String tempdir;
		File f = File.createTempFile("jakefss", "testfile");
		tempdir = f.getParentFile().getAbsolutePath();
		f.delete();
		return tempdir;
	}

	public String getTempFile() throws IOException {
		File f = File.createTempFile("jake", "");
		return f.getAbsolutePath();
	}

	public Boolean isValidRelpath(String relpath) {
		String regex = "[A-Z a-z0-9\\-+_./\\(\\)]+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(relpath);
		
		if(!(m.find() && m.start() == 0 && m.end() == relpath.length())){
			return false;
		}
		if (relpath.contains("/../") || 
			relpath.startsWith("../") || 
			relpath.endsWith("/..") ||
			relpath.equals("..")
		){
			return false;
		}
		return true;
	}

	public void registerModificationListener(IModificationListener ob) {
		// TODO Auto-generated method stub
		
	}

	public boolean deleteFile(String relpath) 
		throws InvalidFilenameException, FileNotFoundException, NotAFileException
	{
		File f = new File(getFullpath(relpath));
		if(!f.exists())
			throw new FileNotFoundException();
		if(!f.isFile())
			throw new NotAFileException();
		if(!f.delete())
			return false;
		
		/* TODO: Check if this is a infinite loop on a empty drive on windows*/
		do{
			f = f.getParentFile();
		}while(f.isDirectory() && f.getAbsolutePath().startsWith(getRootPath()) 
			&& f.list().length>0 && f.delete());
		
		return true;
	}
}
