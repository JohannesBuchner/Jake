package com.jakeapp.core.domain;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import com.jakeapp.core.services.XMPPMsgService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

import java.util.UUID;

/**
 * UserId for the XMPP-Messaging Type.
 */
@Entity
@DiscriminatorValue("XMPP")
public class XMPPUserId extends UserId {
	private static Logger log = Logger.getLogger(XMPPUserId.class);
	
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
    	log.debug("building XMPPUserId for " + user.getCredentials().getUserId() + " pw: "
				+ user.getCredentials().getPlainTextPassword());
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
