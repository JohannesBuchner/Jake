package com.jakeapp.violet.synchronization.pull;



import org.apache.log4j.Logger;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.filetransfer.TransferWatcherThread;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.violet.model.JakeObject;

public class PullListener implements INegotiationSuccessListener {

	private static Logger log = Logger.getLogger(PullListener.class);

	private ChangeListener changeListener;

	private JakeObject jo;

	private IFSService fss;

	public PullListener(JakeObject jo, IFSService fss,  ChangeListener changeListener) {
		this.changeListener = changeListener;
		this.jo = jo;
		this.fss = fss;
	}

	@Override
	public void failed(Throwable reason) {
		log.error("pulling failed.", reason);
		changeListener.pullFailed(jo, reason);
	}

	@Override
	public void succeeded(IFileTransfer ft) {
		log.info("pulling negotiation succeeded");
		this.changeListener.pullNegotiationDone(this.jo);
		PullVerificationWatcher pw = new PullVerificationWatcher(this.jo, fss, this.changeListener, ft);

		new Thread(new TransferWatcherThread(ft, pw)).start();
	}
}
