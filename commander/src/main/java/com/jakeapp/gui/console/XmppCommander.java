package com.jakeapp.gui.console;

import java.io.IOException;

import com.jakeapp.gui.console.commandline.LazyCommand;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;


/**
 * Test client accepting cli input
 */
public class XmppCommander extends Commander {

    public XmppCommander(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        new XmppCommander(args);
    }

    @Override
    protected void onShutdown() {
        //
    }

    @Override
    protected void onStartup() {
        //
    }

    private IMessageReceiveListener receiver = new IMessageReceiveListener() {
        @Override
        public void receivedMessage(UserId from_userid, String content) {
            System.out.println("Got message from User: " + from_userid);
            System.out.println("content = " + content);
        }
    };


    private XmppICService ics = null;

    class Login extends LazyCommand {

        public Login() {
            super("login", "login <xmppid> <passwd> <groupname>",
                    "xmppid = user@host/resource where resource is 'Jake' or the "
                            + "projectid, groupname = projectname (group in roster)");
        }

        @Override
        public boolean handleArguments(String[] args) {
            if (args.length != 4)
                return false;
            XmppUserId id = new XmppUserId(args[1]);
            XmppCommander.this.ics = new XmppICService(JakeCommander.namespace, args[3]);
            try {
                XmppCommander.this.ics.getStatusService().login(id, args[2], null, 0);
                XmppCommander.this.ics.getMsgService().registerReceiveMessageListener(

                        new IMessageReceiveListener() {
                            @Override
                            public void receivedMessage(UserId from_userid, String content) {

                                System.out.println("Got message from User: " + from_userid);
                                System.out.println("content = " + content);
                            }
                        }
                );
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (NetworkException e) {
                e.printStackTrace();
            }
            return true;
        }

    }

    class SendMessage extends LazyCommand {
        public SendMessage() {
            super("sendMessage", "sendMessage <xmppuserid>  <message>", "test");
        }

        @Override
        public boolean handleArguments(String[] args) {
            if (args.length < 3) {
                System.out.println("Insufficient arguments");
                return false;
            }

            if (XmppCommander.this.ics == null) {
                System.out.println("ICS must be set");
                return false;
            }

            String username = args[1];

            StringBuffer sb = new StringBuffer(1000);
            for (int i = 2, n = args.length; i < n; i++) {
                sb.append(args[i]).append(" ");
            }
            String message = sb.toString();


            System.out.println("username = " + username);
            System.out.println("message = " + message);

            UserId recipient = new XmppUserId(username);

            try {
                XmppCommander.this.ics.getMsgService().sendMessage(recipient, message);
            } catch (NetworkException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (OtherUserOfflineException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return true;
        }
    }


    class Logout extends LazyCommand {

        public Logout() {
            super("logout", "logout", "groupname = projectname (group in roster)");
        }

        @Override
        public boolean handleArguments(String[] args) {
            try {
                XmppCommander.this.ics.getStatusService().logout();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (NetworkException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    class ListUsers extends LazyCommand {

        public ListUsers() {
            super("listUsers", "listUsers", "bly");
        }

        @Override
        public boolean handleArguments(String[] args) {

            if (XmppCommander.this.ics == null) {
                System.out.println("ICS not set!");
                return true;
            }


            try {
                System.out.println("getting users");
                Iterable<UserId> users = XmppCommander.this.ics.getUsersService().getUsers();


                for (UserId u : users) {
                    System.out.println("u = " + u);
                    System.out.println(u + " - capable? "
                            + XmppCommander.this.ics.getUsersService().isCapable(u));
                }
            } catch (NetworkException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

}
