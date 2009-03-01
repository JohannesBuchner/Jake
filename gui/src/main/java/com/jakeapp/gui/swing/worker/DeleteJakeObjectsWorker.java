package com.jakeapp.gui.swing.worker;

import java.util.EnumSet;
import java.util.List;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.xcore.EventCore;


public class DeleteJakeObjectsWorker extends SwingWorkerWithAvailableLaterObject<Integer> {
	Project project;
	List<JakeObject> jos;
	
	private Project getProject() {
		return project;
	}

	
	private void setProject(Project project) {
		this.project = project;
	}
	
	private List<JakeObject> getJos() {
		return jos;
	}
	
	private void setJos(List<JakeObject> jos) {
		this.jos = jos;
	}

	public DeleteJakeObjectsWorker(Project project, List<JakeObject> jos) {
		super();
		this.setProject(project);
		this.setJos(jos);
	}
	
	@Override
	protected AvailableLaterObject<Integer> calculateFunction() throws RuntimeException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void done() {
		// inform the core that there are new log entries available.
		EventCore.get().fireDataChanged(EnumSet.of(DataChanged.Reason.Files), null);
		if ((this.jos.get(0)) instanceof FileObject)
			EventCore.get().fireFilesChanged(this.project);
		else
			EventCore.get().fireNotesChanged(this.project);
	}
}