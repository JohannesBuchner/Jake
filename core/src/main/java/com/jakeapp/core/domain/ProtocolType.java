package com.jakeapp.core.domain;

/**
 * An enumeration for possible protocol types.
 */
public enum ProtocolType {
    XMPPP("XMPP"),
    ICQ("ICQ"),
    MSN("MSN"),
    UNKNOWN("");

    private String realName;

    private ProtocolType(String realName)
    {
        this.realName = realName;
    }

    public String toString()
    {
/*
        switch (this) {

            case ICQ:
                return "ICQ";

            case MSN:
                return "MSN";

            
            default:
            case XMPPP:
                return "XMPP";
        }
*/

        return this.realName;

    }


    public static ProtocolType getValue(String protocol)
    {
      try
      {
          return valueOf(protocol.toUpperCase());
      }
      catch(Exception e)
      {
          return ProtocolType.UNKNOWN;
      }
//
//        if(protocol.equals("XMPP"))
//            return ProtocolType.XMPPP;
//
//        if(protocol.equals("MSN"))
//            return ProtocolType.MSN;
//
//        if(protocol.equals("ICQ"))
//            return ProtocolType.ICQ;
//
//        return null;
    }

}
