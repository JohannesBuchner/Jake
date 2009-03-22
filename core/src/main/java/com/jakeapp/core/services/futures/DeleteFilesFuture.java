package com.jakeapp.core.services.futures;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.logentries.JakeObjectDeleteLogEntry;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.util.UnprocessedBlindLogEntryDaoProxy;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAFileException;

/**
 * <code>AvailableLaterObject</code> deleting some <code>FileObject</code>
 */
public class DeleteFilesFuture extends AvailableLaterObject<Integer> {

	private static final Logger log = Logger.getLogger(ProjectSizeTotalFuture.class);

	private IFileObjectDao dao;
	private UnprocessedBlindLogEntryDaoProxy ledao;
	private IFSService fsService;
	List<FileObject> toDelete;
	boolean trash;
	private User self;

	private IFileObjectDao getDao() {
		return dao;
	}

	private void setDao(IFileObjectDao dao) {
		this.dao = dao;
	}


	private IFSService getFsService() {
		return fsService;
	}

	private void setFsService(IFSService fsService) {
		this.fsService = fsService;
	}


	private List<FileObject> getToDelete() {
		return toDelete;
	}

	private void setToDelete(List<FileObject> toDelete) {
		this.toDelete = toDelete;
	}


	// TODO: UnprocessedBlindLogEntryDAO sucks! Fix this!
	public DeleteFilesFuture(IFileObjectDao dao, UnprocessedBlindLogEntryDaoProxy ledao, User self, IFSService fsService,
					List<FileObject> toDelete, boolean trash) {
		super();
		this.self = self;
		this.setDao(dao);
		this.setLedao(ledao);
		this.setFsService(fsService);
		this.setToDelete(toDelete);
		this.trash = trash;
	}

	private void deleteFile(FileObject fo) {
		log.debug("trashing/deleting a file!!" + fo.getRelPath());

		// update database if object has entry (uuid is true)
		if (fo.getUuid() != null) {
			log.info("We have a UUID to delete: " + fo.getUuid());

			// // OLD CRAP
			// fo.setDeleted(true);
			// this.getDao().persist(fo);
			try {
				log.info("Deleting object " + fo + " from DAO!");
				this.getLedao().create(new JakeObjectDeleteLogEntry(fo, self, "", true));
				this.getDao().delete(fo);
			} catch (NoSuchJakeObjectException e) {
				log.fatal("Couldn't delete object because it didn't exist", e);
				throw new IllegalStateException("Expected JakeObject but it wasn't there", e);
			}
		}

		// delete physical file
		try {
			if (trash) {
				this.getFsService().trashFile(fo.getRelPath());
			} else {
				this.getFsService().deleteFile(fo.getRelPath());
			}
		} catch (FileNotFoundException ignored) {
			log.warn("File not found: ", ignored);
		} catch (InvalidFilenameException ignored) {
			log.warn("Invalid Filename: ", ignored);
		} catch (NotAFileException e) {
			log.warn("Not a File: ", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer calculate() throws Exception {
		for (FileObject fo : this.getToDelete())
			this.deleteFile(fo);

		return this.getToDelete().size();
	}

	public UnprocessedBlindLogEntryDaoProxy getLedao() {
		return ledao;
	}

	public void setLedao(UnprocessedBlindLogEntryDaoProxy ledao) {
		this.ledao = ledao;
	}
}
