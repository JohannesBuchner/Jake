package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.*;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.callbacks.ErrorCallback;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.RegistrationStatus;
import com.jakeapp.gui.swing.exceptions.ProjectNotFoundException;
import com.jakeapp.gui.swing.exceptions.ProjectFolderMissingException;
import com.jakeapp.gui.swing.helpers.FolderObject;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;

public class CoreAccessMock implements ICoreAccess {
   private static final Logger log = Logger.getLogger(CoreAccessMock.class);

   private boolean isSignedIn;
   private List<Project> projects = new ArrayList<Project>();
   private List<Project> invitedProjects = new ArrayList<Project>();
   private Map<Project, List<ProjectMember>> peopleProjectMap = new HashMap<Project, List<ProjectMember>>();
   private IFrontendService frontendService;

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


   @Override
   public void setFrontendService(IFrontendService frontendService) {
      this.frontendService = frontendService;
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
            fireConnectionStatus(ConnectionStatus.ConnectionStati.SigningIn, "");

            try {
               Thread.sleep(2000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }

            isSignedIn = true;
            currentUser = user;

            fireConnectionStatus(ConnectionStatus.ConnectionStati.Online, "");
         }
      };

      // start our runner thread, that makes callbacks to connection status
      new Thread(runner).start();
   }

   public void addConnectionStatusCallbackListener(ConnectionStatus cb) {
      log.info("Registers connection status callback: " + cb);

      connectionStatus.add(cb);
   }

   public void removeConnectionStatusCallbackListener(ConnectionStatus cb) {
      log.info("Deregisters connection status callback: " + cb);

      connectionStatus.remove(cb);
   }


   private void fireConnectionStatus(ConnectionStatus.ConnectionStati state, String str) {
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
            fireRegistrationStatus(RegistrationStatus.RegisterStati.RegistrationActive, "");

            try {
               Thread.sleep(2000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }

            fireRegistrationStatus(RegistrationStatus.RegisterStati.RegisterSuccess, "");

            // logging in after registering
            fireConnectionStatus(ConnectionStatus.ConnectionStati.SigningIn, "");

            try {
               Thread.sleep(1500);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }

            isSignedIn = true;

            fireConnectionStatus(ConnectionStatus.ConnectionStati.Online, "");
         }
      };

      // start our runner thread, that makes callbacks to connection status
      new Thread(runner).start();
   }

   public void addRegistrationStatusCallbackListener(RegistrationStatus cb) {
      log.info("Registers registration status callback: " + cb);
   }

   public void removeRegistrationStatusCallbackListener(RegistrationStatus cb) {
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

      fireConnectionStatus(ConnectionStatus.ConnectionStati.Offline, "");
   }

   public String[] getLastSignInNames() {
      return new String[]{"pstein", "csutter"};
   }


   public void addProjectChangedCallbackListener(ProjectChanged cb) {
      log.info("Mock: register project changed callback: " + cb);

      projectChanged.add(cb);
   }

   public void removeProjectChangedCallbackListener(ProjectChanged cb) {
      log.info("Mock: deregister project changed callback: " + cb);

      if (projectChanged.contains(cb)) {
         projectChanged.remove(cb);
      }
   }

   private void fireProjectChanged(ProjectChanged.ProjectChangedEvent ev) {
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
      fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
            ProjectChanged.ProjectChangedEvent.ProjectChangedReason.State));
   }

   public void startProject(Project project) {
      project.setStarted(true);

      // generate event
      fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
            ProjectChanged.ProjectChangedEvent.ProjectChangedReason.State));
   }


   public int getProjectFileCount(Project project) {
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

               fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
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


               fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
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

               fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
                     ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Rejected));

            } catch (RuntimeException run) {
               fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
            }
         }
      };

      // start our runner thread, that makes a callback to project status
      new Thread(runner).start();
   }

   public void setProjectName(Project project, String prName) {
      project.setName(prName);

      fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
            ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Name));
   }

   @Override
   public FolderObject getProjectRootFolder(Project project) throws ProjectFolderMissingException {
      // This is all mocked from the actual file system
      String rootPath = project.getRootPath();
      log.debug("File mocking: Project root path is " + rootPath);

      File rootFolder = new File(rootPath);
      if (!rootFolder.exists()) {
         throw new ProjectFolderMissingException(rootPath);
      }

      FolderObject fo = recursiveFileSystemHelper(project, rootFolder, System.getProperty("file.separator"), "");

      return fo;
   }

   /**
    * Helper method for this mock: Works recursively through the file system to
    * build the FolderObject for getProjectRootFolder()
    *
    * @param file
    *
    * @return
    */
   private FolderObject recursiveFileSystemHelper(Project prj, File file, String relPath, String name) {
      FolderObject fo = new FolderObject(relPath, name);
      log.debug("File mocking: Started recursing through folder " + file.getAbsolutePath());

      for (File f : file.listFiles()) {
         if (f.isDirectory()) {
            log.debug("File mocking: Recursing into subdirectory " + relPath + f.getName() + System.getProperty("file.separator"));
            FolderObject subfolder = recursiveFileSystemHelper(prj, f, relPath + f.getName() + System.getProperty("file.separator"),
                  f.getName());
            fo.addFolder(subfolder);
         } else {
            log.debug("File mocking: Adding file " + relPath + f.getName());
            fo.addFile(new FileObject(new UUID(5, 3), prj, relPath + f.getName()));
         }
      }

      return fo;
   }

   @Override
   public int getFileStatus(FileObject file) {
      // TODO: Make this useful
      return 0;
   }

   public List<NoteObject> getNotes(Project project) {
      List<NoteObject> list = new ArrayList<NoteObject>();
      list.add(new NoteObject(new UUID(1, 1), project, "Project: " + project.getName()));
      list.add(new NoteObject(new UUID(1, 1), project, "If you have five dollars and Chuck Norris has five dollars, Chuck Norris has more money than you"));
      list.add(new NoteObject(new UUID(2, 1), project, "Apple pays Chuck Norris 99 cents every time he listens to a song."));
      list.add(new NoteObject(new UUID(3, 1), project, "Chuck Norris is suing Myspace for taking the name of what he calls everything around you."));
      list.add(new NoteObject(new UUID(4, 1), project, "Chuck Norris destroyed the periodic table, because he only recognizes the element of surprise."));
      list.add(new NoteObject(new UUID(4, 1), project, "Chuck Norris can kill two stones with one bird."));
      list.add(new NoteObject(new UUID(5, 1), project, "The leading causes of death in the United States are: 1. Heart Disease 2. Chuck Norris 3. Cancer."));
      list.add(new NoteObject(new UUID(6, 1), project, "Chuck Norris does not sleep. He waits."));
      list.add(new NoteObject(new UUID(7, 1), project, "There is no theory of evolution. Just a list of animals Chuck Norris allows to live. "));
      list.add(new NoteObject(new UUID(8, 1), project, "Guns don't kill people, Chuck Norris does."));
      return list;
   }

   @Override
   public Date getLastEdit(NoteObject note, Project project) {
      return new Date();
   }

   @Override
   public ProjectMember getLastEditor(NoteObject note, Project project) {
      return new ProjectMember(new XMPPUserId(new ServiceCredentials("Chuck Norris", "foo"), new UUID(1, 1), "chuck norris", "chuck", "Chuck", "Norris"), TrustState.TRUST);
   }

   /**
    * Generates a list so that people are remembered when they change.
    *
    * @param project: project that should be evaluated
    *
    * @return
    */
   public List<ProjectMember> getPeople(Project project) {
      log.info("Mock: getPeople from project " + project);

      if (project == null) {
         return null;
      }

      if (!peopleProjectMap.containsKey(project)) {
         List<ProjectMember> people = new ArrayList<ProjectMember>();

         people.add(new ProjectMember(new XMPPUserId(new ServiceCredentials("User1", "pass2"),
               new UUID(22, 33), "pstein@jabber.fsinf.at", "", "Peter", "Steinberger"), TrustState.TRUST));

         people.add(new ProjectMember(new XMPPUserId(new ServiceCredentials("User2", "pass2"),
               new UUID(222, 333), "test@jabber.org", "Pr-" + project.getName(), "ProjectTestUser", project.getName()), TrustState.AUTO_ADD_REMOVE));


         people.add(new ProjectMember(new XMPPUserId(new ServiceCredentials("User3", "pass3"),
               new UUID(22, 33), "max@jabber.org", "Max", "Max", "Mustermann"), TrustState.NO_TRUST));

         peopleProjectMap.put(project, people);
      }

      return peopleProjectMap.get(project);
   }

   @Override
   public boolean setPeopleNickname(Project project, ProjectMember pm, String nick) {
      // TODO: ignore this and create a regex for checking!
      if (nick.indexOf("<") != -1) {
         return false;
      } else {
         pm.getUserId().setNickname(nick);

         fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
               ProjectChanged.ProjectChangedEvent.ProjectChangedReason.People));

         return true;
      }
   }

   @Override
   public void peopleSetTrustState(Project project, ProjectMember member, TrustState trust) {
      member.setTrustState(trust);

      fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
            ProjectChanged.ProjectChangedEvent.ProjectChangedReason.People));
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

               fireProjectChanged(new ProjectChanged.ProjectChangedEvent(pr1,
                     ProjectChanged.ProjectChangedEvent.ProjectChangedReason.Created));

            } catch (RuntimeException run) {
               fireErrorListener(new ErrorCallback.JakeErrorEvent(run));
            }
         }
      };

      // start our runner thread, that makes a callback to project status
      new Thread(runner).start();
   }


   private void fireRegistrationStatus(RegistrationStatus.RegisterStati state, String str) {
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
