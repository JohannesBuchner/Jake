package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.TrustState;
import com.jakeapp.gui.swing.JakeMainApp;
import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * PeopleListActions - extends the ProjectAction to save the PeopleList
 * (needed to determine the people that are selected)
 */
public abstract class PeopleListAction extends ProjectAction {
    private static final Logger log = Logger.getLogger(PeopleListAction.class);
    private JList list;

    public PeopleListAction(JList list) {
        setList(list);
    }

    /**
     * Checks a ProjectMember in the list for a specific TrustState.
     *
     * @param trust: The TrustState to compare.
     * @return true if TrustState is equal, false if not equal or != 1 members selected.
     */
    protected boolean checkProjectMemberStatus(TrustState trust) {
        boolean selected;
        if (getList().getSelectedValue() != null) {
            ProjectMember member = (ProjectMember) getList().getSelectedValue();
            selected = member.getTrustState() == trust;
        } else {
            selected = false;
        }
        return selected;
    }

    /**
     * Set the TrustState for selected People in JList.
     *
     * @param trust: new truststate.
     */
    protected void actionOnSelectedPeople(TrustState trust) {
        // support multiselect
        for (Object oMember : getList().getSelectedValues()) {
            ProjectMember member = (ProjectMember) oMember;

            if (member == null) {
                log.warn("Action TrustNoPeopleAction failed for " + oMember);
                return;
            } else {
                JakeMainApp.getApp().getCore().peopleSetTrustState(
                        getProject(), member, trust);
            }
        }
    }


    public JList getList() {
        return list;
    }

    public void setList(JList list) {
        this.list = list;
    }
}
