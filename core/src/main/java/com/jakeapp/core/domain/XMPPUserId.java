package com.jakeapp.core.domain;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Transient;

import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

import java.util.UUID;

/**
 * UserId for the XMPP-Messaging Type.
 */
@Entity
@DiscriminatorValue("XMPP")
public class XMPPUserId extends UserId {
    private static final long serialVersionUID = -6654192869495553670L;
    {
        this.setProtocolType(ProtocolType.XMPP);
    }

    /**
     * Construct a new <code>XMPPUserId</code>.
     * {@inheritDoc}
     */
    public XMPPUserId(ServiceCredentials credentials, UUID uuid, String userId, String nickname,
                      String firstName, String surName) {
        super(credentials, uuid, userId, nickname, firstName, surName);
    }

    public static XMPPUserId createFromUserId(UserId user)
    {
        return new XMPPUserId(user.getCredentials(),
                user.getUuid(),
                user.getUserId(),
                user.getNickname(),
                user.getFirstName(), user.getSurName());
    }


    private XMPPUserId()
    {
        
    }
    

}
