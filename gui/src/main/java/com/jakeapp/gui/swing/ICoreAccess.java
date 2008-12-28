package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.callbacks.RegistrationStatus;

import java.util.List;


public interface ICoreAccess {

    /**
     * Get all my projects(started/stopped), but not the invited ones.
     * List is alphabetically sorted.
     *
     * @return list of projects.
     */
    List<Project> getMyProjects();

    /**
     * Get projects where i am invited to.
     * List is alphabetically sorted.
     *
     * @return list of invited projects.
     */
    List<Project> getInvitedProjects();


    /**
     * Sync Sercice Log In.
     *
     * @param user
     * @param pass
     */
    void signIn(String user, String pass);

    /**
     * Registers the Connection Status Callback
     *
     * @param cb
     */
    void registerConnectionStatusCallback(ConnectionStatus cb);

    /**
     * Deregisters the Connecton Status Callback
     *
     * @param cb
     */
    void deRegisterConnectionStatusCallback(ConnectionStatus cb);

    /**
     * Register on sync sercices.
     *
     * @param user
     * @param pass
     */
    void register(String user, String pass);


    /**
     * Registers the Registration Callback
     *
     * @param cb
     */
    void registerRegistrationStatusCallback(RegistrationStatus cb);


    /**
     * Deregsters the Registration Status Callback
     *
     * @param cb
     */
    void deRegisterRegistrationStatusCallback(RegistrationStatus cb);

    /**
     * Returns true if a user is signed in successfully.
     *
     * @return
     */
    boolean isSignedIn();

    /**
     * Signs the current user out.
     */
    void signOut();


    /**
     * Returns an Array of the last isers that signed in.
     *
     * @return
     */
    String[] getLastSignInNames();
}
