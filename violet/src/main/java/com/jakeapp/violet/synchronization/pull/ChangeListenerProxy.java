package com.jakeapp.violet.synchronization.pull;

import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.violet.model.JakeObject;


abstract class ChangeListenerProxy implements ChangeListener {
	
	private ChangeListener innercl;
	
	public ChangeListenerProxy(ChangeListener cl) {
		this.setInnercl(cl);
	}

	@Override
	public INegotiationSuccessListener beganRequest(JakeObject jo) {
		return this.getInnercl().beganRequest(jo);
	}

	@Override
	public void pullDone(JakeObject jo) {
		this.getInnercl().pullDone(jo);

	}

	@Override
	public void pullNegotiationDone(JakeObject jo) {
		this.getInnercl().pullNegotiationDone(jo);
	}

	@Override
	public void pullProgressUpdate(JakeObject jo, Status status, double progress) {
		this.getInnercl().pullProgressUpdate(jo, status, progress);
	}
	
	@Override
	public void pullFailed(JakeObject jo, Throwable reason) {
		this.getInnercl().pullFailed(jo,reason);
	}

	protected void setInnercl(ChangeListener innercl) {
		if(innercl == null) 
			throw new NullPointerException();
		this.innercl = innercl;
	}

	protected ChangeListener getInnercl() {
		return this.innercl;
	}

}
