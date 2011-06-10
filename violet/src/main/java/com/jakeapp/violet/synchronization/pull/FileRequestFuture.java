package com.jakeapp.violet.synchronization.pull;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.ProjectModel;

public class FileRequestFuture extends AvailableLaterObject<IFileTransfer> {

	private IFileTransferService transferService;

	private FileRequest request;

	private Semaphore sem;

	private ChangeListener changeListener;

	private JakeObject jo;

	private Throwable innerException;

	private ProjectModel model;

	private static Logger log = Logger.getLogger(FileRequestFuture.class);

	protected class FileProgressChangeListener extends ChangeListenerProxy {

		public FileProgressChangeListener(ChangeListener cl) {
			super(cl);
		}

		@Override
		public void pullDone(JakeObject jo) {
			log.debug("pull done for " + jo);
			sem.release();
		}

		@Override
		public void pullFailed(JakeObject jo, Throwable reason) {
			log.debug("pull failed for " + jo);
			sem.release();
			innerException = reason;
		}

	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "]" + "transferService="
				+ transferService + ", request=" + request + ", jo=" + jo + "]";
	}

	public FileRequestFuture(ProjectModel model, JakeObject jo,
			FileRequest request, ChangeListener changeListener) {
		super();
		this.model = model;
		this.request = request;
		this.changeListener = new FileProgressChangeListener(changeListener);
		this.jo = jo;
		this.sem = new Semaphore(0);
		this.innerException = null;
		log.debug(this.toString());
	}

	@Override
	public IFileTransfer calculate() throws Exception {
		INegotiationSuccessListener listener = new PullListener(this.jo,
				model.getFss(), changeListener);
		this.transferService.request(this.request,
				new INegotiationSuccessListener() {
					@Override
					public void failed(Throwable reason) {
						FileRequestFuture.this.innerException = reason;
						FileRequestFuture.this.sem.release();
					}

					@Override
					public void succeeded(IFileTransfer ft) {
						FileRequestFuture.this.setInnercontent(ft);
						FileRequestFuture.this.sem.release();
					}
				});
		log.debug("waiting for negotiation-success-listener");

		sem.acquire();

		if (this.innerException != null) {
			try {
				listener.failed(this.innerException);
			} catch (Exception ignored) {
			}
			throw (Exception) this.innerException;
		}

		try {
			listener.succeeded(getInnercontent());
		} catch (Exception ignored) {
		}

		log.debug("waiting for PullListener");
		sem.acquire();
		if (this.innerException != null) {
			log.debug("listener threw Exception: " + this.innerException);
			throw (Exception) this.innerException;
		}

		return this.getInnercontent();
	}
}
