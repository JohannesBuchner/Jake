package com.jakeapp.core.domain;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Column;
import java.util.UUID;

import com.jakeapp.core.dao.IUserIdDao;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;

/**
 * The representation of a project member. It consists of an <code>ID</code>
 * and a <code>trustState</code>
 *
 * @author johannes, domdorn, simon, christopher
 */
@Entity
public class ProjectMember implements ILogable {
    private static final long serialVersionUID = -9208035210417004558L;
    private static final Logger log = Logger.getLogger(ProjectMember.class);

    private UUID userId;
    private TrustState trustState;
    private String nickname;


    private transient IUserIdDao userIdDao;

    @Autowired
    public void setUserIdDao(IUserIdDao userIdDao) {
        this.userIdDao = userIdDao;
    }

    /**
     * Default constructor.
     */
    private ProjectMember() {

    }

    /**
     * Construct a new <code>PrjectMember</code>.
     *
     * @param userId     the userId
     * @param nickname
     * @param trustState the trust state of the <code>ProjectMember</code>
     */
    public ProjectMember(UUID userId, String nickname, TrustState trustState) {
        this.setUserId(userId);
        this.setNickname(nickname);
        this.setTrustState(trustState);
    }

    /**
     * Set the <code>trustState</code> of this <code>ProjectMember</code>.
     *
     * @param trustState the level of trust we have to the <code>ProjectMember</code>.
     * @see TrustState
     */
    public void setTrustState(TrustState trustState) {
        this.trustState = trustState;
    }

    /**
     * @return The level of trust the user has to this projectMember.
     */
    @Column(name = "trustlevel")
    public TrustState getTrustState() {
        return this.trustState;
    }


    /**
     * @return the UUID of the <code>userId</code> of this <code>ProjectMember</code>.
     * look the userId up using the 
     */
    @Transient
    public UUID getUserId() {
        return this.userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }


    /**
     * The String representation of the ProjectMember
     *
     * @return string of ProjectMember
     */
    @Override
	public String toString() {
        return getUserId() + " trust: " + getTrustState();
    }

    @Id
    @Column(name = "memberId")
    private String getUserIdString() {
        return this.userId.toString();
    }

    private void setUserIdString(String uuid) {
        this.userId = UUID.fromString(uuid);
    }


    @Column(name = "nickname", length = 100)
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectMember)) return false;

        ProjectMember that = (ProjectMember) o;

        if (nickname != null ? !nickname.equals(that.nickname) : that.nickname != null) return false;
        if (trustState != that.trustState) return false;
        if (!userId.equals(that.userId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + trustState.hashCode();
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        return result;
    }
}
