package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.callbacks.RegistrationStatus;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CoreAccessMock implements ICoreAccess {
    private static final Logger log = Logger.getLogger(CoreAccessMock.class);

    /**
     * Core Access Mock initialisation code
     */
    public CoreAccessMock() {
        connectionStatus = new ArrayList<ConnectionStatus>();
        registrationStatus = new ArrayList<RegistrationStatus>();
    }


    @Override
    public List<Project> getMyProjects() {
        List<Project> projects = new ArrayList<Project>();

        Project pr1 = new Project("ASE", null, null, new File("/Users/studpete/Desktop"));
        pr1.setStarted(true);
        projects.add(pr1);

        Project pr2 = new Project("SEPM", null, null, new File("/Users/studpete/"));
        projects.add(pr2);

        Project pr3 = new Project("Shared Music", null, null, new File(""));
        projects.add(pr3);

        return projects;
    }

    @Override
    public List<Project> getInvitedProjects() {
        List<Project> projects = new ArrayList<Project>();

        Project pr1 = new Project("DEMO INVITATION", null, null, new File(""));
        projects.add(pr1);


        Project pr2 = new Project("Not that secret Docs", null, null, new File(""));
        projects.add(pr2);

        return projects;
    }


    public void signIn(String user, String pass) {
        log.info("Signs in: " + user + "pass: " + pass);

        Runnable runner = new Runnable() {
            public void run() {
                callbackConnectionStatus(ConnectionStatus.ConnectionStati.SigningIn, "");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
    }

    public void registerRegistrationStatusCallback(RegistrationStatus cb) {
        log.info("Registers registration status callback: " + cb);
    }

    public void deRegisterRegistrationStatusCallback(RegistrationStatus cb) {
        log.info("Deregisters registration status callback: " + cb);

    }

    private void callbackRegistrationStatus(RegistrationStatus.RegisterStati state, String str) {
        for (RegistrationStatus callback : registrationStatus) {
            callback.setRegistrationStatus(state, str);
        }
    }


    private List<ConnectionStatus> connectionStatus;
    private List<RegistrationStatus> registrationStatus;
}
