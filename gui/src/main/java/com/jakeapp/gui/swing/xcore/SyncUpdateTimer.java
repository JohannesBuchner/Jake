package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author studpete
 */
public class SyncUpdateTimer implements DataChangedCallback {
	private static final Logger log = Logger.getLogger(SyncUpdateTimer.class);
	private final SyncUpdateTimer instance;
	private Timer syncTimer;
	private static final int JakeSyncProjectTime = 60000;
	private static final int JakeInitialSyncProjectTime = 8000;
	private static final int TimerSyncRate = 2000;

	private Map<Project, Integer> projectsUpdateTime = new HashMap<Project, Integer>();

	public SyncUpdateTimer() {
		instance = this;

		EventCore.get().addDataChangedCallbackListener(this);

		syncTimer = new Timer(TimerSyncRate, new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {

				for (Map.Entry<Project, Integer> entry : projectsUpdateTime.entrySet()) {
					Integer time = entry.getValue();
					Project pr = entry.getKey();
					if (time <= 0 && pr.isStarted()) {
						try {
							JakeMainApp.getCore().syncProject(pr, null);
						} catch (Exception ex) {
							log.warn("Error auto-syncing project", ex);
						}
						entry.setValue(JakeSyncProjectTime);
					} else {
						entry.setValue(time - TimerSyncRate);
					}
				}

				syncTimer.start();
			}
		});

		syncTimer.start();
	}

	public void registerProject(Project p) {
		if (p != null && !projectsUpdateTime.containsKey(p)) {
			projectsUpdateTime.put(p, JakeInitialSyncProjectTime);
		}
	}

	public void deRegisterProject(Project p) {
		if (projectsUpdateTime.containsKey(p)) {
			projectsUpdateTime.remove(p);
		}
	}

	@Override public void dataChanged(EnumSet<DataReason> dataReason, Project p) {
		if (dataReason.contains(DataReason.Projects)) {

			List<Project> registeredProjects = new ArrayList<Project>();
			for (Map.Entry<Project, Integer> entry : projectsUpdateTime.entrySet()) {
				registeredProjects.add(entry.getKey());
			}

			// register new projects
			List<Project> myProjects = ObjectCache.get().getMyProjects();
			for (Project project : myProjects) {
				registerProject(project);
				registeredProjects.remove(project);
			}

			// deregister old projects
			for (Project project : registeredProjects) {
				deRegisterProject(project);
			}
		}
	}
}