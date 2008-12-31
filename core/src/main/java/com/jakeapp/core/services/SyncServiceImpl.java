package com.jakeapp.core.services;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;

import java.util.List;


public class SyncServiceImpl implements ISyncService {
    @Override
    public void startLogSync(Project project) throws IllegalArgumentException {
        // TODO
    }

    @Override
    public void startLogSync(Project project, UserId userId) throws IllegalArgumentException, IllegalProtocolException {
        // TODO
    }

    @Override
    public void pullObjects(Project project) throws IllegalArgumentException {
        // TODO
    }

    @Override
    public void pullObjects(List<JakeObject> objects) {
        // TODO
    }

    @Override
    public void pushObjects(Project project) throws IllegalArgumentException {
        // TODO
    }

    @Override
    public void pushObjects(List<JakeObject> objects) {
        // TODO
    }

    @Override
    public List<JakeObject> getChangedObjects(Project project) throws IllegalArgumentException {
        return null; // TODO
    }

    @Override
    public List<JakeObject> getOutOfSyncObjects(Project project) throws IllegalArgumentException {
        return null; // TODO
    }

    @Override
    public boolean isObjectLocked(JakeObject object) throws IllegalArgumentException {
        return false; // TODO
    }

    @Override
    public void setObjectLocked(JakeObject object) throws IllegalArgumentException, ProjectNotLoadedException {
        // TODO
    }
}
