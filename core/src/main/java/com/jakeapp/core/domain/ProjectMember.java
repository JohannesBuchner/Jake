package com.jakeapp.core.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The representation of a project member. It consists of an <code>ID</code>
 * and a <code>trustState</code>
 * @author johannes, domdorn, simon, christopher
 */
@Entity
public class ProjectMember implements ILogable {
	private static final long serialVersionUID = -9208035210417004558L;
    private UserId userId;
    private TrustState trustState;
    

    {
        this.trustState = TrustState.NO_TRUST;
    }

    /**
     * Default constructor.
     */
    public ProjectMember() {
    	//default ctor for hibernate
    }

    /**
     * Construct a new <code>PrjectMember</code>.
     * @param userId the userId
     * @param trustState the trust state of the <code>ProjectMember</code>
     */
    public ProjectMember(UserId userId, TrustState trustState) {
        this.userId = userId;
        this.setTrustState(trustState);
    }

    /**
     * Set the <code>trustState</code> of this <code>ProjectMember</code>.
     * @param trustState the level of trust we have to the <code>ProjectMember</code>.
     * @see TrustState
     */
	public void setTrustState(TrustState trustState) {
		this.trustState = trustState;
	}

	/**
	 * @return The level of trust the user has to this projectMember.
	 */
	public TrustState getTrustState() {
		return this.trustState;
	}
	
	/**
	 * @return the <code>userId</code> of this <code>ProjectMember</code>.
	 */
	@Id
	public UserId getUserId() {
		return this.userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}
}
