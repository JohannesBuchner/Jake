package com.jakeapp.core.domain;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
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


    private XMPPUserId()
    {
        
    }

    

}
