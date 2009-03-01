package com.jakeapp.core.synchronization;

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
import com.jakeapp.core.synchronization.exceptions.PullFailedException;
import com.jakeapp.core.services.IProjectsFileServices;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.log4j.Logger;

import java.io.*;

public class PullWatcher implements ITransferListener {

	private static Logger log = Logger.getLogger(PullWatcher.class);


	@Autowired
	private IProjectsFileServices projectsFileServices;

	@Autowired
	private ProjectApplicationContextFactory applicationContextFactory;


	public IProjectsFileServices getProjectsFileServices() {
		return projectsFileServices;
	}

	public void setProjectsFileServices(IProjectsFileServices projectsFileServices) {
		this.projectsFileServices = projectsFileServices;
	}

	IFSService getFSS(Project p) {
		return this.getProjectsFileServices().getProjectFSService(p);
	}


	private ChangeListener changeListener;

	@SuppressWarnings("unused")
	private IFileTransfer ft;

	private JakeObject jakeObject;

	public PullWatcher(
			JakeObject jo,
			ChangeListener cl,
			IFileTransfer ft,
			ProjectApplicationContextFactory applicationContextFactory
	) {
		this.changeListener = cl;
		this.ft = ft;
		this.jakeObject = jo;
		this.applicationContextFactory = applicationContextFactory;
	}

	@Override
	public void onFailure(AdditionalFileTransferData transfer, String error) {
		changeListener.pullFailed(jakeObject, new PullFailedException(error));
		log.error("transfer for " + jakeObject + " failed: " + error);
	}

	@Override
	@Transactional
	public void onSuccess(AdditionalFileTransferData transfer) {
		log.info("transfer for " + jakeObject + " succeeded");
		FileInputStream data;
		try {
			data = new FileInputStream(transfer.getDataFile());
		} catch (FileNotFoundException e2) {
			log.error("opening file failed:", e2);
			return;
		}
		if (jakeObject instanceof NoteObject) {
			NoteObject no;
			try {
				no = applicationContextFactory.getNoteObjectDao(jakeObject.getProject()).get(jakeObject.getUuid());
			} catch (Exception e1) {
				log.error("404", e1);
				return;
			}

			BufferedReader bis = new BufferedReader(new InputStreamReader(data));
			String content;
			try {
				content = bis.readLine(); // TODO: read whole thing
				bis.close();
			} catch (IOException e) {
				content = "foo";
			}
			no.setContent(content);
			changeListener.pullDone(jakeObject);
		}
		if (jakeObject instanceof FileObject) {
			String target = ((FileObject) jakeObject).getRelPath();
			try {
				getFSS(jakeObject.getProject()).writeFileStream(target, data);
			} catch (Exception e) {
				log.error("writing file failed:", e);
				return;
			}
			changeListener.pullDone(jakeObject);
		}
		try {
			data.close();
		} catch (IOException ignored) {
			log.debug("We don't care 'bout this exception");
		}
	}

	@Override
	public void onUpdate(AdditionalFileTransferData transfer, Status status,
						 double progress) {
		log.info("progress for " + jakeObject + " : " + status + " - " + progress);
		changeListener.pullProgressUpdate(jakeObject, status, progress);
	}

}
