package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.callbacks.ErrorCallback;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.RegistrationStatus;
import com.jakeapp.gui.swing.exceptions.ProjectNotFoundException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoreAccessMock implements ICoreAccess {
    private static final Logger log = Logger.getLogger(CoreAccessMock.class);

    private boolean isSignedIn;
    private List<Project> projects = new ArrayList<Project>();
    private List<Project> invitedProjects = new ArrayList<Project>();

    /**
     * Core Access Mock initialisation code
     */
    public CoreAccessMock() {
        isSignedIn = false;
        connectionStatus = new ArrayList<ConnectionStatus>();
        registrationStatus = new ArrayList<RegistrationStatus>();
        projectChanged = new ArrayList<ProjectChanged>();
        errorCallback = new ArrayList<ErrorCallback>();

        // init the demo projects
        Project pr1 = new Project("ASE", new UUID(212, 383), null, new File("/Users/studpete/Desktop"));
        pr1.setStarted(true);
        pr1.setInvitationState(InvitationState.ACCEPTED);
        projects.add(pr1);

        Project pr2 = new Project("SEPM", new UUID(222, 373), null, new File("/Users/studpete/"));
        pr2.setInvitationState(InvitationState.ACCEPTED);
        projects.add(pr2);

        Project pr3 = new Project("Shared Music", new UUID(232, 363), null, new File("/Users"));
        pr3.setInvitationState(InvitationState.ACCEPTED);
        projects.add(pr3);

        // Yes, we need a windows testing project too...
        Project pr4 = new Project("Windows Project", new UUID(242, 353), null, new File("C:\\test"));
        pr4.setInvitationState(InvitationState.ACCEPTED);
        projects.add(pr4);

        Project ipr1 = new Project("DEMO INVITATION 1", new UUID(212, 33), null, new File(""));
        ipr1.setInvitationState(InvitationState.INVITED);
        invitedProjects.add(ipr1);

        Project ipr2 = new Project("DEMO INVITATION 2", new UUID(222, 33), null, new File(""));
        ipr2.setInvitationState(InvitationState.INVITED);
        invitedProjects.add(ipr2);

        Project ipr3 = new Project("DEMO INVITATION 3", new UUID(232, 33), null, new File(""));
        ipr3.setInvitationState(InvitationState.INVITED);
        invitedProjects.add(ipr3);
    }


    @Override
    public List<Project> getMyProjects() {
        return projects;
    }

    // TODO: change this: invitations are only runtime specific... (are they?)
    @Override
    public List<Project> getInvitedProjects() {

        return invitedProjects;
    }


    public void addErrorListener(ErrorCallback ec) {
        errorCallback.add(ec);
    }

    public void removeErrorListener(ErrorCallback ec) {
        errorCallback.remove(ec);
    }

    private void fireErrorListener(ErrorCallback.JakeErrorEvent ee) {
        for (ErrorCallback callback : errorCallback) {
            callback.reportError(ee);
        }
    }

    public void signIn(final String user, final String pass) {
        log.info("Signs in: " + user + "pass: " + pass);

        Runnable runner = new Runnable() {
            public void run() {
                callbackConnectionStatus(ConnectionStatus.ConnectionStati.SigningIn, "");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                isSignedIn = true;
                currentUser = user;

                callbackConnectionStatus(ConnectionStatus.ConnectionStati.Online, "");
            }
        };

        // start our runner thread, that makes callbacks to connection status
        new Thread(runner).start();
    }

    public void registerConnectionStatusCallback(ConnectionStatus cb) {
        log.info("Registers connection status callback: " + cb);

        connectionStatus.add(cb);
    }

    public void deRegisterConnectionStatusCallback(ConnectionStatus cb) {
        log.info("Deregisters connection status callback: " + cb);

        connectionStatus.remove(cb);
    }


    private void callbackConnectionStatus(ConnectionStatus.ConnectionStati state, String str) {
        log.info("spead callback event...");
        for (ConnectionStatus callback : connectionStatus) {
            callback.setConnectionStatus(state, str);
        }
    }

    public void register(String user, String pass) {
        log.info("Registering user: " + user + " pass: " + pass);

        Runnable runner = new Runnable() {
            public void run() {

                // registering
                callbackRegistrationStatus(RegistrationStatus.RegisterStati.RegistrationActive, "");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                callbackRegistrationStatus(RegistrationStatus.RegisterStati.RegisterSuccess, "");

                // logging in after registering
                callbackConnectionStatus(ConnectionStatus.ConnectionStati.SigningIn, "");

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                isSignedIn = true;

                callbackConnectionStatus(ConnectionStatus.ConnectionStati.Online, "");
            }
        };

        // start our runner thread, that makes callbacks to connection status
        new Thread(runner).start();
    }

    public void registerRegistrationStatusCallback(RegistrationStatus cb) {
        log.info("Registers registration status callback: " + cb);
    }

    public void deRegisterRegistrationStatusCallback(RegistrationStatus cb) {
        log.info("Deregisters registration status callback: " + cb);

    }


    public boolean isSignedIn() {
        return isSignedIn;
    }


    public String getSignInUser() {
        return currentUser;
    }


    public void signOut() {
        isSignedIn = false;

        callbackConnectionStatus(ConnectionStatus.ConnectionStati.Offline, "");
    }

    public String[] getLastSignInNames() {
        return new String[]{"pstein", "csutter"};
    }


    public void registerProjectChangedCallback(ProjectChanged cb) {
        log.info("Mock: register project changed callback: " + cb);

        projectChanged.add(cb);
    }

    public void deregisterProjectChangedCallback(ProjectChanged cb) {
        log.info("Mock: deregister project changed callback: " + cb);

        if (projectChanged.contains(cb)) {
            projectChanged.remove(cb);
        }
    }

    private void callbackProjectChanged(ProjectChanged.ProjectChangedEvent ev) {
        for (ProjectChanged callback : projectChanged) {
            callback.projectChanged(ev);
        }
    }


    public void stopProject(Project project) {
        log.info("stop project: " + project);

        //if(!project.isStarted())
        //    throw new ProjectNotStartedException();

        project.setStarted(false);

        // generate event
        callbackProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
                ProjectChanged.ProjectChangedEvent.ProjectChangedReason.State));
    }

    public void startProject(Project project) {
        project.setStarted(true);

        // generate event
        callbackProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
                ProjectChanged.ProjectChangedEvent.ProjectChangedReason.State));
    }


    public int getProjectFileCout(Project project) {
        return 100;
    }

    public int getProjectSizeTotal(Project project) {
        return 50000;
    }


    public void deleteProject(final Project project) {
        log.info("Mock: delete project: " + project);

        if (project == null) {
            throw new IllegalArgumentException("Cannot delete empty project!");
        }


        Runnable runner = new Runnable() {
            public void run() {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    // search project in list
                    boolean ret = projects.remove(project);

                    if (!ret) {
                        throw new ProjectNotFoundException("Project not found in list!");
                    }

                    callbackProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
                            ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Deleted));

                } catch (RuntimeException run) {
                    fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
                }
            }
        };

        // start our runner thread, that makes a callback to project status
        new Thread(runner).start();
    }

    public void joinProject(final String path, final Project project) {
        log.info("Mock: join project: " + project + " path: " + path);

        if (path == null) {
            //throw new
        }

        Runnable runner = new Runnable() {
            public void run() {

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    project.setRootPath(path);
                    projects.add(project);
                    invitedProjects.remove(project);
                    project.setInvitationState(InvitationState.ACCEPTED);


                    callbackProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
                            ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Joined));

                } catch (RuntimeException run) {
                    fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
                }
            }
        };

        // start our runner thread, that makes a callback to project status
        new Thread(runner).start();
    }


    public void rejectProject(final Project project) {
        log.info("Mock: reject project: " + project);


        Runnable runner = new Runnable() {
            public void run() {

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    invitedProjects.remove(project);

                    callbackProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
                            ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Rejected));

                } catch (RuntimeException run) {
                    fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
                }
            }
        };

        // start our runner thread, that makes a callback to project status
        new Thread(runner).start();
    }

    public List<NoteObject> getNotes(Project project) {
        return new ArrayList<NoteObject>();
    }

    public List<ProjectMember> getPeople(Project project) {
        return new ArrayList<ProjectMember>();
    }


    public void createProject(final String name, final String path) {
        log.info("Mock: create project: " + name + " path: " + path);
        if (path == null) {
            //throw new
        }

        Runnable runner = new Runnable() {
            public void run() {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Project pr1 = new Project(name, new UUID(22, 33), null, new File(path));
                    pr1.setStarted(true);
                    projects.add(pr1);

                    callbackProjectChanged(new ProjectChanged.ProjectChangedEvent(pr1,
                            ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Created));

                } catch (RuntimeException run) {
                    fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
                }
            }
        };

        // start our runner thread, that makes a callback to project status
        new Thread(runner).start();
    }


    private void callbackRegistrationStatus(RegistrationStatus.RegisterStati state, String str) {
        for (RegistrationStatus callback : registrationStatus) {
            callback.setRegistrationStatus(state, str);
        }
    }

    private String currentUser = null;

    // event spread
    private List<ConnectionStatus> connectionStatus;
    private List<RegistrationStatus> registrationStatus;
    private List<ProjectChanged> projectChanged;
    private List<ErrorCallback> errorCallback;
}
