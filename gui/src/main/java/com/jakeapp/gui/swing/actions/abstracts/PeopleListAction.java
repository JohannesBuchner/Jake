package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.synchronization.UserInfo;
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
     * Checks a UserId in the list for a specific TrustState.
     *
     * @param trust: The TrustState to compare.
     * @return true if TrustState is equal, false if not equal or != 1 members selected.
     */
    protected boolean checkUserIdStatus(TrustState trust) {
        boolean selected;
        if (getList().getSelectedValue() != null) {
            UserInfo userInfo = (UserInfo) getList().getSelectedValue();
            selected = userInfo.getTrust() == trust;
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
            UserId member = (UserId) oMember;

            if (member == null) {
                log.warn("Action TrustNoPeopleAction failed for " + oMember);
                return;
            } else {
                JakeMainApp.getCore().setTrustState(
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
