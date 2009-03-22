package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback;
import com.jakeapp.gui.swing.worker.tasks.IJakeTask;
import com.jakeapp.gui.swing.worker.tasks.PullAndLaunchJakeObjectsTask;
import com.jakeapp.gui.swing.worker.tasks.PullJakeObjectsTask;
import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FileObjects are saved here and queued for downloading.
 * The DownloadManager itself is monitoring the application.
 * Adding/Removing download requests on the fly, thread secure.
 *
 * @author studpete
 */
// TODO: retry & co
public class JakeDownloadMgr implements DataChangedCallback {
	private static final Logger log = Logger.getLogger(JakeDownloadMgr.class);
	private static final int MaxDownloads = 5;
	private static JakeDownloadMgr instance;

	public enum DlOptions {
		None, AutoAdded, StartAfterDownload
	}

	private Map<FileObject, EnumSet<DlOptions>> queue =
					new ConcurrentHashMap<FileObject, EnumSet<DlOptions>>();

	private Map<FileObject, DownloadInfo> downloading =
					new ConcurrentHashMap<FileObject, DownloadInfo>();


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
			if(options.contains(DlOptions.StartAfterDownload)) {
				task = new PullAndLaunchJakeObjectsTask(fo);
			}else {
				task = new PullJakeObjectsTask(fo);
			}

			// create info & start download
			DownloadInfo info = new DownloadInfo(task, options);
			this.downloading.put(fo, info);
			JakeExecutor.exec(task);
		}
	}

	private void addToQueue(FileObject fo, EnumSet<DlOptions> options) {
		// if alredy in queue, change options (intelligently)
		if (this.queue.containsKey(fo)) {
			EnumSet<DlOptions> preOpts = this.queue.get(fo);

			// override options if this was previously added automatically
			if (preOpts.contains(DlOptions.AutoAdded)) {
				this.queue.put(fo, options);
			} else {
				log.debug("Not adding " + fo + " to queue, because it's already there");
			}
		} else {
			this.queue.put(fo, options);
		}
	}

	public boolean removeDownload(FileObject fo) {
		// is in queue?
		if (queue.containsKey(fo)) {
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

	}

	/**
	 * Returns true if we have free slots in the download manager
	 *
	 * @return
	 */
	public boolean hasFreeSlots() {
		return downloading.size() < MaxDownloads;
	}


	@Override public void dataChanged(EnumSet<DataReason> dataReason, Project p) {
		if (dataReason.contains(DataReason.Projects)) {

		}
	}
}
