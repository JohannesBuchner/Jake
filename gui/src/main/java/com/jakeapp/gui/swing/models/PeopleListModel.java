package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.controls.MutableListModel;
import com.jakeapp.gui.swing.exceptions.PeopleOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.JakeHelper;
import org.apache.log4j.Logger;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Capsulates ProjectMember into a ListModel.
 */
public class PeopleListModel extends AbstractListModel
		  implements MutableListModel, ProjectSelectionChanged, ProjectChanged {
	private static final Logger log = Logger.getLogger(PeopleListModel.class);

	private List<ProjectMember> people;
	private Project project;

	public PeopleListModel() {

		this.people = new ArrayList<ProjectMember>();
		
		// register for events
		JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);
		JakeMainApp.getApp().addProjectSelectionChangedListener(this);

		updateModel();
	}

	public int getSize() {
		return this.people != null ? this.people.size() : 0;
	}

	public Object getElementAt(int i) {
//		if (i == 0) {
		// modify first projectMember(WE)
//			ProjectMember member = people.get(0);
//			return member;
//		} else {
		return this.people.get(i);
//		}
	}


	public void projectChanged(ProjectChangedEvent ev) {
		updateModel();
	}

	private void updateModel() {
		try {
			this.people = JakeMainApp.getApp().getCore().getPeople(getProject());
		} catch (PeopleOperationFailedException e) {
			this.people = new ArrayList<ProjectMember>();
			ExceptionUtilities.showError(e);
		}

		this.fireContentsChanged(this, 0, getSize());
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project pr) {
		this.project = pr;

		updateModel();
	}

	@Override
	public boolean isCellEditable(int index) {

		// we are not editable!
		if (index > 0)
			return true;
		else
			return false;
	}

	@Override
	public void setValueAt(Object value, int index) {
		if (!JakeMainApp.getApp().getCore().setPeopleNickname(getProject(), people.get(index), (String) value)) {

			JakeHelper.showMsgTranslated("PeopleListRenameNicknameInvalid", JOptionPane.WARNING_MESSAGE);

			// redraw
			fireContentsChanged(this, index, index);
		}
	}
}