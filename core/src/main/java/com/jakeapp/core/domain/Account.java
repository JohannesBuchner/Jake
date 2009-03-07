package com.jakeapp.core.domain;

import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.UUID;

/**
 * A representation of the users credentials. It consists of a
 * <code>userId</code> and a <code>plainTextPassword</code>.
 */
@Entity(name = "servicecredentials")
public class Account implements Serializable {

	private static final long serialVersionUID = -3550631428630088119L;

	private UUID uuid;

	private String userId;

	private String plainTextPassword;

	private String serverAddress;

	private long serverPort;


	private boolean autologin;

	private boolean encryptionUsed;

	private ProtocolType protocol;


	private boolean savePassword = false;

	public Account() {

		this.uuid = UUID.randomUUID();
	}

	/**
	 * Construct new user credentials with the given params. <br/> Note: the
	 * password will not be persisted unless you call
	 * {@link #setSavePassword(boolean)}
	 * 
	 * @param userId
	 *            the userid to be used
	 * @param plainTextPassword
	 *            the password in plaintext-format for this userId
	 * @param protocolType
	 *            The Protocol the credentials are bound to.
	 */
	public Account(String userId, String plainTextPassword, ProtocolType protocolType) {
		this.userId = userId;
		this.plainTextPassword = plainTextPassword;
		this.setProtocol(protocolType);
	}

	@Id
	@Column(name = "uuid", nullable = false)
	public String getUuid() {
		if (uuid == null)
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
	 * <code>ServiceCredentials</code> are registered. Both IPv4 or IPv6 should
	 * work.
	 * 
	 * @return the ip address/hostname set.
	 */
	@Column(name = "server", nullable = false)
	public String getServerAddress() {
		if (this.serverAddress == null)
			return "";
		return this.serverAddress;
	}

	public void setServerAddress(String serverAddress) throws InvalidCredentialsException {
		this.serverAddress = serverAddress;

	}

	/**
	 * Returns the port on the specified <code>serverAddress</code> where the
	 * server listens for incoming connections.
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

	@Column(name = "autologin")
	public boolean isAutologin() {
		return autologin;
	}

	public void setAutologin(boolean autologin) {
		this.autologin = autologin;
	}

	private void setProtocolType(String protocol) {
		this.protocol = ProtocolType.getValue(protocol);
	}

	@Column(name = "protocol")
	public String getProtocolType() {
		if(this.protocol == null)
			return "";
		return this.protocol.toString();
	}

	@Transient
	public ProtocolType getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolType protocol) {
		this.protocol = protocol;
	}


	@Column(name = "savepassword")
	public boolean isSavePassword() {
		return savePassword;
	}

	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Account))
			return false;

		Account that = (Account) o;

		if (encryptionUsed != that.encryptionUsed)
			return false;
		if (serverPort != that.serverPort)
			return false;
		if (plainTextPassword != null ? !plainTextPassword.equals(that.plainTextPassword)
				: that.plainTextPassword != null)
			return false;
		if ( (serverAddress!=null)?(!serverAddress.equals(that.serverAddress)):that.serverAddress!=null)
			return false;
		if ( (userId!=null)?( !userId.equals(that.userId) ):that.userId!=null)
			return false;
		return !((uuid != null) ? (!uuid.equals(that.uuid)) : that.uuid != null);

	}

	@Override
	public int hashCode() {
		int result = (uuid==null)?0:uuid.hashCode();
		result = 31 * result + ((userId==null)?0:userId.hashCode());
		result = 31 * result
				+ (plainTextPassword != null ? plainTextPassword.hashCode() : 0);
		result = 31 * result + ((serverAddress==null)?0:serverAddress.hashCode());
		result = 31 * result + (int) (serverPort ^ (serverPort >>> 32));
		result = 31 * result + (encryptionUsed ? 1 : 0);
		return result;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + this.getProtocol() + ":"
				+ this.getUserId() + " [" + this.getUuid() + "]";
	}
}
