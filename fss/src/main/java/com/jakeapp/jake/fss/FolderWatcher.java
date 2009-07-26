package com.jakeapp.jake.fss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jakeapp.jake.fss.IModificationListener.ModifyActions;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;

/**
 * Implementation of {@link IFSService}
 * 
 * @author johannes
 * @see IFSService
 */
public class FolderWatcher {

	private File rootpath;

	private HashMap<File, Long> lastmodifieddates = new HashMap<File, Long>();

	private HashMap<File, String> hashes = new HashMap<File, String>();

	private List<File> files = new ArrayList<File>();

	private List<IModificationListener> listeners = new ArrayList<IModificationListener>();

	private Timer timer;

	private boolean isCanceled = false;

	private long pollingInterval;

	private StreamFileHashCalculator hasher = null;

	public FolderWatcher(File rootpath, long pollingInterval)
			throws NotADirectoryException, NoSuchAlgorithmException {

		if (!rootpath.exists() || !rootpath.isDirectory()) {
			throw new NotADirectoryException();
		}

		this.rootpath = rootpath;
		this.pollingInterval = pollingInterval;

		hasher = new StreamFileHashCalculator();

	}

	public void initialRun() {
		FolderScanTask fsc = new FolderScanTask();
		fsc.run();
	}

	public void run() {
		timer = new Timer(true);
		timer.schedule(new FolderScanTask(), 0, pollingInterval);
	}

	public void addListener(IModificationListener l) {
		listeners.add(l);
	}

	public void removeListener(IModificationListener l) {
		listeners.remove(l);
	}

	public void cancel() {
		isCanceled = true;
		if (timer != null)
			timer.cancel();
	}

	private class FolderScanTask extends TimerTask {

		/**
		 * scans the folder for modified files
		 */
		@Override
		public void run() {
			if (!rootpath.isDirectory()) {
				System.err.println("FolderScanTask was not shutdown, doing "
						+ "it myself.");
				cancel();
				return;
			}

			checkFolder(rootpath);
			if (isCanceled)
				return;

			/* check for deleted files */
			for (int i = files.size() - 1; i >= 0; i--) {
				File f = files.get(i);
				if (!f.exists()) {
					lastmodifieddates.remove(f);
					hashes.remove(f);
					files.remove(i);
					changeHappened(f, ModifyActions.DELETED);
				}
			}
		}

		/**
		 * recursively finds modified and newly created files
		 * 
		 * @param folder
		 *            important: must be a existing directory!
		 */
		private void checkFolder(File folder) {
			if (isCanceled)
				return;
			Iterable<File> fl = FileUtils.listFilesMinusA(folder);
			for (File f : fl) {
				if (f.isDirectory()) {
					checkFolder(f);
				}
				if (f.isFile()) {
					if (files.contains(f)) {
						if (f.lastModified() != lastmodifieddates.get(f)) {
							String newhash = null;
							try {
								newhash = calculateHash(f);
							} catch (NotAReadableFileException e) {
								e.printStackTrace();
								continue;
							}

							lastmodifieddates.put(f, f.lastModified());
							if (!newhash.equals(hashes.get(f))) {
								hashes.put(f, newhash);
								changeHappened(f, ModifyActions.MODIFIED);
							} else {
								/*
								 * System.out.println("Got modification update, "
								 * + "but hash stayed the same.");
								 */
							}
						}
					} else {
						String newhash = null;
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

	private String calculateHash(File f) throws NotAReadableFileException {
		try {
			return this.hasher.calculateHash(f);
		} catch (FileNotFoundException e) {
			throw new NotAReadableFileException();
		}
	}

	private void changeHappened(File f, ModifyActions event) {
		if (isCanceled)
			return;
		for (IModificationListener l : listeners) {
			l.fileModified(f, event);
		}
	}
}
