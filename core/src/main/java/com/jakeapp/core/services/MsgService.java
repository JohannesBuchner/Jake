package com.jakeapp.core.services;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.JakeMessage;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;

import java.util.List;

/**
 * Abstract MessagingService declasring what the classes for the
 * instant-messaging protocols (XMPP, ICQ, etc.)
 * need to implement.
 */
public abstract class MsgService<T extends UserId> {
    private String name = "notInitialized";
    private VisibilityStatus visibilityStatus = VisibilityStatus.OFFLINE;
    private T userId;
    private ServiceCredentials serviceCredentials;

	/**
	 * @return Servicecredentials if they are already set.
	 */
	private ServiceCredentials getServiceCredentials() {
		return serviceCredentials;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The name of the Service associated to this <code>MsgService</code>.
	 */
	protected String getName() {
		return name;
	}

    /**
     * This method gets called by clients to login on this message service.
     *
     * @return true on success, false on error
     * @throws InvalidCredentialsException if the credentials supplied to
     *                                     this message service are invalid
     */
    public final boolean login() throws InvalidCredentialsException {
        if (serviceCredentials == null)
            throw new InvalidCredentialsException("serviceCredentials are null");

        if (!checkCredentails())
            return false;

        return this.doLogin();
    }

    /**
     * Checks whether the ServiceCredentials in <code>serviceCredentials</code>
     * are valid.
     * @return <code>true</code> iff the credentials are valid.
     * @throws InvalidCredentialsException if the credentials stored
     * in this <code>MsgService</code> are 
     * insufficiently specified (e.g. they are null)
     */
    protected boolean checkCredentails() throws InvalidCredentialsException {
    	if (this.getServiceCredentials() == null) {
    		throw new InvalidCredentialsException("credentials must not be null");
    	}
        if (serviceCredentials.getUserId() == null)
            throw new InvalidCredentialsException(
                    "credentials.userId must not be null");

        if (serviceCredentials.getPlainTextPassword() == null)
            throw new InvalidCredentialsException(
                    "credentials.plainTextPassword must not be null");


        if (serviceCredentials.getServerAddress() == null)
            throw new InvalidCredentialsException(
                    "credentials.serverAddress must not be null");

        return this.doCredentialsCheck();
    }

    protected abstract boolean doCredentialsCheck();

    protected abstract boolean doLogin();


    public final void logout() {
        this.doLogout();
    }

    protected abstract void doLogout();

    public void setCredentials(ServiceCredentials credentials) {
        this.serviceCredentials = credentials;
    }



    public abstract void sendMessage(JakeMessage message);

    //public void addMessageReceiveListener(IMessageReceiveListener listener);

    //public void removeMessageReceiveListener(IMessageReceiveListener listener);

    public boolean setVisibilityStatus(VisibilityStatus newStatus) {
        this.visibilityStatus = newStatus;
        return false;
    }

    public VisibilityStatus getVisibilityStatus() {
        return this.visibilityStatus;
    }

    public final T registerId(ServiceCredentials credentials) {
        this.serviceCredentials = credentials;
        return this.doRegister();

    }


    protected abstract T doRegister();

    public T getUserId() {
        return this.userId;
    }

    public abstract List<T> getUserList();

    /**
     * Get a UserId Instance from this Messaging-Service
     *
     * @param userId the String representation of the userId
     * @return a &lt;T extends UserId&gt; Object
     * @throws UserIdFormatException if the format of the input
     *                               is not valid for this Messaging-Service
     */
    public abstract T getUserId(String userId) throws UserIdFormatException;

    /**
     * Find out if the supplied &lt;T extends UserId&gt; is a
     * friend of the current
     * user of this MsgService
     *
     * @param friend the <code>T extends UserId</code> to check friendship
     * @return true if friends, false if not
     * @throws IllegalArgumentException if the supplied friend is null
     */
    public final boolean isFriend(T friend) throws IllegalArgumentException {
        if (friend == null)
            throw new IllegalArgumentException("friend must not be null");
        return this.checkFriends(friend);
    }
    
    protected abstract boolean checkFriends(T friend);
    
    /**
     * Searches for Users matching a pattern, to add them as
     * trusted users later.
     * @param pattern The pattern that is searched for in Usernames.
     * 	Implementations of <code>MsgService</code> may look for the pattern
     *  in other userdata as well.
     * @return A list of users matching the pattern.
     */
    public abstract List<T> findUser(String pattern);

    /**
     * Get the ServiceType of this MsgService (XMPP, ICQ, MSN, etc.)
     * @return the ServiceType of this MsgService (XMPP, ICQ, MSN, etc.)
     */
    public abstract String getServiceName();
    
    /**
     * Creates an account for the Service, with the specified ServiceCredentials.
     */
    public abstract void createAccount();
}
