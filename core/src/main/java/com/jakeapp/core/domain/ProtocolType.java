package com.jakeapp.core.domain;

/**
 * An enumeration for possible protocol types.
 */
public enum ProtocolType {
    XMPPP,
    ICQ,
    MSN;

    public String toString()
    {
        switch (this) {

            case ICQ:
                return "ICQ";

            case MSN:
                return "MSN";

            
            default:
            case XMPPP:
                return "XMPP";
        }

    }


    public static ProtocolType get(String protocol)
    {
        if(protocol.equals("XMPP"))
            return ProtocolType.XMPPP;

        if(protocol.equals("MSN"))
            return ProtocolType.MSN;

        if(protocol.equals("ICQ"))
            return ProtocolType.ICQ;

        return null;
    }

}
