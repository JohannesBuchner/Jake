package com.jakeapp.violet.actions.global;

import java.io.File;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.FailoverCapableFileTransferService;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.SimpleSocketFileTransferFactory;
import com.jakeapp.jake.ics.msgservice.IMsgService;
import com.jakeapp.violet.actions.Actions;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.Context;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.ProjectModelImpl;

/**
 * <code>AvailableLaterObject</code> which is responsible for starting or
 * stoping a given <code>Project</code>.
 */
public class StartProjectAction extends AvailableLaterObject<Context> {

	private ProjectDir dir;

	public StartProjectAction(ProjectDir dir) {
		this.dir = dir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Context calculate() throws Exception {
		ProjectModelImpl model = new ProjectModelImpl();

		IFSService fss = DI.getImpl(IFSService.class);
		fss.setRootPath(dir);
		model.setFss(fss);
		ICService ics = DI.getImplForProject(ICService.class, dir);
		model.setIcs(ics);
		model.setPreferences(DI.getPreferencesImpl(new File(fss.getRootPath(),
				DI.getProperty("project.preferenceFilename"))));

		model.setTransfer(createTransferMethod(ics.getMsgService(),
				DI.getUserId(model.getUserid()), ics));

		model.setLog(DI.getImplForProject(Log.class, fss.getRootPath()));

		Actions actions = new Actions(model);

		return new Context(model, actions);
	}

	private IFileTransferService createTransferMethod(IMsgService msg,
			UserId user, ICService ics) {
		IFileTransferService fcfts;
		fcfts = new FailoverCapableFileTransferService();
		if ("true".equals(DI.getProperty("filetransfers.sockets")))
			fcfts.addTransferMethod(new SimpleSocketFileTransferFactory(), msg,
					user);

		ITransferMethodFactory inbandMethod = ics.getTransferMethodFactory();
		if (inbandMethod != null) {
			fcfts.addTransferMethod(inbandMethod, msg, user);
		}
		return fcfts;
	}
}