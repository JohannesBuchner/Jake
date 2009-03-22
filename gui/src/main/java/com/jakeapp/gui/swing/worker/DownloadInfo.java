package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.worker.tasks.IJakeTask;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

import java.util.EnumSet;

/**
 * Download Information for a File.
 *
 * @author studpete
 */
public class DownloadInfo {
	private FileObject fileObject;
	private boolean started = false;
	private double progress = 0;
	private Status status = Status.initial;
	private IJakeTask task = null;
	private EnumSet<JakeDownloadMgr.DlOptions> options =
					EnumSet.of(JakeDownloadMgr.DlOptions.None);

	public DownloadInfo(FileObject fileObject, IJakeTask task, EnumSet<JakeDownloadMgr.DlOptions> options) {
		this.fileObject = fileObject;
		this.task = task;
		this.options = options;
	}

	public DownloadInfo(FileObject fileObject, EnumSet<JakeDownloadMgr.DlOptions> options) {
		this.fileObject = fileObject;
		this.options = options;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public IJakeTask getTask() {
		return task;
	}

	public void setTask(IJakeTask task) {
		this.task = task;
	}

	public EnumSet<JakeDownloadMgr.DlOptions> getOptions() {
		return options;
	}

	public void setOptions(EnumSet<JakeDownloadMgr.DlOptions> options) {
		this.options = options;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public FileObject getFileObject() {
		return fileObject;
	}

	public void setFileObject(FileObject fileObject) {
		this.fileObject = fileObject;
	}
}