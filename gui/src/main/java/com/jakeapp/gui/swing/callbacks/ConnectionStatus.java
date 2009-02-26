package com.jakeapp.gui.swing.callbacks;

/**
 * The Connection Status Callback Interface
 * User: studpete
 * Date: Dec 26, 2008
 * Time: 9:59:13 PM
 */
public interface ConnectionStatus {
    enum ConnectionStati {
        Offline, SigningIn, Online, NoConnection, UnknownError
    }

	/**
     * Set the Connection Status
     *
     * @param status
	 * @param msg
     */
    void setConnectionStatus(final ConnectionStati status, final String msg);
}
