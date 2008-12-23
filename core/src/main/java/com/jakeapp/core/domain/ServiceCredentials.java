package com.jakeapp.core.domain;

import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;

/**
 * A representation of the users credentials. It consists of a
 * <code>userId</code>
 * and a <code>plainTextPassword</code>.
 */
@Entity
public class ServiceCredentials {
    private UUID uuid;
    private String userId;
    private String plainTextPassword;
    private InetAddress serverAddress;
    private long serverPort;
    private boolean encryptionUsed;
    private String resourceName = "JakeApp";


    public ServiceCredentials() {
    }

    /**
     * Construct new user credentials with the given params.
     *
     * @param userId            the userid to be used
     * @param plainTextPassword the password in plaintext-format for this userId
     */
    public ServiceCredentials(String userId, String plainTextPassword) {
        this.userId = userId;
        this.plainTextPassword = plainTextPassword;
    }

    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    public String getUuid() {
        if(uuid == null)
            return null;
        return uuid.toString();
    }

    public void setUuid(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the <code>userId</code>.
     *
     * @return the userId
     */
    @Column(name = "username", nullable = false)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get the password
     *
     * @return the <code>plainTextPassword</code>
     */
    @Column(name = "password", nullable = true)
    public String getPlainTextPassword() {
        return this.plainTextPassword;
    }

    public void setPlainTextPassword(String plainTextPassword) {
        this.plainTextPassword = plainTextPassword;
    }

    /**
     * Returns the InetAddress of the server on which these
     * <code>ServiceCredentials</code> are registered. Both IPv4
     * or IPv6 should work.
     *
     * @return the ip address/hostname set.
     */

    @Column(name = "serveraddress", nullable = false)
    public InetAddress getServerAddress() {
        return this.serverAddress;
    }

    public void setServerAddress(InetAddress serverAddress) {
        this.serverAddress = serverAddress;
    }


    /**
     * Returns the port on the specified <code>serverAddress</code> where
     * the server listens for incoming connections.
     *
     * @return the port used by the IM-Server
     */
    @Column(name = "port", nullable = false)
    public long getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(long serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * @return whether the communication with the server is encrypted.
     */
    @Column(name = "encryption", nullable = false)
    public boolean isEncryptionUsed() {
        return this.encryptionUsed;
    }

    public void setEncryptionUsed(boolean encryptionUsed) {
        this.encryptionUsed = encryptionUsed;
    }


    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }


    public void setServerAddress(String serverAddress) throws InvalidCredentialsException {

        try {
            InetAddress.getByName(serverAddress);
        } catch (UnknownHostException e) {
            throw new InvalidCredentialsException(e);
        }
    }
}
