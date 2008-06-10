package com.doublesignal.sepm.jake.fss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.doublesignal.sepm.jake.fss.IModificationListener.ModifyActions;

/**
 * Implementation of {@link IFSService}
 * 
 * @author johannes
 * @see IFSService
 */
public class FolderWatcher {

	private File rootpath;

	private HashMap<File, Long> lastmodifieddates = new HashMap<File, Long>();

	private HashMap<File, byte[]> hashes = new HashMap<File, byte[]>();

	private List<File> files = new ArrayList<File>();

	private List<IModificationListener> listeners = new ArrayList<IModificationListener>();

	private Timer timer;

	private boolean isCanceled = false;

	private long pollingInterval;
	
	public FolderWatcher(File rootpath, long pollingInterval) throws NotADirectoryException {

		if (!rootpath.exists() || !rootpath.isDirectory()) {
			throw new NotADirectoryException();
		}

		this.rootpath = rootpath;
		this.pollingInterval = pollingInterval;
	}

	public void initialRun(){
		FolderScanTask fsc = new FolderScanTask();
		fsc.run();
	}
	
	public void run(){
		timer = new Timer (true);
		timer.schedule (new FolderScanTask(), 0, pollingInterval);
	}

	public void addListener(IModificationListener l){
		listeners.add(l);
	}

	public void removeListener(IModificationListener l){
		listeners.remove(l);
	}

	public void cancel(){
		isCanceled = true;
		if(timer!=null)
			timer.cancel();
	}

	private class FolderScanTask extends TimerTask {
		/**
		 * scans the folder for modified files
		 */
		@Override
		public void run() {
			if(!rootpath.isDirectory()){
				System.err.println("FolderScanTask was not shutdown, doing " +
						"it myself.");
				cancel();
				return;
			}
			
			checkFolder(rootpath);
			if(isCanceled)
				return;

			/* check for deleted files */
			System.out.println("checking for deleted files ...");

			for (int i = files.size() - 1; i>=0; i-- ) {
				File f = files.get(i);
				if(!f.exists()){
					lastmodifieddates.remove(f);
					hashes.remove(f);
					files.remove(i);
					changeHappened(f, ModifyActions.DELETED);
				}else{
					System.out.println(f.getAbsolutePath() + " still exists.");
				}
			}
		}

		/**
		 * recursively finds modified and newly created files 
		 * @param folder
		 */
		private void checkFolder(File folder) {
			if(isCanceled)
				return;
			if(!folder.isDirectory()){
				System.out.println("checkFolder got a non-directory: " + 
						folder.getAbsolutePath());
				return;
			}
			for (File f : folder.listFiles()) {
				if (f.isDirectory()) {
					checkFolder(f);
				}
				if (f.isFile()) {
					if (files.contains(f)) {
						if (f.lastModified() != lastmodifieddates.get(f)) {
							byte[] newhash = null;
							try {
								newhash = calculateHash(f);
							} catch (NotAReadableFileException e) {
							}

							lastmodifieddates.put(f, f.lastModified());
							hashes.put(f, newhash);
							
							changeHappened(f, ModifyActions.MODIFIED);
						}
					}else{
						byte[] newhash = null;
						try {
							newhash = calculateHash(f);
						} catch (NotAReadableFileException e) {
						}

						lastmodifieddates.put(f, f.lastModified());
						hashes.put(f, newhash);
						files.add(f);

						changeHappened(f, ModifyActions.CREATED);
					}
				}
			}
		}

	}

	private static byte[] calculateHash(File f)
	throws NotAReadableFileException {

		FileInputStream fr;
		try {
			fr = new FileInputStream(f);
		} catch (FileNotFoundException e1) {
			throw new NotAReadableFileException();
		}
		
		int len = (int) f.length();
		byte[] buf = new byte[len];
		int n;
		
		try {
			n = fr.read(buf, 0, len);
		} catch (IOException e) {
			throw new NotAReadableFileException();
		}
		if (len > n)
			throw new NotAReadableFileException();

		return buf;
	}

	private void changeHappened(File f, ModifyActions event) {
		if(isCanceled)
			return;
		System.out.println("changeHappened: " + f.getAbsolutePath() + ": " + event);
		for(IModificationListener l : listeners){
			l.fileModified(f, event);
		}
	}
}
