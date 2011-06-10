package com.jakeapp.violet.synchronization.pull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.synchronization.exceptions.PullFailedException;

public class PullVerificationWatcher implements ITransferListener {

	private static Logger log = Logger.getLogger(PullVerificationWatcher.class);

	private ChangeListener changeListener;

	private IFileTransfer ft;

	private JakeObject jakeObject;

	private IFSService fss;

	public PullVerificationWatcher(JakeObject jo, IFSService fss,
			ChangeListener cl, IFileTransfer ft) {
		this.changeListener = cl;
		this.ft = ft;
		this.fss = fss;
		this.jakeObject = jo;
	}

	@Override
	public void onFailure(AdditionalFileTransferData transfer, String error) {
		log.error("transfer for " + jakeObject + " failed: " + error);
		changeListener.pullFailed(jakeObject, new PullFailedException(error));
	}

	@Override
	public void onSuccess(AdditionalFileTransferData data) {
		log.info("transfer for " + jakeObject + " succeeded. (additionalData:"
				+ data + ")");
		try {
			checkPulledFile();
			changeListener.pullDone(jakeObject);
		} catch (Exception reason) {
			log.error("unexpected failure", reason);
			changeListener.pullFailed(jakeObject, reason);
		}
	}

	private boolean checkPulledFile() throws Exception {
		log.debug("checking file " + this.ft.getLocalFile());
		FileInputStream data;
		try {
			data = new FileInputStream(this.ft.getLocalFile());
		} catch (FileNotFoundException e2) {
			throw new Exception("opening file failed:", e2);
		}

		String target = jakeObject.getRelPath();
		try {
			fss.writeFileStream(target, data);
		} catch (Exception e) {
			throw new Exception("writing file failed:", e);
		}
		changeListener.pullDone(jakeObject);
		try {
			data.close();
		} catch (IOException ignored) {
			log.debug("We don't care 'bout this exception");
		}
		return true;
	}

	@Override
	public void onUpdate(AdditionalFileTransferData transfer, Status status,
			double progress) {
		log.info("progress for " + jakeObject + " : " + status + " - "
				+ progress);
		changeListener.pullProgressUpdate(jakeObject, status, progress);
	}

}
