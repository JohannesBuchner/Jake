package com.jakeapp.violet.actions.project.interact.pull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.metastatic.rsync.Rdiff;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.StatusUpdate;
import com.jakeapp.jake.fss.HashValue;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.TransferWatcher;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.files.IRequestMarshaller;
import com.jakeapp.violet.protocol.files.RequestFileMessage;
import com.jakeapp.violet.protocol.files.RequestFileMessage.RequestType;

/**
 * Downloads the file from another user. If storeInFss = false, the File where
 * it is stored is given back.
 * 
 * @author johannes
 */
class FileRequestAction extends AvailableLaterObject<File> {

	private IFileTransferService transferService;

	private FileRequest request;

	private Semaphore sem;

	private Exception innerException;

	private ProjectModel model;

	protected IFileTransfer fileTransfer;

	private IRequestMarshaller requestMarshaller = DI
			.getImpl(IRequestMarshaller.class);

	private LogEntry logEntry;

	private JakeObject jo;

	private RequestFileMessage msg;

	private User peer;

	private boolean storeInFss;

	private INegotiationSuccessListener helperNegotiationListener;

	private ITransferListener helperListener;

	private static Logger log = Logger.getLogger(FileRequestAction.class);

	@Override
	public String toString() {
		return getClass().getSimpleName() + "]" + "transferService="
				+ transferService + ", request=" + request + "]";
	}

	public FileRequestAction(ProjectModel model, User peer, LogEntry logEntry,
			boolean storeInFss) {
		super();
		this.model = model;
		this.helperListener = new ITransferListener() {

			@Override
			public void onFailure(AdditionalFileTransferData transfer,
					String error) {
				innerException = new Exception(error);
				sem.release();
			}

			@Override
			public void onSuccess(AdditionalFileTransferData transfer) {
				sem.release();
			}

			@Override
			public void onUpdate(AdditionalFileTransferData transfer,
					Status status, double progress) {
				setStatus(new StatusUpdate(progress, status.toString()));
			}
		};

		this.helperNegotiationListener = new INegotiationSuccessListener() {

			@Override
			public void failed(Exception reason) {
				innerException = reason;
				sem.release();
			}

			@Override
			public void succeeded(IFileTransfer ft) {
				sem.release();
				fileTransfer = ft;
			}

		};

		this.sem = new Semaphore(0);
		this.innerException = null;
		this.logEntry = logEntry;
		this.jo = logEntry.getWhat();
		log.debug(this.toString());
		this.peer = peer;
		this.storeInFss = storeInFss;
	}

	@Override
	public File calculate() throws Exception {
		UserId user = DI.getUserId(peer.getUserId());

		if (model.getFss().fileExists(jo.getRelPath())) {
			log.debug("requesting a delta");
			this.msg = RequestFileMessage.createRequestDeltaMessage(
					model.getProjectid(), user, logEntry);
		} else {
			log.debug("requesting the full file");
			this.msg = RequestFileMessage.createRequestFileMessage(
					model.getProjectid(), user, logEntry);
		}
		String contentname = this.requestMarshaller.serialize(msg);
		log.debug("content addressed with: " + contentname);
		this.request = new FileRequest(contentname, false, msg.getUser());

		this.transferService.request(this.request,
				this.helperNegotiationListener);
		log.debug("waiting for negotiation-success-listener");
		sem.acquire();

		if (this.innerException != null) {
			throw innerException;
		}

		new Thread(new TransferWatcher(fileTransfer, helperListener)).start();

		sem.acquire();
		if (this.innerException != null) {
			throw innerException;
		}
		// success so far.

		return checkPulledFile();
	}

	private File checkPulledFile() throws Exception {
		File local = null;
		IFSService fss = model.getFss();

		if (msg.getType() == RequestType.DELTA) {
			File merge = File.createTempFile("merge", "recv");
			merge.deleteOnExit();

			log.debug("merging delta " + local);
			Rdiff rdiff = new Rdiff();
			rdiff.rebuildFile(new File(fss.getFullpath(jo.getRelPath())),
					new FileInputStream(this.fileTransfer.getLocalFile()),
					new FileOutputStream(merge));
			local = merge;
		} else {
			local = this.fileTransfer.getLocalFile();
		}
		log.debug("checking file " + local);

		HashValue hash = fss.calculateHash(new FileInputStream(local));

		if (!hash.equals(logEntry.getHow())) {
			throw new Exception("hash doesn't match");
		}

		if (storeInFss) {
			try {
				log.info("storing in file system");
				fss.writeFileStream(jo.getRelPath(), new FileInputStream(local));
			} catch (Exception e) {
				throw new Exception("copying file failed:", e);
			}
			local.delete();
			return null;
		} else {
			return local;
		}
	}

}
