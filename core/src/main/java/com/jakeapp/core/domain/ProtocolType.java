package com.jakeapp.core.domain;

/**
 * An enumeration for possible protocol types, which are supported by Jake.
 */
public enum ProtocolType {
    XMPP("XMPP")
	// ,
	// MSN("MSN"),
	// ICQ("ICQ")
	;

    private String realName;

    private ProtocolType(String realName)
    {
        this.realName = realName;
    }

	/**
	 * Returns the name of the <code>ProtocolType</code>.
	 * @return
	 */
    @Override
	public String toString()
    {
        return this.realName;
    }

	/**
	 * Get a <code>ProtocolType</code> by the given <code>String</code>
	 * @param protocol the textual representation of a <code>ProtocolType</code>
	 * @return a <code>ProtocolType</code> Element, null if it given <code>String</code> cannot be resolved to a known
	 * <code>ProtocolType</code>
	 */
    public static ProtocolType getValue(final String protocol)
    {

        if(protocol.toUpperCase().equals("XMPP"))
            return ProtocolType.XMPP;
        return null;
    }

}