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
     * FULL_TRUST: Means, "I" fully trust this other ProjectMember and also do
     * the things he does (add other members, delete files, etc. ).
     */
    AUTO_ADD_REMOVE,

    /**
     * TRUST: I "normally" trust this ProjectMember, files will be
     * pulled from him or her.
     */
    TRUST,


    /**
     * NO_TRUST: I either don't know this ProjectMember (person not in Project)
     * or explicitly don't trust him.
     * The other user is just another project member, no files will be
     * pulled from this member. The other member does not belong
     * to this Nodes neighborhood.
     * This is the default level of trust.
     */
    NO_TRUST
}
