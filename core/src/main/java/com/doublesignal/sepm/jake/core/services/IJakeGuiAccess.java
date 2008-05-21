package com.doublesignal.sepm.jake.core.services;

import com.doublesignal.sepm.jake.core.domain.exceptions.NoSuchConfigOptionException;
import com.doublesignal.sepm.jake.core.domain.exceptions.NoSuchJakeObjectException;
import com.doublesignal.sepm.jake.core.domain.exceptions.NoSuchFolderException;
import com.doublesignal.sepm.jake.core.domain.exceptions.NoSuchFileException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeMessage;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.Project;
import com.doublesignal.sepm.jake.core.domain.ProjectInvitation;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;

import java.util.List;
import java.util.Map;
import java.util.Observer;


/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 8, 2008
 * Time: 10:48:39 PM
 */
public interface IJakeGuiAccess {

    /**
     * Login on the network
     * @throws NetworkException if the ICS couldn't log us onto the network.
      */
    public void login()
            throws NetworkException;

    /**
     * Logout from the network
     * @throws NetworkException if the ICS tells, that the Logout could not be propagated to the network
     * (others may see us still online)
     */
    public void logout()
            throws NetworkException;

    /**
     * Sets a config
     * @param configKey
     * @param configValue
     * @throws NoSuchConfigOptionException
     */
    public void setConfigOption(String configKey, String configValue)
            throws NoSuchConfigOptionException;

    /**
     * Gets a config option by the specified key
     * @param configKey
     * @return
     * @throws NoSuchConfigOptionException
     */
    public String getConfigOption(String configKey)
            throws NoSuchConfigOptionException
            ;


    /**
     * Get all the available config options
     * @return all configs options
     */
    public Map<String,String> getConfigOptions();


    /**
     * do a logSync
     */
    public void logSync()
            throws NetworkException;

    /**
     * Returns a list of one or more JakeObjects which are reported as OutOfSync,
     * so they can be visualized by the gui.
     * @return list of Jake Objects
     */
    public List<JakeObject> getOutOfSyncObjects();

    /**
     * Returns a list of one or more JakeObjects which where changed by a recent
     * PullObjects() call, so they can be visualized by the gui.
     * @return list of Jake Objects
     */
    public List<JakeObject> getChangedObjects();

    public void PushObjects()
            throws NetworkException;

    public void PullObjects()
            throws NetworkException;

    /*
    TODO: Resolve version conflicts!!!
     */

    public List<LogEntry> getLog();

    public List<LogEntry> getLog(JakeObject object);

    public void registerReceiveMessageCallback(Observer observer);

    public List<JakeMessage> getNewMessages();

    public Project createProject(String projectName, String projectId, String projectPath);


    public Project getProject();


/*
    ich muss mir nochmal genau ueberlegen was ich mir dabei gedacht habe. ist schon etwas
    zu spaet dafuer. - dominik

    public String findProjectMember(String UserId);


    public ProjectMember addProjectProjectMember(String UserId)
            throws NoSuchNetworkUserException;

*/


    /**
     * Registers
     * @param obs
     */
    public void registerProjectInvitationCallback(Observer obs);


    /**
     * Gets the ProjectInvitation from the Core, so it can be displayed
     * in the GUI and the corresponding Project can be created, if the user
     * wishes so.
     * @return a ProjectInvitation
     */
    public ProjectInvitation getProjectInvitation();


    /**
     * Create a new note
     * @param content
     * @return the NoteObject in question.
     */
    public NoteObject createNote(String content);


    /**
     * Get the list of available NoteObjects. 
     * @return list of NoteObjects
     */
    public List<NoteObject> getNotes();


    /**
     * Make a file manually copied in the the project folder aware to jake.
     * @param relPath
     * @return list of one or more FileObjects
     */
    public List<FileObject> createFileObjects(String relPath);

    /**
     * Import an external File into our Jake Project by Copying it
     * into the Project Folder
     * @param absolutePath
     * @return a new JakeObject
     */
    public FileObject createFileObjectFromExternalFile(String absolutePath)
            throws NoSuchFileException;


    /**
     * Imports a whole folder from the filesystem into the project folder
     * and returns a list of all the FileObjects created.
     * @param absolutePath
     * @return
     * @throws NoSuchFolderException
     */
    public List<FileObject> importFolder(String absolutePath)
            throws NoSuchFolderException
            ;

    /**
     * Gets a list of all the JakeObjects in one directory (if relpath is a directory)
     * or the JakeObject in question
     * @param relPath
     * @return list of JakeObjects
     * @throws NoSuchJakeObjectException
     */
    public List<JakeObject> getJakeObjectsByPath(String relPath)
            throws NoSuchJakeObjectException;


    /**
     * Searches for Jake Objects by specifying a name
     * @param name
     * @return list of JakeObjects
     */
    public List<JakeObject> getJakeObjectsByName(String name);

    /**
     * Searches for Jake Objects by specifying one or more tags
     * @param tags
     * @return list of JakeObjects
     */
    public List<JakeObject> getJakeObjectsByTags(List<Tag> tags);

    /**
     * Searches for Jake Objects by specifying a name and one or more tags
     * @param name
     * @param tags
     * @return list of JakeObjects
     */
    public List<JakeObject> getJakeObjectsByNameAndTag(String name, List<Tag> tags);


    /**
     * get a list of all available tags in the project by generating a list of all
     * used ones.
     * @return a list of Tag Objects
     */
    public List<Tag> getTags();


    /**
     * Get a list of all the tags of an certain object.
     * @param object
     * @return a list of Tag Objects
     */
    public List<Tag> getTagsOfObject(JakeObject object);





}
