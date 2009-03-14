package com.jakeapp.core.synchronization.pull;

import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.core.synchronization.exceptions.PullFailedException;
import com.jakeapp.core.services.IProjectsFileServices;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.log4j.Logger;

import java.io.*;

public class PullWatcher implements ITransferListener {

	private static Logger log = Logger.getLogger(PullWatcher.class);

	private IProjectsFileServices projectsFileServices;

	private IProjectsFileServices getProjectsFileServices() {
		return this.projectsFileServices;
	}

	private void setProjectsFileServices(IProjectsFileServices projectsFileServices) {
		this.projectsFileServices = projectsFileServices;
	}

	private IFSService getFSS(Project p) {
		return this.getProjectsFileServices().getProjectFSService(p);
	}

	private ChangeListener changeListener;

	private IFileTransfer ft;

	private JakeObject jakeObject;

	public PullWatcher(
			JakeObject jo,
			ChangeListener cl,
			IFileTransfer ft,
			IProjectsFileServices projectsFileServices
	) {
		this.changeListener = cl;
		this.ft = ft;
		this.jakeObject = jo;
		this.setProjectsFileServices(projectsFileServices);
	}

	@Override
	public void onFailure(AdditionalFileTransferData transfer, String error) {
		log.error("transfer for " + jakeObject + " failed: " + error);
		changeListener.pullFailed(jakeObject, new PullFailedException(error));
	}

	@Override
	@Transactional
	public void onSuccess(AdditionalFileTransferData data) {
		log.info("transfer for " + jakeObject + " succeeded. (additionalData:" + data + ")");
		try {
			checkPulledFile();
			changeListener.pullDone(jakeObject);
		}catch (Exception reason) {
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
		
		if (!(jakeObject instanceof FileObject)) 
			throw new IllegalStateException("unexpected type of JakeObject");
		
		String target = ((FileObject) jakeObject).getRelPath();
		try {
			getFSS(jakeObject.getProject()).writeFileStream(target, data);
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
		log.info("progress for " + jakeObject + " : " + status + " - " + progress);
		changeListener.pullProgressUpdate(jakeObject, status, progress);
	}

}
