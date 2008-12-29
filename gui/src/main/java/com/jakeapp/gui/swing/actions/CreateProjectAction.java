package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.JakeMainHelper;
import net.roydesign.ui.FolderDialog;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 12:20:54 AM
 */
public class CreateProjectAction extends ProjectAction {
    private static final Logger log = Logger.getLogger(CreateProjectAction.class);

    public CreateProjectAction(Project project) {
        super(project);
    }

    public CreateProjectAction() {
        super();
    }


    public void actionPerformed(ActionEvent actionEvent) {
        log.info("Create Project: " + getProject());

        //FileDialog fd = new FileDialog(JakeMainView.getMainView().getFrame(), "Choose Directory", FileDialog.LOAD);
        //fd.setVisible(true);

        FolderDialog fod = new FolderDialog(JakeMainView.getMainView().getFrame(), "Choose Directory");
        fod.setVisible(true);
    }


    @Override
    public void updateAction() {

        if (getProject() != null) {
            String oldName = (String) getValue(Action.NAME);
            String newName = JakeMainHelper.getProjectStartStopString(getProject());

            putValue(Action.NAME, newName);

            firePropertyChange(Action.NAME, oldName, newName);
        }

        setEnabled(getProject() != null);
    }
}