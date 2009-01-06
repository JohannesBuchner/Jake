package com.jakeapp.core.domain;

/**
 * The available trust states for projectmembers; the level of trust one
 * user has to another. Since each member of a project trusts
 * the others somehow, a web of trust is established.
 * User: Dominik
 * Date: Dec 10, 2008
 * Time: 1:39:39 AM
 * Module: ${MAVEN-MODULE-NAME}
 * Version: ${MAVEN-VERSION}
 */
public enum TrustState {
    /**
     * AUTO_ADD_REMOVE: (formerly FULL_TRUST)
     * Means, "I" trust this ProjectMember and follow his/her list of trusted users. 
     * If he/she adds or removes other members to his/her list, I do so to.
     */
    AUTO_ADD_REMOVE(2),

    /**
     * TRUST: I "normally" trust this ProjectMember, files will be
     * pulled from him or her.
     */
    TRUST(1),


    /**
     * NO_TRUST: I either don't know this ProjectMember (person not in Project)
     * or explicitly don't trust him.
     * The other user is just another project member, no files will be
     * pulled from this member. The other member does not belong
     * to this Nodes neighborhood.
     * This is the default level of trust.
     */
    NO_TRUST(0);


    private int trustState;

    private TrustState(int state) {
        this.trustState = state;
    }

    public String toString() {
        switch (trustState) {
            default:
            case 0:
                return TrustState.NO_TRUST.toString();
            case 1:
                return TrustState.TRUST.toString();
            case 2:
                return TrustState.AUTO_ADD_REMOVE.toString();
        }
    }


}
