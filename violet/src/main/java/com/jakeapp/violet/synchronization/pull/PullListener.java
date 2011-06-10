package com.jakeapp.violet.synchronization.pull;


import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.filetransfer.TransferWatcherThread;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.violet.model.JakeObject;

public class PullListener implements INegotiationSuccessListener {

	private static Logger log = Logger.getLogger(PullListener.class);

	private ChangeListener changeListener;

	private JakeObject jo;

	public PullListener(JakeObject jo, ChangeListener changeListener) {
		this.changeListener = changeListener;
		this.jo = jo;
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
		PullWatcher pw = new PullWatcher(this.jo, this.changeListener, ft,
				this.projectsFileServices);

		new Thread(new TransferWatcherThread(ft, pw)).start();
	}
}
