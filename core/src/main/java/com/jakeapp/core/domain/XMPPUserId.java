package com.jakeapp.core.domain;

import java.util.UUID;

/**
 * UserId for the XMPP-Messaging Type.
 */
public class XMPPUserId extends UserId {
    private static final long serialVersionUID = -6654192869495553670L;

    {
        //TODO: gehtda nixa? fixen!
        //this.protocolType = ProtocolType.XMPPP;
    }

    /**
     * Construct a new <code>XMPPUserId</code>.
     * {@inheritDoc}
     */
    public XMPPUserId(UUID uuid, String userId, String nickname,
                      String firstName, String surName) {
        super(uuid, userId, nickname, firstName, surName);
    }


}