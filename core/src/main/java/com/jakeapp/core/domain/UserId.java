package com.jakeapp.core.domain;

//import org.hibernate.annotations.Entity;
//import org.hibernate.type.DiscriminatorType;

import com.jakeapp.core.dao.IUserIdDao;
import com.jakeapp.core.dao.IServiceCredentialsDao;

import javax.persistence.*;
import java.util.UUID;




/**
 * An abstract representation of a userID.
 */

@Entity(name="users")
@DiscriminatorColumn(name = "protocol", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
public abstract class UserId implements ILogable {

    private UUID uuid;
    private String userId;
    private String nickname;
    private String firstName;
    private String surName;
    private transient ProtocolType protocolType;

    private ServiceCredentials credentials;


    /**
     * Default ctor.
     */
    public UserId() {
        // default ctor for hibernate
    }

    /**
     * Construct a new userID.
     *
     * @param credentials the credentials this userId belongs to
     * @param uuid      the universally unique user id within this jake project
     * @param userId    the instant-messenger userId
     * @param nickname  the nickname contained in the userID
     * @param firstName the first name of the user
     * @param surName   the surname of the user
     */
    protected UserId(ServiceCredentials credentials, UUID uuid,
                     String userId, String nickname,
                     String firstName, String surName) {
        this.setCredentials(credentials);
        this.setUuid(uuid);
        this.setUserId(userId);
        this.setNickname(nickname);
        this.setFirstName(firstName);
        this.setSurName(surName);
    }


    /**
     * The Universally Unique Identifier for this user-object.
     * Syntax: AAAAAAAA-BBBB-CCCC-DDDD-EEEEEEEEEEEE (36 Characters)
     *
     * @return the uuid of this user.
     */
    @Transient
    public UUID getUuid() {
        return this.uuid;
    }


    @Id
    @Column(name="uuid")
    private String getUuidString()
    {
        return this.uuid.toString();
    }

    private void setUuidString(String uuid)
    {
        this.uuid = UUID.fromString(uuid);
    }
        


    /**
     * Get the user ID.
     *
     * @return the unique userID  that identifies a user.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Get the nick name.
     *
     * @return the nickname contained in the userID.
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Get the first name.
     *
     * @return the first name of the user
     */
    @Column(name="firstname")
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Get the surname.
     *
     * @return the surname of the user
     */
    @Column(name="surname")
    public String getSurName() {
        return this.surName;
    }

//    @Column(name = "sc_uuid")
//    @JoinColumn(name = "sc_uuid")
//    @JoinTable(name = "servicecredentials")
//    @ManyToOne(fetch = FetchType.LAZY)
    //@Column(name = "sc_uuid")
    //@OneToOne(targetEntity = ServiceCredentials.class, fetch = FetchType.LAZY)
    //@Column(name = "sc_uuid")
    //@JoinColumn(name = "sc_uuid")
    //@JoinTable(name = "servicecredentials")
    //@OneToOne(fetch = FetchType.LAZY)
    //@JoinColumns(value = )


    //@Column(name = "sc_uuid")
//    @ManyToOne(targetEntity = ServiceCredentials.class)
//    @JoinColumn(name = "uuid")
//    @Column(name = "sc_uuid")

   // @Any( metaDef="property", metaColumn = @Column( name = "property_type" ), fetch=FetchType.EAGER )
    @Column(name = "sc_uuid")
//    @JoinColumn( name = "sc_uuid", referencedColumnName = "uuid", table = "xxxx")
    @JoinTable(name = "DDDD")
    private String getCredentialsUuid()
    {
        if(credentials != null && credentials.getUserId() != null)
            return credentials.getUuid().toString();

        return null;
    }

    private void setCredentialsUuid(String bla)
    {
       ServiceCredentials credentials = new ServiceCredentials();
        credentials.setUuid(UUID.fromString(bla));
        this.setCredentials(credentials);
    }

    @Transient
    public ServiceCredentials getCredentials() {
        return credentials;
    }

    private void setCredentials(ServiceCredentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Get the protocolType.
     *
     * @return the Type of the protocol associated with that user
     */

    //@Column(name="servicecredentials.protocol", insertable = false, updatable = false)
    @Column(name = "protocol", insertable = false, updatable = false)
    public ProtocolType getProtocolType() {
        return this.protocolType;
    }

    /**
     * Set the <code>uuid</code> of the userId.
     *
     * @param uuid the given <code>uuid</code> must obey the
     *             following constraint:
     *             <code>[A-F]{8}-[A-F]{4}-[A-F]{4}-[A-F]{4}-[A-F]{12}</code>
     *             i.e.
     *             AAAAAAAA-BBBB-CCCC-DDDD-EEEEEEEEEEEE (36 Characters)
     * @throws IllegalArgumentException if the given <code>uuid</code> does not
     *                                  meet the constraint.
     */
    public void setUuid(UUID uuid) throws IllegalArgumentException {
        if (uuid == null) {
            throw new IllegalArgumentException("uuid must not be null");
        }

        this.uuid = uuid;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    protected void setType(ProtocolType type) {
        this.protocolType = type;
    }

    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }


}
