package com.jakeapp.core.services;

import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.UserId;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.FileNotFoundException;


public class ProjectServiceMock implements IProjectService {
    private List<Project> projectList = new ArrayList<Project>();


    @Override
    public List<Project> getProjectList() {

        return null;
    }

    @Override
    public Project createProject(String name, String rootPath)
            throws FileNotFoundException, IllegalArgumentException {
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
}
