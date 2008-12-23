package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CoreAccessMock implements ICoreAccess {


    @Override
    public List<Project> getMyProjects() {
        List<Project> projects = new ArrayList<Project>();

        Project pr1 = new Project("ASE", null, null, new File("/Users/studpete/Desktop"));
        pr1.setStarted(true);
        projects.add(pr1);

        Project pr2 = new Project("SEPM", null, null, new File("/Users/studpete/"));
        projects.add(pr2);

        Project pr3 = new Project("Freigegebene Musik", null, null, null);
        projects.add(pr3);

        return projects;
    }

    @Override
    public List<Project> getInvitedProjects() {
        List<Project> projects = new ArrayList<Project>();

        Project pr1 = new Project("Urlaubsvideos", null, null, null);
        projects.add(pr1);


        Project pr2 = new Project("Geheimakte", null, null, null);
        projects.add(pr2);

        return projects;
    }
}
