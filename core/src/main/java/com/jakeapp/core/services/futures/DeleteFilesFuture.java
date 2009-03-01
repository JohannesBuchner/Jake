package com.jakeapp.core.services.futures;

import java.io.FileNotFoundException;
import java.util.List;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;


public class DeleteFilesFuture extends AvailableLaterObject<Integer> {
	
	private IFileObjectDao dao;
	private IFSService fsService;
	List<FileObject> toDelete;
	
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


	public DeleteFilesFuture(IFileObjectDao dao, IFSService fsService,List<FileObject> toDelete) {
		super();
		this.setDao(dao);
		this.setFsService(fsService);
		this.setToDelete(toDelete);
	}
	
	private void deleteFile(FileObject fo) {
		//update
		fo.setDeleted(true);
		this.getDao().persist(fo);
		
		//delete physical file
		try {
			this.getFsService().trashFile(fo.getRelPath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFilenameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Integer calculate() throws Exception {
		for (FileObject fo : this.getToDelete())
			this.deleteFile(fo);
		
		return this.getToDelete().size();
	}
}
