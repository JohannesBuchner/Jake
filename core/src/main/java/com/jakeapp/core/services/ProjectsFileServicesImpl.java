package com.jakeapp.core.services;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.fss.exceptions.NotADirectoryException;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import org.apache.log4j.Logger;


public class ProjectsFileServicesImpl implements IProjectsFileServices {
   private static Logger log = Logger.getLogger(ProjectsFileServicesImpl.class);


    private Map<UUID, IFSService> fileServices;


    public ProjectsFileServicesImpl()
    {
        fileServices = new HashMap<UUID, IFSService>();
    }

	@Override
    public IFSService startProject(Project project) {
        if(this.fileServices.containsKey(UUID.fromString(project.getProjectId())))
            return this.fileServices.get(UUID.fromString(project.getProjectId()));

        try {
            IFSService fss = new FSService();
            fss.setRootPath(project.getRootPath());
            fileServices.put(UUID.fromString(project.getProjectId()), fss);

            return fss;
        } catch (NoSuchAlgorithmException e) {
            log.warn("Got a NoSuchAlgorithmException");
            return null;
        } catch (NotADirectoryException e) {
            log.warn("Got a NotADirectoryException");
            return null;

        } catch (IOException e) {
            log.warn("Got a IOException");
            return null;
        }
    }

    @Override
    public IFSService getProjectFSService(Project project) throws ProjectNotLoadedException {
        if(fileServices.containsKey(UUID.fromString(project.getProjectId())))
        {
            return fileServices.get(UUID.fromString(project.getProjectId()));
        }
        throw new ProjectNotLoadedException("Project with uuid " + project.getProjectId() + " not loaded");
    }

    public IFSService getProjectFSServiceByUUID(String uuid) throws ProjectNotLoadedException {
        UUID projectUuid = UUID.fromString(uuid);
        if(fileServices.containsKey(projectUuid))
        {
            return fileServices.get(projectUuid);
        }
        throw new ProjectNotLoadedException("Project with uuid " + uuid + " not loaded");
    }


    @Override
    public void stopProject(Project project) {
        fileServices.remove(project);
    }
}
