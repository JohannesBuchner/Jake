package com.doublesignal.sepm.jake.ics;


/**
 * 
 * The task of the InterClient Communication Service (ICService) is to provide 
 * a communication layer based on the network for communication between users 
 * based on messages and objects.
 * 
 * @author johannes
 **/

public interface IICService {
	
	public Boolean login(String userid, String pw);
	
	public Boolean logout();
	
	public Boolean isConnected();
	
	/* TODO: This pretty much have to be strings, right? 
	 * or should we try some fancy serialization stuff? */
	public Boolean sendObject(String to_userid, String objectidentifier, String content);
	
	public void registerReceiveObjectCallback(IObjectReceiveListener rl);
	
	public Boolean sendMessage(String to_userid, String content);
	
	public void registerReceiveObjectMessage(IMessageReceiveListener rl);
	
}
