package com.jakeapp.violet.actions.global.serve;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;
import org.metastatic.rsync.ChecksumPair;
import org.metastatic.rsync.Rdiff;

import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.TransferWatcher;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.violet.actions.project.local.AttributedCalculator;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.model.attributes.Attributed;
import com.jakeapp.violet.model.exceptions.NoSuchLogEntryException;
import com.jakeapp.violet.protocol.files.IRequestMarshaller;
import com.jakeapp.violet.protocol.files.RequestFileMessage;
import com.jakeapp.violet.protocol.files.RequestFileMessage.RequestType;
import com.jakeapp.violet.protocol.msg.ILogEntryMarshaller;

public class ProjectRequestListener implements IncomingTransferListener,
		FileRequestFileMapper {

	private static Logger log = Logger.getLogger(ProjectRequestListener.class);

	private IRequestMarshaller requestMarshaller = DI
			.getImpl(IRequestMarshaller.class);

	private ILogEntryMarshaller logEntryMarshaller = DI
			.getImpl(ILogEntryMarshaller.class);

	private ProjectModel model;

	private ISyncListener listener;

	public ProjectRequestListener(ProjectModel model, ISyncListener l) {
		this.model = model;
		this.listener = l;
	}

	/**
	 * decides if we know about the subject of this request.
	 * 
	 * @param req
	 *            Incoming request
	 * @return null if we don't know this version for some reason, the
	 *         corresponding logEntry otherwise
	 */
	private LogEntry getLogEntryForRequest(RequestFileMessage req) {
		UUID leuuid = UUID.fromString(req.getIdentifier());
		LogEntry le;
		try {
			le = model.getLog().getById(leuuid, false);
		} catch (NoSuchLogEntryException e) {
			log.debug("we don't know about this version");
			return null;
		}
		log.debug("got request for file belonging to entry " + leuuid);
		JakeObject fo = le.getWhat();

		LogEntry version;
		try {
			version = model.getLog().getLastOfJakeObject(fo, false);
		} catch (NoSuchLogEntryException e1) {
			log.debug("we don't have a version");
			return null;
		}
		if (!version.getId().equals(leuuid)) {
			log.debug("we have a different last version");
			return null;
		}
		Attributed status;
		try {
			status = AttributedCalculator.calculateAttributed(model.getFss(),
					model.getLog(), fo);
		} catch (Exception e) {
			log.debug("status of the requested object is weird", e);
			return null;
		}
		if (status.isModifiedLocally()) {
			log.debug("can't distribute tainted object");
			return null;
		}
		return le;
	}

	@Override
	public boolean accept(FileRequest fr) {
		try {
			log.info("incoming request: " + fr);
			RequestFileMessage req = requestMarshaller
					.decodeRequestFileMessage(fr.getFileName(), fr.getPeer());

			if (!canHandleFileRequest(req)) {
				return false;
			}
			if (!listener.acceptSending(req.getUser(),
					new JakeObject(req.getIdentifier()))) {
				return false;
			}

			log.info("we accept the request");
			return true;
		} catch (Exception e) {
			log.warn("unexpected Exception", e);
			return false;
		}
	}

	@Override
	public void started(final IFileTransfer t) {
		log.debug("we are transmitting." + t);
		new Thread(new TransferWatcher(t, new ITransferListener() {

			@Override
			public void onFailure(AdditionalFileTransferData transfer,
					String error) {
				log.warn("transmitting failed: " + error);
				listener.sendingFailed(new User(t.getPeer().getUserId()), t
						.getFileRequest().getFileName(), error);
			}

			@Override
			public void onSuccess(AdditionalFileTransferData transfer) {
				log.info("transmitting was successful");
				listener.sendingSucceeded(new User(t.getPeer().getUserId()), t
						.getFileRequest().getFileName());
			}

			@Override
			public void onUpdate(AdditionalFileTransferData transfer,
					Status status, double progress) {
				log.debug("transmitting update: " + progress + " - " + status);
				listener.sendingUpdateProgress(
						new User(t.getPeer().getUserId()), t.getFileRequest()
								.getFileName(), status.toString(), progress);
			}

		})).start();
	}

	private File getDeliveryDirectory() {
		String systmpdir = System.getProperty("java.io.tmpdir", "");
		if (!systmpdir.endsWith(File.separator))
			systmpdir = systmpdir + File.separator;

		File f = new File(systmpdir, "jakeDelivery");
		f.mkdir();
		return f;
	}

	private boolean canHandleFileRequest(RequestFileMessage req) {
		if (req.getType() == RequestType.LOGS) {
			return true;
		} else {
			if (req.getType() == RequestType.FILE
					|| req.getType() == RequestType.SIGNATURE
					|| req.getType() == RequestType.DELTA) {
				LogEntry le = getLogEntryForRequest(req);
				if (le == null) {
					return false;
				}
				String hash = le.getHow();
				JakeObject fo = le.getWhat();

				if (req.getType() == RequestType.SIGNATURE) {
					try {
						if (model.getFss().fileExists(fo.getRelPath()))
							return true;
						else
							return false;
					} catch (InvalidFilenameException e) {
						return false;
					}
				} else {
					try {
						if (!model.getFss()
								.calculateHashOverFile(fo.getRelPath())
								.equals(hash)) {
							log.debug("file was modified locally, so can't send it");
							return false;
						}
					} catch (FileNotFoundException e) {
						return false;
					} catch (InvalidFilenameException e) {
						return false;
					} catch (NotAReadableFileException e) {
						return false;
					}
					return true;
				}
			}
			log.debug("file request type not implemented");
			return false;
		}
	}

	@Override
	public File getFileForRequest(FileRequest fr) {
		try {
			log.info("incoming request: " + fr);
			File od = File.createTempFile(fr.getPeer().getUserId(), "",
					getDeliveryDirectory());
			od.delete();
			od.mkdir();
			od.deleteOnExit();
			File tempfile = new File(od, fr.getFileName());
			tempfile.deleteOnExit();
			OutputStream os = new FileOutputStream(tempfile);

			RequestFileMessage req = requestMarshaller
					.decodeRequestFileMessage(fr.getFileName(), fr.getPeer());

			if (!canHandleFileRequest(req))
				return null;

			if (req.getType() == RequestType.LOGS) {
				List<LogEntry> logs = model.getLog().getAll(true);

				logEntryMarshaller.packLogEntries(model.getProjectid(), logs,
						new GZIPOutputStream(os));
				return tempfile;
			} else {
				if (req.getType() == RequestType.FILE
						|| req.getType() == RequestType.SIGNATURE
						|| req.getType() == RequestType.DELTA) {
					LogEntry le = model.getLog().getById(req.getProjectId(),
							false);
					String hash = le.getHow();
					JakeObject fo = le.getWhat();
					File origfile = new File(model.getFss().getFullpath(
							fo.getRelPath()));
					log.info("original file at " + origfile);

					if (req.getType() == RequestType.DELTA) {
						// so the other guy wants a delta
						// what a smart-ass
						// he has to send his signature first, so lets ask him
						log.debug("requesting signature");
						RequestFileMessage msg = RequestFileMessage
								.createRequestSignatureMessage(
										model.getProjectid(), fr.getPeer(), le);

						InputStream fis = BlockingFileTransfer.requestFile(
								model, requestMarshaller, msg, null);
						if (fis == null) {
							log.debug("requesting signatures did not succeed.");
							return null;
						}
						log.debug("creating delta from signature");
						Rdiff rdiff = new Rdiff();
						List<ChecksumPair> sums = rdiff.readSignatures(fis);
						InputStream is = FSService.readFileStreamAbs(origfile);
						rdiff.makeDeltas(sums, is, os);
						log.debug("delta created");
					}
					if (req.getType() == RequestType.SIGNATURE) {
						// create a signature file
						Rdiff rdiff = new Rdiff();
						InputStream is = FSService.readFileStreamAbs(origfile);
						rdiff.makeSignatures(is, os);
						log.debug("signature created");
					} else {
						// just ship the file
						if (!model.getFss()
								.calculateHashOverFile(fo.getRelPath())
								.equals(hash)) {
							log.debug("can not provide file as we modified it.");
							return null;
						}
						// copy the whole file
						FSService.writeFileStreamAbs(tempfile,
								FSService.readFileStreamAbs(origfile));
						log.debug("file duplicated");
					}
				} else {
					log.warn("can't understand message");
					return null;
				}
				return tempfile;
			}
		} catch (Exception e) {
			log.warn("unexpected Exception", e);
			return null;
		}
	}
}