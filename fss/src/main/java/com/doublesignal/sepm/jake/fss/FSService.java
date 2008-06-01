package com.doublesignal.sepm.jake.fss;

import java.awt.Desktop;
import java.io.*;
import java.security.MessageDigest;
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
	private MessageDigest md = null;
	Desktop desktop = null;
	public FSService() throws NoSuchAlgorithmException{
		md = MessageDigest.getInstance("SHA-512");
		Desktop desktop = null;

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
	}
	
	public String calculateHash(String relpath) throws InvalidFilenameException {
		// TODO Auto-generated method stub
		return null;
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

	public String calculateHash(byte[] bytes) {
		md.update(bytes);
		byte[] b = md.digest();
		String s = "";
		for(int i=0;i<b.length;i++){
			int c = b[i]; 
			if ( b[i] < 0 )
				c = c + 256;
			s = s.concat( halfbyte2str(c/16) + halfbyte2str(c%16));
		}
		return s;
	}

	private String halfbyte2str(int i) {
		switch(i){
			case  0: return "0";
			case  1: return "1";
			case  2: return "2";
			case  3: return "3";
			case  4: return "4";
			case  5: return "5";
			case  6: return "6";
			case  7: return "7";
			case  8: return "8";
			case  9: return "9";
			case 10: return "a";
			case 11: return "b";
			case 12: return "c";
			case 13: return "d";
			case 14: return "e";
			case 15: return "f";
			default: throw new NullPointerException();
		}
	}

	public String calculateHashOverFile(String relpath) 
		throws InvalidFilenameException, NotAReadableFileException, FileNotFoundException 
	{
		return calculateHash(readFile(relpath));
	}

	public int getHashLength() {
		return md.getDigestLength()*2;
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

}
