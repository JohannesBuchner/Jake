package com.doublesignal.sepm.jake.core.services;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;

import com.doublesignal.sepm.jake.core.dao.IConfigurationDao;
import com.doublesignal.sepm.jake.core.dao.IJakeDatabase;
import com.doublesignal.sepm.jake.core.dao.IJakeObjectDao;
import com.doublesignal.sepm.jake.core.dao.ILogEntryDao;
import com.doublesignal.sepm.jake.core.dao.IProjectMemberDao;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeMessage;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogAction;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.Project;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.services.exceptions.ExistingProjectException;
import com.doublesignal.sepm.jake.core.services.exceptions.InvalidDatabaseException;
import com.doublesignal.sepm.jake.core.services.exceptions.InvalidRootPathException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataNotValidException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataRequiredException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginUseridNotValidException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoProjectLoadedException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFolderException;
import com.doublesignal.sepm.jake.core.services.exceptions.NonExistantDatabaseException;
import com.doublesignal.sepm.jake.fss.*;
import com.doublesignal.sepm.jake.fss.NotAReadableFileException;
import com.doublesignal.sepm.jake.ics.IICService;
import com.doublesignal.sepm.jake.ics.IMessageReceiveListener;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NoSuchUseridException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.sync.ISyncService;
import com.doublesignal.sepm.jake.sync.NotAProjectMemberException;
import com.doublesignal.sepm.jake.sync.exceptions.ObjectNotConfiguredException;

/**
 * @author johannes, domdorn, peter, philipp
 */
public class JakeGuiAccess implements IJakeGuiAccess, IMessageReceiveListener {
    private IICService ics = null;
    private ISyncService sync = null;
    private IFSService fss = null;
    private IJakeDatabase db = null;
    private IProjectMemberDao pm = null;
    private Project currentProject;
    private static Logger log = Logger.getLogger(JakeGuiAccess.class);

    private List<FileObject> filesFSS;
    private List<FileObject> filesDB;
    private Map<String, Integer> filesStatus;

	private IJakeMessageReceiveListener messageListener;



    public void login(String user, String pw) throws LoginDataRequiredException,
            LoginDataNotValidException, NetworkException, LoginUseridNotValidException {
        try {
            if (user == null)
                user = getConfigOption("userid");
            if (pw == null)
                pw = getConfigOption("password");
        } catch (NoSuchConfigOptionException e) {
            throw new LoginDataRequiredException();
        }
        try {
            if (!ics.login(user, pw)) {
                throw new LoginDataNotValidException();
            }
        } catch (NoSuchUseridException e) {
            throw new LoginUseridNotValidException();
        }
    }

    public void logout() throws NetworkException {
        log.info("Logout");
        ics.logout();
    }

    public String getLoginUserid() {
    	try {
			return getConfigOption("userid");
		} catch (NoSuchConfigOptionException e) {
			// can not happen, we check in 
	    	return null;
		}
    }

    public boolean isLoggedIn() {
        return ics.isLoggedIn();
    }

    public FileObject createFileObjectFromExternalFile(String absolutePath)
            throws NoSuchFileException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<FileObject> createFileObjects(String relPath) {
        // TODO Auto-generated method stub
        return null;
    }

    public NoteObject createNote(String content) {
        log.info("createNote: " + content);

        String userId = "";
        try {
            userId = ics.getUserid();
        } catch (NotLoggedInException e) {
            log.warn("NotLoggedInException in createNote.");
            //e.printStackTrace();
        }

        NoteObject note = NoteObject.createNoteObject(userId, content);
        db.getJakeObjectDao().save(note);
        return note;
    }

    public void editNote(NoteObject note) {
        log.info("edit Note: " + note);
        db.getJakeObjectDao().save(note);
    }

    public void removeNote(NoteObject note) {
        log.info("remove Note:" + note);
        db.getJakeObjectDao().delete(note);
    }
    
    public void removeProjectMember(ProjectMember selectedMember)	{
    	 log.info("remove Project Member with ID :" + selectedMember.getUserId());
    	//pm.remove(selectedMember);
    	currentProject.removeMember(selectedMember);
    }

    public List<JakeObject> getChangedObjects() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setProjectMemberNote(String userId,String note) throws NoSuchProjectMemberException {
       log.info("Get User by"+userId+" . Set Note "+note);
       pm.getByUserId(userId).setNotes(note);
    }



