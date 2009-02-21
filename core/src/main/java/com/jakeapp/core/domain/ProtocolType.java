package com.jakeapp.core.domain;

/**
 * An enumeration for possible protocol types.
 */
public enum ProtocolType {
    XMPP("XMPP"),;

    private String realName;

    private ProtocolType(String realName)
    {
        this.realName = realName;
    }

    @Override
	public String toString()
    {
        return this.realName;
    }


    public static ProtocolType getValue(final String protocol)
    {

        if(protocol.toUpperCase().equals("XMPP"))
            return ProtocolType.XMPP;
        return null;
    }

}