package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.callbacks.ErrorCallback;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.RegistrationStatus;

import java.util.List;


public interface ICoreAccess {

    /**************** Main core integration point *************/

    /**
     * Sets a frontendService to use by the gui - either a direct
     * java implementation or e.g. a proxy (sockets, rmi, corba, whatever)
     *
     * @param frontendService the <code>FrontendService</code> to use
     *                        by the gui
     */
    public void setFrontendService(IFrontendService frontendService);


    /******************* Generic functions ********************/

    /**
     * Adds an error listener for error events
     *
     * @param ec
     */
    void addErrorListener(ErrorCallback ec);

    /**
     * Removes the error listener for error events
     *
     * @param ec
     */
    void removeErrorListener(ErrorCallback ec);


    /******************* User functions ********************/

    /**
     * Sync Sercice Log In.
     *
     * @param user
     * @param pass
     */
    void signIn(final String user, final String pass);

    /**
     * Registers the Connection Status Callback
     *
     * @param cb
     */
    void addConnectionStatusCallbackListener(ConnectionStatus cb);

    /**
     * Deregisters the Connecton Status Callback
     *
     * @param cb
     */
    void removeConnectionStatusCallbackListener(ConnectionStatus cb);

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
    void addRegistrationStatusCallbackListener(RegistrationStatus cb);


    /**
     * Deregsters the Registration Status Callback
     *
     * @param cb
     */
    void removeRegistrationStatusCallbackListener(RegistrationStatus cb);

    /**
     * Returns true if a user is signed in successfully.
     *
     * @return
     */
    boolean isSignedIn();

    /**
     * Returns the Username of the current logged in user
     *
     * @return
     */
    String getSignInUser();


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


    /******************* Project functions ********************/


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
     * Registers the Project changed Callback.
     * This is called when a project changes somehow.
     *
     * @param cb
     */
    void addProjectChangedCallbackListener(ProjectChanged cb);

    /**
     * Deregisters the project changed callbac.
     *
     * @param cb
     */
    void removeProjectChangedCallbackListener(ProjectChanged cb);

    /**
     * Stops the given project
     *
     * @param project
     */
    void stopProject(Project project);


    /**
     * Starts the given project
     *
     * @param project
     */
    void startProject(Project project);

    /**
     * Returns absolute Number of files of the project.
     *
     * @param project
     * @return
     */
    int getProjectFileCout(Project project);

    /**
     * Returns absolute Size of all files in the project.
     *
     * @param project
     * @return size in bytes.
     */
    int getProjectSizeTotal(Project project);


    /**
     * Creates a new project.
     * Works asyn, fires the callback when finished.
     * Throws exceptions if path is null or invalid.
     *
     * @param name: name of the project
     * @param path: path of the project
     */
    void createProject(String name, String path);


    /**
     * Deletes a project.
     * Works asyn, fires the callback when finished.
     * Throws exceptions if path is null or invalid.
     *
     * @param project: project that should be deleted
     */
    void deleteProject(Project project);

    /**
     * Joins into a invited project
     *
     * @param loc
     * @param project
     */
    void joinProject(String loc, Project project);


    /**
     * Rejects join of a invited project.
     *
     * @param project
     */
    void rejectProject(Project project);


    /**
     * Changes the project name.
     * Needed in the interface, because we need change events.
     *
     * @param project
     * @param prName
     */
    void setProjectName(Project project, String prName);

    /******************* Notes functions ********************/


    /**
     * Returns the list of all notes
     *
     * @param project: project that should be evaluated
     * @return
     */
    List<NoteObject> getNotes(Project project);


    /******************* People functions ********************/

    /**
     * Get all project members for the current project
     *
     * @param project: project that should be evaluated
     * @return
     */
    List<ProjectMember> getPeople(Project project);

    /**
     * Sets the nickname of people.
     * Checks for error
     *
     * @param pm
     * @param nick
     * @return
     */
    boolean setPeopleNickname(ProjectMember pm, String nick);
}