    /**
     * Returns the configuration option for a <code>configKey</code>
     *
     * @param configKey the name of the configuration option
     * @return the associated value to the key
     * @throws NoSuchConfigOptionException Raised if no option exists with the given
     *                                     <code>configKey</code>.
     */
    public String getConfigOption(String configKey) throws NoSuchConfigOptionException {
        return db.getConfigurationDao().getConfigurationValue(configKey);
    }

    /**
     * Set a configuration option.
     *
     * @param configKey   the name of the option
     * @param configValue the value of the option
     */
    public void setConfigOption(String configKey, String configValue) {
        db.getConfigurationDao().setConfigurationValue(configKey, configValue);
    }

    public List<JakeObject> getJakeObjectsByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<JakeObject> getJakeObjectsByNameAndTag(String name, List<Tag> tags) {
        // TODO Auto-generated method stub
        return null;
    }

    private List<FileObject> getFileObjectsByRelPath(String relPath)
            throws InvalidFilenameException, IOException {
        List<FileObject> results = new ArrayList<FileObject>();

        String[] files;
        try {
            files = fss.listFolder(relPath);
        } catch (InvalidFilenameException e) {
            throw new InvalidFilenameException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        File tmp;
        for (String file : files) {
            try {
                tmp = new File(fss.getFullpath(relPath + file));
                if (tmp.isDirectory()) {
                    results.addAll(getFileObjectsByRelPath(relPath + file + "/"));
                }
                if (tmp.isFile())
                    results.add(new FileObject(relPath + file));
            } catch (InvalidFilenameException e) {
                continue;
                // we simply ignore invalid filenames
            }
        }
        return results;
    }

    public List<JakeObject> getFileObjects(String relPath) {
        Set<JakeObject> tmp = new HashSet<JakeObject>();
        if(filesDB == null || filesFSS == null || filesStatus == null)
                refreshFileObjects();
        tmp.addAll(filesDB);
        tmp.addAll(filesFSS);
        List<JakeObject> results = new ArrayList<JakeObject>();
        results.addAll(tmp);
        return results;
    }

    public List<JakeObject> getJakeObjectsByTags(List<Tag> tags) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<LogEntry> getLog() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<LogEntry> getLog(JakeObject object) {
        // TODO Auto-generated method stub
        return null;
    }

	public void registerReceiveMessageListener(IJakeMessageReceiveListener listener) {
		this.messageListener = listener;
	}

	public List<JakeMessage> getNewMessages() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<NoteObject> getNotes() {
        return db.getJakeObjectDao().getAllNoteObjects();
    }

    public List<JakeObject> getOutOfSyncObjects() {
        // TODO Auto-generated method stub
        return null;
    }

    public Project getProject() {
        return currentProject;
    }

    public List<Tag> getTags() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Tag> getTagsOfObject(JakeObject object) {
        // TODO ??? brauchen wir das wirklich?? ich glaube nicht. - dominik
        return null;
    }

    public List<FileObject> importFolder(String absolutePath) throws NoSuchFolderException {
        // TODO Auto-generated method stub
        return null;
    }

    public void logSync() throws NetworkException {
    	log.info("startign logsync...");
    	List<ProjectMember> members = currentProject.getMembers();
    	for (ProjectMember member : members) {
    		try {
    			sync.syncLogAndGetChanges(member.getUserId());
    		} catch (ObjectNotConfiguredException e) {
    			log.warn("sync is not configured; " + e.getMessage());
    		} catch (OtherUserOfflineException e) {
    			log.warn("other user is offline");
    		} catch (NotAProjectMemberException e) {
    			log.warn("the user: " + member.getUserId() + " is not a valid project member");
    		} catch (NetworkException e) {
    			log.warn("a network exception was raised: " + e.getMessage());
    			log.debug("userid: " + member.getUserId());
    		}
        }
    }

    public void pullObjects() throws NetworkException {
        // TODO Auto-generated method stub

    }

    public void pushObjects() throws NetworkException {
        // TODO Auto-generated method stub

    }

    public void registerProjectInvitationCallback(Observer obs) {
        // TODO Auto-generated method stub

    }

    public void registerReceiveMessageCallback(Observer observer) {
        // TODO Auto-generated method stub

    }

    public long getFileSize(FileObject fileObject) {
        try {
            return fss.getFileSize(fileObject.getName());
        } catch (InvalidFilenameException e) {
            return 0;
        } catch (FileNotFoundException e) {
            return 0;
        } catch (NotAFileException e) {
            return 0;
        }
    }

    public void addProjectMember(String networkUserId) {
        ProjectMember PM = new ProjectMember(networkUserId);
        currentProject.addMember(PM);
        db.getProjectMemberDao().save(PM);
    }

    public List<ProjectMember> getMembers()	{
    	return currentProject.getMembers();
    	
    }
    
    public ProjectMember getLastModifier(JakeObject jakeObject) {
        // sync.getLogEntries(jakeObject)
        // return new
        // ProjectMember(logEntryDAO.getMostRecentFor(jakeObject).getUserId());
        return new ProjectMember("dominik"); // TODO
        // return null;
    }

    public Date getLastModified(JakeObject jakeObject) {
        GregorianCalendar date = new GregorianCalendar();
        date.set(2008, 05, 13, 13, 12);
        return date.getTime();

        // return logEntryDAO.getMostRecentFor(jakeObject).getTimestamp();
    }

    public JakeObject addTag(JakeObject jakeObject, Tag tag) {
        log.debug("Adding tag '" + tag + "' to JakeObject '" + jakeObject.getName() + "' ");
        jakeObject.addTag(tag);
        // todo access jakeObjectDao
        return jakeObject;
    }

    public JakeObject removeTag(JakeObject jakeObject, Tag tag) {
        log.debug("removing tag '" + tag + "' from JakeObject '" + jakeObject.getName() + "' ");
        jakeObject.removeTag(tag);
        // todo access jakeObjectDao
        return jakeObject;
    }

    public void launchFile(String relpath) throws InvalidFilenameException, LaunchException,
            IOException, NoProjectLoadedException {
        fss.launchFile(relpath);
    }

    private JakeGuiAccess(String rootPath) throws NotADirectoryException, InvalidDatabaseException,
            InvalidRootPathException {
        log.info("Setup the JakeGuiAccess Object");
        BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));

