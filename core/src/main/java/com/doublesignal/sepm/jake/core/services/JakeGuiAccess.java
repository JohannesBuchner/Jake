package com.doublesignal.sepm.jake.core.services;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;

import com.doublesignal.sepm.jake.core.dao.*;
import com.doublesignal.sepm.jake.core.dao.exceptions.*;
import com.doublesignal.sepm.jake.core.domain.*;
import com.doublesignal.sepm.jake.core.services.exceptions.*;
import com.doublesignal.sepm.jake.fss.*;
import com.doublesignal.sepm.jake.ics.*;
import com.doublesignal.sepm.jake.ics.exceptions.*;
import com.doublesignal.sepm.jake.sync.*;

/**
 * 
 * @author johannes, domdorn, peter, philipp
 * 
 */
public class JakeGuiAccess implements IJakeGuiAccess {
	/*
	 * TODO: do we want this here or somewhere else? IMO it's OK here.
	 */
	private IICService ics = null;
	private ISyncService sync = null;
	private IFSService fss = null;
	private IJakeDatabase db = null;
	
	private Project currentProject;
	private static Logger log = Logger.getLogger(JakeGuiAccess.class);

    private List<FileObject> filesFSS;
    private List<FileObject> filesDB;

    public void login(String user, String pw)
			throws LoginDataRequiredException, LoginDataNotValidException,
			NetworkException, LoginUseridNotValidException {
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

	public String getLoginUser() throws NotLoggedInException {
		return ics.getUserid();
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

		NoteObject note = NoteObject.createNoteObject("0", content);

		db.getJakeObjectDao().save(note);
		return null;
	}


    public void editNote(NoteObject note) {
		log.info("edit Note: " + note);
	}

	public void removeNote(NoteObject note) {
		log.info("remove Note:" + note);
	}



	public List<JakeObject> getChangedObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the configuration option for a <code>configKey</code>
	 * 
	 * @param configKey
	 *            the name of the configuration option
	 * @return the associated value to the key
	 * @throws NoSuchConfigOptionException
	 *             Raised if no option exists with the given
	 *             <code>configKey</code>.
	 */
	public String getConfigOption(String configKey)
			throws NoSuchConfigOptionException {
		return db.getConfigurationDao().getConfigurationValue(configKey);
	}
	/**
	 * Set a configuration option.
	 * 
	 * @param configKey
	 *            the name of the option
	 * @param configValue
	 *            the value of the option
	 */
	public void setConfigOption(String configKey, String configValue) {
		db.getConfigurationDao().setConfigurationValue(configKey, configValue);
	}


	public List<JakeObject> getJakeObjectsByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<JakeObject> getJakeObjectsByNameAndTag(String name,
			List<Tag> tags) {
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
					results
							.addAll(getFileObjectsByRelPath(relPath + file
									+ "/"));
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

	public List<JakeObject> getFileObjectsByPath(String relPath)
			throws NoSuchJakeObjectException {

		List<JakeObject> results = new ArrayList<JakeObject>();

		if (fss.getRootPath() != null && !fss.getRootPath().equals("")) {
			try {
				results.addAll(getFileObjectsByRelPath(relPath));
			} catch (InvalidFilenameException e) {
				log
						.debug("getFileObjectsByPath: cought invalidFilenameException");
				e.printStackTrace();
			} catch (IOException e) {
				log.debug("getFileObjectsByPath: cought IOException");
				e.printStackTrace();
			}
		} else {
			if (fss.getRootPath() == null)
				log
						.debug("getFileObjectsByPath: fss.getRootPath is null, cannot read any files");
			else if (fss.getRootPath().equals(""))
				log
						.debug("getFileObjectsByPath: fss.getRootPath is not null, but empty!");
		}


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

	public List<FileObject> importFolder(String absolutePath)
			throws NoSuchFolderException {
		// TODO Auto-generated method stub
		return null;
	}

	public void logSync() throws NetworkException {
		// TODO Auto-generated method stub

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
		log.debug("Adding tag '" + tag + "' to JakeObject '"
				+ jakeObject.getName() + "' ");
		jakeObject.addTag(tag);
		// todo access jakeObjectDao
		return jakeObject;
	}

	public JakeObject removeTag(JakeObject jakeObject, Tag tag) {
		log.debug("removing tag '" + tag + "' from JakeObject '"
				+ jakeObject.getName() + "' ");
		jakeObject.removeTag(tag);
		// todo access jakeObjectDao
		return jakeObject;
	}



    public void launchFile(String relpath) throws InvalidFilenameException, LaunchException, IOException, NoProjectLoadedException {
		fss.launchFile(relpath);
    }
    
    private JakeGuiAccess(String rootPath) throws NotADirectoryException, 
    	InvalidDatabaseException, InvalidRootPathException 
	{
		log.info("Setup the JakeGuiAccess Object");
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource(
				"beans.xml"));
		
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
		
	}
	
	private static void copyFile(File source, File target) throws IOException{
		BufferedReader fis = new BufferedReader(new FileReader(source));
		BufferedWriter fos = new BufferedWriter(new FileWriter(target));
		boolean hasCreates = false;
		int lines = 0;
		while(true){
			String l = fis.readLine();
			if(l == null)
				break;
			System.out.println("Line "+(lines+1) + ": " + l);
			lines++;
			if(l.contains("CREATE TABLE"))
				hasCreates = true;
			fos.write(l);
			fos.newLine();
		}
		log.debug("copied " + source.getAbsolutePath() + " -> " + 
				target.getAbsoluteFile() + "; " + lines + " lines");
		
		if (fis != null) 
			fis.close();
		if (fos != null) 
			fos.close();
	}
	
    public static void createSchema(String rootPath) throws InvalidDatabaseException {
		File scriptfile = new File(rootPath + ".script");
		File propertiesfile = new File(rootPath + ".properties");
		log.debug("create schema of " + scriptfile.getAbsolutePath());
		if(!scriptfile.exists())
			try {
				scriptfile.createNewFile();
			} catch (IOException e1) {
				throw new InvalidDatabaseException();
			}
		if(!(scriptfile.exists() && scriptfile.isFile() && scriptfile.canWrite())){
			throw new InvalidDatabaseException();
		}
		log.debug("copying over ...");
		try {
			ClassPathResource scriptres = new ClassPathResource("skeleton.script");
			log.debug("ClassPathResource: " + scriptres.getFile().getAbsolutePath());
			copyFile(scriptres.getFile(), scriptfile);
			ClassPathResource propertiesres = new ClassPathResource("skeleton.properties");
			log.debug("ClassPathResource: " + propertiesres.getFile().getAbsolutePath());
			copyFile(propertiesres.getFile(), propertiesfile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidDatabaseException();
		} catch (RuntimeException e){
			e.printStackTrace();
			throw new InvalidDatabaseException();
		}
		System.err.println("copying done ...");
		log.debug("copying done ...");
		
	}
	
	public static boolean hasRootpathAProject(String rootPath) 
	{
		File d = new File(rootPath);
		rootPath = d.getAbsolutePath();
		File jakeFile = new File(d.getParentFile(), d.getName() + ".script");
		return jakeFile.exists();
	}

	public static JakeGuiAccess createNewProjectByRootpath(String rootPath, 
			String projectname) 
		throws ExistingProjectException, InvalidDatabaseException, 
			NotADirectoryException, InvalidRootPathException 
	{
		rootPath = new File(rootPath).getAbsolutePath();
		log.debug("createNewProjectByRootpath: " + rootPath);
		if(hasRootpathAProject(rootPath))
			throw new ExistingProjectException();
		
		log.debug("createSchema");
		createSchema(rootPath);
		log.debug("JakeGuiAccess");
		JakeGuiAccess jga;
		jga = new JakeGuiAccess(rootPath);
		try{
			jga.initializeDatabase(projectname);
		}catch(BadSqlGrammarException e){
			log.error("Database invalid - Schema was not accepted by hsqldb");
			throw new InvalidDatabaseException();
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new InvalidDatabaseException();
		}
		
		log.debug("project created and loaded.");

		jga.currentProject = new Project(new File(rootPath), projectname);
		
		return jga;
	}
	
	private void initializeDatabase(String projectname) {
		log.debug("setting config option rootpath ... ");
		db.getConfigurationDao().setConfigurationValue("rootpath", fss.getRootPath());
		log.debug("setting config option projectname ... ");
		db.getConfigurationDao().setConfigurationValue("projectname", projectname);
		
		db.getConfigurationDao().setConfigurationValue("autoPush",
				String.valueOf(1));
		db.getConfigurationDao().setConfigurationValue("autoPull",
				String.valueOf(1));
		db.getConfigurationDao().setConfigurationValue("logsyncInterval",
				String.valueOf(5));
		/* don't know. maybe scan in files? */
		
	}

	public static JakeGuiAccess openProjectByRootpath(String rootPath) throws 
		NonExistantDatabaseException, InvalidDatabaseException, InvalidRootPathException 
	{
		File f = new File(rootPath);
		rootPath = f.getAbsolutePath();
		log.debug("openProjectByRootpath: "  + rootPath);
		if(!hasRootpathAProject(rootPath))
			throw new NonExistantDatabaseException();
		
		JakeGuiAccess jga;
		String projectName = null;
		
		
		try{
			jga = new JakeGuiAccess(rootPath);
			try {
				projectName = jga.db.getConfigurationDao().getConfigurationValue("projectname");
			} catch (NoSuchConfigOptionException e) {
				log.error("projectname config entry is missing!");
				throw new InvalidDatabaseException();
			}
			log.debug("Project is named " + projectName + ".");
			
			jga.db.getConfigurationDao().setConfigurationValue("rootpath", f.getAbsolutePath());
			
			jga.currentProject = new Project(f, projectName);
			
			log.debug("Project opened.");
		}catch(BadSqlGrammarException e){
			System.out.println(e);
			throw new InvalidDatabaseException();
		}catch (DataAccessException e) {
			System.out.println(e);
			throw new InvalidDatabaseException();
		} catch (NotADirectoryException e) {
			System.out.println(e);
			throw new InvalidRootPathException();
		}
		
		/* if no exceptions were thrown until here, the database is (assumed) 
		 * to be valid. */
		
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
    }

    	public String getFileObjectSyncStatus(JakeObject jakeObject)
    {
        log.debug("calling getFileObjectSyncStatus on JakeObject "+ jakeObject.getName());
        boolean fileInDB = false;
        boolean fileOnFS = false;
        boolean fileInLog = false;

        try {
            LogEntry currentLogEntry = db.getLogEntryDao().getMostRecentFor(jakeObject);
            fileInLog = true;
        } catch (NoSuchLogEntryException e) {
            fileInLog = false;
        }

        if (filesDB.contains(jakeObject))
            fileInDB = true;

        if(filesFSS.contains(jakeObject))
            fileOnFS = true;


        if(fileOnFS && !fileInDB)
        {
            return "File not in Project";
        }
        if(fileInDB && fileInLog && !fileOnFS)
        {
            return "File remote; not synced";
        }


        return "default, shouldn't happen";


    }


    private List<FileObject> getFileObjectsFromDB()
    {
        if(filesDB == null)
            filesDB = db.getJakeObjectDao().getAllFileObjects();
        List<FileObject> results = new ArrayList<FileObject>();
        results.addAll(filesDB);
        return results;
    }

}
