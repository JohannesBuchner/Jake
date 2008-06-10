package com.doublesignal.sepm.jake.fss;

import java.awt.Desktop;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link IFSService}
 * @author johannes
 * @see IFSService
 */
public class FSService implements IFSService {
	
	private String rootPath = null;
	
	private Desktop desktop = null;
	
	private FileHashCalculator hasher = null;
	
	private FolderWatcher fw = null;
	
	public FSService() throws NoSuchAlgorithmException{
		hasher = new FileHashCalculator();
		if (!Desktop.isDesktopSupported())
			throw new NoSuchAlgorithmException("Desktop not supported");
		
		desktop = Desktop.getDesktop();
		
		if (!desktop.isSupported(Desktop.Action.OPEN)) 
			throw new NoSuchAlgorithmException("Open not supported in Desktop");
	}
	
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
		
		if(fw != null)
			fw.cancel();
		startModificationThread();
	}
	
	private void startModificationThread() throws NotADirectoryException {
		try {
			fw = new FolderWatcher(new File(this.rootPath), 700);
		} catch (NoSuchAlgorithmException e) {
			/* won't happen as we use the same algorithm here and it loaded. */
		}
		fw.initialRun();
		fw.run();
	}
	
	public void addModificationListener(IModificationListener l) {
		if(fw != null)
			fw.addListener(l);
	}
	
	public void removeModificationListener(IModificationListener l) {
		if(fw != null)
			fw.removeListener(l);
	}
	
	public Boolean fileExists(String relpath) throws InvalidFilenameException {
		File f = new File(getFullpath(relpath));
		return f.exists() && f.isFile();
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
		if(getRootPath()==null)
			return null;
		if(!isValidRelpath(relpath))
			throw new InvalidFilenameException("File "+relpath + " is not a valid filename!");
		File f = new File(joinPath(getRootPath(), relpath));
		return f.getAbsolutePath();
	}

	public String joinPath(String rootPath, String relpath) {
		if('/'!=File.separatorChar)
			relpath = relpath.replace('/', File.separatorChar);
		String p = rootPath + File.separator + relpath;
		if(File.separatorChar == '\\'){
			p = p.replaceAll("\\\\\\\\", "\\\\");
		}else{
			p = p.replaceAll(File.separator + File.separator, File.separator);
		}
		return p;
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

	public void launchFile(String relpath) 
		throws InvalidFilenameException, LaunchException 
	{
		try {
			desktop.open(new File(getFullpath(relpath)));
		} catch (IOException e) {
			throw new LaunchException(e);
		}
	}

	public long getFileSize(String relpath)
		throws InvalidFilenameException, FileNotFoundException, NotAFileException 
	{
		String filename = getFullpath(relpath);
		File f = new File(filename);
		if(!f.exists())
			throw new FileNotFoundException();
		if(!f.isFile())
			throw new NotAFileException();
		return f.length();
	}

	public String calculateHash(byte[] bytes) {
		return hasher.calculateHash(bytes);
	}

	public String calculateHashOverFile(String relpath) throws InvalidFilenameException, NotAReadableFileException, FileNotFoundException {
		return hasher.calculateHash(readFile(relpath));
	}

	public int getHashLength() {
		return hasher.getHashLength();
	}


}