        ics = (IICService) factory.getBean("ICService");
        sync = (ISyncService) factory.getBean("SyncService");
        fss = (IFSService) factory.getBean("FSService");
        db = (IJakeDatabase) factory.getBean("JakeDatabase");
        
        db.setConfigurationDao((IConfigurationDao) factory.getBean("ConfigurationDao"));
        db.setJakeObjectDao((IJakeObjectDao) factory.getBean("JakeObjectDao"));
        db.setProjectMemberDao((IProjectMemberDao) factory.getBean("ProjectMemberDao"));
        db.setLogEntryDao((ILogEntryDao) factory.getBean("LogEntryDao"));
        
        try {
            db.connect(rootPath);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidDatabaseException();
        }
        try {
            fss.setRootPath(rootPath);
        } catch (IOException e) {
            throw new InvalidRootPathException();
        }
        
        sync.setLogEntries(db.getLogEntryDao().getAll());
        sync.setProjectMembers(db.getProjectMemberDao().getAll());
    }


    private static void copyTextFile(File source, File target) throws IOException {
        if(!source.exists())
        	throw new IOException("Resource " + source.getAbsolutePath() + 
        			"does not exist.");
    	BufferedReader fis = new BufferedReader(new FileReader(source));
        BufferedWriter fos = new BufferedWriter(new FileWriter(target));
        while (true) {
            String l = fis.readLine();
            if (l == null)
                break;
            fos.write(l);
            fos.newLine();
        }
        if (fis != null)
            fis.close();
        if (fos != null)
            fos.close();
    }
    
    private void copyFile(File source, File target) throws IOException {
        FileChannel srcChannel = new FileInputStream(source).getChannel();
        FileChannel dstChannel = new FileOutputStream(target).getChannel();
        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
        srcChannel.close();
        dstChannel.close();
    }


    public static void createSchema(String rootPath) throws InvalidDatabaseException {
        File scriptfile = new File(rootPath + ".script");
        File propertiesfile = new File(rootPath + ".properties");
        log.debug("create schema of " + scriptfile.getAbsolutePath());
        if (!scriptfile.exists())
            try {
                scriptfile.createNewFile();
            } catch (IOException e1) {
                throw new InvalidDatabaseException();
            }
        if (!(scriptfile.exists() && scriptfile.isFile() && scriptfile.canWrite())) {
            throw new InvalidDatabaseException();
        }
        log.debug("copying over ...");
        try {
            ClassPathResource scriptres = new ClassPathResource("skeleton.script");
            log.debug("ClassPathResource: " + scriptres.getFile().getAbsolutePath());
            copyTextFile(scriptres.getFile(), scriptfile);
            ClassPathResource propertiesres = new ClassPathResource("skeleton.properties");
            log.debug("ClassPathResource: " + propertiesres.getFile().getAbsolutePath());
            copyTextFile(propertiesres.getFile(), propertiesfile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidDatabaseException();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new InvalidDatabaseException();
        }
        System.err.println("copying done ...");
        log.debug("copying done ...");

    }

    public static boolean hasRootpathAProject(String rootPath) {
        File d = new File(rootPath);
        rootPath = d.getAbsolutePath();
        File jakeFile = new File(d.getParentFile(), d.getName() + ".script");
        return jakeFile.exists();
    }

    public static JakeGuiAccess createNewProjectByRootpath(
    		String rootPath, String projectname, String userid)
            throws ExistingProjectException, InvalidDatabaseException, 
            NotADirectoryException, InvalidRootPathException 
    {
        rootPath = new File(rootPath).getAbsolutePath();
        log.debug("createNewProjectByRootpath: " + rootPath);
        if (hasRootpathAProject(rootPath))
            throw new ExistingProjectException();
        
        log.debug("createSchema");
        createSchema(rootPath);
        log.debug("JakeGuiAccess");
        JakeGuiAccess jga;
        jga = new JakeGuiAccess(rootPath);
        try {
        	jga.initializeDatabase(projectname, userid);
        } catch (BadSqlGrammarException e) {
            log.error("Database invalid - Schema was not accepted by hsqldb");
            throw new InvalidDatabaseException();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new InvalidDatabaseException();
        }
        try{
            jga.checkIfValidDatabase();
        }catch (BadSqlGrammarException e) {
            e.printStackTrace();
        	log.error("Database invalid - We have to change our schema");
            throw new InvalidDatabaseException();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new InvalidDatabaseException();
        }
        log.debug("project created and loaded.");

        jga.currentProject = new Project(new File(rootPath), projectname);

        return jga;
    }

    private void initializeDatabase(String projectname, String userid) {
        log.debug("setting config option rootpath ... ");
        db.getConfigurationDao().setConfigurationValue("rootpath", fss.getRootPath());
        log.debug("setting config option projectname ... ");
        db.getConfigurationDao().setConfigurationValue("projectname", projectname);
        log.debug("setting config option userid ... ");
        db.getConfigurationDao().setConfigurationValue("userid", userid);

        db.getConfigurationDao().setConfigurationValue("autoPush", String.valueOf(false));
        db.getConfigurationDao().setConfigurationValue("autoPull", String.valueOf(true));
        db.getConfigurationDao().setConfigurationValue("logsyncInterval", String.valueOf(5));
        db.getConfigurationDao().setConfigurationValue("showOfflineProjectMembers", String.valueOf(true));
        db.getConfigurationDao().setConfigurationValue("autoRefresh", String.valueOf(true));
        /* don't know. maybe scan in files? */
        
    }


    private void checkIfValidDatabase() throws InvalidDatabaseException {
    	
    	db.getJakeObjectDao().getAllFileObjects();
    	db.getJakeObjectDao().getAllNoteObjects();
    	db.getLogEntryDao().getAll();
    	db.getProjectMemberDao().getAll();
    	try {
			getConfigOption("rootpath");
	    	getConfigOption("projectname");
	    	getConfigOption("userid");
		} catch (NoSuchConfigOptionException e) {
			e.printStackTrace();
			throw new InvalidDatabaseException();
		}
	}
    
    
    public static JakeGuiAccess openProjectByRootpath(String rootPath)
            throws NonExistantDatabaseException, InvalidDatabaseException, InvalidRootPathException {
        File f = new File(rootPath);
        rootPath = f.getAbsolutePath();
        log.debug("openProjectByRootpath: " + rootPath);
        if (!hasRootpathAProject(rootPath))
            throw new NonExistantDatabaseException();

        JakeGuiAccess jga;
        String projectName = null;

        try {
            jga = new JakeGuiAccess(rootPath);
            try {
                projectName = jga.db.getConfigurationDao().getConfigurationValue("projectname");
            } catch (NoSuchConfigOptionException e) {
                log.error("projectname config entry is missing!");
                throw new InvalidDatabaseException();
            }
            log.debug("Project is named " + projectName + ".");
            
            jga.checkIfValidDatabase();

            jga.db.getConfigurationDao().setConfigurationValue("rootpath", f.getAbsolutePath());

            jga.currentProject = new Project(f, projectName);

            log.debug("Project opened.");
            
        } catch (BadSqlGrammarException e) {
            System.out.println(e);
            throw new InvalidDatabaseException();
        } catch (DataAccessException e) {
            System.out.println(e);
            throw new InvalidDatabaseException();
        } catch (NotADirectoryException e) {
            System.out.println(e);
            throw new InvalidRootPathException();
        }

        /*
           * if no exceptions were thrown until here, the database is (assumed) to
           * be valid.
           */

        return jga;
    }

	public void close() throws SQLException {
        db.close();
    }

    public boolean getJakeObjectLock(JakeObject jakeObject) {
        // TODO
        return false;
    }

    public boolean setJakeObjectLock(boolean isLocked, JakeObject jakeObject) {
        // TODO
        return false;
    }

    public boolean deleteJakeObject(JakeObject jakeObject) {
        try {
			fss.deleteFile(jakeObject.getName());
			db.getLogEntryDao().create(new LogEntry(LogAction.DELETE, new Date(), jakeObject.getName(), fss.calculateHash(fss.readFile(jakeObject.getName())), getLoginUserid(), "deleting file"));
		} catch (FileNotFoundException e) {
			log.warn("Failed to delete file: File not found!");
		} catch (NotAFileException e) {
			log.warn("Failed to delete file: Not a file!");
		} catch (InvalidFilenameException e) {
			log.warn("Failed to delete file: Invalid file name!");
		} catch (NotAReadableFileException e) {
			log.warn("Failed to delete file: File is not readable");
		}
		return false;
    }

    public void propagateJakeObject(JakeObject jakeObject) {
        // TODO
    }

    public void pullJakeObject(JakeObject jakeObject) {
        // todo
    }

    public void refreshFileObjects() {
        log.debug("calling refreshFileObjects() ");
        
        try {
			logSync();
		} catch (NetworkException e) {
			log.warn("a network exception occured; " + e.getMessage());
		}
        
        filesDB = getFileObjectsFromDB();
        try {
            filesFSS = getFileObjectsByRelPath("/");
        } catch (InvalidFilenameException e) {
            // slightly ignore
            filesFSS = null;
        } catch (IOException e) {
            // slightly ignore
            filesFSS = null;
        }


        Set<FileObject> result = new HashSet<FileObject>();
        result.addAll(filesDB);
        result.addAll(filesFSS);
        if (filesStatus == null)
            filesStatus = new HashMap<String, Integer>();

        for (FileObject file : result) {
            filesStatus.put(file.getName(), getRealFileObjectSyncStatus(file));
        }
    }





    private int getRealFileObjectSyncStatus(FileObject jakeObject) {
        /**
         *    A = in FileSystem
         *    B = in DataBase
         *    C = changed local
         *    D = changed remote
         *    -A -B -C -D | 100 | - no valid status -                   SYNC_NO_VALID_STATE
         *    -A -B -C  D | 100 | - no valid status -                   SYNC_NO_VALID_STATE
         *    -A -B  C -D | 100 | - no valid status -                   SYNC_NO_VALID_STATE
         *    -A -B  C  D | 100 | - no valid status -                   SYNC_NO_VALID_STATE
         *    -A  B -C -D | 101 | - Remote File -> pull -               SYNC_FILE_IS_REMOTE
         *    -A  B -C  D | 101 | - Remote File -> pull -               SYNC_FILE_IS_REMOTE
         *    -A  B  C -D | 107 | - SYNC_FILE_DELETED_LOCALLY - // TODO SYNC_FILE_DELETED_LOCALLY
         *    -A  B  C  D | 107 | - SYNC_FILE_DELETED_LOCALLY - // TODO SYNC_FILE_DELETED_LOCALLY
         *     A -B -C -D | 102 | - local file, not in project -        SYNC_LOCAL_FILE_NOT_IN_PROJECT
         *     A -B -C  D | 100 | - no valid state -                    SYNC_NO_VALID_STATE
         *     A -B  C -D | 102 | - local file, not in project -        SYNC_LOCAL_FILE_NOT_IN_PROJECT
         *     A -B  C  D | 100 | - no valid state -                    SYNC_NO_VALID_STATE
         *     A  B -C -D | 103 | - File in Sync -                      SYNC_FILE_IN_SYNC
         *     A  B -C  D | 104 | - File remotely changed -             SYNC_FILE_REMOTELY_CHANGED
         *     A  B  C -D | 105 | - File locally changed -              SYNC_FILE_LOCALLY_CHANGED
         *     A  B  C  D | 106 | - File in conflict -                  SYNC_FILE_IN_CONFLICT

         // TODO possibility to optimize using quine-mcklusky's method

         */

        boolean fileOnFS = false;
        boolean fileInDB = false;
        boolean fileLocallyChanged = false;
        boolean fileRemotelyChanged = false;


        if (filesFSS.contains(new FileObject(jakeObject.getName())))
            fileOnFS = true;

        if (filesDB.contains(new FileObject(jakeObject.getName())))
            fileInDB = true;

        if (fileOnFS && fileInDB) {
            // todo check if file locally changed

        }
        if (fileInDB) {
            // todo check logEntry
        }

        /** this part is finished, no need to modify, except possible optimizations */

        if (fileOnFS && fileInDB && !fileLocallyChanged && !fileRemotelyChanged) {
            return IJakeGuiAccess.SYNC_FILE_IN_SYNC;
        }

        if (fileOnFS && fileInDB && !fileLocallyChanged && fileRemotelyChanged) {
            return IJakeGuiAccess.SYNC_FILE_REMOTELY_CHANGED;
        }

        if (
                fileOnFS && fileInDB && !fileLocallyChanged && !fileRemotelyChanged
                ) {
            return IJakeGuiAccess.SYNC_FILE_LOCALLY_CHANGED;
        }

        if (fileOnFS && fileInDB && fileLocallyChanged && fileRemotelyChanged) {
            return IJakeGuiAccess.SYNC_FILE_IN_CONFLICT;
        }


        if (
            // doing status 101
                !fileOnFS && !fileInDB && !fileLocallyChanged && !fileRemotelyChanged ||
                        !fileOnFS && !fileInDB && !fileLocallyChanged && fileRemotelyChanged
                ) {
            return IJakeGuiAccess.SYNC_FILE_IS_REMOTE;
        }

        if (
            // doing 102
                fileOnFS && !fileInDB && !fileLocallyChanged && !fileRemotelyChanged ||
                        fileOnFS && !fileInDB && fileLocallyChanged && !fileRemotelyChanged
                ) {
            return IJakeGuiAccess.SYNC_LOCAL_FILE_NOT_IN_PROJECT;
        }
        /*
         *    -A  B  C -D | 107 | - no valid status - // TODO
         *    -A  B  C  D | 107 | - no valid status - // TODO

         */

        if(
        !fileOnFS && fileInDB &&  fileLocallyChanged
                /* &&  !fileRemotelyChanged ||
        !fileOnFS && fileInDB &&  fileLocallyChanged &&   fileRemotelyChanged */
        )
        {
            /* local file is in database but deleted from project folder */
            return IJakeGuiAccess.SYNC_FILE_DELETED_LOCALLY;
        }

/*        if( // doing code 100
        !fileOnFS && !fileInDB && !fileLocallyChanged && !fileRemotelyChanged ||
        !fileOnFS && !fileInDB && !fileLocallyChanged &&  fileRemotelyChanged ||
        !fileOnFS && !fileInDB &&  fileLocallyChanged && !fileRemotelyChanged ||
        !fileOnFS && !fileInDB &&  fileLocallyChanged &&  fileRemotelyChanged ||

         fileOnFS && fileInDB &&  !fileLocallyChanged &&  !fileRemotelyChanged
        )
{
    return 100;
}*/

        return IJakeGuiAccess.SYNC_NO_VALID_STATE;
    }




    private List<FileObject> getFileObjectsFromDB() {
        if (filesDB == null)
            filesDB = db.getJakeObjectDao().getAllFileObjects();
        List<FileObject> results = new ArrayList<FileObject>();
        results.addAll(filesDB);
        return results;
    }

    public boolean importLocalFileIntoProject(String relPath) {
        FileObject fileObject = new FileObject(relPath);

        db.getJakeObjectDao().save(fileObject);
        // create logEntry


        try {


            // userId = ics.getUserid();
            String userId = getLoginUserid();
            String comment = "";
            String hash = fss.calculateHashOverFile(fileObject.getName());

            LogEntry logEntry = new LogEntry(
                    LogAction.NEW_JAKEOBJECT,
                    new Date(),
                    fileObject.getName(),
                    /* hash */
                    hash,
                    /* userId */
                    userId,
                    /* comment */
                    comment
            );

            db.getLogEntryDao().create(logEntry);
            log.debug("persisted logentry");
        } catch (InvalidFilenameException e) {
            e.printStackTrace();
            return false;
        } catch (NotAReadableFileException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }  

        filesDB.add(fileObject);

        filesStatus.put(relPath, getRealFileObjectSyncStatus(fileObject));
        //log.debug("putted status of file " + relPath + " to " + filesStatus.get(relPath));

        // TODO
        return true;
    }

    public boolean importLocalFileIntoProject(String absolutePath, String destinationFolderAbsolutePath) {
        log.debug("calling importLocalFileIntoProject(\n"+ absolutePath +",\n"+destinationFolderAbsolutePath +"\n);");
        File srcFile = new File(absolutePath);
        if(srcFile == null || !srcFile.exists() )
            return false;

        File destinationFolder = new File(destinationFolderAbsolutePath);
        if(!destinationFolder.getAbsolutePath().startsWith(getProject().getRootPath().getAbsolutePath()))
        {
            log.debug("destination folder not in projectRootPath");
            return false;
        }



        String filename = srcFile.getName();
        File destinationFile = new File(destinationFolderAbsolutePath, filename);

        if(destinationFile.exists())
        {
            log.debug("destinationFile already exists");
            return false;
        }

        try
        {
            destinationFile.createNewFile();
            copyFile(srcFile, destinationFile );
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String relPath = destinationFile.getAbsolutePath().
                replace(getProject().getRootPath().getAbsolutePath(),"");

        return importLocalFileIntoProject(relPath);
    }

    public Integer getFileObjectSyncStatus(JakeObject jakeObject) {
        //   log.debug("calling getFileObjectSyncStatus on JakeObject "+ jakeObject.getName());


        if(filesDB == null)
            log.debug("filesDB is null!");

        if(filesFSS == null)
            log.debug("FilesFSS is null");

        if(filesStatus == null)
            log.debug("filesStatus is null");

        if (filesDB == null && filesFSS == null || filesStatus == null)
            refreshFileObjects();

        //return 100;
        return filesStatus.get(jakeObject.getName());

       // return status == null ? 0 : status;
    }

	public static String getICSName() {
        BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
		IICService ics = (IICService) factory.getBean("ICService");
		return ics.getServiceName();
	}

	public static boolean isOfCorrectUseridFormat(String userid) {
        BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
		IICService ics = (IICService) factory.getBean("ICService");
		return ics.isOfCorrectUseridFormat(userid);
	}

	/**
	 * This comes from the ICS and is called whenever a message is received from another project member
	 *
	 * @param from_userid The project member sending the message
	 * @param content The content of the message
	 */
	public void receivedMessage(String from_userid, String content) {
		try {
			JakeMessage jm = new JakeMessage(db.getProjectMemberDao().getByUserId(ics.getUserid()), db.getProjectMemberDao().getByUserId(from_userid), content);
			if(this.messageListener != null) {
				messageListener.receivedJakeMessage(jm);
			}
		} catch (NoSuchProjectMemberException e) {
			/*
			 * Apparently, this user does not exist in our project. We don't care about messages from people who don't
			 * exist in that context, so we can safely discard the message. */
		} catch (NotLoggedInException e) {
			/*
			 * "Technically", this should never happen, since this listener only gets called when we are logged in
			 * anyway. In this case, it is safe to assume something weird has happened and we will simply discard
			 * the message.
			 */
		}
	}
}
