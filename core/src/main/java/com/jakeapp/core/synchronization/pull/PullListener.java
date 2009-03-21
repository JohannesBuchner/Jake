package com.jakeapp.core.synchronization.pull;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.services.IProjectsFileServices;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.jake.ics.filetransfer.TransferWatcherThread;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import org.apache.log4j.Logger;

public class PullListener implements INegotiationSuccessListener {

	private static Logger log = Logger.getLogger(PullListener.class);

	private ChangeListener changeListener;

	private JakeObject jo;

	private IProjectsFileServices projectsFileServices;

	public PullListener(JakeObject jo, ChangeListener changeListener,
			IProjectsFileServices projectsFileServices) {
		this.changeListener = changeListener;
		this.jo = jo;
		this.projectsFileServices = projectsFileServices;
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
