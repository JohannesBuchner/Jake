package com.jakeapp.violet.actions.global.serve;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.metastatic.rsync.v2.FileInfo;

import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.TransferWatcher;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.protocol.files.IRequestMarshaller;
import com.jakeapp.violet.protocol.files.RequestFileMessage;

public class BlockingFileTransfer {

	private static Logger log = Logger.getLogger(BlockingFileTransfer.class);

	public static InputStream requestFile(ProjectModel model,
			IRequestMarshaller requestMarshaller, RequestFileMessage msg,
			final INegotiationSuccessListener listener)
			throws InterruptedException, NotLoggedInException {
		ITransferMethod method = model
				.getIcs()
				.getTransferMethodFactory()
				.getTransferMethod(model.getIcs().getMsgService(),
						model.getIcs().getStatusService().getUserid());

		String filename = requestMarshaller.serialize(msg);

		FileRequest sigfr = new FileRequest(filename, false, msg.getUser());
		final Semaphore s = new Semaphore(0);
		final AtomicBoolean success = new AtomicBoolean();
		final Container<IFileTransfer> ft = new Container<IFileTransfer>();
		method.request(sigfr, new INegotiationSuccessListener() {

			@Override
			public void succeeded(IFileTransfer ft1) {
				success.set(true);
				ft.setValue(ft1);
				s.release();
				if (listener != null)
					listener.succeeded(ft1);
			}

			@Override
			public void failed(Exception reason) {
				success.set(false);
				s.release();
				if (listener != null)
					listener.failed(reason);
			}
		});
		s.acquire();
		if (!success.get()) {
			log.debug("other side didn't send a signature, so "
					+ "we can't provide a delta.");
			return null;
		}
		new Thread(new TransferWatcher(ft.getValue(), new ITransferListener() {

			@Override
			public void onUpdate(AdditionalFileTransferData transfer,
					Status status, double progress) {
				// don't care
			}

			@Override
			public void onSuccess(AdditionalFileTransferData transfer) {
				success.set(true);
				s.release();
			}

			@Override
			public void onFailure(AdditionalFileTransferData transfer,
					String error) {
				success.set(false);
				s.release();
			}
		})).start();
		s.acquire();
		if (!success.get()) {
			log.debug("other side didn't complete to send a "
					+ "signature, so we can't provide a delta.");
			return null;
		}
		// got the signature in
		File f = ft.getValue().getLocalFile();
		try {
			return new FileInputStream(f);
		} catch (FileNotFoundException e) {
			return null;
		}
	}
}
