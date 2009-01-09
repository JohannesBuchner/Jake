package com.jakeapp.core.services;

import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.util.ApplicationContextFactory;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.UUID;
import java.io.FileNotFoundException;
import java.io.File;


public class ProjectsManagingServiceImpl implements IProjectsManagingService {
    private List<Project> projectList = new ArrayList<Project>();
    private ApplicationContextFactory applicationContextFactory;

    private IProjectDao projectDao;


    /************** GETTERS & SETTERS *************/

    public IProjectDao getProjectDao() {
        return projectDao;
    }

    public void setProjectDao(IProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    public ApplicationContextFactory getApplicationContextFactory() {
        return applicationContextFactory;
    }

    public void setApplicationContextFactory(ApplicationContextFactory applicationContextFactory) {
        this.applicationContextFactory = applicationContextFactory;
    }

    /******** STARTING IMPLEMENTATIONS **************/
    @Override
    public List<Project> getProjectList() {

        return null;
    }

    @Override
    public List<Project> getProjectList(InvitationState state) {
        if (state == InvitationState.ACCEPTED) {
            List<Project> projects = new ArrayList<Project>();


            Project pr1 = new Project("Desktop", new UUID(212, 383), null,
                          new File ("")
            //        new File(FileUtilities.getUserHomeDirectory() + FileUtilities.getPathSeparator() + "Desktop")
            );
            pr1.setStarted(true);
            pr1.setInvitationState(InvitationState.ACCEPTED);
            projects.add(pr1);

            Project pr2 = new Project("Downloads", new UUID(222, 373), null,
                    new File ("")
            //        new File(FileUtilities.getUserHomeDirectory() + FileUtilities.getPathSeparator() + "Downloads")
            );
            pr2.setInvitationState(InvitationState.ACCEPTED);
            projects.add(pr2);

            Project pr3 = new Project("Jake", new UUID(232, 363), null,
                    new File ("")
            //        new File(FileUtilities.getUserHomeDirectory() + FileUtilities.getPathSeparator() + "Jake")
            );
            pr3.setInvitationState(InvitationState.ACCEPTED);
            projects.add(pr3);

            // Yes, we need a windows testing project too...
            Project pr4 = new Project("Windows test", new UUID(242, 353), null, new File("C:\\test"));
            pr4.setInvitationState(InvitationState.ACCEPTED);
            projects.add(pr4);
            return projects;


        } else if (state == InvitationState.INVITED) {
            List<Project> invitedProjects = new ArrayList<Project>();
                   invitedProjects.clear(); // TMP RESET PROJECTS

        Project ipr1 = new Project("DEMO INVITATION 1", new UUID(212, 33), null, new File(""));
        ipr1.setInvitationState(InvitationState.INVITED);
        invitedProjects.add(ipr1);

        Project ipr2 = new Project("DEMO INVITATION 2", new UUID(222, 33), null, new File(""));
        ipr2.setInvitationState(InvitationState.INVITED);
        invitedProjects.add(ipr2);

        Project ipr3 = new Project("DEMO INVITATION 3", new UUID(232, 33), null, new File(""));
        ipr3.setInvitationState(InvitationState.INVITED);
        invitedProjects.add(ipr3);
        return invitedProjects;

        }


        return null;
    }

    @Override
    public Project createProject(String name, String rootPath, MsgService msgService)
            throws FileNotFoundException, IllegalArgumentException {
        Project project = new Project(name, UUID.randomUUID(), msgService, new File(rootPath));




        return null; // TODO
    }

    @Override
    public boolean startProject(Project project)
            throws IllegalArgumentException, FileNotFoundException {
        return false; // TODO
    }

    @Override
    public boolean stopProject(Project project)
            throws IllegalArgumentException, FileNotFoundException {
        return false; // TODO
    }

    @Override
    public void closeProject(Project project) throws IllegalArgumentException,
            FileNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public Project openProject(String project) throws IllegalArgumentException,
            FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean deleteProject(Project project)
            throws IllegalArgumentException,
            SecurityException, FileNotFoundException {
        return false; // TODO
    }

    @Override
    public Map<Project, List<LogEntry<? extends ILogable>>> getLog() {
        return null; // TODO
    }

    @Override
    public List<LogEntry<? extends ILogable>> getLog(Project project)
            throws IllegalArgumentException {
        return null; // TODO
    }

    @Override
    public List<LogEntry<? extends ILogable>> getLog(JakeObject jakeObject)
            throws IllegalArgumentException {
        return null; // TODO
    }

    @Override
    public void assignUserToProject(Project project, UserId userId)
            throws IllegalArgumentException, IllegalAccessException {
        // TODO
    }

    @Override
    public void setTrust(Project project, UserId userid, TrustState trust) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<FileObject> getFiles(Project project, String relPath) throws IllegalArgumentException {
        return null; // TODO
    }

    @Override
    public List<NoteObject> getNotes(Project project) throws IllegalArgumentException, ProjectNotLoadedException {

        if(false)  // TODO check if project is loaded
            throw new ProjectNotLoadedException("Project with uuid " + project.getUserId() + " is not loaded");

        // todo replace with dao access
        List<NoteObject> list = new ArrayList<NoteObject>();
        list.add(new NoteObject(new UUID(1, 1), project, "Project: " + project.getName()));
        list.add(new NoteObject(new UUID(1, 1), project, "If you have five dollars and Chuck Norris has five dollars, Chuck Norris has more money than you"));
        list.add(new NoteObject(new UUID(2, 1), project, "Apple pays Chuck Norris 99 cents every time he listens to a song."));
        list.add(new NoteObject(new UUID(3, 1), project, "Chuck Norris is suing Myspace for taking the name of what he calls everything around you."));
        list.add(new NoteObject(new UUID(4, 1), project, "Chuck Norris destroyed the periodic table, because he only recognizes the element of surprise."));
        list.add(new NoteObject(new UUID(4, 1), project, "Chuck Norris can kill two stones with one bird."));
        list.add(new NoteObject(new UUID(5, 1), project, "The leading causes of death in the United States are: 1. Heart Disease 2. Chuck Norris 3. Cancer."));
        list.add(new NoteObject(new UUID(6, 1), project, "Chuck Norris does not sleep. He waits."));
        list.add(new NoteObject(new UUID(7, 1), project, "There is no theory of evolution. Just a list of animals Chuck Norris allows to live. "));
        list.add(new NoteObject(new UUID(8, 1), project, "Guns don't kill people, Chuck Norris does."));
        return list;
    }
}
