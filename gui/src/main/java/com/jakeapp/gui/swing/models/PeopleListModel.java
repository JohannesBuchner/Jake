package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.controls.MutableListModel;
import com.jakeapp.gui.swing.helpers.JakeMainHelper;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.List;

/**
 * Capsulates the People into a ListModel
 * User: studpete
 * Date: Jan 4, 2009
 * Time: 9:06:39 AM
 */
public class PeopleListModel extends AbstractListModel
        implements MutableListModel, ProjectSelectionChanged, ProjectChanged {
    private static final Logger log = Logger.getLogger(PeopleListModel.class);

    private List<ProjectMember> people;
    private Project project;

    public PeopleListModel() {

        // register for events
        JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);
        JakeMainApp.getApp().addProjectSelectionChangedListener(this);

        updateModel();
    }

    public int getSize() {
        return people != null ? people.size() : 0;
    }

    public Object getElementAt(int i) {
        return people.get(i);
    }


    public void projectChanged(ProjectChangedEvent ev) {
        updateModel();
    }

    private void updateModel() {
        people = JakeMainApp.getApp().getCore().getPeople(getProject());

        this.fireContentsChanged(this, 0, getSize());
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project pr) {
        this.project = pr;

        updateModel();
    }

    @Override
    public boolean isCellEditable(int index) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int index) {
        if (!JakeMainApp.getApp().getCore().setPeopleNickname(getProject(), people.get(index), (String) value)) {

            JakeMainHelper.showMsg("PeopleListRenameNicknameInvalid", JOptionPane.WARNING_MESSAGE);

            // redraw
            fireContentsChanged(this, index, index);
        }
    }
}