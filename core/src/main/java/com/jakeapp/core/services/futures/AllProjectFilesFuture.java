package com.jakeapp.core.services.futures;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.util.availablelater.AvailableNowObject;


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
	
	public AllProjectFilesFuture(IFileObjectDao dao) {
		super(new ArrayList<FileObject>());
		this.setFileObjectDao(dao);
	}


    public AllProjectFilesFuture(ProjectApplicationContextFactory
            applicationContextFactory, Project project) {
		super(new ArrayList<FileObject>());

        log.debug("Creating a AllProjectFilesFuture");

		this.applicationContextFactory = applicationContextFactory;
        this.project = project;
	}



	@Override
	@Transactional
	public List<FileObject> calculate() {
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
        log.debug("Calculating the  AllProjectFilesFuture");

        List<FileObject> result = new ArrayList<FileObject>();
        result = this.applicationContextFactory.getFileObjectDao(project).getAll();

        log.debug("found " + result.size() + " files in the DB ");
        for(FileObject file : result)
        {
            System.out.println("file = " + file);
        }


        result.add(new FileObject(project, "blabla"));

        return result;



//        log.debug("close session");
//        sf.close();
//        sf = null;
//        log.debug("\n\n\n\n\n\n\n\n\n\n\nend thread... \n\n\n\n\n\n\n\n\n\n\n");

	}
}
