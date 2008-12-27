package com.jakeapp.gui.swing.callbacks;

/**
 * Callback for registration state
 * User: studpete
 * Date: Dec 26, 2008
 * Time: 10:03:53 PM
 */
public interface RegistrationStatus {
    enum RegisterStati {
        None, RegistrationActive, RegisterSuccess, NoConnection, UnknownError
    }

    ;

    /**
     * Set the Registration Status
     *
     * @param status
     */
    void setRegistrationStatus(final RegisterStati status, final String msg);
}
