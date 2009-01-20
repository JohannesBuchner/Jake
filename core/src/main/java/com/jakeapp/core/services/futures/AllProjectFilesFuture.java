package com.jakeapp.core.services.futures;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailabilityListener;
import com.jakeapp.core.util.availablelater.AvailableNowObject;
import com.jakeapp.core.util.ProjectApplicationContextFactory;

import java.util.List;
import java.util.ArrayList;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;


public class AllProjectFilesFuture extends
	/*AvailableLaterObject<List<FileObject>>*/
	AvailableNowObject<List<FileObject>> {

    private static Logger log = Logger.getLogger(AllProjectFilesFuture.class);

	private IFileObjectDao fileObjectDao;


    private ProjectApplicationContextFactory applicationContextFactory;
    private Project project;

	private void setFileObjectDao(IFileObjectDao fileObjectDao) {
		this.fileObjectDao = fileObjectDao;
	}

	private IFileObjectDao getFileObjectDao() {
		return fileObjectDao;
	}
	
	public AllProjectFilesFuture(AvailabilityListener listener, IFileObjectDao dao) {
		super(listener,new ArrayList<FileObject>());
		this.setFileObjectDao(dao);
	}


    public AllProjectFilesFuture(AvailabilityListener listener, ProjectApplicationContextFactory
            applicationContextFactory, Project project) {
		super(listener,new ArrayList<FileObject>());
		
		this.applicationContextFactory = applicationContextFactory;
        this.project = project;
	}

	@Override
	@Transactional
	public void run() {
		//FIXME simplicistic implementation

//        log.debug("\n\n\n\n\n\n\n\n\n\n\nstarting thread & running it... \n\n\n\n\n\n\n\n\n\n\n");

        /**
         *  I'M NOT QUIETE SURE WHAT EXACTLY MADE THIS WORK. PLEASE DON'T REMOVE THE COMMENTS.
         * THAnKS, Dominik
         */


//        SessionFactory sf = (SessionFactory) this
//                .applicationContextFactory.getApplicationContext(project).getBean("sessionFactory");

//        if(sf.isClosed())
//        {
//            log.debug("sf.isClosed()");
//        }
//        else
//        {
//            log.debug("NOT sf.isClosed()");
//        }
//
//        log.debug("open session");
//        sf.openSession();

        List<FileObject> result = new ArrayList<FileObject>();
        result = this.applicationContextFactory.getFileObjectDao(project).getAll();

//        for(FileObject file : result)
//        {
//            System.out.println("file = " + file);
//        }

        this.set(result);



//        log.debug("close session");
//        sf.close();
//        sf = null;
//        log.debug("\n\n\n\n\n\n\n\n\n\n\nend thread... \n\n\n\n\n\n\n\n\n\n\n");

	}
}
