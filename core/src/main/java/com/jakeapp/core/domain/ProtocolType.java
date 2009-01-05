package com.jakeapp.core.domain;

/**
 * An enumeration for possible protocol types.
 */
public enum ProtocolType {
    XMPP("XMPP"),
    ICQ("ICQ"),
    MSN("MSN");


    private String realName;

    private ProtocolType(String realName)
    {
        this.realName = realName;
    }

    public String toString()
    {
        return this.realName;
    }


    public static ProtocolType getValue(final String protocol)
    {

        if(protocol.toUpperCase().equals("XMPP"))
            return ProtocolType.XMPP;

        if(protocol.toUpperCase().equals("MSN"))
            return ProtocolType.MSN;

        if(protocol.toUpperCase().equals("ICQ"))
            return ProtocolType.ICQ;

        return null;
    }

}