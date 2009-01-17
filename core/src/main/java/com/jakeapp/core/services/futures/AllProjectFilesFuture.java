package com.jakeapp.core.services.futures;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import java.util.List;


public class AllProjectFilesFuture extends
	AvailableLaterObject<List<FileObject>> {

	private IFileObjectDao fileObjectDao;
	
	private void setFileObjectDao(IFileObjectDao fileObjectDao) {
		this.fileObjectDao = fileObjectDao;
	}

	private IFileObjectDao getFileObjectDao() {
		return fileObjectDao;
	}
	
	public AllProjectFilesFuture(AvailabilityListener listener, IFileObjectDao dao) {
		super(listener);
		this.setFileObjectDao(dao);
	}

	@Override
	public void run() {
		//FIXME simplicistic implementation
		this.set(this.getFileObjectDao().getAll());
	}
}
