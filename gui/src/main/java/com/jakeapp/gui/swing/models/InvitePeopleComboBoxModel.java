package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.JakeObjectHelpers;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Special Model to preserve data
 *
 * @author: studpete
 */
// FIXME: could be improved...
public class InvitePeopleComboBoxModel extends DefaultComboBoxModel {
	public InvitePeopleComboBoxModel(Project project) {
		super(convertToProxyMemberProjectList(JakeMainApp.getApp().getCore().getSuggestedPeople(project)).toArray());
	}

	private static List<ProjectMemberProxy> convertToProxyMemberProjectList(List<ProjectMember> members) {
		// proxy
		List<ProjectMemberProxy> list = new ArrayList<ProjectMemberProxy>();

		for (ProjectMember m : members) {
			list.add(new ProjectMemberProxy(m));
		}
		return list;
	}


	private static class ProjectMemberProxy {
		private ProjectMember pm;

		public ProjectMemberProxy(ProjectMember pm) {
			this.setPm(pm);
		}

		@Override
		public String toString() {
			return getPm().getUserId() + " (" + JakeObjectHelpers.getNickOrFullName(getPm(), 30) + ")";
		}

		public ProjectMember getPm() {
			return pm;
		}

		public void setPm(ProjectMember pm) {
			this.pm = pm;
		}
	}
}


