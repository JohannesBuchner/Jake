package com.jakeapp.gui.swing.worker;

import com.jakeapp.gui.swing.worker.tasks.IJakeTask;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

import java.util.EnumSet;

/**
 * Download Information for a File.
 *
 * @author studpete
 */
public class DownloadInfo {
	private boolean started = false;
	private double progress = 0;
	private Status status = Status.initial;
	private IJakeTask task = null;
	private EnumSet<JakeDownloadMgr.DlOptions> options =
					EnumSet.of(JakeDownloadMgr.DlOptions.None);

	public DownloadInfo(IJakeTask task, EnumSet<JakeDownloadMgr.DlOptions> options) {
		this.task = task;
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
}