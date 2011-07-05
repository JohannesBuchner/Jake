package com.jakeapp.violet.actions.global.serve;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.violet.context.ProjectModel;
import com.jakeapp.violet.protocol.files.IRequestMarshaller;
import com.jakeapp.violet.protocol.files.RequestFileMessage;

public class BlockingFileTransfer {

	private static Logger log = Logger.getLogger(BlockingFileTransfer.class);

	public static InputStream requestFile(ProjectModel model,
			IRequestMarshaller requestMarshaller, RequestFileMessage msg,
			final INegotiationSuccessListener listener)
			throws NotLoggedInException {
		final ITransferMethod method = model
				.getIcs()
				.getTransferMethodFactory()
				.getTransferMethod(model.getIcs().getMsgService(),
						model.getIcs().getStatusService().getUserid());

		String filename = requestMarshaller.serialize(msg);

		final FileRequest sigfr = new FileRequest(filename, false,
				msg.getUser());

		IFileTransfer ft;
		try {
			ft = AvailableLaterWaiter.await(new FileTransferNegotiationWaiter(
					method, sigfr));
		} catch (Exception e1) {
			log.error("negotiation failed", e1);
			return null;
		}
		try {
			AvailableLaterWaiter.await(new FileTransferWaiter(ft));
		} catch (Exception e1) {
			log.error("transfer failed", e1);
			return null;
		}

		// got the signature in
		File f = ft.getLocalFile();
		try {
			return new FileInputStream(f);
		} catch (FileNotFoundException e) {
			return null;
		}
	}
}
