package com.doublesignal.sepm.jake.core.services;

import com.doublesignal.sepm.jake.core.InvalidApplicationState;
import com.doublesignal.sepm.jake.core.InvalidApplicationState.StateType;
import com.doublesignal.sepm.jake.core.dao.*;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchLogEntryException;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.core.domain.*;
import com.doublesignal.sepm.jake.core.services.exceptions.*;
import com.doublesignal.sepm.jake.fss.*;
import com.doublesignal.sepm.jake.ics.IICService;
import com.doublesignal.sepm.jake.ics.IMessageReceiveListener;
import com.doublesignal.sepm.jake.ics.exceptions.*;
import com.doublesignal.sepm.jake.sync.ISyncService;
import com.doublesignal.sepm.jake.sync.exceptions.NotAProjectMemberException;
import com.doublesignal.sepm.jake.sync.exceptions.ObjectNotConfiguredException;
import com.doublesignal.sepm.jake.sync.exceptions.SyncException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;

import java.io.*;
import java.nio.channels.FileChannel;
import java.rmi.NoSuchObjectException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

/**
 * @author johannes, domdorn, peter, philipp
 */
public class JakeGuiAccess implements IJakeGuiAccess, IMessageReceiveListener {
    private IICService ics = null;
    private ISyncService sync = null;
    private IFSService fss = null;
    private IJakeDatabase db = null;
    private Project currentProject;
    private static Logger log = Logger.getLogger(JakeGuiAccess.class);

    private List<FileObject> filesFSS;
    private List<FileObject> filesDB;
    private HashMap<String, Integer> filesStatus;
    
	private IJakeMessageReceiveListener messageListener;
	private IConflictCallback conflictCallback = null;
	private TreeSet<String> lastConflicts = new TreeSet<String>();
	
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
        log.debug("Logout");
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
    
    public Boolean isLoggedIn(String userId) throws NotLoggedInException, 
    	NoSuchUseridException 
    {
    	try {
			return ics.isLoggedIn(userId);
    	} catch (NotLoggedInException e) {
    		throw e;
    	} catch (NoSuchUseridException e) {
    		throw e;
    	} catch (TimeoutException e) {
			InvalidApplicationState.die("unhandled");
			return null;
		} catch (NetworkException e) {
			InvalidApplicationState.die("unhandled");
			return null;
		}
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
        log.debug("createNote: " + content);

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
        log.debug("edit Note: " + note);
        db.getJakeObjectDao().save(note);
    }

    public void removeNote(NoteObject note) {
        log.debug("remove Note:" + note);
        db.getJakeObjectDao().delete(note);
    }
    
    public void removeProjectMember(ProjectMember selectedMember)	{
    	log.debug("remove Project Member with ID :" + selectedMember.getUserId());
    	db.getProjectMemberDao().remove(selectedMember);
    }
    
    public void editProjectMemberNote(ProjectMember selectedMember , String note)	{
    	log.debug("edit Note for Project Member with ID :" + selectedMember.getUserId());
    	db.getProjectMemberDao().editNote(selectedMember,note);
    }
    
    public void editProjectMemberNickName(ProjectMember selectedMember , String nickName)	{
    	log.debug("edit NickName for Project Member with ID :" + selectedMember.getUserId());
    	db.getProjectMemberDao().editNickName(selectedMember,nickName);
    }
    
    
	public void editProjectMemberUserId(ProjectMember selectedMember , String userId) {
		log.debug("edit UserId for Project Member with ID :" + selectedMember.getUserId());
		db.getProjectMemberDao().editUserId(selectedMember , userId);
	}

