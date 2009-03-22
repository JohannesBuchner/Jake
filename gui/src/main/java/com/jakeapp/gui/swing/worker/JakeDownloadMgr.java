package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback;
import com.jakeapp.gui.swing.callbacks.TaskChangedCallback;
import com.jakeapp.gui.swing.worker.tasks.IJakeTask;
import com.jakeapp.gui.swing.worker.tasks.PullAndLaunchJakeObjectsTask;
import com.jakeapp.gui.swing.worker.tasks.PullJakeObjectsTask;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * FileObjects are saved here and queued for downloading.
 * The DownloadManager itself is monitoring the application.
 * Adding/Removing download requests on the fly, thread secure.
 *
 * @author studpete
 */
// TODO: retry & co
public class JakeDownloadMgr implements DataChangedCallback, TaskChangedCallback {
	private static final Logger log = Logger.getLogger(JakeDownloadMgr.class);
	private static final int MaxDownloads = 5;
	private static JakeDownloadMgr instance;

	/**
	 * Returns the DownloadInfo for specific
	 *
	 * @param fo
	 * @return
	 */
	public DownloadInfo getInfo(FileObject fo) {
		if (!isQueued(fo)) {
			return null;
		} else {
			return this.queue.get(fo);
		}
	}

	public void pullProgressUpdate(JakeObject jo, Status status, double progress) {
		if(jo == null || !(jo instanceof FileObject)) {
			log.trace("Ignoring progress update of Non-FileObject: " + jo);
			return;
		}

		FileObject fo = (FileObject)jo;
		if(!isQueued(fo)) {
			log.warn("Received Progress Update for Object that is not in Queue!? " + fo);
			return;
		}

		// get info & update
		DownloadInfo info = getInfo(fo);
		info.setStatus(status);
		info.setProgress(progress);

		// fire that files are changed
		EventCore.get().fireFilesChanged(jo.getProject());
	}

	public enum DlOptions {
		None, AutoAdded, StartAfterDownload
	}

	// queued + current downloads.
	private Map<FileObject, DownloadInfo> queue =
					new ConcurrentHashMap<FileObject, DownloadInfo>();

	// copy currently downloading items here for faster access
	private List<FileObject> downloads = new CopyOnWriteArrayList<FileObject>();


	/**
	 * Get the Instance, or make on if there isn't...
	 *
	 * @return
	 */
	public static JakeDownloadMgr getInstance() {
		if (instance == null) {
			instance = new JakeDownloadMgr();
		}

		return instance;
	}


	public void queueDownload(FileObject fo, EnumSet<DlOptions> options) {
		log.info("Queuing download of " + fo + options);

		addQueueOrDirectStart(fo, options);
	}

	private void addQueueOrDirectStart(FileObject fo, EnumSet<DlOptions> options) {
		if (hasFreeSlots()) {
			startDownload(fo, options);
		} else {
			addToQueue(fo, options);
		}
	}

	private void startDownload(FileObject fo, EnumSet<DlOptions> options) {
		if (!hasFreeSlots()) {
			log.warn("Not starting download, no free slots left, queuing...");
			addToQueue(fo, options);
		} else {

			// create the task!
			IJakeTask task;
			if (options.contains(DlOptions.StartAfterDownload)) {
				task = new PullAndLaunchJakeObjectsTask(fo);
			} else {
				task = new PullJakeObjectsTask(fo);
			}

			// create info & start download
			DownloadInfo info = new DownloadInfo(fo, task, options);
			this.downloads.add(fo);
			JakeExecutor.exec(task);
		}
	}

	private void addToQueue(FileObject fo, EnumSet<DlOptions> options) {
		boolean addToQueue = true;

		// if alredy in queue, change options (intelligently)
		if (isQueued(fo)) {
			DownloadInfo info = getInfo(fo);

			// override options if this was previously added automatically
			if (info.getOptions().contains(DlOptions.AutoAdded)) {
			} else {
				addToQueue = false;
				log.debug("Not adding " + fo + " to queue, because it's already there");
			}
		}

		if (addToQueue) {
			this.queue.put(fo, new DownloadInfo(fo, options));
		}
	}

	public boolean removeDownload(FileObject fo) {
		// is in queue?
		if (isQueued(fo)) {
			queue.remove(fo);
			return true;
		} else {
			//if (downloading.containsKey())
		}
		return false;
	}


	/**
	 * Download the FileObjects.
	 * Manually added FileObjects have a higher priority,
	 * so does the currently selected project.
	 */
	private void downloadFiles() {
		// TODO
	}

	/**
	 * Returns true if we have free slots in the download manager
	 *
	 * @return
	 */
	public boolean hasFreeSlots() {
		return downloads.size() < MaxDownloads;
	}


	/**
	 * Return true if File is in Queue
	 *
	 * @param fo
	 * @return
	 */
	public boolean isQueued(FileObject fo) {
		return this.queue.containsKey(fo);
	}

	@Override public void dataChanged(EnumSet<DataReason> dataReason, Project p) {
		if (dataReason.contains(DataReason.Projects)) {

		}
	}


	@Override public void taskStarted(IJakeTask task) {
	}

	@Override public void taskUpdated(IJakeTask task) {
	}

	@Override public void taskFinished(IJakeTask task) {
		// remove task from downloads!

		DownloadInfo info = isDownloading(task);
		if(info != null) {
			log.info("Download for " + task + " is finished.");
			this.downloads.remove(info.getFileObject());
		}
	}

	private DownloadInfo isDownloading(IJakeTask task) {
		for(FileObject fo : this.downloads) {
			DownloadInfo info = getInfo(fo);
			if(info.getTask() == task) {
				return info;
			}
		}
		return null;
	}
}
