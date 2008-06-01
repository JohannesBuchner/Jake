package com.doublesignal.sepm.jake.core.services;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.doublesignal.sepm.jake.core.dao.IJakeObjectDao;
import com.doublesignal.sepm.jake.core.dao.ILogEntryDao;
import com.doublesignal.sepm.jake.core.dao.IProjectMemberDao;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeMessage;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.Project;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataNotValidException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataRequiredException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginUseridNotValidException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFolderException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchJakeObjectException;
import com.doublesignal.sepm.jake.fss.IFSService;
import com.doublesignal.sepm.jake.fss.InvalidFilenameException;
import com.doublesignal.sepm.jake.fss.NotAFileException;
import com.doublesignal.sepm.jake.fss.NotADirectoryException;
import com.doublesignal.sepm.jake.ics.IICService;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NoSuchUseridException;
import com.doublesignal.sepm.jake.sync.ISyncService;


import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 
 * @author johannes, domdorn, peter
 * 
 */
public class JakeGuiAccess implements IJakeGuiAccess {
	/*
	 * TODO: do we want this here or somewhere else? IMO it's OK here.
	 */
	IICService ics = null;
	ISyncService sync = null;
	IFSService fss = null;

	IProjectMemberDao projectMemberDAO = null;
	IJakeObjectDao jakeObjectDAO = null;
	ILogEntryDao logEntryDAO = null;

	private static Logger log = Logger.getLogger(JakeGuiAccess.class);

	public JakeGuiAccess() {
		log.info("Setup the JakeGuiAccess Object");
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource(
				"beans.xml"));
		ics = (IICService) factory.getBean("ICService");
		sync = (ISyncService) factory.getBean("SyncService");
		fss = (IFSService) factory.getBean("FSService");

		projectMemberDAO = (IProjectMemberDao) factory
				.getBean("ProjectMemberDAO");
		jakeObjectDAO = (IJakeObjectDao) factory.getBean("JakeObjectDAO");
		logEntryDAO = (ILogEntryDao) factory.getBean("LogEntryDAO");
	}

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

		jakeObjectDAO.save(note);
		return null;
	}


	public Project createProject(String projectName,
                                 String projectPath) throws
            InvalidFilenameException, IOException, NotADirectoryException {
        // todo advice fss to create new project
        // todo advice ics to create new project
        // todo advice database to create new project
        log.info("Creating a new JakeProject with name '"+projectName+"' and Path '"+projectPath+"' ");
        Project newProject = new Project(new File(projectPath),projectName);
        fss.setRootPath(newProject.getRootPath().toString());
        return newProject;
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

	public String getConfigOption(String configKey)
			throws NoSuchConfigOptionException {
		// TODO Auto-generated method stub
		throw new NoSuchConfigOptionException(configKey);
	}

	public Map<String, String> getConfigOptions() {
		// TODO Auto-generated method stub
		return null;
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

	public List<JakeObject> getJakeObjectsByPath(String relPath)
			throws NoSuchJakeObjectException {

		List<JakeObject> results = new ArrayList<JakeObject>();

/*
		try {
			results
					.add(new FileObject("SEPM_SS08_Artefaktenbeschreibung.pdf")
							.addTag(new Tag("someTag")).addTag(
									new Tag("someothertag")));
		} catch (InvalidTagNameException e) {
			e.printStackTrace();
		}
		results.add(new FileObject("SEPM_VO_Block_1.pdf"));
		results.add(new FileObject("SEPM_SS08_Artefaktenliste"));
		results.add(new FileObject("ToDos.txt"));
*/

		System.out.println("fss.getRootPath() = " + fss.getRootPath());

        if(fss.getRootPath() != null && !fss.getRootPath().equals(""))
        {
            try {
                String[] files = fss.listFolder(relPath);

                File tmp;
                for(String file : files)
                {
                    tmp = new File(fss.getFullpath(relPath+file));
                    if(tmp.isDirectory())
                        continue;

                    results.add(new FileObject(relPath+file));
                    System.out.println("file = " + file);
                }

            } catch (InvalidFilenameException e) {
                System.out.println("cought invalidFilenameException");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("cought IOException");
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("condition not met.");
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
		// return *something*.
		ArrayList<NoteObject> list = new ArrayList<NoteObject>();
		list.add(NoteObject.createNoteObject("peter", "Test-Content"));
		list.add(NoteObject.createNoteObject("dom", "Noch ein Test"));
		return list;
	}

	public List<JakeObject> getOutOfSyncObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	public Project getProject() {
		// TODO Auto-generated method stub
		return null;
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

	public void setConfigOption(String configKey, String configValue) {
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

	public String getJakeObjectSyncStatus(JakeObject jakeObject) {

		return "Offline";
	}
}