package com.jakeapp.core.synchronization;

import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.TransferWatcherThread;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import org.apache.log4j.Logger;

public class PullListener implements INegotiationSuccessListener {
	private static Logger log = Logger.getLogger(PullListener.class);
	private ProjectApplicationContextFactory db;

	private ChangeListener cl;

	private JakeObject jo;

	public PullListener(JakeObject jo, ChangeListener cl, ProjectApplicationContextFactory db) {
		this.cl = cl;
		this.jo = jo;
		this.db = db;
	}

	@Override
	public void failed(Throwable reason) {
		log.error("pulling failed.", reason);
	}

	@Override
	public void succeeded(IFileTransfer ft) {
		log.info("pulling negotiation succeeded");
		cl.pullNegotiationDone(jo);
		new TransferWatcherThread(ft, new PullWatcher(jo, cl, ft, db));
	}

}
