package com.jakeapp.core.integration;

import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.Project;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

/**
 * Integration class to integrate Multi-Connect XMPPICService with core.
 */
public final class XMPPICServiceHolder {

    public static final String namespace = "http://jakeapp.com/protocols/xmpp/versions/1";
    public static final String defaultGroupId = "Jake";


    // Key: UserId String
    private static final Map<String, ServiceCredentials> user_cred = new HashMap<String, ServiceCredentials>();

    // key: UserId String
    private static final Map<String, XmppICService> user_service = new HashMap<String, XmppICService>();



    // key: Project UUID
    private static final Map<UUID, String> project_user = new HashMap<UUID, String>();




    private ServiceCredentials credentials;

    private String groupName;

    public XMPPICServiceHolder() {

    }


    public XMPPICServiceHolder getXmppService(final ServiceCredentials credentials) {
//        this.credentials = credentials;







        if (credentials.getUserId() != null) {
            if (!user_cred.containsKey(credentials.getUserId())) {
                user_cred.put(credentials.getUserId(), credentials);
            } else {
                if(!user_service.containsKey(credentials.getUserId()))
                {
                    user_cred.put(credentials.getUserId(), credentials);
                }
                else
                {
                    if(user_cred.get(credentials.getUserId()).equals(credentials))
                    {
                        // do nothing
                    }
                    else
                    {
                        // TODO throw new Exception; Modifying an already logged in user!
                    }
                }
            }
         // create xmpp instance
            return this;
        }
        else
        {
            // todo throw new Exception;
            return null;
        }
    }


    public XMPPICServiceHolder getXmppService(final Project project)
    {


        return null;
    }


    public boolean login() {
        if (this.credentials == null)
            return false;


        if (!user_cred.containsKey(credentials.getUserId()))
            return false;


        XmppICService service = user_service.get(credentials.getUserId());

        if(service == null)
            service = new XmppICService(namespace, this.groupName);





//            service.getStatusService().login(
//
//                    new XmppUserId(credentials.getUser()),
//                    credentials.getPlainTextPassword());


        return false;

    }

}
