package com.doublesignal.sepm.jake.core.services;

import java.util.List;
import java.util.Map;
import java.util.Observer;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeMessage;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.Project;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.domain.exceptions.NoSuchConfigOptionException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataNotValidException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataRequiredException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFolderException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchJakeObjectException;
import com.doublesignal.sepm.jake.fss.IFSService;
import com.doublesignal.sepm.jake.ics.IICService;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.sync.ISyncService;

/**
 * 
 * @author johannes
 *
 */
public class JakeGuiAccess implements IJakeGuiAccess{
	/* TODO: do we want this here or somewhere else? 
	 * IMO it's OK here. */
	IICService ics = null; 
	ISyncService sync = null; 
	IFSService fss = null;
	
	public JakeGuiAccess(){
		BeanFactory factory = new XmlBeanFactory(new FileSystemResource("beans.xml"));
		ics  = (IICService)   factory.getBean("ICService");
		sync = (ISyncService) factory.getBean("SyncService");
		fss  = (IFSService)   factory.getBean("FSService");
	}
	
	public void login(String user, String pw) throws LoginDataRequiredException, 
		LoginDataNotValidException, NetworkException 
	{
		try{
			if(user==null)
				user = getConfigOption("userid");
			if(pw == null)
				pw = getConfigOption("password");
		}catch(NoSuchConfigOptionException e){
			throw new LoginDataRequiredException();
		}
		if(!ics.login(user, pw)){
			throw new LoginDataNotValidException();
		}
	}

	public void logout() throws NetworkException {
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
		// TODO Auto-generated method stub
		return null;
	}

	public Project createProject(String projectName, String projectId,
			String projectPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<JakeObject> getChangedObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getConfigOption(String configKey)
			throws NoSuchConfigOptionException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
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

	public void setConfigOption(String configKey, String configValue)
			throws NoSuchConfigOptionException {
		// TODO Auto-generated method stub
		
	}

}