    public void setProjectMemberNote(String userId,String note) throws NoSuchProjectMemberException {
       log.debug("Get User by"+userId+" . Set Note "+note);
       db.getProjectMemberDao().getByUserId(userId).setNotes(note);
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

    public List<JakeObject> getJakeObjectsByTags(List<Tag> tags) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<LogEntry> getLog() {
        return db.getLogEntryDao().getAll();
    }
    
    public void createLog(LogEntry logEntry) {
         db.getLogEntryDao().create(logEntry);
    }

    public List<LogEntry> getLog(JakeObject object) {
        return db.getLogEntryDao().getAllOfJakeObject(object);
    }

	public void registerReceiveMessageListener(IJakeMessageReceiveListener listener) {
		this.messageListener = listener;
	}

	public void sendMessage(JakeMessage message) throws NetworkException, NotLoggedInException, TimeoutException,
			NoSuchUseridException, OtherUserOfflineException {
		this.ics.sendMessage(message.getRecipient().getUserId(), message.getContent());
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
    
	public List<JakeMessage> getNewMessages() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<NoteObject> getNotes() {
        return db.getJakeObjectDao().getAllNoteObjects();
    }

    public Project getProject() {
        return currentProject;
    }

	public ProjectMember getProjectMember(String userId) throws NoSuchProjectMemberException {
		return this.db.getProjectMemberDao().getByUserId(userId);
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

    public void addProjectMember(String networkUserId) {
        ProjectMember PM = new ProjectMember(networkUserId);
        currentProject.addMember(PM);
        db.getProjectMemberDao().save(PM);
    }

    public List<ProjectMember> getMembers()	{
    	return db.getProjectMemberDao().getAll();
    	
    }
    
    public ProjectMember getLastModifier(JakeObject jakeObject) throws NoSuchLogEntryException {
        LogEntry logEntry = db.getLogEntryDao().getMostRecentFor(jakeObject);
        return new ProjectMember(logEntry.getUserId());
    }

    public Date getLastModified(JakeObject jakeObject) throws NoSuchLogEntryException {
        LogEntry logEntry = db.getLogEntryDao().getMostRecentFor(jakeObject);
        return logEntry.getTimestamp();
    }
    public Date getLocalLastModified(FileObject jo) {
        try {
			return new Date(fss.getLastModified(jo.getName()));
		} catch (NotAFileException e) {
			InvalidApplicationState.shouldNotHappen();
		} catch (InvalidFilenameException e) {
			InvalidApplicationState.shouldNotHappen();
		}
		return null;
    }



    public JakeObject addTag(JakeObject jakeObject, Tag tag) {
        log.debug("Adding tag '" + tag + "' to JakeObject '" + jakeObject.getName() + "' ");
        jakeObject.addTag(tag);
        db.getLogEntryDao().create(new LogEntry(LogAction.TAG_ADD, new Date(), jakeObject.getName(), "", getLoginUserid(), tag.getName()));

        db.getJakeObjectDao().addTagsTo(jakeObject, tag);
        return jakeObject;
    }

    public JakeObject removeTag(JakeObject jakeObject, Tag tag) {
        log.debug("removing tag '" + tag + "' from JakeObject '" + jakeObject.getName() + "' ");

        jakeObject.removeTag(tag);
        db.getLogEntryDao().create(new LogEntry(LogAction.TAG_REMOVE, new Date(), jakeObject.getName(), "",
                getLoginUserid(), tag.getName()));

        db.getJakeObjectDao().removeTagsFrom(jakeObject,tag);
        return jakeObject;
    }

    public void launchFile(String relpath) throws InvalidFilenameException, LaunchException,
            IOException {
        fss.launchFile(relpath);
    }

    private JakeGuiAccess(String rootPath) throws NotADirectoryException, InvalidDatabaseException,
            InvalidRootPathException {
        log.debug("Setup the JakeGuiAccess Object");
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
//            e.printStackTrace();
            throw new InvalidDatabaseException();
        }
        try {
            fss.setRootPath(rootPath);
        } catch (IOException e) {
            throw new InvalidRootPathException();
        }
        
        sync.setDatabase(db);

	    ics.registerReceiveMessageListener(this);
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
//            e.printStackTrace();
            throw new InvalidDatabaseException();
        } catch (RuntimeException e) {
//            e.printStackTrace();
            throw new InvalidDatabaseException();
        }
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
//            e.printStackTrace();
            throw new InvalidDatabaseException();
        }
        try{
            jga.checkIfValidDatabase();
        }catch (BadSqlGrammarException e) {
//            e.printStackTrace();
        	log.error("Database invalid - We have to change our schema");
            throw new InvalidDatabaseException();
        } catch (RuntimeException e) {
//            e.printStackTrace();
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
//			e.printStackTrace();
			throw new InvalidDatabaseException();
		}
	}
    
    
    public static IJakeGuiAccess openProjectByRootpath(String rootPath)
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
//        	e.printStackTrace();
            throw new InvalidDatabaseException();
        } catch (DataAccessException e) {
//            e.printStackTrace();
            throw new InvalidDatabaseException();
        } catch (NotADirectoryException e) {
//            e.printStackTrace();
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
        try {
			fss.setRootPath(null);
		} catch (Exception e) {
			/* we know this fails */
		}
    }

    public LogEntry getJakeObjectLockLogEntry(JakeObject jakeObject) {
        List<LogEntry> logs = getLog(jakeObject);
        for (LogEntry log: logs) {
        	if (log.getAction() == LogAction.LOCK || log.getAction() == LogAction.UNLOCK) {
        		return log;
        	}
        }
        return null;
    }
    
    public boolean getJakeObjectLock(JakeObject jakeObject) {
    	LogEntry log = getJakeObjectLockLogEntry(jakeObject);
    	if (log == null || log.getAction() == LogAction.UNLOCK) {
    		return false;
    	}
    	return true;
    }

    public void setJakeObjectLock(JakeObject jakeObject, boolean isLocked) {
        if (isLocked) {
        	setJakeObjectLockComment(jakeObject, "");
        } else {
        	try {
    			db.getLogEntryDao().create(new LogEntry(LogAction.UNLOCK, new Date(), jakeObject.getName(),
                        fss.calculateHash(fss.readFile(jakeObject.getName())), getLoginUserid(), ""));
    		} catch (FileNotFoundException e) {
    			log.warn("failed to create log entry: file not found");
    		} catch (InvalidFilenameException e) {
    			log.warn("failed to create log entrwhoy: invalid file name");
    		} catch (NotAReadableFileException e) {
    			log.warn("failed to create log entry: not a readable file exception");
    		}
        }
    }
    
    public void setJakeObjectLockComment(JakeObject jakeObject, String lockComment) {
		try {
			db.getLogEntryDao().create(new LogEntry(LogAction.LOCK, new Date(), jakeObject.getName(),
                    fss.calculateHash(fss.readFile(jakeObject.getName())), getLoginUserid(), lockComment));
		} catch (FileNotFoundException e) {
			log.warn("failed to create log entry: file not found");
		} catch (InvalidFilenameException e) {
			log.warn("failed to create log entry: invalid file name");
		} catch (NotAReadableFileException e) {
			log.warn("failed to create log entry: not a readable file exception");
		}
	}
    
   	public ProjectMember getJakeObjectLockedBy(JakeObject jakeObject) {
		// TODO Auto-generated method stub
		return new ProjectMember("foobar User");
	}

	public boolean deleteJakeObject(JakeObject jakeObject) {
    	log.debug("trying to delete object: " + jakeObject.getName());
    	Boolean success = false;
        try {
			db.getLogEntryDao().create(new LogEntry(LogAction.DELETE, new Date(), jakeObject.getName(),
                    fss.calculateHash(fss.readFile(jakeObject.getName())), getLoginUserid(), "deleting file"));
			success = fss.deleteFile(jakeObject.getName());
			filesStatus.remove(jakeObject.getName());
			filesFSS.remove(jakeObject.getName());
			filesDB.remove(jakeObject.getName());
		} catch (FileNotFoundException e) {
			log.warn("Failed to delete file: File not found!");
		} catch (NotAFileException e) {
			log.warn("Failed to delete file: Not a file!");
		} catch (InvalidFilenameException e) {
			log.warn("Failed to delete file: Invalid file name!");
		} catch (NotAReadableFileException e) {
			log.warn("Failed to delete file: File is not readable");
		}
		return success;
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
			InvalidApplicationState.die("NotLoggedInException from receiveMessage", e);
		}
	}
	
	
	/* *** Synchronisation stuff *** */

	public void setConflictCallback(IConflictCallback cc) {
		this.conflictCallback = cc;
	}
	private void conflictOccured(final JakeObject jo) {
		log.debug("conflictOccured on " + jo.getName());
		if(conflictCallback!=null)
			conflictCallback.conflictOccured(jo);
	}

	public void pushJakeObject(JakeObject jo, String commitmsg)
			throws SyncException, NotLoggedInException {
		try {
			sync.push(jo, getConfigOption("userid"), commitmsg);
		} catch (NoSuchConfigOptionException e) {
			InvalidApplicationState.shouldNotHappen();
		} catch (ObjectNotConfiguredException e) {
			InvalidApplicationState.die(e);
		} catch (SyncException e) {
			throw e;
		}
	}

	public void pullJakeObject(JakeObject jo) throws NotLoggedInException,
			OtherUserOfflineException, NoSuchObjectException, NoSuchLogEntryException {
		byte[] content;
		try {
			content = sync.pull(jo);
			if (jo.getName().startsWith("note:")) {
				NoteObject no = new NoteObject(jo.getName(),
						new String(content));
				db.getJakeObjectDao().save(no);
			} else {
				fss.writeFile(jo.getName(), content);
			}
			db.getLogEntryDao().setIsLastPulled(db.getLogEntryDao().getMostRecentFor(jo));
		} catch (NoSuchObjectException e) {
			throw e;
		} catch (NotLoggedInException e) {
			throw e;
		} catch (TimeoutException e) {
			InvalidApplicationState.notImplemented(e);
		} catch (NetworkException e) {
			InvalidApplicationState.notImplemented(e);
		} catch (OtherUserOfflineException e) {
			throw e;
		} catch (ObjectNotConfiguredException e) {
			InvalidApplicationState.die(e);
		} catch (NoSuchLogEntryException e) {
			throw e;
		} catch (FileTooLargeException e) {
			InvalidApplicationState.die(e);
		} catch (NotAFileException e) {
			InvalidApplicationState.die(e);
		} catch (InvalidFilenameException e) {
			InvalidApplicationState.die(e);
		} catch (IOException e) {
			InvalidApplicationState.die(e);
		} catch (CreatingSubDirectoriesFailedException e) {
			InvalidApplicationState.notImplemented(e);
		}
	}

	
	public File pullRemoteFileInTempFile(FileObject jo) 
		throws NotLoggedInException, OtherUserOfflineException
	{
		log.debug("pullRemoteFileInTempFile");
		try {
			byte[] content = sync.pull(jo);
			File f = new File(fss.getTempFile());
			FileOutputStream fr = new FileOutputStream(f);
			fr.write(content);
			fr.close();
			log.debug("was successful, tempfile=" + f.getAbsolutePath());
			return f;
		} catch (NoSuchObjectException e) {
			InvalidApplicationState.shouldNotHappen(e);
		} catch (NotLoggedInException e) {
			throw e;
		} catch (TimeoutException e) {
			InvalidApplicationState.notImplemented();
		} catch (NetworkException e) {
			InvalidApplicationState.notImplemented(e);
		} catch (OtherUserOfflineException e) {
			throw e;
		} catch (ObjectNotConfiguredException e) {
			InvalidApplicationState.shouldNotHappen();
		} catch (NoSuchLogEntryException e) {
			InvalidApplicationState.shouldNotHappen(e);
		} catch (IOException e) {
			InvalidApplicationState.shouldNotHappen(e);
		}
		return null;
	}
	
	private List<JakeObject> syncLogAndGetChanges(String userid)
			throws OtherUserOfflineException, NotAProjectMemberException,
			NotLoggedInException 
	{
		List<JakeObject> changes = null;
		try {
			changes = sync.syncLogAndGetChanges(userid);
		} catch (ObjectNotConfiguredException e) {
			InvalidApplicationState.die(e);
		} catch (OtherUserOfflineException e) {
			throw e;
		} catch (NotAProjectMemberException e) {
			throw e;
		} catch (NotLoggedInException e) {
			throw e;
		} catch (TimeoutException e) {
			InvalidApplicationState.notImplemented();
		} catch (NetworkException e) {
			InvalidApplicationState.notImplemented();
		}
		return changes;
	}

	private String getLocalContentHash(JakeObject jo)
			throws NoSuchFileException, FileNotFoundException,
			NotAReadableFileException {
		if (jo.getName().startsWith("note:")) {
			return fss.calculateHash(db.getJakeObjectDao().getNoteObjectByName(
					jo.getName()).getContent().getBytes());
		} else {
			try {
				return fss.calculateHashOverFile(jo.getName());
			} catch (FileNotFoundException e) {
				throw e;
			} catch (InvalidFilenameException e) {
				InvalidApplicationState.die(e);
			} catch (NotAReadableFileException e) {
				throw e;
			}
		}
		return null;
	}

	private boolean hasLocalModification(JakeObject jo)
			throws NoSuchFileException, FileNotFoundException,
			NotAReadableFileException, NoSuchLogEntryException 
	{
		log.debug("hasLocalModification("+jo.getName()+") start");
		String hash = getLocalContentHash(jo);
		log.debug("hash calculated.");
		log.debug("local: " + hash.substring(0,5));
		LogEntry le = db.getLogEntryDao().getLastPulledFor(jo);
		log.debug("log:   " + le.getHash().substring(0,5));
		boolean equal = le.getHash().equals(hash);
		log.debug("hasLocalModification done.");
		return !equal;
	}
	
	public int calculateJakeObjectSyncStatus(JakeObject jo) {
		int state = 0;
		log.debug("getJakeObjectSyncStatus(" + jo.getName() + ")");
		if (jo.getName().startsWith("note:")) {
			try {
				db.getJakeObjectDao().getNoteObjectByName(jo.getName());
				log.debug("note; local, in project");
				state |= SYNC_EXISTS_LOCALLY | SYNC_IS_IN_PROJECT;
			} catch (NoSuchFileException e) {
			}
		} else {
			log.debug("file;");
			try {
				if (fss.fileExists(jo.getName()))
					state |= SYNC_EXISTS_LOCALLY;
				log.debug("local");
			} catch (InvalidFilenameException e) {
				InvalidApplicationState.shouldNotHappen(e);
			} catch (IOException e) {
				/* interpreting as we don't have it accessible */
			}
			try {
				db.getJakeObjectDao().getFileObjectByName(jo.getName());
				state |= SYNC_IS_IN_PROJECT;
				log.debug("in project");
			} catch (NoSuchFileException e) {
			}
		}
		
		if((state & SYNC_IS_IN_PROJECT) != 0){
			LogEntry mostRecent;
			try {
				mostRecent = db.getLogEntryDao().getMostRecentFor(jo);
				log.debug("has logentries");
				state |= SYNC_HAS_LOGENTRIES;
	
				if (mostRecent.getAction() != LogAction.DELETE) {
					state |= SYNC_EXISTS_REMOTELY;
					log.debug("exists remotely");
				}
				LogEntry lastPulled;
				try {
					lastPulled = db.getLogEntryDao().getLastPulledFor(jo);
					if ( 
						lastPulled.getUserId().equals(mostRecent.getUserId()) && 
						lastPulled.getTimestamp().getTime() == mostRecent.getTimestamp().getTime() &&
						lastPulled.getJakeObjectName().equals(mostRecent.getJakeObjectName())
					){
						log.debug("local is latest");
						state |= SYNC_LOCAL_IS_LATEST;
					}
					else if (mostRecent.getTimestamp().getTime() > lastPulled.getTimestamp().getTime()){
						log.debug("remote is newer");
						state |= SYNC_REMOTE_IS_NEWER;
					}else{
						InvalidApplicationState.die("Weird: not latest, but nothing newer? \n" + 
							lastPulled.getUserId() + " <=> " + mostRecent.getUserId() + " -- " +  
							lastPulled.getTimestamp().getTime() + " <=> " + mostRecent.getTimestamp().getTime() + " -- " + 
							lastPulled.getJakeObjectName() + " <=> " + mostRecent.getJakeObjectName() + " -- " +
							""
						);
						
					}
				} catch (NoSuchLogEntryException e) {
					log.debug("no last pulled");
				}
				try {
					if ((state & SYNC_EXISTS_LOCALLY)!=0 && hasLocalModification(jo)) {
						log.debug("locally changed");
						state |= SYNC_LOCALLY_CHANGED;
					}
				} catch (NoSuchFileException e) {
					InvalidApplicationState.shouldNotHappen(e);
				} catch (FileNotFoundException e) {
					InvalidApplicationState.shouldNotHappen(e);
				} catch (NotAReadableFileException e) {
					InvalidApplicationState.shouldNotHappen(e);
				}
			} catch (NoSuchLogEntryException e) {
			}
			
		}
		if(conflictCallback != null && ics.isLoggedIn()){
			if((state & SYNC_IN_CONFLICT) == SYNC_IN_CONFLICT){
				if(!lastConflicts.contains(jo.getName())){
					log.debug("new conflict on " + jo.getName());
					lastConflicts.add(jo.getName());
					conflictOccured(jo);
				}
				else
					log.debug("old conflict on " + jo.getName());
			}else{
				if(lastConflicts.contains(jo.getName())){
					log.debug("The conflict on " + jo.getName() + " is resolved");
					lastConflicts.remove(jo.getName());
				}
			}
		}
		log.debug("getJakeObjectSyncStatus(" + jo.getName() + ") done");
		return state;
	}
	
	public void refreshFileObject(File f){
		log.debug("Refreshing file: " + f.getAbsolutePath());
		for(String relpath : filesStatus.keySet()){
			if(fss.joinPath(fss.getRootPath(), relpath).equals(f.getAbsolutePath())){
				FileObject jo = new FileObject(relpath);
				filesStatus.put(relpath, calculateJakeObjectSyncStatus(jo));
				log.debug("file found and recalculated.");
				return;
			}
		}
		InvalidApplicationState.die("File to refresh not found?");
	}
	
	public void refreshFileObject(JakeObject jo) {
    	log.debug("refreshFileObject start");
		filesStatus.put(jo.getName(), calculateJakeObjectSyncStatus(jo));
    	log.debug("refreshFileObject end");
	}
	
	public void syncWithProjectMembers(){
		if(ics.isLoggedIn()){
			try {
				for (ProjectMember pm : db.getProjectMemberDao().getAll()) {
					if(pm.getUserId().equals(ics.getUserid()))
						continue;
					try {
						syncLogAndGetChanges(pm.getUserId());
					} catch (OtherUserOfflineException e) {
						/* thats ok. */
					} catch (NotAProjectMemberException e) {
						InvalidApplicationState.shouldNotHappen(e);
					}
				}
			} catch (NotLoggedInException e1) {
				InvalidApplicationState.shouldNotHappen();
			}
		}else{
			log.warn("Not logged in.");
		}
		refreshFileObjects();
	}
	
	public synchronized void refreshFileObjects() {
		log.debug("calling refreshFileObjects() ");
		
		filesDB = getFileObjectsFromDB();
		filesFSS = getFileObjectsByRelPath("/");
		
		if (filesStatus == null)
			filesStatus = new HashMap<String, Integer>();
		
		for(FileObject file : filesFSS) {
			log.debug("from fs: '" + file.getName() + "'");
			filesStatus.put(file.getName(), SYNC_EXISTS_LOCALLY);
		}
		int status;
		for(FileObject file : filesDB) {
			log.debug("from db: '" + file.getName() + "'");
			status = calculateJakeObjectSyncStatus(file) | SYNC_IS_IN_PROJECT;
			if(filesFSS.contains(file)){
				status = status | SYNC_EXISTS_LOCALLY;
			}
			filesStatus.put(file.getName(), status);
		}

    }

    private List<FileObject> getFileObjectsFromDB() {
    	log.debug("getFileObjectsFromDB start");
        if (filesDB == null)
            filesDB = db.getJakeObjectDao().getAllFileObjects();
        List<FileObject> results = new ArrayList<FileObject>();
        results.addAll(filesDB);
    	log.debug("getFileObjectsFromDB done");
        return results;
    }
	

    public List<FileObject> getFileObjectsByRelPath(String relPath){
    	log.debug("getFileObjectsByRelPath start");
		List<FileObject> results = new ArrayList<FileObject>();

		String[] files = new String[0];
		try {
			files = fss.listFolder(relPath);
		} catch (InvalidFilenameException e) {
			InvalidApplicationState.die("should not happen", e);
		} catch (IOException e) {
			InvalidApplicationState.die("should not happen", e);
		}
    	log.debug("getFileObjectsByRelPath files listed");
		String fileRelPath;
		for (String file : files) {
			fileRelPath = relPath + "/" + file;
			
			while(fileRelPath.startsWith("/")){
				fileRelPath = fileRelPath.substring(1);
			}
			
			try {
				if (fss.folderExists(fileRelPath)) {
					results.addAll(getFileObjectsByRelPath(fileRelPath));
				}
				if (fss.fileExists(fileRelPath))
					results.add(new FileObject(fileRelPath));
			} catch (InvalidFilenameException e) {
				/* we simply ignore invalid filenames */
				log.info("We ignored the invalid-named file: " + fileRelPath);
				continue;
			} catch (IOException e) {
				InvalidApplicationState.die(StateType.SHOULD_NOT_HAPPEN, e);
			}
		}
    	log.debug("getFileObjectsByRelPath done");
		return results;
	}

    public List<JakeObject> getFileObjects() {
    	log.debug("getFileObjects start");
        if(filesDB == null || filesFSS == null || filesStatus == null)
        	refreshFileObjects();
        
        List<JakeObject> results = new ArrayList<JakeObject>();
        results.addAll(filesFSS);
        
        for(FileObject jo : filesDB) {
            List<Tag> tags = db.getJakeObjectDao().getTagsForObject(jo);
            
            if(results.contains(new FileObject(jo.getName())))
                results.remove(new FileObject(jo.getName()));
            
            for(Tag tag : tags)
            {
                jo.addTag(tag);
            }
            
            results.add(jo);
        }
    	log.debug("getFileObjects done");
        return results;
    }
	
	public void launchExternalFile(File f) throws InvalidFilenameException, LaunchException{
		try {
			FileLauncher launcher = new FileLauncher();
			launcher.launchFile(f);
		} catch (NoSuchAlgorithmException e) {
			InvalidApplicationState.shouldNotHappen(e);
		}
	}
	
	public void addModificationListener(IModificationListener ob){
		fss.addModificationListener(ob);
	}
	public void removeModificationListener(IModificationListener ob){
		fss.removeModificationListener(ob);
	}


    /*** Stuff required for filesPanel *******/

    public synchronized boolean importExternalFileIntoProject(String absolutePath, String destinationFolderAbsolutePath) {
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
            String relPath = destinationFile.getAbsolutePath().
                    replaceAll(currentProject.getRootPath().getAbsolutePath(), "");
            if(File.separatorChar == '\\')
            {
                relPath = relPath.replace('\\','/');
            }
            importLocalFileIntoProject(relPath);
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }
        return true;
    }

    public synchronized boolean importLocalFileIntoProject(String relPath) {
        FileObject fileObject = new FileObject(relPath);

        db.getJakeObjectDao().save(fileObject);
        
        /* We create the Domain-Objects, but no log entries, which would be a 
         * push. If we did that, we would use the push function. 
         * Most likely, if users always want to push after a import, we would 
         * call push after import, from the gui. Don't create logentries here 
         * manually. 
         */
        
        filesDB.add(fileObject);

        filesStatus.put(relPath, calculateJakeObjectSyncStatus(fileObject) | SYNC_EXISTS_LOCALLY | SYNC_IS_IN_PROJECT );

        return true;
    }

    public Integer getJakeObjectSyncStatus(JakeObject jakeObject) {
        int status = 0;
        if(filesStatus.containsKey(jakeObject.getName()))
        {
            return filesStatus.get(jakeObject.getName());
        }
        else
        {
            log.debug("getJakeObjectSyncStatus of "+jakeObject.getName());
            log.debug("is NOT cached");
            status = calculateJakeObjectSyncStatus(jakeObject);
            filesStatus.put(jakeObject.getName(), status);
        }
        return status;
    }


}
